package ai.residences.clanhall;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.PositionUtils;
import ai.residences.SiegeGuardFighter;

/**
 * @author VISTALL
 * @date 18:22/10.05.2011
 */
public class LidiaVonHellmann extends SiegeGuardFighter
{
	private static final SkillEntry DRAIN_SKILL = SkillTable.getInstance().getSkillEntry(4999, 1);
	private static final SkillEntry DAMAGE_SKILL = SkillTable.getInstance().getSkillEntry(4998, 1);

	public LidiaVonHellmann(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		Functions.npcShout(getActor(), NpcString.HMM_THOSE_WHO_ARE_NOT_OF_THE_BLOODLINE_ARE_COMING_THIS_WAY_TO_TAKE_OVER_THE_CASTLE__HUMPH__THE_BITTER_GRUDGES_OF_THE_DEAD);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);

		Functions.npcShout(getActor(), NpcString.GRARR_FOR_THE_NEXT_2_MINUTES_OR_SO_THE_GAME_ARENA_ARE_WILL_BE_CLEANED);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();

		super.onEvtAttacked(attacker, skill, damage);

		if(Rnd.chance(0.22))
			addTaskCast(attacker, DRAIN_SKILL);
		else if(actor.getCurrentHpPercents() < 20 && Rnd.chance(0.22))
			addTaskCast(attacker, DRAIN_SKILL);

		if(PositionUtils.calculateDistance(actor, attacker, false) > 300 && Rnd.chance(0.13))
			addTaskCast(attacker, DAMAGE_SKILL);
	}
}
