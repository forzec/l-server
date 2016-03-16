package instances;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.mmocore.commons.geometry.Polygon;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.listener.actor.OnCurrentHpDamageListener;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.model.Zone;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.EventTrigger;
import org.mmocore.gameserver.network.l2.s2c.ExChangeClientEffectInfo;
import org.mmocore.gameserver.network.l2.s2c.ExSendUIEvent;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.network.l2.s2c.ExStartScenePlayer;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;

/**
 * @author pchayka
 *         <p/>
 *         Инстанс Фреи в режиме общей сложности.
 */

public class FreyaNormal extends Reflection
{
	private static final int FreyaThrone = 29177;
	private static final int FreyaStandNormal = 29179;
	private static final int IceKnightNormal = 18855; //state 1 - in ice, state 2 - ice shattering, then normal state
	private static final int IceKnightLeaderNormal = 25699;
	private static final int IceCastleBreath = 18854;
	private static final int Glacier = 18853; // state 1 - falling, state 2 - waiting
	private static final int IceCastleController = 18932; // state 1-7
	private static final int Sirra = 32762;
	private static final int Jinia = 18850;
	private static final int Kegor = 18851;

	private static final int[] _eventTriggers = {
			23140202,
			23140204,
			23140206,
			23140208,
			23140212,
			23140214,
			23140216
	};

	private Zone damagezone = null, attackUp = null, pcbuff = null, pcbuff2 = null;

	private Future<?> stageTask = null;
	private Future<?> firstStageGuardSpawn = null;
	private Future<?> secondStageGuardSpawn = null;
	private Future<?> thirdStageGuardSpawn = null;

	private ZoneListener _epicZoneListener = new ZoneListener();
	private ZoneListenerL _landingZoneListener = new ZoneListenerL();
	private DeathListener _deathListener = new DeathListener();
	private CurrentHpListener _currentHpListener = new CurrentHpListener();

	private boolean _entryLocked = false;
	private boolean _startLaunched = false;
	private boolean _freyaSlayed = false;
	private boolean _thirdStageActive = false;

	private int _damageLevel = 0;
	private int _contollerLevel = 0;

	private AtomicInteger raidplayers = new AtomicInteger();

	private static Territory centralRoom = new Territory().add(new Polygon().add(114264, -113672).add(113640, -114344).add(113640, -115240).add(114264, -115912).add(115176, -115912).add(115800, -115272).add(115800, -114328).add(115192, -113672).setZmax(-11225).setZmin(-11225));

	@Override
	protected void onCreate()
	{
		super.onCreate();

		attackUp = getZone("[freya_attack_up]");
		pcbuff = getZone("[freya_pc_buff1]");
		pcbuff2 = getZone("[freya_pc_buff2]");
		getZone("[freya_normal_epic]").addListener(_epicZoneListener);
		getZone("[freya_landing_room_epic]").addListener(_landingZoneListener);
	}

	private void manageDamageZone(int level)
	{
		if(damagezone != null)
			damagezone.setActive(false);

		switch(level)
		{
			case 0:
				return;
			case 1:
				damagezone = getZone("[freya_normal_freezing_01]");
				break;
			case 2:
				damagezone = getZone("[freya_normal_freezing_02]");
				break;
			case 3:
				damagezone = getZone("[freya_normal_freezing_03]");
				break;
			case 4:
				damagezone = getZone("[freya_normal_freezing_04]");
				break;
			case 5:
				damagezone = getZone("[freya_normal_freezing_05]");
				break;
			case 6:
				damagezone = getZone("[freya_normal_freezing_06]");
				break;
			case 7:
				damagezone = getZone("[freya_normal_freezing_07]");
				break;
			default:
				break;
		}
		if(damagezone != null)
			damagezone.setActive(true);
	}

	private void manageAttackUpZone(boolean disable)
	{
		if(attackUp != null && disable)
		{
			attackUp.setActive(false);
			return;
		}
		if(attackUp != null)
			attackUp.setActive(true);
	}

	private void managePcBuffZone(boolean disable)
	{
		if(pcbuff != null && pcbuff2 != null && disable)
		{
			pcbuff.setActive(false);
			pcbuff2.setActive(false);
			return;
		}
		if(pcbuff != null)
			pcbuff.setActive(true);
		if(pcbuff2 != null)
			pcbuff2.setActive(true);
	}

	private void manageCastleController(int state)
	{
		// 1-7 enabled, 8 - disabled
		for(NpcInstance n : getNpcs())
			if(n.getNpcId() == IceCastleController)
				n.setNpcState(state);
	}

