package ai.seedofinfinity;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.base.SpecialEffectState;
import org.mmocore.gameserver.model.instances.NpcInstance;

/**
 * @author pchayka
 */

public class FeralHound extends Fighter
{
	public FeralHound(NpcInstance actor)
	{
		super(actor);
		actor.setInvul(SpecialEffectState.TRUE);
	}

	@Override
	protected boolean randomAnimation()
	{
		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}