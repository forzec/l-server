package ai.kamaloka;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.base.SpecialEffectState;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;

public class KnightMontagnarFollower extends Fighter
{
	public KnightMontagnarFollower(NpcInstance actor)
	{
		super(actor);
		actor.setInvul(SpecialEffectState.TRUE);
	}

	@Override
	protected void onEvtAggression(Creature attacker, int aggro)
	{
		if(aggro < 1000000)
			return;
		super.onEvtAggression(attacker, aggro);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{}
}