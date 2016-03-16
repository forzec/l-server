package ai.hellbound;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.scripts.Functions;

/**
 * AI Tortured Native в городе-инстанте на Hellbound<br>
 * - периодически кричат
 *
 * @author SYS
 */
public class TorturedNative extends Fighter
{
	public TorturedNative(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return true;

		if(Rnd.chance(1))
			if(Rnd.chance(10))
				Functions.npcSay(actor, "Eeeek... I feel sick... yow...!");
			else
				Functions.npcSay(actor, "It... will... kill... everyone...!");

		return super.thinkActive();
	}
}