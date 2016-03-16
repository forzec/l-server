package npc.model.residences.clanhall;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.model.AggroList;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.entity.events.impl.ClanHallSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.entity.events.objects.SpawnExObject;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillUse;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;
import npc.model.residences.SiegeGuardInstance;

/**
 * @author VISTALL
 * @date 21:08/07.05.2011
 * 35410
 * 4235 skill id
 */
public class GustavInstance extends SiegeGuardInstance implements _34SiegeGuard
{
	private AtomicBoolean _canDead = new AtomicBoolean();

	public GustavInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();

		_canDead.set(false);

		ChatUtils.shout(this, NpcString.PREPARE_TO_DIE_FOREIGN_INVADERS_I_AM_GUSTAV_THE_ETERNAL_RULER_OF_THIS_FORTRESS_AND_I_HAVE_TAKEN_UP_MY_SWORD_TO_REPEL_THEE);
	}

	@Override
	public void onDeath(Creature killer)
	{
		if(!_canDead.get())
		{
			_canDead.set(true);
			setCurrentHp(1, true);

			// Застваляем снять таргет и остановить аттаку
			for(Creature cha : World.getAroundCharacters(this))
				cha.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, this);

			ClanHallSiegeEvent siegeEvent = getEvent(ClanHallSiegeEvent.class);
			if(siegeEvent == null)
				return;

			SpawnExObject obj = siegeEvent.getFirstObject(ClanHallSiegeEvent.BOSS);

			for(int i = 0; i < 3; i++)
			{
				final NpcInstance npc = obj.getSpawns().get(i).getFirstSpawned();

				ChatUtils.say(this, ((_34SiegeGuard)npc).teleChatSay());
				npc.broadcastPacket(new MagicSkillUse(npc, npc, 4235, 1, 10000, 0));

				_skillTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
				{
					@Override
					public void runImpl() throws Exception
					{
						Location loc = Location.findAroundPosition(177134, -18807, -2256, 50, 100, npc.getGeoIndex());

						npc.teleToLocation(loc);

						if(npc == GustavInstance.this)
							npc.reduceCurrentHp(npc.getCurrentHp(), npc, null, 0, false, false, false, false, false, false, false, false);
					}
				}, 10000L);
			}
		}
		else
		{
			if(_skillTask != null)
			{
				_skillTask.cancel(false);
				_skillTask = null;
			}

			SiegeEvent<?,?> siegeEvent = getEvent(SiegeEvent.class);
			if(siegeEvent == null)
				return;

			siegeEvent.processStep(getMostDamagedClan());

			super.onDeath(killer);
		}
	}

	public Clan getMostDamagedClan()
	{
		ClanHallSiegeEvent siegeEvent = getEvent(ClanHallSiegeEvent.class);

		Player temp = null;

		Map<Player, Integer> damageMap = new HashMap<Player, Integer>();

		for(AggroList.HateInfo info : getAggroList().getPlayableMap().values())
		{
			Playable killer = (Playable)info.attacker;
			int damage = info.damage;
			if(killer.isServitor())
				temp = killer.getPlayer();
			else if(killer.isPlayer())
				temp = (Player) killer;

			if(temp == null || siegeEvent.getSiegeClan(SiegeEvent.ATTACKERS, temp.getClan()) == null)
				continue;

			if(!damageMap.containsKey(temp))
				damageMap.put(temp, damage);
			else
			{
				int dmg = damageMap.get(temp) + damage;
				damageMap.put(temp, dmg);
			}
		}

		int mostDamage = 0;
		Player player = null;

		for(Map.Entry<Player, Integer> entry : damageMap.entrySet())
		{
			int damage = entry.getValue();
			Player t = entry.getKey();
			if(damage > mostDamage)
			{
				mostDamage = damage;
				player = t;
			}
		}

		return player == null ? null : player.getClan();
	}

	@Override
	public NpcString teleChatSay()
	{
		return NpcString.THIS_IS_UNBELIEVABLE_HAVE_I_REALLY_BEEN_DEFEATED_I_SHALL_RETURN_AND_TAKE_YOUR_HEAD;
	}

	@Override
	public boolean isEffectImmune()
	{
		return true;
	}
}
