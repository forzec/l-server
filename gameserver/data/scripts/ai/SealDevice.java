package ai;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillUse;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * AI Emperor's Seal Device.
 * @author pchayka
 */
public class SealDevice extends Fighter
{
	private boolean _firstAttack = false;

	public SealDevice(NpcInstance actor)
	{
		super(actor);
		actor.block();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();
		if(!_firstAttack)
		{
			actor.broadcastPacket(new MagicSkillUse(actor, actor, 5980, 1, 0, 0));
			_firstAttack = true;
		}
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{}
}