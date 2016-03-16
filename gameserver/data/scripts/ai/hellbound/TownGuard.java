package ai.hellbound;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.scripts.Functions;

/**
 * AI Town Guard в городе-инстанте на Hellbound<br>
 * - перед тем как броситься в атаку кричат
 *
 * @author SYS
 */
public class TownGuard extends Fighter
{
	public TownGuard(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onIntentionAttack(Creature target)
	{
		NpcInstance actor = getActor();
		if(getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && Rnd.chance(50))
			Functions.npcSay(actor, "Invader!");
		super.onIntentionAttack(target);
	}
}