package ai.seedofdestruction;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.instancemanager.SoDManager;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.base.SpecialEffectState;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.network.l2.s2c.ExStartScenePlayer;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Location;

/**
  * @author pchayka
  */
public class Tiat extends Fighter
{
	private static final int TIAT_TRANSFORMATION_SKILL_ID = 5974;
	private static final SkillEntry TIAT_TRANSFORMATION_SKILL = SkillTable.getInstance().getSkillEntry(TIAT_TRANSFORMATION_SKILL_ID, 1);
	private boolean _notUsedTransform = true;
	private static final int TRAPS_COUNT = 4;
	private static final Location[] TRAP_LOCS = {
			new Location(-252022, 210130, -11995, 16384),
			new Location(-248782, 210130, -11995, 16384),
			new Location(-248782, 206875, -11995, 16384),
			new Location(-252022, 206875, -11995, 16384) };
	private static final long COLLAPSE_BY_INACTIVITY_INTERVAL = 10 * 60 * 1000; // 10 мин
	private long _lastAttackTime = 0;
	private static final int TRAP_NPC_ID = 18696;
	private static final int[] TIAT_MINION_IDS = { 29162, 22538, 22540, 22547, 22542, 22548 };
	private static final String[] TIAT_TEXT = { "You'll regret challenging me!", "You shall die in pain!", "I will wipe out your entire kind!" };
	private long _lastFactionNotifyTime = 0;
	private boolean _immobilized;
	private boolean _failed = false;

	public Tiat(NpcInstance actor)
	{
		super(actor);
		_immobilized = true;
		actor.startImmobilized();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return;

		_lastAttackTime = System.currentTimeMillis();

		if(_notUsedTransform && actor.getCurrentHpPercents() < 50)
		{
			if(_immobilized)
			{
				_immobilized = false;
				actor.stopImmobilized();
			}
			_notUsedTransform = false;
			clearTasks();
			spawnTraps();
			actor.abortAttack(true, false);
			actor.abortCast(true, false);
			// Transform skill cast [custom: making Tiat invul while casting]
			actor.setInvul(SpecialEffectState.TRUE);
			actor.doCast(TIAT_TRANSFORMATION_SKILL, actor, true);
			ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl() throws Exception
				{
					getActor().setCurrentHpMp(getActor().getMaxHp(), getActor().getMaxMp());
					getActor().setInvul(SpecialEffectState.FALSE);
				}
			}, TIAT_TRANSFORMATION_SKILL.getTemplate().getHitTime());
		}
		if(System.currentTimeMillis() - _lastFactionNotifyTime > _minFactionNotifyInterval)
		{
			_lastFactionNotifyTime = System.currentTimeMillis();
			for(NpcInstance npc : World.getAroundNpc(actor))
				if(ArrayUtils.contains(TIAT_MINION_IDS, npc.getNpcId()))
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 30000);
			if(Rnd.chance(15) && !_notUsedTransform)
				actor.broadcastPacket(new ExShowScreenMessage(TIAT_TEXT[Rnd.get(TIAT_TEXT.length)], 4000, ScreenMessageAlign.MIDDLE_CENTER, false));
		}
		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return true;

		// Коллапсируем инстанс, если Тиата не били более 10 мин
		if(!_failed && _lastAttackTime != 0 && _lastAttackTime + COLLAPSE_BY_INACTIVITY_INTERVAL < System.currentTimeMillis())
		{
			final Reflection r = actor.getReflection();
			_failed = true;

			// Показываем финальный ролик при фейле серез секунду после очистки инстанса
			ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl() throws Exception
				{
					for(Player pl : r.getPlayers())
						pl.showQuestMovie(ExStartScenePlayer.SCENE_TIAT_FAIL);
					r.clearReflection(5, true);
				}
			}, 1000);
			return true;
		}
		return super.thinkActive();
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_notUsedTransform = true;
		_lastAttackTime = 0;
		_lastFactionNotifyTime = 0;

		NpcInstance actor = getActor();
		SoDManager.addTiatKill();
		final Reflection r = actor.getReflection();
		r.setReenterTime(System.currentTimeMillis());
		for(NpcInstance n : r.getNpcs())
			n.deleteMe();
		// Показываем финальный ролик серез секунду после очистки инстанса
		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				for(Player pl : r.getPlayers())
					if(pl != null)
						pl.showQuestMovie(ExStartScenePlayer.SCENE_TIAT_SUCCESS);
			}
		}, 1000);
	}

	private void spawnTraps()
	{
		NpcInstance actor = getActor();
		actor.broadcastPacket(new ExShowScreenMessage("Come out, warriors. Protect Seed of Destruction.", 5000, ScreenMessageAlign.MIDDLE_CENTER, false));
		for(int i = 0; i < TRAPS_COUNT; i++)
			actor.getReflection().addSpawnWithRespawn(TRAP_NPC_ID, TRAP_LOCS[i], 0, 180);
	}
}