package ai.beastfarm;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.collections.CollectionUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.GameObjectTasks.NotifyAITask;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import npc.model.beastfarm.FeedableBeastInstance;
import npc.model.beastfarm.TamedBeastInstance;

/**
 * @author VISTALL
 * @date 15:34/30.10.2011
 */
public class BeastAI extends Fighter
{
	public static enum FeedType
	{
		NORMAL(9049, 9050),
		BLESS(9051, 9052),
		S_GRADE(9053, 9054);

		private static final FeedType[] VALUES = values();

		private int[] _skills;

		private FeedType(int... a)
		{
			_skills = a;
		}

		public static FeedType valueOf(SkillEntry e)
		{
			for(FeedType f : VALUES)
				if(ArrayUtils.contains(f._skills, e.getId()))
					return f;

			return null;
		}
	}

	public BeastAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSeeSpell(final SkillEntry skill, Creature caster)
	{
		FeedType type = FeedType.valueOf(skill);

		if(type == null)
		{
			super.onEvtSeeSpell(skill, caster);
			return;
		}

		final Player player = (Player) caster;
		FeedableBeastInstance actor = getActor();

		switch(type)
		{
			case BLESS:
				if(actor.getStep() != 1)
				{
					dropItem(player, skill);
					return;
				}

				if(!Rnd.chance(90))
				{
					player.sendPacket(new SystemMessage(SystemMsg.S1).addNpcString(NpcString.THE_BEAST_SPIT_OUT_THE_FEED_AND_IS_INSTEAD_ATTACKING_YOU));
					super.onEvtSeeSpell(skill, player);
					return;
				}

				int tamedNpcId = actor.getTamedNpcId();
				if(tamedNpcId == 0)
				{
					dropItem(player, skill);
					return;
				}

				spawnTimedBeast(player, tamedNpcId, 15474 + ArrayUtils.indexOf(type._skills, skill.getId()));  // 15474 / 15475
				break;
			case S_GRADE:
				if(actor.getStep() != 1)
				{
					dropItem(player, skill);
					return;
				}

				if(!Rnd.chance(90))
				{
					player.sendPacket(new SystemMessage(SystemMsg.S1).addNpcString(NpcString.THE_BEAST_SPIT_OUT_THE_FEED_AND_IS_INSTEAD_ATTACKING_YOU));
					super.onEvtSeeSpell(skill, player);
					return;
				}

				int lastGrowNpcId = actor.getLastGrowNpcIds()[ArrayUtils.indexOf(type._skills, skill.getId())];
				if(lastGrowNpcId == 0)
				{
					dropItem(player, skill);
					return;
				}

				spawnNext(player, lastGrowNpcId);
				break;
			case NORMAL:
				if(actor.getFeederObjectId() > 0 && actor.getFeederObjectId() != player.getObjectId() || actor.getStep() == 4)
				{
					dropItem(player, skill);
					return;
				}

				if(Rnd.chance(5))
				{
					int tamedNpcId0 = actor.getTamedNpcId();
					if(tamedNpcId0 == 0)
					{
						dropItem(player, skill);
						return;
					}

					spawnTimedBeast(player, tamedNpcId0, 15474 + ArrayUtils.indexOf(type._skills, skill.getId()));  // 15474 / 15475
				}
				else
				{
					int growNpcId = actor.getGrowNpcIds()[ArrayUtils.indexOf(type._skills, skill.getId())];
					if(growNpcId == 0)
					{
						dropItem(player, skill);
						return;
					}

					spawnNext(player, growNpcId);
				}

				break;
		}
	}

	private void spawnTimedBeast(final Player player, int npcId, int itemId)
	{
		FeedableBeastInstance actor = getActor();

		actor.decayOrDelete();

		NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);

		NpcInstance prevNpc = CollectionUtils.safeGet(player.getTamedBeasts(), 0);

		TamedBeastInstance tamedBeast = (TamedBeastInstance)template.getNewInstance();
		tamedBeast.setCurrentHpMp(tamedBeast.getMaxHp(), tamedBeast.getMaxMp());
		tamedBeast.setConsumeItemId(itemId);
		tamedBeast.setOwner(player);

		if(prevNpc != null)
			tamedBeast.spawnMe(Location.coordsRandomize(prevNpc.getLoc(), 50, 50));
		else
			tamedBeast.spawnMe(Location.coordsRandomize(player.getLoc(), 100, 200));

		tamedBeast.setRunning();

		tamedBeast.setFollowTarget(player);

		tamedBeast.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player, Config.FOLLOW_RANGE);

		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				QuestState st = player.getQuestState(20);
				if(st != null && !st.isCompleted() && Rnd.chance(5) && st.getQuestItemsCount(7185) == 0)
				{
					st.giveItems(7185, 1);
					st.setCond(2);
				}

				st = player.getQuestState(655);
				if(st != null && !st.isCompleted() && st.getCond() == 1)
					if (st.getQuestItemsCount(8084) < 10)
						st.giveItems(8084, 1);
			}
		}, 5000L);
	}

	private void spawnNext(Player player, int npcId)
	{
		FeedableBeastInstance actor = getActor();
		actor.decayOrDelete();

		FeedableBeastInstance f = (FeedableBeastInstance) NpcUtils.spawnSingle(npcId, actor.getLoc(), actor.getReflection());
		f.setFeederObjectId(player.getObjectId());
		f.onAction(player, false);

		ThreadPoolManager.getInstance().schedule(new NotifyAITask(f, CtrlEvent.EVT_AGGRESSION, player, 5000), 5000L);

		player.sendPacket(new SystemMessage(SystemMsg.S1).addNpcString(NpcString.THE_BEAST_SPIT_OUT_THE_FEED_AND_IS_INSTEAD_ATTACKING_YOU));
	}

	private void dropItem(Player player, SkillEntry skill)
	{
		FeedableBeastInstance actor = getActor();

		actor.dropItem(player, skill.getTemplate().getItemConsumeId()[0], 1L, false);

		player.sendPacket(new SystemMessage(SystemMsg.S1).addNpcString(NpcString.THE_BEAST_SPIT_OUT_THE_FEED_INSTEAD_OF_EATING_IT));

		super.onEvtSeeSpell(skill, player);
	}

	@Override
	public FeedableBeastInstance getActor()
	{
		return (FeedableBeastInstance) super.getActor();
	}
}
