package ai.freya;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.scripts.Functions;

/**
 * @author pchayka
 */
public class FreyaQuest extends Fighter
{
	public FreyaQuest(NpcInstance actor)
	{
		super(actor);
		MAX_PURSUE_RANGE = Integer.MAX_VALUE;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		Reflection r = getActor().getReflection();
		for(Player p : r.getPlayers())
			this.notifyEvent(CtrlEvent.EVT_ATTACKED, p, null, 300);

		Functions.npcSay(getActor(), NpcString.FREYA_HAS_STARTED_TO_MOVE);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected void returnHome(boolean clearAggro, boolean teleport)
	{
		//
	}
}