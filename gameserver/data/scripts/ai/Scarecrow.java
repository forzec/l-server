package ai;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.base.SpecialEffectState;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;

public class Scarecrow extends Fighter
{
	public Scarecrow(NpcInstance actor)
	{
		super(actor);
		actor.block();
		actor.setInvul(SpecialEffectState.TRUE);
	}

	@Override
	protected void onIntentionAttack(Creature target)
	{}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{}

	@Override
	protected void onEvtAggression(Creature attacker, int aggro)
	{}
}