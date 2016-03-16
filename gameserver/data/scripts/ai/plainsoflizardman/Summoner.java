package ai.plainsoflizardman;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.PositionUtils;

/** Author: Bonux
	При ударе монстра спавнятся 2 х Tanta Lizardman Scout и они агрятся на игрока.
**/
public class Summoner extends Mystic
{
	private static final int TANTA_LIZARDMAN_SCOUT = 22768;
	private static final int SPAWN_COUNT = 2;
	private boolean _spawnedMobs;

	public Summoner(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		_spawnedMobs = false;
		super.onEvtSpawn();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		if(!_spawnedMobs && attacker.isPlayable())
		{
			_spawnedMobs = true;

			NpcInstance actor = getActor();
			for(int i = 0; i < SPAWN_COUNT; i++)
			{
				NpcInstance npc = NpcUtils.spawnSingle(TANTA_LIZARDMAN_SCOUT, actor.getLoc());
				npc.setHeading(PositionUtils.calculateHeadingFrom(npc, attacker));
				npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 1000);
			}
		}
		super.onEvtAttacked(attacker, skill, damage);
	}
}
