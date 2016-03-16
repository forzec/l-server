package npc.model.beastfarm;

import java.util.List;
import java.util.concurrent.Future;

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
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Skill.SkillType;
import org.mmocore.gameserver.model.base.SpecialEffectState;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.PositionUtils;

public class TamedMonsterInstance extends NpcInstance
{
	private static final int MAX_DISTANCE_FROM_OWNER = 2000;
	private static final int MAX_DISTANCE_FOR_BUFF = 200;

	private HardReference<Player> _ownerRef = HardReferences.emptyRef();
	private int _consumeItemId, _tickCount = 60; // 60 минут
	private Future<?> _consumeTask, _buffTask;
	private OnTeleportListener _teleportListener = new OnTeleportListenerImpl();

	public TamedMonsterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);

		setUndying(SpecialEffectState.FALSE);
		_hasChatWindow = false;
		_hasRandomAnimation = false;
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
		if(tamedBeasts.size() >= 1)
		{
			NpcInstance old = CollectionUtils.safeGet(tamedBeasts, 0);
			if(old != null)
				old.deleteMe();
		}

		player.addListener(_teleportListener);
		player.addTamedBeast(this);
	}

	@Override
	public Player getPlayer()
	{
		return _ownerRef.get();
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();

		_consumeTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ConsumeTask(), 60000L, 60000L);
		_buffTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new BuffTask(), 5000L, 5000L);

		if (Rnd.chance(5))
			ChatUtils.say(this, NpcString.valueOf(Rnd.get(2024, 2028)), getPlayer().getName());
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

		if(_buffTask != null)
		{
			_buffTask.cancel(false);
			_buffTask = null;
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

	private class OnTeleportListenerImpl implements OnTeleportListener
	{
		@Override
		public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
		{
			deleteIfOutOfRange(x, y);
		}
	}

	private class BuffTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			final Player player = getPlayer();
			if(player == null)
			{
				deleteMe();
				return;
			}

			if(!isInRange(player, MAX_DISTANCE_FOR_BUFF))
			{
				setFollowTarget(player);
				getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player, Config.FOLLOW_RANGE);
				return;
			}

			int count = 0;
			SkillEntry skill;
			SkillEntry[] skills;
			boolean[] list;

			skills = getTemplate().getHealSkills();
			if (skills.length > 0 && player.isInCombat()) // can heal/recharge
			{
				skill = skills[0];
				final double value = skill.getTemplate().getSkillType() == SkillType.MANAHEAL ? player.getCurrentMpPercents() : player.getCurrentHpPercents();
				if (value < 25)
				{
					if (Rnd.chance(40) && !isSkillDisabled(skill) && skill.checkCondition(TamedMonsterInstance.this, player, false, true, true))
					{
						doCast(skill, player, false);
						return;
					}
				}
				else if (value < 50)
				{
					if (Rnd.chance(20) && !isSkillDisabled(skill) && skill.checkCondition(TamedMonsterInstance.this, player, false, true, true))
					{
						doCast(skill, player, false);
						return;
					}
				}
			}

			skills = getTemplate().getBuffSkills();
			if (skills.length > 0) // can buff
			{
				list = new boolean[skills.length];
				for (int i = 0; i < skills.length; i++)
				{
					boolean found = player.getEffectList().containEffectFromSkills(skills[i].getId());
					list[i] = found;
					if (found)
						count++;
				}				

				if (count <= skills.length / 2)
				{
					int idx = Rnd.get(list.length);
					if (!list[idx])
					{
						skill = skills[idx];
						if (!isSkillDisabled(skill) && skill.checkCondition(TamedMonsterInstance.this, player, false, true, true))
						{
							doCast(skills[idx], player, false);
							return;
						}
					}
				}
			}

			// TODO: DS: debuffs
			//skills = getTemplate().getDebuffSkills();
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

			final Player player = getPlayer();
			if(player == null)
			{
				deleteMe();
				return;
			}

			if(deleteIfOutOfRange(player.getX(), player.getY()))
				return;

			if(!player.consumeItem(_consumeItemId, 1L))
				deleteMe();

			ChatUtils.say(TamedMonsterInstance.this, NpcString.valueOf(Rnd.get(2029, 2038)));
		}
	}
}