package ai.freya;

import java.util.List;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author pchayka
 */

public class JiniaGuild extends Fighter
{
	public JiniaGuild(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return false;

		List<NpcInstance> around = actor.getAroundNpc(4000, 300);
		if(around != null && !around.isEmpty())
			for(NpcInstance npc : around)
				if(npc.getNpcId() == 29179)
					actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, npc, 3000);
		return true;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		if(attacker == null || attacker.isPlayable())
			return;

		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	public boolean checkAggression(Creature target)
	{
		if(target.isPlayable())
			return false;

		return super.checkAggression(target);
	}
}