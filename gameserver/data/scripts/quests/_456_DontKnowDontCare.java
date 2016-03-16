package quests;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * @author pchayka
 *         Daily quest
 *         ВНИМАНИЕ! Данный квест можно выполнять не только группой, но и командным каналом, все персонажи в командном канале имеют шанс получить квестовые предметы. После убийства боссов будут появляться специальные НПЦ - мертвые тела боссов, для получения квестовых предметов необходимо будет "поговорить" с этим НПЦ.
 */
public class _456_DontKnowDontCare extends Quest
{
	private static final int[] SEPARATED_SOULS = {32864, 32865, 32866, 32867, 32868, 32869, 32870};
	private static final int DRAKE_LORD_ESSENCE = 17251;
	private static final int BEHEMOTH_LEADER_ESSENCE = 17252;
	private static final int DRAGON_BEAST_ESSENCE = 17253;

	private static final int DRAKE_LORD_CORPSE = 32884;
	private static final int BEHEMOTH_LEADER_CORPSE = 32885;
	private static final int DRAGON_BEAST_CORPSE = 32886;

	// Rewards: chance/id
	private static final int[][] REWARDS =
	{
		// armor, chance 0.1% each
		{100, 15743},
		{100, 15744},
		{100, 15745},
		{100, 15746},
		{100, 15747},
		{100, 15748},
		{100, 15749},
		{100, 15750},
		{100, 15751},
		{100, 15752},
		{100, 15753},
		{100, 15754},
		{100, 15755},
		{100, 15756},
		{100, 15757},
		{100, 15758},
		{100, 15759},
		// accessory, chance 0.1% each
		{100, 15763},
		{100, 15764},
		{100, 15765},
		// weapons, chance 0.05% each
		{50, 15558},
		{50, 15559},
		{50, 15560},
		{50, 15561},
		{50, 15562},
		{50, 15563},
		{50, 15564},
		{50, 15565},
		{50, 15566},
		{50, 15567},
		{50, 15568},
		{50, 15569},
		{50, 15570},
		{50, 15571},
		// BEWS, chance 0.55%
		{550, 6577},
		// BEAS, chance 1%
		{1000, 6578},
		// attributes, chance 5%
		{5000, 9552},
		{5000, 9553},
		{5000, 9554},
		{5000, 9555},
		{5000, 9556},
		{5000, 9557},
		// EWS, chance 1.75%
		{1750, 959},
		// gemstone s
		{100000, 2134}
	};

	public _456_DontKnowDontCare()
	{
		super(PARTY_ALL);
		addStartNpc(SEPARATED_SOULS);
		addTalkId(DRAKE_LORD_CORPSE, BEHEMOTH_LEADER_CORPSE, DRAGON_BEAST_CORPSE);
		addFirstTalkId(DRAKE_LORD_CORPSE, BEHEMOTH_LEADER_CORPSE, DRAGON_BEAST_CORPSE);
		addQuestItem(DRAKE_LORD_ESSENCE, BEHEMOTH_LEADER_ESSENCE, DRAGON_BEAST_ESSENCE);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("sepsoul_q456_05.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("take_essense"))
		{
			if(st.getCond() == 1 && npc != null && st.getInt("RaidKilled") == npc.getObjectId())
			{
				switch(npc.getNpcId())
				{
					case DRAKE_LORD_CORPSE:
						if(st.getQuestItemsCount(DRAKE_LORD_ESSENCE) == 0)
						{
							st.giveItems(DRAKE_LORD_ESSENCE, 1);
							htmltext = "corpse_drake_lord_q0456_02.htm";
						}
						break;
					case BEHEMOTH_LEADER_CORPSE:
						if(st.getQuestItemsCount(BEHEMOTH_LEADER_ESSENCE) == 0)
						{
							st.giveItems(BEHEMOTH_LEADER_ESSENCE, 1);
							htmltext = "corpse_behemoth_leader_q0456_02.htm";
						}
						break;
					case DRAGON_BEAST_CORPSE:
						if(st.getQuestItemsCount(DRAGON_BEAST_ESSENCE) == 0)
						{
							st.giveItems(DRAGON_BEAST_ESSENCE, 1);
							htmltext = "corpse_dragon_beast_q0456_02.htm";
						}
						break;
				}
				if(st.getQuestItemsCount(DRAKE_LORD_ESSENCE) > 0 && st.getQuestItemsCount(BEHEMOTH_LEADER_ESSENCE) > 0 && st.getQuestItemsCount(DRAGON_BEAST_ESSENCE) > 0)
					st.setCond(2);
			}
		}
		else if(event.equalsIgnoreCase("sepsoul_q456_08.htm"))
		{
			st.takeAllItems(DRAKE_LORD_ESSENCE);
			st.takeAllItems(BEHEMOTH_LEADER_ESSENCE);
			st.takeAllItems(DRAGON_BEAST_ESSENCE);

			int random = Rnd.get(100000);
			for (int i = 0; i < REWARDS.length; i++)
			{
				random -= REWARDS[i][0];
				if (random <= 0)
				{
					st.giveItems(REWARDS[i][1], 1);
					break;
				}
			}

			st.setState(COMPLETED);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(this);
		}

		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		if(ArrayUtils.contains(SEPARATED_SOULS, npc.getNpcId()))
		{
			switch(st.getState())
			{
				case CREATED:
					if(st.isNowAvailable())
					{
						if(st.getPlayer().getLevel() >= 80)
							htmltext = "sepsoul_q456_01.htm";
						else
						{
							htmltext = "sepsoul_q456_00.htm";
							st.exitCurrentQuest(true);
						}
					}
					else
						htmltext = "sepsoul_q456_00a.htm";
					break;
				case STARTED:
					if(cond == 1)
						htmltext = "sepsoul_q456_06.htm";
					else if(cond == 2)
						htmltext = "sepsoul_q456_07.htm";
					break;
			}
		}

		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		final QuestState st = player.getQuestState(this);
		final boolean result = (st != null && st.getInt("RaidKilled") == npc.getObjectId());
		switch (npc.getNpcId())
		{
			case DRAKE_LORD_CORPSE:
				return result ? st.getQuestItemsCount(DRAKE_LORD_ESSENCE) == 0 ? "corpse_drake_lord_q0456_01.htm" : "corpse_drake_lord001.htm" : "corpse_drake_lord_q0456_03.htm";
			case BEHEMOTH_LEADER_CORPSE:
				return result ? st.getQuestItemsCount(BEHEMOTH_LEADER_ESSENCE) == 0 ? "corpse_behemoth_leader_q0456_01.htm" : "corpse_behemoth_leader001.htm" : "corpse_behemoth_leader_q0456_03.htm";
			case DRAGON_BEAST_CORPSE:
				return result ? st.getQuestItemsCount(DRAGON_BEAST_ESSENCE) == 0 ? "corpse_dragon_beast_q0456_01.htm" : "corpse_dragon_beast001.htm" : "corpse_dragon_beast_q0456_03.htm";
		}
		return "noquest";
	}
}