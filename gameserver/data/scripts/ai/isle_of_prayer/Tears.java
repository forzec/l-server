package ai.isle_of_prayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Party;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillUse;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;
import org.napile.primitive.maps.IntObjectMap;
import instances.CrystalCaverns;

/**
 * @author Diamond
 */
public class Tears extends DefaultAI
{
	private class DeSpawnTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			for(NpcInstance npc : spawns)
				if(npc != null)
					npc.deleteMe();
			spawns.clear();
			despawnTask = null;
		}
	}

	private class SpawnMobsTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			spawnMobs();
			spawnTask = null;
		}
	}

	final SkillEntry Invincible;
	final SkillEntry Freezing;

	private static final int Water_Dragon_Scale = 2369;
	private static final int Tears_Copy = 25535;

	ScheduledFuture<?> spawnTask = null;
	ScheduledFuture<?> despawnTask = null;

	List<NpcInstance> spawns = new ArrayList<NpcInstance>();

	private boolean _isUsedInvincible = false;
	private boolean _attacked = false;

	private int _scale_count = 0;
	private long _last_scale_time = 0;

	public Tears(NpcInstance actor)
	{
		super(actor);

		IntObjectMap<SkillEntry> skills = getActor().getTemplate().getSkills();

		Invincible = skills.get(5420);
		Freezing = skills.get(5238);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();
		if(!_attacked && actor.getReflection().getInstancedZoneId() == 10)
		{
			_attacked = true;
			((CrystalCaverns) actor.getReflection()).notifyTearsAttacked();
		}
		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected void onEvtSeeSpell(SkillEntry skill, Creature caster)
	{
		super.onEvtSeeSpell(skill, caster);

		NpcInstance actor = getActor();
		if(actor.isDead() || skill == null || caster == null)
			return;

		if(System.currentTimeMillis() - _last_scale_time > 5000)
			_scale_count = 0;

		if(skill.getId() == Water_Dragon_Scale)
		{
			_scale_count++;
			_last_scale_time = System.currentTimeMillis();
		}

		Player player = caster.getPlayer();
		if(player == null)
			return;

		int count = 1;
		Party party = player.getParty();
		if(party != null)
			count = party.getMemberCount();

		// Снимаем неуязвимость
		if(_scale_count >= count)
		{
			_scale_count = 0;
			actor.getEffectList().stopEffect(Invincible);
		}
	}

	@Override
	protected boolean createNewTask()
	{
		clearTasks();
		Creature target;
		if((target = prepareTarget()) == null)
			return false;

		NpcInstance actor = getActor();
		if(actor.isDead())
			return false;

		double distance = actor.getDistance(target);
		double actor_hp_precent = actor.getCurrentHpPercents();
		int rnd_per = Rnd.get(100);

		if(actor_hp_precent < 15 && !_isUsedInvincible)
		{
			_isUsedInvincible = true;
			addTaskBuff(actor, Invincible);
			Functions.npcSay(actor, NpcString.NO__YOU_KNEW_MY_WEAKNESS);
			return true;
		}

		if(rnd_per < 5 && spawnTask == null && despawnTask == null)
		{
			actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, 5441, 1, 3000, 0));
			spawnTask = ThreadPoolManager.getInstance().schedule(new SpawnMobsTask(), 3000);
			return true;
		}

		if(!actor.isAMuted() && rnd_per < 75)
			return chooseTaskAndTargets(null, target, distance);

		return chooseTaskAndTargets(Freezing, target, distance);
	}

	private void spawnMobs()
	{
		NpcInstance actor = getActor();

		Location pos;
		Creature hated;

		// Спавним 9 копий
		for(int i = 0; i < 9; i++)
		{
			pos = Location.findPointToStay(144298, 154420, -11854, 300, 320, actor.getGeoIndex());
			NpcInstance copy = actor.getReflection().addSpawnWithoutRespawn(Tears_Copy, pos, 0);
			spawns.add(copy);
			hated = actor.getAggroList().getRandomHated();
			if(hated != null)
				copy.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, hated, Rnd.get(1, 100));
		}

		// Прячемся среди них
		pos = Location.findPointToStay(144298, 154420, -11854, 300, 320, actor.getReflectionId());
		actor.teleToLocation(pos);
		actor.getAggroList().clear(true);

		// Атакуем случайную цель
		hated = actor.getAggroList().getRandomHated();
		if(hated != null)
			actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, hated, Rnd.get(1, 100));

		if(despawnTask != null)
			despawnTask.cancel(false);
		despawnTask = ThreadPoolManager.getInstance().schedule(new DeSpawnTask(), 45000);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if(getActor().getReflection().getInstancedZoneId() == 10)
			((CrystalCaverns) getActor().getReflection()).notifyTearsDead(getActor());
		super.onEvtDead(killer);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}