package ai.dragonvalley;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * @author pchayka
 */
public class BloodyMonster extends Fighter
{
	public BloodyMonster(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);
		if(Rnd.get(15) < 1)
		{
			for(int i = 0; i < 5; i++)
			{
				NpcInstance n = NpcUtils.spawnSingle(getActor().getNpcId(), Location.coordsRandomize(getActor().getLoc(), 50, 100));
				n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 1000);
			}
		}
	}
}