package ai;

import java.util.List;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * Author: VISTALL
 * Date:  9:03/17.11.2010
 * npc Id : 18601
 */
public class KrateisCubeWatcherRed extends DefaultAI
{
	private static final int[][] SKILLS = { { 1064, 14 }, { 1160, 15 }, { 1164, 19 }, { 1167, 6 }, { 1168, 7 } };
	private static final int SKILL_CHANCE = 25;

	public KrateisCubeWatcherRed(NpcInstance actor)
	{
		super(actor);
		AI_TASK_ACTIVE_DELAY = 3000;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{}

	@Override
	protected void onEvtThink()
	{
		NpcInstance actor = getActor();
		List<Creature> around = World.getAroundCharacters(actor, 600, 300);
		if(around.isEmpty())
			return;

		for(Creature cha : around)
			if(cha.isPlayer() && !cha.isDead() && Rnd.chance(SKILL_CHANCE))
			{
				int rnd = Rnd.get(SKILLS.length);
				SkillEntry skill = SkillTable.getInstance().getSkillEntry(SKILLS[rnd][0], SKILLS[rnd][1]);
				if(skill != null)
					skill.getEffects(cha, cha, false, false);
			}
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		final NpcInstance actor = getActor();
		super.onEvtDead(killer);

		actor.deleteMe();
		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				NpcTemplate template = NpcHolder.getInstance().getTemplate(18602);
				if(template != null)
				{
					NpcInstance a = template.getNewInstance();
					a.setCurrentHpMp(a.getMaxHp(), a.getMaxMp());
					a.spawnMe(actor.getLoc());
				}
			}
		}, 10000L);
	}
}
