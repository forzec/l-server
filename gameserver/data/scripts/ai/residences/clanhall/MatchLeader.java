package ai.residences.clanhall;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author VISTALL
 * @date 16:38/22.04.2011
 */
public class MatchLeader extends MatchFighter
{
	public static final SkillEntry ATTACK_SKILL = SkillTable.getInstance().getSkillEntry(4077, 6);

	public MatchLeader(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int dam)
	{
		super.onEvtAttacked(attacker, skill, dam);

		if(Rnd.chance(10))
			addTaskCast(attacker, ATTACK_SKILL);
	}
}
