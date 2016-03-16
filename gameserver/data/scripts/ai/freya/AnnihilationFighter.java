package ai.freya;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * @author pchayka
 */
public class AnnihilationFighter extends Fighter
{
	public AnnihilationFighter(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if(Rnd.chance(5))
			NpcUtils.spawnSingle(18839, Location.findPointToStay(getActor(), 40, 120), getActor().getReflection()); // Maguen

		super.onEvtDead(killer);
	}

	@Override
	public boolean canSeeInSilentMove(Playable target)
	{
		return true;
	}
}