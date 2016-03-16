package ai.isle_of_prayer;

import java.util.HashMap;
import java.util.Map;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.idfactory.IdFactory;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.TrapInstance;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillUse;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Location;
import org.napile.primitive.maps.IntObjectMap;
import instances.CrystalCaverns;

/**
 * @author Diamond
 */
public class Darnel extends DefaultAI
{
	private class TrapTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			if(actor.isDead())
				return;

			// Спавним 10 ловушек
			TrapInstance trap;
			for(int i = 0; i < 10; i++)
			{
				trap = new TrapInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(13037), actor, trapSkills[Rnd.get(trapSkills.length)], new Location(Rnd.get(151896, 153608), Rnd.get(145032, 146808), -12584));
				trap.spawnMe();
			}
		}
	}

	final SkillEntry[] trapSkills = new SkillEntry[] {
			SkillTable.getInstance().getSkillEntry(5267, 1),
			SkillTable.getInstance().getSkillEntry(5268, 1),
			SkillTable.getInstance().getSkillEntry(5269, 1),
			SkillTable.getInstance().getSkillEntry(5270, 1) };

	final SkillEntry Poison;
	final SkillEntry Paralysis;

	public Darnel(NpcInstance actor)
	{
		super(actor);

		IntObjectMap<SkillEntry> skills = getActor().getTemplate().getSkills();

		Poison = skills.get(4182);
		Paralysis = skills.get(4189);
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

		int rnd_per = Rnd.get(100);

		if(rnd_per < 5)
		{
			actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, 5440, 1, 3000, 0));
			ThreadPoolManager.getInstance().schedule(new TrapTask(), 3000);
			return true;
		}

		double distance = actor.getDistance(target);

		if(!actor.isAMuted() && rnd_per < 75)
			return chooseTaskAndTargets(null, target, distance);

		Map<SkillEntry, Integer> d_skill = new HashMap<SkillEntry, Integer>();

		addDesiredSkill(d_skill, target, distance, Poison);
		addDesiredSkill(d_skill, target, distance, Paralysis);

		SkillEntry r_skill = selectTopSkill(d_skill);

		return chooseTaskAndTargets(r_skill, target, distance);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();
		if(actor.getReflection().getInstancedZoneId() == 10)
			((CrystalCaverns) actor.getReflection()).notifyDarnelAttacked();
		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if(getActor().getReflection().getInstancedZoneId() == 10)
			((CrystalCaverns) getActor().getReflection()).notifyDarnelDead(getActor());
		super.onEvtDead(killer);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}