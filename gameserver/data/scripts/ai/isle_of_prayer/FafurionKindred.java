package ai.isle_of_prayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.SimpleSpawner;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.stats.funcs.FuncTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;

/**
 * @author Diamond
 * @corrected n0nam3
 */
public class FafurionKindred extends Fighter
{
	private static final int DETRACTOR1 = 22270;
	private static final int DETRACTOR2 = 22271;

	private static final int Spirit_of_the_Lake = 2368;

	private static final int Water_Dragon_Scale = 9691;
	private static final int Water_Dragon_Claw = 9700;

	ScheduledFuture<?> poisonTask;
	ScheduledFuture<?> despawnTask;

	List<SimpleSpawner> spawns = new ArrayList<SimpleSpawner>();

	private static final FuncTemplate ft = new FuncTemplate(null, "Mul", Stats.HEAL_EFFECTIVNESS, 0x90, 0);

	public FafurionKindred(NpcInstance actor)
	{
		super(actor);
		actor.addStatFunc(ft.getFunc(this));
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		spawns.clear();

		ThreadPoolManager.getInstance().schedule(new SpawnTask(DETRACTOR1), 500);
		ThreadPoolManager.getInstance().schedule(new SpawnTask(DETRACTOR2), 500);
		ThreadPoolManager.getInstance().schedule(new SpawnTask(DETRACTOR1), 500);
		ThreadPoolManager.getInstance().schedule(new SpawnTask(DETRACTOR2), 500);

		poisonTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new PoisonTask(), 3000, 3000);
		despawnTask = ThreadPoolManager.getInstance().schedule(new DeSpawnTask(), 300000);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		cleanUp();

		super.onEvtDead(killer);
	}

	@Override
	protected void onEvtSeeSpell(SkillEntry skill, Creature caster)
	{
		super.onEvtSeeSpell(skill, caster);

		NpcInstance actor = getActor();
		if(actor.isDead() || skill == null)
			return;
		// Лечим
		if(skill.getId() == Spirit_of_the_Lake)
			actor.setCurrentHp(actor.getCurrentHp() + 3000, false);
		actor.getAggroList().remove(caster, true);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	private void cleanUp()
	{
		if(poisonTask != null)
		{
			poisonTask.cancel(false);
			poisonTask = null;
		}
		if(despawnTask != null)
		{
			despawnTask.cancel(false);
			despawnTask = null;
		}

		for(SimpleSpawner spawn : spawns)
			spawn.deleteAll();
		spawns.clear();
	}

	private class SpawnTask extends RunnableImpl
	{
		private final int _id;

		public SpawnTask(int id)
		{
			_id = id;
		}

		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(_id));
			sp.setLoc(Location.findPointToStay(actor, 100, 120));
			sp.setRespawnDelay(30, 40);
			sp.doSpawn(true);
			spawns.add(sp);
		}
	}

	private class PoisonTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			actor.reduceCurrentHp(500, actor, null, 0, false, true, false, true, false, false, false, false); // Травим дракошу ядом
		}
	}

	private class DeSpawnTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();

			// Если продержались 5 минут, то выдаем награду, и деспавним
			dropItem(actor, Water_Dragon_Scale, Rnd.get(1, 2));
			if(Rnd.chance(36))
				dropItem(actor, Water_Dragon_Claw, Rnd.get(1, 3));

			cleanUp();
			actor.deleteMe();
		}
	}

	private void dropItem(NpcInstance actor, int id, int count)
	{
		ItemInstance item = ItemFunctions.createItem(id);
		item.setCount(count);
		item.dropToTheGround(actor, Location.findPointToStay(actor, 100));
	}
}