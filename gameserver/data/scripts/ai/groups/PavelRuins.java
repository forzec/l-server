package ai.groups;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.PositionUtils;

/**
 * @author B0nux
 */
public class PavelRuins extends Fighter
{
	//Monster ID's
	private static final int PAVEL_SAFETY_DEVICE = 18917;
	private static final int CRUEL_PINCER_GOLEM_1 = 22801;
	private static final int CRUEL_PINCER_GOLEM_2 = 22802;
	private static final int CRUEL_PINCER_GOLEM_3 = 22803;
	private static final int DRILL_GOLEM_OF_TERROR_1 = 22804;
	private static final int DRILL_GOLEM_OF_TERROR_2 = 22805;
	private static final int DRILL_GOLEM_OF_TERROR_3 = 22806;

	public PavelRuins(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		super.onEvtDead(killer);
		ThreadPoolManager.getInstance().schedule(new SpawnNext(actor, killer), 5000);
	}

	private static class SpawnNext extends RunnableImpl
	{
		private NpcInstance _actor;
		private Creature _killer;

		public SpawnNext(NpcInstance actor, Creature killer)
		{
			_actor = actor;
			_killer = killer;
		}

		public void runImpl()
		{
			if(Rnd.chance(70))
			{
				Location loc = _actor.getLoc();
				switch(_actor.getNpcId())
				{
					case PAVEL_SAFETY_DEVICE:
						loc = new Location(loc.x + 30, loc.y + -30, loc.z);
						spawnNextMob(CRUEL_PINCER_GOLEM_3, _killer, loc);
						loc = new Location(loc.x + -30, loc.y + 30, loc.z);
						spawnNextMob(DRILL_GOLEM_OF_TERROR_3, _killer, loc);
						break;
					case CRUEL_PINCER_GOLEM_1:
						spawnNextMob(CRUEL_PINCER_GOLEM_2, _killer, loc);
						break;
					case CRUEL_PINCER_GOLEM_3:
						spawnNextMob(CRUEL_PINCER_GOLEM_1, _killer, loc);
						break;
					case DRILL_GOLEM_OF_TERROR_1:
						spawnNextMob(DRILL_GOLEM_OF_TERROR_2, _killer, loc);
						break;
					case DRILL_GOLEM_OF_TERROR_3:
						spawnNextMob(DRILL_GOLEM_OF_TERROR_1, _killer, loc);
						break;
				}
			}
		}
	}

	private static void spawnNextMob(int npcId, Creature killer, Location loc)
	{
		NpcInstance npc = NpcUtils.spawnSingle(npcId, loc);
		npc.setHeading(PositionUtils.calculateHeadingFrom(npc, killer));
		npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 1000);
	}
}
