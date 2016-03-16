package instances;

import java.util.concurrent.Future;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.Location;

/**
 * Управление замковой тюрьмой
 *
 * @author pchayka
 */

public class CastlePrison extends Reflection
{
	private Future<?> bossSpawnTask = null;
	private DeathListener _deathListener = new DeathListener();
	private static final int[][] dungeonCastleArray = new int[][]{
			{
					1,
					13
			},
			{
					2,
					14
			},
			{
					3,
					15
			},
			{
					4,
					16
			},
			{
					5,
					17
			},
			{
					6,
					18
			},
			{
					7,
					19
			},
			{
					8,
					20
			},
			{
					9,
					21
			}
	};

	private static final int RhiannaTheTraitor = 25546;
	private static final int TeslaTheDeceiver = 25549;
	private static final int SoulHunterChakundel = 25552;

	private static final int DurangoTheCrusher = 25553;
	private static final int BrutusTheObstinate = 25554;
	private static final int RangerKarankawa = 25557;
	private static final int SargonTheMad = 25560;

	private static final int BeautifulAtrielle = 25563;
	private static final int NagenTheTomboy = 25566;
	private static final int JaxTheDestroyer = 25569;

	private static final long _spawnInterval = 180 * 1000L;

	private static final int[] type1 = new int[]{
			RhiannaTheTraitor,
			TeslaTheDeceiver,
			SoulHunterChakundel
	};
	private static final int[] type2 = new int[]{
			DurangoTheCrusher,
			BrutusTheObstinate,
			RangerKarankawa,
			SargonTheMad
	};
	private static final int[] type3 = new int[]{
			BeautifulAtrielle,
			NagenTheTomboy,
			JaxTheDestroyer
	};

	private boolean _issetReuse = false;

	@Override
	protected void onCreate()
	{
		super.onCreate();
		bossSpawnTask = ThreadPoolManager.getInstance().schedule(new SpawnTask(type1[Rnd.get(type1.length)]), _spawnInterval);
		Castle _castle = ResidenceHolder.getInstance().getResidence(getCastleId(getInstancedZoneId()));
		if(_castle != null)
			_castle.setPrisonReuseTime(System.currentTimeMillis() + 14400 * 1000L);
	}

	@Override
	protected void onCollapse()
	{
		if(bossSpawnTask != null)
		{
			bossSpawnTask.cancel(false);
			bossSpawnTask = null;
		}
		super.onCollapse();
	}

	private class SpawnTask extends RunnableImpl
	{
		int _npcId;

		public SpawnTask(int npcId)
		{
			_npcId = npcId;
		}

		@Override
		public void runImpl() throws Exception
		{
			String bossLocation = getInstancedZone().getAddParams().getString("boss_spawn_location", null);
			NpcInstance boss = addSpawnWithoutRespawn(_npcId, Location.parseLoc(bossLocation), 0);
			boss.addListener(_deathListener);
		}
	}

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(!_issetReuse)
			{
				_issetReuse = true;
				setReenterTime(System.currentTimeMillis());
			}
			if(bossSpawnTask != null)
			{
				bossSpawnTask.cancel(false);
				bossSpawnTask = null;
			}
			int nextBossId = 0;
			if(ArrayUtils.contains(type1, self.getNpcId()))
				nextBossId = type2[Rnd.get(type2.length)];
			else if(ArrayUtils.contains(type2, self.getNpcId()))
				nextBossId = type3[Rnd.get(type3.length)];

			if(nextBossId != 0)
				bossSpawnTask = ThreadPoolManager.getInstance().schedule(new SpawnTask(nextBossId), _spawnInterval);
			else
				clearReflection(5, true);
		}
	}

	public static int getPrisonId(int castleId)
	{
		for(int[] id : dungeonCastleArray)
			if(id[0] == castleId)
				return id[1];
		return 0;
	}

	public static int getCastleId(int prisonId)
	{
		for(int[] id : dungeonCastleArray)
			if(id[1] == prisonId)
				return id[0];
		return 0;
	}


}