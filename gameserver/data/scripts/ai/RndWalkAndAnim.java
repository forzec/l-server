package ai;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.geodata.GeoEngine;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;

public class RndWalkAndAnim extends DefaultAI
{
	protected static final int PET_WALK_RANGE = 100;

	public RndWalkAndAnim(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if(actor.isMoving)
			return false;

		int val = Rnd.get(100);

		if(val < 10)
			randomWalk();
		else if(val < 20)
			actor.onRandomAnimation();

		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		NpcInstance actor = getActor();
		if(actor == null)
			return false;

		Location sloc = actor.getSpawnedLoc();

		int x = sloc.x + Rnd.get(2 * PET_WALK_RANGE) - PET_WALK_RANGE;
		int y = sloc.y + Rnd.get(2 * PET_WALK_RANGE) - PET_WALK_RANGE;
		int z = GeoEngine.getHeight(x, y, sloc.z, actor.getGeoIndex());

		actor.setRunning();
		actor.moveToLocation(x, y, z, 0, true);

		return true;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{}
}