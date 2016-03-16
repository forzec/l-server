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
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * Author: VISTALL
 * Date:  9:03/17.11.2010
 * npc Id : 18602
 */
public class KrateisCubeWatcherBlue extends DefaultAI
{
	private static final int RESTORE_CHANCE = 60;

	public KrateisCubeWatcherBlue(NpcInstance actor)
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
			if(cha.isPlayer() && !cha.isDead() && Rnd.chance(RESTORE_CHANCE))
			{
				double valCP = cha.getMaxCp() - cha.getCurrentCp();
				if(valCP > 0)
				{
					cha.setCurrentCp(valCP + cha.getCurrentCp());
					cha.sendPacket(new SystemMessage(SystemMsg.S1_CP_HAS_BEEN_RESTORED).addNumber(Math.round(valCP)));
				}

				double valHP = cha.getMaxHp() - cha.getCurrentHp();
				if(valHP > 0)
				{
					cha.setCurrentHp(valHP + cha.getCurrentHp(), false);
					cha.sendPacket(new SystemMessage(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addNumber(Math.round(valHP)));
				}

				double valMP = cha.getMaxMp() - cha.getCurrentMp();
				if(valMP > 0)
				{
					cha.setCurrentMp(valMP + cha.getCurrentMp());
					cha.sendPacket(new SystemMessage(SystemMsg.S1_MP_HAS_BEEN_RESTORED).addNumber(Math.round(valMP)));
				}
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
				NpcTemplate template = NpcHolder.getInstance().getTemplate(18601);
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
