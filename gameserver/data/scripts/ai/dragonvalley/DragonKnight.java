package ai.dragonvalley;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * @author pchayka
 */
public class DragonKnight extends Fighter
{
	public DragonKnight(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);
		switch(getActor().getNpcId())
		{
			case 22844:
				if(Rnd.chance(50))
				{
					NpcInstance n = NpcUtils.spawnSingle(22845, getActor().getLoc());
					n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 2);
				}
				break;
			case 22845:
				if(Rnd.chance(50))
				{
					NpcInstance n = NpcUtils.spawnSingle(22846, getActor().getLoc());
					n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 2);
				}
				break;
		}

	}
}