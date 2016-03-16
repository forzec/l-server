package npc.model.residences.clanhall;

import java.util.HashMap;
import java.util.Map;

import org.mmocore.gameserver.model.AggroList;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.events.impl.ClanHallSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import npc.model.residences.SiegeGuardInstance;

/**
 * @author VISTALL
 * @date 12:25/08.05.2011
 */
public class LidiaVonHellmannInstance extends SiegeGuardInstance
{
	public LidiaVonHellmannInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onDeath(Creature killer)
	{
		SiegeEvent<?, ?> siegeEvent = getEvent(SiegeEvent.class);
		if(siegeEvent == null)
			return;

		siegeEvent.processStep(getMostDamagedClan());

		super.onDeath(killer);
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
	public boolean isEffectImmune()
	{
		return true;
	}
}
