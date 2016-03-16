package instances;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.utils.Location;

/**
 * Класс контролирует Rim Pailaka - Rune
 *
 * @author pchayka
 */

public class RimPailaka extends Reflection
{
	private static final int SeducedKnight = 36562;
	private static final int SeducedRanger = 36563;
	private static final int SeducedMage = 36564;
	private static final int SeducedWarrior = 36565;
	private static final int KanadisGuide1 = 25659;
	private static final int KanadisGuide2 = 25660;
	private static final int KanadisGuide3 = 25661;
	private static final int KanadisFollower1 = 25662;
	private static final int KanadisFollower2 = 25663;
	private static final int KanadisFollower3 = 25664;
	private static final long initdelay = 30 * 1000L;
	private static final long firstwavedelay = 120 * 1000L;
	private static final long secondwavedelay = 480 * 1000L; // 8 минут после первой волны
	private static final long thirdwavedelay = 480 * 1000L; // 16 минут после первой волны

	private ScheduledFuture<?> initTask = null;
	private ScheduledFuture<?> firstwaveTask = null;
	private ScheduledFuture<?> secondWaveTask = null;
	private ScheduledFuture<?> thirdWaveTask = null;
	private DeathListener _deathListener = new DeathListener();

	private int _stage = 0;

	private boolean _issetReuse = false;

	private static final int[][] dungeonCastleArray = new int[][]{
			//castle
			{1,	80},
			{2, 81},
			{3, 82},
			{4, 83},
			{5, 84},
			{6, 85},
			{7, 86},
			{8, 87},
			{9,	88},
			//fortress
			{101,89},
			{102,90},
			{103,91},
			{104,92},
			{105,93},
			{106,94},
			{107,95},
			{108,96},
			{109,97},
			{110,98},
			{111,99},
			{112,100},
			{113,101},
			{114,102},
			{115,103},
			{116,104},
			{117,105},
			{118,106},
			{119,107},
			{120,108},
			{121,109}
	};

	@Override
	public void onCreate()
	{
		super.onCreate();

		ThreadPoolManager.getInstance().schedule(new CollapseTimer(10), (getInstancedZone().getTimelimit() - 10) * 60 * 1000L);
		initTask = ThreadPoolManager.getInstance().schedule(new InvestigatorsSpawn(), initdelay);
		firstwaveTask = ThreadPoolManager.getInstance().schedule(new FirstWave(), firstwavedelay);
		Residence _residence = ResidenceHolder.getInstance().getResidence(getResidenceId(getInstancedZoneId()));
		if(_residence != null)
			_residence.setPrisonReuseTime(System.currentTimeMillis() + 14400 * 1000L);
	}

	public class InvestigatorsSpawn extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			Location ranger = new Location(49192, -12232, -9384, 0);
			Location mage = new Location(49192, -12456, -9392, 0);
			Location warrior = new Location(49192, -11992, -9392, 0);
			Location knight = new Location(49384, -12232, -9384, 0);
			addSpawnWithoutRespawn(SeducedKnight, knight, 0);
			addSpawnWithoutRespawn(SeducedRanger, ranger, 0);
			addSpawnWithoutRespawn(SeducedMage, mage, 0);
			addSpawnWithoutRespawn(SeducedWarrior, warrior, 0);
		}
	}

	public class FirstWave extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_stage = 1;
			List<Player> who = getPlayers();
			if(who != null && !who.isEmpty())
				for(Player player : who)
					player.sendPacket(new ExShowScreenMessage(NpcString.BEGIN_STAGE_1, 3000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));

			Location bossnminions = new Location(50536, -12232, -9384, 32768);
			NpcInstance boss = addSpawnWithoutRespawn(KanadisGuide1, bossnminions, 0);
			boss.addListener(_deathListener);
			for(int i = 0; i < 10; i++)
				addSpawnWithoutRespawn(KanadisFollower1, bossnminions, 400);
			secondWaveTask = ThreadPoolManager.getInstance().schedule(new SecondWave(), secondwavedelay);
		}
	}

	public class SecondWave extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_stage = 2;
			List<Player> who = getPlayers();
			if(who != null && !who.isEmpty())
				for(Player player : who)
					player.sendPacket(new ExShowScreenMessage(NpcString.BEGIN_STAGE_2, 3000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));

			Location bossnminions = new Location(50536, -12232, -9384, 32768);
			addSpawnWithoutRespawn(KanadisGuide2, bossnminions, 0);
			for(int i = 0; i < 10; i++)
				addSpawnWithoutRespawn(KanadisFollower2, bossnminions, 400);
			thirdWaveTask = ThreadPoolManager.getInstance().schedule(new ThirdWave(), thirdwavedelay);
		}
	}

	public class ThirdWave extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_stage = 3;
			List<Player> who = getPlayers();
			if(who != null && !who.isEmpty())
				for(Player player : who)
					player.sendPacket(new ExShowScreenMessage(NpcString.BEGIN_STAGE_3, 3000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));

			Location bossnminions = new Location(50536, -12232, -9384, 32768);
			addSpawnWithoutRespawn(KanadisGuide3, bossnminions, 100);
			addSpawnWithoutRespawn(KanadisGuide3, bossnminions, 100);
			for(int i = 0; i < 10; i++)
				addSpawnWithoutRespawn(KanadisFollower3, bossnminions, 400);
		}
	}

	public class CollapseTimer extends RunnableImpl
	{
		private int _minutes = 0;

		public CollapseTimer(int minutes)
		{
			_minutes = minutes;
		}

		@Override
		public void runImpl() throws Exception
		{
			_stage = 4;
			List<Player> who = getPlayers();
			if(who != null && !who.isEmpty())
				for(Player player : who)
					player.sendPacket(new SystemMessage(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(_minutes));
		}
	}

	@Override
	public void onCollapse()
	{
		if(initTask != null)
			initTask.cancel(true);
		if(firstwaveTask != null)
			firstwaveTask.cancel(true);
		if(secondWaveTask != null)
			secondWaveTask.cancel(true);
		if(thirdWaveTask != null)
			thirdWaveTask.cancel(true);

		super.onCollapse();
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
		}
	}

	public static int getRimPailakaId(int castleId)
	{
		for(int[] id : dungeonCastleArray)
			if(id[0] == castleId)
				return id[1];
		return 0;
	}

	public static int getResidenceId(int prisonId)
	{
		for(int[] id : dungeonCastleArray)
			if(id[1] == prisonId)
				return id[0];
		return 0;
	}

	public int getStage()
	{
		return _stage;
	}
}