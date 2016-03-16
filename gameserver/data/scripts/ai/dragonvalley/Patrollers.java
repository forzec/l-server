package ai.dragonvalley;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Location;

/**
 * @author pchayka
 */

public class Patrollers extends Fighter
{
	protected Location[] _points = null;
	private int[] _teleporters = {
			22857,
			22833,
			22834
	};

	private int _lastPoint = 0;
	private boolean _firstThought = true;
	private volatile boolean _moving = false;

	public Patrollers(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected void thinkAttack()
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return;

		if(doTask() && !actor.isAttackingNow() && !actor.isCastingNow())
		{
			if(!createNewTask())
			{
				if(System.currentTimeMillis() > getAttackTimeout())
					returnHome();
			}
		}
	}

	@Override
	protected boolean thinkActive()
	{
		if(super.thinkActive())
			return true;

		if(!getActor().isMoving)
			startMoveTask();

		return true;
	}

	private void startMoveTask()
	{
		if(_moving)
			return;

		try
		{
			_moving = true;

			NpcInstance npc = getActor();
			if(_firstThought)
			{
				_lastPoint = getIndex(Location.findNearest(npc, _points));
				_firstThought = false;
			}
			else
				_lastPoint++;

			if(_lastPoint >= _points.length)
			{
				_lastPoint = 0;
				if(ArrayUtils.contains(_teleporters, npc.getNpcId()))
					npc.teleToLocation(_points[_lastPoint]);
			}

			npc.setRunning();
			if(Rnd.chance(30))
				npc.altOnMagicUseTimer(npc, SkillTable.getInstance().getSkillEntry(6757, 1));
			addTaskMove(Location.findPointToStay(_points[_lastPoint], 250, npc.getGeoIndex()), true);
			doTask();
		}
		finally
		{
			_moving = false;
		}
	}

	private int getIndex(Location loc)
	{
		for(int i = 0; i < _points.length; i++)
			if(_points[i] == loc)
				return i;
		return 0;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		return false;
	}

	@Override
	protected void teleportHome()
	{
	}

	@Override
	protected void returnHome(boolean clearAggro, boolean teleport)
	{
		super.returnHome(clearAggro, teleport);
		clearTasks();
		_firstThought = true;
		startMoveTask();
	}
}
