package npc.model.beastfarm;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.collections.CollectionUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.GameObjectTasks.NotifyAITask;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.network.l2.s2c.SocialAction;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

public class FeedableMonsterInstance extends MonsterInstance
{
	private static final int SKILL_GOLDEN_SPICE = 2188;
	private static final int SKILL_CRYSTAL_SPICE = 2189;

	private final int _growChance;
	private final int[] _goldenGrowNpcIds;
	private final int[] _crystalGrowNpcIds;
	// для животных последнего этапа 0 приручен для бойцов, 1 приручен для магов
	private final int[] _tamedNpcIds;

	private int _step;
	private int _feederObjectId;
	private volatile boolean _grown;

	public FeedableMonsterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		_growChance = getParameter("grow_chance", 100);
		_goldenGrowNpcIds = getParameters().getIntegerArray("golden_grow_npcs", ArrayUtils.EMPTY_INT_ARRAY);
		_crystalGrowNpcIds = getParameters().getIntegerArray("crystal_grow_npcs", ArrayUtils.EMPTY_INT_ARRAY);
		_tamedNpcIds = getParameters().getIntegerArray("tamed_npcs", ArrayUtils.EMPTY_INT_ARRAY);
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();
		_step = 1;
		_feederObjectId = 0;
		_grown = false;
	}

	@Override
	public void endDecayTask()
	{
		if(_decayTask != null)
		{
			_decayTask.cancel(false);
			_decayTask = null;
		}
		onDecay();
	}

	@Override
	public void onSeeSpell(SkillEntry skill, Creature caster)
	{
		super.onSeeSpell(skill, caster);

		if (caster == null || !caster.isPlayer() || _grown)
			return;

		final boolean isCrystal = skill.getId() == SKILL_CRYSTAL_SPICE;
		if (!isCrystal && skill.getId() != SKILL_GOLDEN_SPICE)
			return;

		broadcastPacket(new SocialAction(getObjectId(), SocialAction.GREETING));

		if (Rnd.chance(5))
			switch (_step)
			{
				case 1:
					ChatUtils.say(this, NpcString.valueOf(Rnd.get(2004, 2013)));
					break;
				case 2:
					ChatUtils.say(this, NpcString.valueOf(Rnd.get(2014, 2018)));
					break;
				case 3:
					ChatUtils.say(this, NpcString.valueOf(Rnd.get(2019, 2023)));
					break;
			}

		final Player player = (Player)caster;

		// Только животное первого этапа может кормить кто угодно
		if (_feederObjectId != 0 && player.getObjectId() != _feederObjectId)
			return;

		final int[] grow = isCrystal ? _crystalGrowNpcIds : _goldenGrowNpcIds;
		if (grow.length == 0)
			return;

		if (Rnd.chance(_growChance))
		{
			_grown = true;
			final int itemId = skill.getTemplate().getItemConsumeId()[0];

			ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl()
				{
					if (isDead() || !isVisible())
						return;

					decayOrDelete();
					if (_tamedNpcIds.length > 1 && Rnd.nextBoolean())
					{
						FeedableMonsterInstance.spawnTamedMonster(player, player.isMageClass() ? _tamedNpcIds[1] : _tamedNpcIds[0], itemId);
						return;
					}

					NpcInstance nextMonster = NpcUtils.spawnSingle(grow[Rnd.get(grow.length)], getLoc());
					if (nextMonster instanceof FeedableMonsterInstance)
						((FeedableMonsterInstance)nextMonster).setFeederObjectId(player.getObjectId(), _step + 1);

					nextMonster.onAction(player, false);
					ThreadPoolManager.getInstance().schedule(new NotifyAITask(nextMonster, CtrlEvent.EVT_AGGRESSION, player, 5000), 5000L);
				}
			}, 1000L);
		}
	}

	private void setFeederObjectId(int objectId, int step)
	{
		_feederObjectId = objectId;
		_step = step;
	}

	private static final void spawnTamedMonster(final Player player, int npcId, int itemId)
	{
		NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);

		NpcInstance prevNpc = CollectionUtils.safeGet(player.getTamedBeasts(), 0);

		TamedMonsterInstance tamedBeast = (TamedMonsterInstance)template.getNewInstance();
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

		if (npcId <= 16018) // мелкие животные на ферме в квесте не участвуют
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
				}
			}, 5000L);
	}
}
