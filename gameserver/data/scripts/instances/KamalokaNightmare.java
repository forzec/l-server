package instances;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import npc.model.PathfinderInstance;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.idfactory.IdFactory;
import org.mmocore.gameserver.model.GameObject;
import org.mmocore.gameserver.model.GameObjectsStorage;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.templates.InstantZone;
import org.mmocore.gameserver.utils.Location;

public class KamalokaNightmare extends Reflection
{
	private static final int PATHFINDER = 32485;

	private static final int RANK_1_MIN_POINTS = 500;
	private static final int RANK_2_MIN_POINTS = 2500;
	private static final int RANK_3_MIN_POINTS = 4500;
	private static final int RANK_4_MIN_POINTS = 5500;
	private static final int RANK_5_MIN_POINTS = 7000;
	private static final int RANK_6_MIN_POINTS = 9000;

	private int _playerId = -1;
	private Future<?> _expireTask;

	private int killedKanabions = 0;
	private int killedDoplers = 0;
	private int killedVoiders = 0;

	private int delay_after_spawn = 0;
	private boolean is_spawn_possible = true;

	@Override
	protected void onCreate()
	{
		super.onCreate();

		InstantZone iz = getInstancedZone();
		if(iz != null)
		{
			int time_limit = iz.getTimelimit() * 1000 * 60;
			delay_after_spawn = time_limit / 3;
			startPathfinderTimer(time_limit - delay_after_spawn); // спавн патчфиндера происходит через 2\3 прошедшего времени.
		}
	}

	@Override
	protected void onCollapse()
	{
		super.onCollapse();

		stopPathfinderTimer();
	}

	public void addKilledKanabion(int type)
	{
		switch(type)
		{
			case 1:
				killedKanabions++;
				break;
			case 2:
				killedDoplers++;
				break;
			case 3:
				killedVoiders++;
				break;
		}
	}

	public int getRank()
	{
		int total = killedKanabions * 10 + killedDoplers * 20 + killedVoiders * 50;
		if(total >= RANK_6_MIN_POINTS)
			return 6;
		else if(total >= RANK_5_MIN_POINTS)
			return 5;
		else if(total >= RANK_4_MIN_POINTS)
			return 4;
		else if(total >= RANK_3_MIN_POINTS)
			return 3;
		else if(total >= RANK_2_MIN_POINTS)
			return 2;
		else if(total >= RANK_1_MIN_POINTS)
			return 1;
		else
			return 0;
	}

	public void startPathfinderTimer(long timeInMillis)
	{
		if(_expireTask != null)
		{
			_expireTask.cancel(false);
			_expireTask = null;
		}

		_expireTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				try
				{
					is_spawn_possible = false;
					for(Spawner s : KamalokaNightmare.this.getSpawns().toArray(new Spawner[KamalokaNightmare.this.getSpawns().size()]))
						s.deleteAll();

					KamalokaNightmare.this.getSpawns().clear();

					List<GameObject> delete = new ArrayList<GameObject>();
					lock.lock();
					try
					{
						for(GameObject o : _objects)
							if(!o.isPlayable())
								delete.add(o);
					}
					finally
					{
						lock.unlock();
					}

					for(GameObject o : delete)
						o.deleteMe();

					Player p = GameObjectsStorage.getPlayer(getPlayerId());
					if(p != null)
					{
						p.sendPacket(new SystemMessage(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(delay_after_spawn / 60000));

						InstantZone iz = KamalokaNightmare.this.getInstancedZone();
						if(iz != null)
						{
							String loc = iz.getAddParams().getString("pathfinder_loc", null);
							if(loc != null)
							{
								PathfinderInstance npc = new PathfinderInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(PATHFINDER));
								npc.setSpawnedLoc(Location.parseLoc(loc));
								npc.setReflection(KamalokaNightmare.this);
								npc.spawnMe(npc.getSpawnedLoc());
							}
						}
					}
					else
						collapse();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}, timeInMillis);
	}

	public void stopPathfinderTimer()
	{
		if(_expireTask != null)
		{
			_expireTask.cancel(false);
			_expireTask = null;
		}
	}

	public void setPlayerId(int id)
	{
		_playerId = id;
	}

	public int getPlayerId()
	{
		return _playerId;
	}

	@Override
	public boolean canChampions()
	{
		return false;
	}

	public boolean isSpawnPossible()
	{
		return is_spawn_possible;
	}
}