	private void manageStorm(boolean active)
	{
		for(Player p : getPlayers())
			for(int _eventTrigger : _eventTriggers)
				p.sendPacket(new EventTrigger(_eventTrigger, active));
	}

	private class StartNormalFreya extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_entryLocked = true;
			closeDoor(23140101);
			for(Player player : getPlayers())
			{
				QuestState qs = player.getQuestState(10286);
				if(qs != null && qs.getCond() == 5)
					qs.setCond(6);
				player.showQuestMovie(ExStartScenePlayer.SCENE_BOSS_FREYA_OPENING);
			}
			stageTask = ThreadPoolManager.getInstance().schedule(new PreStage(), 55000L); // 53.5sec for movie
		}
	}

	private class PreStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_damageLevel = 1;
			manageDamageZone(_damageLevel);
			//screen message
			for(Player player : getPlayers())
				player.sendPacket(new ExShowScreenMessage(NpcString.BEGIN_STAGE_1_FREYA, 6000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
			//spawning few guards
			for(int i = 0; i < 6; i++)
				addSpawnWithoutRespawn(IceKnightNormal, Territory.getRandomLoc(centralRoom, getGeoIndex()), 0);
			stageTask = ThreadPoolManager.getInstance().schedule(new FirstStage(), 40000L);
		}
	}

	private class FirstStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_contollerLevel = 1;
			manageCastleController(_contollerLevel);
			_damageLevel = 2;
			manageDamageZone(_damageLevel);
			for(Player player : getPlayers())
				player.sendPacket(new ExShowScreenMessage(NpcString.FREYA_HAS_STARTED_TO_MOVE, 4000, ScreenMessageAlign.MIDDLE_CENTER, true));
			//Spawning Freya Throne
			NpcInstance freyaTrhone = addSpawnWithoutRespawn(FreyaThrone, new Location(114720, -117085, -11088, 15956), 0);
			freyaTrhone.addListener(_deathListener);
			firstStageGuardSpawn = ThreadPoolManager.getInstance().scheduleAtFixedRate(new GuardSpawnTask(1), 2000L, 30000L);
		}
	}

	private class GuardSpawnTask extends RunnableImpl
	{
		private int _mode, _knightCount = 0, _breathCount = 0;

		public GuardSpawnTask(int mode) // 1 - light, 2 - normal, 3 - hard, 4 - extreme
		{
			_mode = Math.max(Math.min(4, mode), 1);
		}

		@Override
		public void runImpl() throws Exception
		{
			if(FreyaNormal.this.isCollapseStarted())
				return;

			int count = 0;
			for(NpcInstance npc : getNpcs())
				if(!npc.isDead() && ++count > 40)
					return;

			switch(_mode)
			{
				case 1:
					_knightCount = 2;
					_breathCount = 1;
					break;
				case 2:
					_knightCount = 2;
					_breathCount = 1;
					break;
				case 3:
					_knightCount = 2;
					_breathCount = 2;
					break;
				case 4:
					_knightCount = 4;
					_breathCount = 3;
					break;
				default:
					break;
			}
			for(int i = 0; i < _knightCount; i++)
				addSpawnWithoutRespawn(IceKnightNormal, Territory.getRandomLoc(centralRoom, getGeoIndex()), 0);
			for(int i = 0; i < _breathCount; i++)
				addSpawnWithoutRespawn(IceCastleBreath, Territory.getRandomLoc(centralRoom, getGeoIndex()), 0);
			if(Rnd.chance(60))
				for(int i = 0; i < Rnd.get(1, 3); i++)
					addSpawnWithoutRespawn(Glacier, Territory.getRandomLoc(centralRoom, getGeoIndex()), 0);
		}
	}

	private class PreSecondStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			firstStageGuardSpawn.cancel(false);
			for(NpcInstance n : getNpcs())
				if(n.getNpcId() != Sirra && n.getNpcId() != IceCastleController)
					n.deleteMe();

			for(Player p : getPlayers())
				p.showQuestMovie(ExStartScenePlayer.SCENE_BOSS_FREYA_PHASE_A);
			stageTask = ThreadPoolManager.getInstance().schedule(new TimerToSecondStage(), 22000L); // 22.1 secs for movie
		}
	}

	private class TimerToSecondStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			manageDamageZone(0);
			manageCastleController(8);
			for(Player p : getPlayers())
				p.sendPacket(new ExSendUIEvent(p, false, false, 60, 0, NpcString.TIME_REMAINING_UNTIL_NEXT_BATTLE));
			stageTask = ThreadPoolManager.getInstance().schedule(new SecondStage(), 60000L);
		}
	}

	private class SecondStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_contollerLevel = 3;
			manageCastleController(_contollerLevel);
			_damageLevel = 3;
			manageDamageZone(_damageLevel);
			for(Player p : getPlayers())
				p.sendPacket(new ExShowScreenMessage(NpcString.BEGIN_STAGE_2_FREYA, 6000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
			secondStageGuardSpawn = ThreadPoolManager.getInstance().scheduleAtFixedRate(new GuardSpawnTask(2), 2000L, 30000L);
			stageTask = ThreadPoolManager.getInstance().schedule(new KnightCaptainSpawnMovie(), 60000L);
		}
	}

	private class KnightCaptainSpawnMovie extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for(NpcInstance n : getNpcs())
				n.block();
			for(Player p : getPlayers())
				p.showQuestMovie(ExStartScenePlayer.SCENE_ICE_HEAVYKNIGHT_SPAWN);
			stageTask = ThreadPoolManager.getInstance().schedule(new KnightCaptainSpawn(), 7500L);
		}
	}

	private class KnightCaptainSpawn extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_damageLevel = 4;
			manageDamageZone(_damageLevel);
			for(NpcInstance n : getNpcs())
				if(n.getNpcId() != Glacier)
					n.unblock();
			NpcInstance knightLeader = addSpawnWithoutRespawn(IceKnightLeaderNormal, new Location(114707, -114799, -11199, 15956), 0);
			knightLeader.addListener(_deathListener);
		}
	}

	private class PreThirdStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			secondStageGuardSpawn.cancel(true);
			manageDamageZone(0);
			manageCastleController(8);
			for(Player p : getPlayers())
				p.sendPacket(new ExSendUIEvent(p, false, false, 60, 0, NpcString.TIME_REMAINING_UNTIL_NEXT_BATTLE));
			for(NpcInstance n : getNpcs())
				if(n.getNpcId() != Sirra && n.getNpcId() != IceCastleController)
					n.deleteMe();
			stageTask = ThreadPoolManager.getInstance().schedule(new PreThirdStageM(), 60000L);
		}
	}

	private class PreThirdStageM extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for(Player p : getPlayers())
				p.showQuestMovie(ExStartScenePlayer.SCENE_BOSS_FREYA_PHASE_B);
			stageTask = ThreadPoolManager.getInstance().schedule(new ThirdStage(), 22000L); // 21.5 secs for movie
		}
	}

	private class ThirdStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_contollerLevel = 4;
			manageCastleController(_contollerLevel);
			manageAttackUpZone(false);
			_damageLevel = 5;
			manageDamageZone(_damageLevel);
			manageStorm(true);
			_thirdStageActive = true;
			for(Player p : getPlayers())
			{
				p.sendPacket(new ExShowScreenMessage(NpcString.BEGIN_STAGE_3_FREYA, 6000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
				p.sendPacket(new ExChangeClientEffectInfo(2));
			}
			thirdStageGuardSpawn = ThreadPoolManager.getInstance().scheduleAtFixedRate(new GuardSpawnTask(3), 2000L, 30000L);
			NpcInstance freyaStand = addSpawnWithoutRespawn(FreyaStandNormal, new Location(114720, -117085, -11088, 15956), 0);
			freyaStand.addListener(_currentHpListener);
			freyaStand.addListener(_deathListener);
		}
	}

	private class PreForthStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for(NpcInstance n : getNpcs())
				n.block();
			for(Player p : getPlayers())
			{
				p.block();
				p.showQuestMovie(ExStartScenePlayer.SCENE_BOSS_KEGOR_INTRUSION);
			}
			stageTask = ThreadPoolManager.getInstance().schedule(new ForthStage(), 28000L); // 27 secs for movie
		}
	}

	private class ForthStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for(NpcInstance n : getNpcs())
				if(n.getNpcId() != Glacier)
					n.unblock();
			for(Player p : getPlayers())
			{
				p.unblock();
				p.sendPacket(new ExShowScreenMessage(NpcString.BEGIN_STAGE_4_FREYA, 6000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
			}
			addSpawnWithoutRespawn(Jinia, new Location(114727, -114700, -11200, -16260), 0);
			addSpawnWithoutRespawn(Kegor, new Location(114690, -114700, -11200, -16260), 0);
			managePcBuffZone(false);
		}
	}

	private class FreyaDeathStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			setReenterTime(System.currentTimeMillis());
			//Guard spawn task cancellation
			stopTasks();
			//switching off zones
			manageDamageZone(0);
			manageAttackUpZone(true);
			managePcBuffZone(true);
			manageCastleController(8);
			//Deleting all NPCs + Freya corpse
			for(NpcInstance n : getNpcs())
				n.deleteMe();
			//Cancelling zone info
			_thirdStageActive = false;
			manageStorm(false);
			//Movie + quest update
			for(Player p : getPlayers())
			{
				QuestState qs = p.getQuestState(10286);
				if(qs != null && qs.getCond() == 6)
					qs.setCond(7);
				p.showQuestMovie(ExStartScenePlayer.SCENE_BOSS_FREYA_ENDING_A);
				p.sendPacket(new ExChangeClientEffectInfo(1));
			}

			// Spawning Kegor + defeated Freya
			NpcInstance kegor2 = addSpawnWithoutRespawn(32761, new Location(114872, -114744, -11200, 32768), 0);
			kegor2.setNpcState(2);
			NpcInstance defeatedFreya = addSpawnWithoutRespawn(FreyaStandNormal, new Location(114767, -114795, -11200), 0);
			defeatedFreya.setRHandId(15280);
			defeatedFreya.block();
			defeatedFreya.startDamageBlocked();
			defeatedFreya.setShowName(false);
			defeatedFreya.setTargetable(false, true);
		}
	}

	public void notifyElimination()
	{
		for(NpcInstance n : getNpcs())
			n.deleteMe();
		for(Player p : getPlayers())
			p.showQuestMovie(ExStartScenePlayer.SCENE_BOSS_FREYA_ENDING_B);
		stageTask = ThreadPoolManager.getInstance().schedule(new InstanceConclusion(), 57000L); // 56 secs for movie
	}

	private class InstanceConclusion extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			startCollapseTimer(600 * 1000L);
			for(Player p : getPlayers())
				p.sendPacket(new SystemMessage(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(10));
		}
	}

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(self.getNpcId() == FreyaThrone)
				ThreadPoolManager.getInstance().execute(new PreSecondStage());
			else if(self.getNpcId() == IceKnightLeaderNormal)
				ThreadPoolManager.getInstance().execute(new PreThirdStage());
			else if(self.getNpcId() == FreyaStandNormal)
				ThreadPoolManager.getInstance().execute(new FreyaDeathStage());
		}
	}

	public class CurrentHpListener implements OnCurrentHpDamageListener
	{
		@Override
		public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, SkillEntry skill, boolean crit)
		{
			if(actor.isDead() || actor.getNpcId() != FreyaStandNormal)
				return;
			double newHp = actor.getCurrentHp() - damage;
			double maxHp = actor.getMaxHp();
			if(!_freyaSlayed && newHp <= 0.2 * maxHp)
			{
				_freyaSlayed = true;
				actor.removeListener(_currentHpListener);
				if(newHp > 0)
					ThreadPoolManager.getInstance().execute(new PreForthStage());
			}
		}
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if(_entryLocked)
				return;

			if(!cha.isPlayer())
				return;

			Player player = cha.getPlayer();

			if(_thirdStageActive)
				player.sendPacket(new ExChangeClientEffectInfo(2));

			if(!_startLaunched && raidplayers.incrementAndGet() == getInstancedZone().getMinParty())
			{
				_startLaunched = true;
				stageTask = ThreadPoolManager.getInstance().schedule(new StartNormalFreya(), 30000L);
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
			if(!cha.isPlayer())
				return;
			raidplayers.decrementAndGet();
		}
	}

	public class ZoneListenerL implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if(cha.isPlayer())
				cha.sendPacket(new ExChangeClientEffectInfo(1));
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
		}
	}

	private void stopTasks()
	{
		if(stageTask != null)
			stageTask.cancel(false);
		if(firstStageGuardSpawn != null)
			firstStageGuardSpawn.cancel(false);
		if(secondStageGuardSpawn != null)
			secondStageGuardSpawn.cancel(false);
		if(thirdStageGuardSpawn != null)
			thirdStageGuardSpawn.cancel(false);
	}

	@Override
	protected void onCollapse()
	{
		stopTasks();
		super.onCollapse();
	}
}