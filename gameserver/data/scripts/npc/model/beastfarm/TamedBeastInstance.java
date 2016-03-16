package npc.model.beastfarm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.mmocore.commons.collections.CollectionUtils;
import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.lang.reference.HardReferences;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.listener.actor.player.OnTeleportListener;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.GameObjectTasks;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.base.SpecialEffectState;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.skillclasses.TameControl;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.PositionUtils;

/**
 * @author VISTALL
 * @date 12:59/30.10.2011
 */
public class TamedBeastInstance extends NpcInstance implements TameControl.TameControlTarget
{
	private class OnTeleportListenerImpl implements OnTeleportListener
	{
		@Override
		public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
		{
			deleteIfOutOfRange(x, y);
		}
	}

	public class Buff extends RunnableImpl
	{
		private Player _owner;
		private SkillEntry _skill;

		public Buff(Player owner, SkillEntry skill)
		{
		_owner = owner;
			_skill = skill;
		}

		@Override
		public void runImpl()
		{
			doCast(_skill, _owner, true);
		}
	}

	private class ConsumeTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			_tickCount--;
			if(_tickCount <= 0)
			{
				deleteMe();
				return;
			}

			Player player = getPlayer();
			if(player == null)
			{
				deleteMe();
				return;
			}

			if(deleteIfOutOfRange(player.getX(), player.getY()))
				return;

			if(!player.consumeItem(_consumeItemId, 1L))
			{
				player.sendPacket(new SystemMessage(SystemMsg.S1).addNpcString(_consumeItemId == 15474 ? NpcString.S1_IS_LEAVING_YOU_BECAUSE_YOU_DONT_HAVE_ENOUGH_GOLDEN_SPICES : NpcString.S1_IS_LEAVING_YOU_BECAUSE_YOU_DONT_HAVE_ENOUGH_CRYSTAL_SPICES, getName()));
				deleteMe();
			}
		}
	}

	private static final int MAX_DISTANCE_FROM_OWNER = 2000;
	private static final int MAX_DISTANCE_FOR_BUFF = 200;

	private HardReference<Player> _ownerRef = HardReferences.emptyRef();
	private List<SkillEntry> _skills = new ArrayList<SkillEntry>(2);
	private int _consumeItemId, _tickCount = 20; // 20 минут
	private Future<?> _consumeTask;
	private OnTeleportListener _teleportListener = new OnTeleportListenerImpl();

	@SuppressWarnings("unchecked")
	private static final Pair<NpcString, int[]>[] TAMED_DATA = new Pair[6];

	static
	{
		TAMED_DATA[0] = new ImmutablePair<NpcString, int[]>(NpcString.RECKLESS_S1, new int[]{6671});
		TAMED_DATA[1] = new ImmutablePair<NpcString, int[]>(NpcString.S1_OF_BALANCE, new int[]{6431,6666});
		TAMED_DATA[2] = new ImmutablePair<NpcString, int[]>(NpcString.SHARP_S1, new int[]{6432,6668});
		TAMED_DATA[3] = new ImmutablePair<NpcString, int[]>(NpcString.USEFUL_S1, new int[]{6433,6670});
		TAMED_DATA[4] = new ImmutablePair<NpcString, int[]>(NpcString.S1_OF_BLESSING, new int[]{6669,6672});
		TAMED_DATA[5] = new ImmutablePair<NpcString, int[]>(NpcString.SWIFT_S1, new int[]{6434,6667});
	}

	public TamedBeastInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setUndying(SpecialEffectState.FALSE);
		_hasChatWindow = false;
		_hasRandomAnimation = false;

		Pair<NpcString, int[]> type = TAMED_DATA[Rnd.get(TAMED_DATA.length)];
		setNameNpcString(type.getKey());
		setName("#" + getNameNpcStringByNpcId(getNpcId()).getId());

		for(int skillId : type.getValue())
		{
			SkillEntry sk = SkillTable.getInstance().getSkillEntry(skillId, 1);
			if(sk != null)
				_skills.add(sk);
		}
	}

	public void setConsumeItemId(int itemId)
	{
		_consumeItemId = itemId;
	}

	public void setOwner(Player player)
	{
		_ownerRef = player.getRef();

		setTitle(player.getName());

		List<NpcInstance> tamedBeasts = player.getTamedBeasts();
		if(tamedBeasts.size() >= 7)
		{
			NpcInstance old = CollectionUtils.safeGet(tamedBeasts, 0);
			if(old != null)
				old.deleteMe();
		}

		player.addListener(_teleportListener);
		player.addTamedBeast(this);
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();

		_consumeTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ConsumeTask(), 60000L, 60000L);
	}

	@Override
	public void onDelete()
	{
		super.onDelete();

		if(_consumeTask != null)
		{
			_consumeTask.cancel(false);
			_consumeTask = null;
		}

		Player player = getPlayer();
		if(player != null)
		{
			player.removeListener(_teleportListener);
			player.removeTamedBeast(this);
		}

		_ownerRef = HardReferences.emptyRef();
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return false;
	}

	public static NpcString getNameNpcStringByNpcId(int npcId)
	{
		switch(npcId)
		{
			case 18869:
				return NpcString.ALPEN_KOOKABURRA;
			case 18870:
				return NpcString.ALPEN_COUGAR;
			case 18871:
				return NpcString.ALPEN_BUFFALO;
			case 18872:
				return NpcString.ALPEN_GRENDEL;
		}
		return NpcString.NONE;
	}

	@Override
	public void doCastBuffs()
	{
		if(!isInRange(getPlayer(), MAX_DISTANCE_FOR_BUFF))
		{
			setFollowTarget(getPlayer());
			getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, getPlayer(), Config.FOLLOW_RANGE);
			return;
		}

		int delay = 0;
		for(SkillEntry skill : _skills)
		{
			ThreadPoolManager.getInstance().schedule(new Buff(getPlayer(), skill), delay);
			delay = delay + skill.getTemplate().getHitTime() + 500;
		}
	}

	@Override
	public void free()
	{
		getAI().stopAITask();

		double radian = PositionUtils.convertHeadingToRadian(getHeading());
		Location loc = new Location(getX() - (int) (Math.sin(radian) * 500), getY() + (int) (Math.cos(radian) * 500), getZ());

		moveToLocation(loc, 0, false);

		ThreadPoolManager.getInstance().schedule(new GameObjectTasks.DeleteTask(this), 1000L);
	}

	@Override
	public Player getPlayer()
	{
		return _ownerRef.get();
	}

	private boolean deleteIfOutOfRange(int x, int y)
	{
		if(PositionUtils.getDistance(getX(), getY(),  x, y) > MAX_DISTANCE_FROM_OWNER)
		{
			deleteMe();
			return true; 
		}
		else
			return false;
	}
}
