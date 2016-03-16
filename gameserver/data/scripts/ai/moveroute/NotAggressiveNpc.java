package ai.moveroute;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author VISTALL
 * @date 22:38/24.10.2011
 */
public class NotAggressiveNpc extends MoveRoutDefaultAI
{
	public NotAggressiveNpc(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		//
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
		//
	}
}
