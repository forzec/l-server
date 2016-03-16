package ai.hellbound;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;

public class OutpostGuards extends Fighter
{
	public OutpostGuards(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}
}