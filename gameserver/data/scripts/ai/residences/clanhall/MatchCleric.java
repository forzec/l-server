package ai.residences.clanhall;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author VISTALL
 * @date 16:38/22.04.2011
 */
public class MatchCleric extends MatchFighter
{
	public static final SkillEntry HEAL = SkillTable.getInstance().getSkillEntry(4056, 6);

	public MatchCleric(NpcInstance actor)
	{
		super(actor);
	}

	public void heal()
	{
		NpcInstance actor = getActor();
		addTaskCast(actor, HEAL);
		doTask();
	}
}
