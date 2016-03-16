package ai.residences.fortress.siege;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.entity.events.impl.FortressSiegeEvent;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import ai.residences.SiegeGuardFighter;

/**
 * @author VISTALL
 * @date 16:43/17.04.2011
 */
public class General extends SiegeGuardFighter
{
	public General(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int dam)
	{
		super.onEvtAttacked(attacker, skill, dam);
		NpcInstance actor = getActor();

		if(Rnd.chance(1))
			Functions.npcSay(actor, NpcString.DO_YOU_NEED_MY_POWER_YOU_SEEM_TO_BE_STRUGGLING);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		NpcInstance actor = getActor();

		FortressSiegeEvent siegeEvent = actor.getEvent(FortressSiegeEvent.class);
		if(siegeEvent == null)
			return;

		if(siegeEvent.getResidence().getFacilityLevel(Fortress.GUARD_BUFF) > 0)
			actor.doCast(SkillTable.getInstance().getSkillEntry(5432, siegeEvent.getResidence().getFacilityLevel(Fortress.GUARD_BUFF)), actor, false);

		siegeEvent.barrackAction(4, false);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		FortressSiegeEvent siegeEvent = actor.getEvent(FortressSiegeEvent.class);
		if(siegeEvent == null)
			return;

		siegeEvent.barrackAction(4, true);

		siegeEvent.broadcastTo(SystemMsg.THE_BARRACKS_HAVE_BEEN_SEIZED, FortressSiegeEvent.ATTACKERS, FortressSiegeEvent.DEFENDERS);

		Functions.npcShout(actor, NpcString.I_FEEL_SO_MUCH_GRIEF_THAT_I_CANT_EVEN_TAKE_CARE_OF_MYSELF);

		super.onEvtDead(killer);

		siegeEvent.checkBarracks();
	}
}
