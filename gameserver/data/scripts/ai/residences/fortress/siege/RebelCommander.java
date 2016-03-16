package ai.residences.fortress.siege;

import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.scripts.Functions;
import ai.residences.SiegeGuardFighter;

/**
 * @author VISTALL
 * @date 20:10/19.04.2011
 */
public class RebelCommander extends SiegeGuardFighter
{
	public RebelCommander(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);

		Functions.npcSay(getActor(), NpcString.DONT_THINK_THAT_ITS_GONNA_END_LIKE_THIS);
	}
}
