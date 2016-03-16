package ai;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author VISTALL
 * @date 23:59/28.04.2012
 */
public class PrizeLuckyPig extends DefaultAI
{
	public PrizeLuckyPig(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		getActor().doDie(attacker);
	}
}
