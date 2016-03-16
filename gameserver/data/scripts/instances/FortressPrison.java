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
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.Location;

/**
 * Управление фортовой тюрьмой
 *
 * @author pchayka
 */

public class FortressPrison extends Reflection
{
	private Future<?> bossSpawnTask = null;
	private DeathListener _deathListener = new DeathListener();

	private static final int HagerTheOutlaw = 25572;
	private static final int AllSeeingRango = 25575;
	private static final int Jakard = 25578;

	private static final int Helsing = 25579;
	private static final int Gillien = 25582;
	private static final int Medici = 25585;
	private static final int ImmortalMuus = 25588;

	private static final int BrandTheExile = 25589;
	private static final int CommanderKoenig = 25592;
	private static final int GergTheHunter = 25593;

	private static final int[] type1 = new int[] { HagerTheOutlaw, AllSeeingRango, Jakard };
	private static final int[] type2 = new int[] { Helsing, Gillien, Medici, ImmortalMuus };
	private static final int[] type3 = new int[] { BrandTheExile, CommanderKoenig, GergTheHunter };

	private static final int[][] dungeonFortressArray = new int[][]{
			{101,22},
			{102,23},
			{103,24},
			{104,25},
			{105,26},
			{106,27},
			{107,28},
			{108,29},
			{109,30},
			{110,31},
			{111,32},
			{112,33},
			{113,34},
			{114,35},
			{115,36},
			{116,37},
			{117,38},
			{118,39},
			{119,40},
			{120,41},
			{121,42}
	};

	private static final long _spawnInterval = 180 * 1000L;

	private boolean _issetReuse = false;

	@Override
	protected void onCreate()
	{
		super.onCreate();
		bossSpawnTask = ThreadPoolManager.getInstance().schedule(new SpawnTask(type1[Rnd.get(type1.length)]), _spawnInterval);
		Fortress _fortress = ResidenceHolder.getInstance().getResidence(getFortressId(getInstancedZoneId()));
		if(_fortress != null)
			_fortress.setPrisonReuseTime(System.currentTimeMillis() + 14400 * 1000L);
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

	public static int getPrisonId(int fortressId)
	{
		for(int[] id : dungeonFortressArray)
			if(id[0] == fortressId)
				return id[1];
		return 0;
	}

	public static int getFortressId(int prisonId)
	{
		for(int[] id : dungeonFortressArray)
			if(id[1] == prisonId)
				return id[0];
		return 0;
	}


}