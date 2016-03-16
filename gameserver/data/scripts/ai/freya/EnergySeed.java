package ai.freya;

import java.util.HashMap;
import java.util.Map;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * @author pchayka
 */
public class EnergySeed extends DefaultAI
{
	private static final Map<String, Integer> zoneNpc = new HashMap<String, Integer>();
	static
	{
		zoneNpc.put("[13_23_cocracon]", 22761);
		zoneNpc.put("[14_23_raptilicon]", 22755);
		zoneNpc.put("[14_23_beastacon]", 22747);
	}

	public EnergySeed(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();

		// In the SoA gathered seed can spawn a mob
		for(String s : zoneNpc.keySet())
			if(actor.isInZone(s) && Rnd.chance(50))
			{
				NpcInstance npc = NpcUtils.spawnSingle(zoneNpc.get(s), actor.getLoc(), getActor().getReflection());
				npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 2000);
			}
		super.onEvtDead(killer);
	}
}