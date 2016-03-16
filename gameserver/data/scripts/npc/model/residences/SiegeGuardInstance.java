package npc.model.residences;

import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class SiegeGuardInstance extends MonsterInstance
{
	public SiegeGuardInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setHasChatWindow(false);
	}

	@Override
	public boolean isSiegeGuard()
	{
		return true;
	}

	@Override
	public int getAggroRange()
	{
		return 1200;
	}

	@Override
	public double getRewardRate(Player player)
	{
		return Config.RATE_DROP_SIEGE_GUARD; // ПА не действует на эполеты
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
		if(siegeEvent == siegeEvent2 && siegeEvent.getSiegeClan(SiegeEvent.DEFENDERS, clan) != null)
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