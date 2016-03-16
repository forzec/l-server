package ai;

import java.util.List;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * AI Kanadis Follower и минионов для Rim Pailaka
 * @author pchayka
 */

public class KanadisFollower extends Fighter
{
	public KanadisFollower(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		NpcInstance actor = getActor();
		List<NpcInstance> around = actor.getAroundNpc(7000, 300);
		if(around != null && !around.isEmpty())
			for(NpcInstance npc : around)
				if(npc.getNpcId() == 36562)
					actor.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, npc, null, 500);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();
		if(attacker.getNpcId() == 36562)
		{
			actor.getAggroList().addDamageHate(attacker, 0, 100);
			startRunningTask(2000);
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		}
		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		return false;
	}
}