package npc.model.residences;

import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 18:42/23.05.2012
 */
public class SiegeAttackerInstance extends MonsterInstance
{
	public SiegeAttackerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setHasChatWindow(false);
	}

	@Override
	public int getAggroRange()
	{
		return 1200;
	}

	@Override
	public void spawnMinion(NpcInstance minion)
	{
		SiegeEvent<?, ?> siegeEvent = getEvent(SiegeEvent.class);
		if (siegeEvent != null)
			minion.addEvent(siegeEvent);

		super.spawnMinion(minion);
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		final Player player = attacker.getPlayer();
		if(player == null)
			return false;
		final SiegeEvent<?, ?> siegeEvent = getEvent(SiegeEvent.class);
		if(siegeEvent == null)
			return false;
		final Clan clan = player.getClan();
		if (clan == null)
			return false;
		final SiegeEvent<?, ?> siegeEvent2 = attacker.getEvent(SiegeEvent.class);
		if(siegeEvent == siegeEvent2 && siegeEvent.getSiegeClan(SiegeEvent.ATTACKERS, clan) != null)
			return false;
		return true;
	}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}

	@Override
	public boolean isMonster()
	{
		return false;
	}
}
