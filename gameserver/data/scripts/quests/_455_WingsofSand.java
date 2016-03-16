package quests;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * @author pchayka
 *         Daily quest
 */
public class _455_WingsofSand extends Quest
{
	private static final int[] SEPARATED_SOUL = {32864, 32865, 32866, 32867, 32868, 32869, 32870};
	private static final int LARGE_DRAGON_TOOTH = 17250;
	private static final int[] RAIDS = {25718, 25719, 25720, 25721, 25722, 25723, 25724};

	//Rewards
	private static final int[][] REWARDS =
	{
		// vesper armor pieces, 1.8% each
		{18, 15660, 1},
		{18, 15661, 1},
		{18, 15662, 1},
		{18, 15663, 1},
		{18, 15664, 1},
		{18, 15665, 1},
		{18, 15666, 1},
		{18, 15667, 1},
		{18, 15668, 1},
		{18, 15670, 1},
		{18, 15671, 1},
		{18, 15672, 1},
		{18, 15673, 1},
		{18, 15674, 1},
		{18, 15675, 1},
		// vesper sigil piece, 1.8%
		{18, 15691, 1},
		// vesper accessory pieces, 1.8% each
		{18, 15769, 1},
		{18, 15770, 1},
		{18, 15771, 1},
		// vesper weapon pieces, 1.8% each
		{18, 15634, 1},
		{18, 15635, 1},
		{18, 15636, 1},
		{18, 15637, 1},
		{18, 15638, 1},
		{18, 15639, 1},
		{18, 15640, 1},
		{18, 15641, 1},
		{18, 15642, 1},
		{18, 15643, 1},
		{18, 15644, 1},
		// vesper armor pieces, 0.7% each
		{7, 15660, 2},
		{7, 15661, 2},
		{7, 15662, 2},
		{7, 15663, 2},
		{7, 15664, 2},
		{7, 15665, 2},
		{7, 15666, 2},
		{7, 15667, 2},
		{7, 15668, 2},
		{7, 15669, 2},
		{7, 15670, 2},
		{7, 15671, 2},
		{7, 15672, 2},
		{7, 15673, 2},
		{7, 15674, 2},
		{7, 15675, 2},
		// vesper sigil pieces, 0.7%
		{7, 15691, 2},
		// vesper accessory pieces, 0.7% each
		{7, 15769, 2},
		{7, 15770, 2},
		{7, 15771, 2},
		// vesper weapon pieces, 0.7% each
		{7, 15634, 2},
		{7, 15635, 2},
		{7, 15636, 2},
		{7, 15637, 2},
		{7, 15638, 2},
		{7, 15639, 2},
		{7, 15640, 2},
		{7, 15641, 2},
		{7, 15642, 2},
		{7, 15643, 2},
		{7, 15644, 2},
		// vesper armor recipes, 0.5% each
		{5, 15792, 1},
		{5, 15793, 1},
		{5, 15794, 1},
		{5, 15795, 1},
		{5, 15796, 1},
		{5, 15797, 1},
		{5, 15798, 1},
		{5, 15799, 1},
		{5, 15800, 1},
		{5, 15801, 1},
		{5, 15802, 1},
		{5, 15803, 1},
		{5, 15804, 1},
		{5, 15805, 1},
		{5, 15806, 1},
		{5, 15807, 1},
		{5, 15808, 1},
		{5, 15809, 1},
		{5, 15810, 1},
		{5, 15811, 1},
		// vesper weapon recipes, 0.2% each
		{2, 15815, 1},
		{2, 15816, 1},
		{2, 15817, 1},
		{2, 15818, 1},
		{2, 15819, 1},
		{2, 15820, 1},
		{2, 15821, 1},
		{2, 15822, 1},
		{2, 15823, 1},
		{2, 15824, 1},
		{2, 15825, 1},
		// elements, 1.5% each
		{15, 9552, 1},
		{15, 9553, 1},
		{15, 9554, 1},
		{15, 9555, 1},
		{15, 9556, 1},
		{15, 9557, 1},
		// BEWS, 0.2%
		{2, 6577, 1},
		// BEAS, 1.1%
		{11, 6578, 1}
	};

	public _455_WingsofSand()
	{
		super(PARTY_ALL);
		addStartNpc(SEPARATED_SOUL);
		addQuestItem(LARGE_DRAGON_TOOTH);
		addKillId(RAIDS);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("separated_soul_01_q0455_07.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.startsWith("separated_soul_01_q0455_10.htm"))
		{
			st.takeAllItems(LARGE_DRAGON_TOOTH);

			int random = Rnd.get(1000);
			for (int i = 0; i < REWARDS.length; i++)
			{
				random -= REWARDS[i][0];
				if (random <= 0)
				{
					st.giveItems(REWARDS[i][1], REWARDS[i][2]);
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
		if(ArrayUtils.contains(SEPARATED_SOUL, npc.getNpcId()))
		{
			switch(st.getState())
			{
				case CREATED:
					if(st.isNowAvailable())
					{
						if(st.getPlayer().getLevel() >= 80)
							htmltext = "separated_soul_01_q0455_01.htm";
						else
						{
							htmltext = "separated_soul_01_q0455_03.htm";
							st.exitCurrentQuest(true);
						}
					}
					else
						htmltext = "separated_soul_01_q0455_02.htm";
					break;
				case STARTED:
					if(cond == 1)
						htmltext = "separated_soul_01_q0455_08.htm";
					else if(cond == 2)
						htmltext = "separated_soul_01_q0455_09.htm";
					else if(cond == 3)
					{
						htmltext = "separated_soul_01_q0455_12.htm";

						// Внимание, хардкод ! При изменении награды пересчитать базу и позицию !
						int random = Rnd.get(558, 1000);
						for (int i = 0; i < REWARDS.length; i++)
						{
							random -= REWARDS[i][0];
							if (random <= 0)
							{
								st.giveItems(REWARDS[i][1], REWARDS[i][2]);
								break;
							}
						}

						st.setState(COMPLETED);
						st.playSound(SOUND_FINISH);
						st.exitCurrentQuest(this);
					}
					break;
			}
		}

		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			st.setCond(2);
			st.giveItems(LARGE_DRAGON_TOOTH, 1);
			st.playSound(SOUND_ITEMGET);
		}
		else if(cond == 2)
		{
			st.setCond(3);
			st.giveItems(LARGE_DRAGON_TOOTH, 1);
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}