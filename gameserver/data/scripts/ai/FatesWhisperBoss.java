package ai;

import java.util.HashMap;
import java.util.Map;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * @author pchayka
 */
public class FatesWhisperBoss extends Fighter
{
	private static final Map<Integer, Integer> chestMap = new HashMap<Integer, Integer>();
	static
	{
		chestMap.put(25035, 31027); // Shilens Messenger Cabrio
		chestMap.put(25054, 31028); // Demon Kernon
		chestMap.put(25126, 31029); // Golkonda, the Longhorn General
		chestMap.put(25220, 31030); // Death Lord Hallate
	}

	public FatesWhisperBoss(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		Integer foundNpcId = chestMap.get(actor.getNpcId());
		if(foundNpcId != null)
			NpcUtils.spawnSingle(foundNpcId, actor.getLoc(), 120000L);

		super.onEvtDead(killer);
	}
}