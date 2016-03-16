package quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _019_GoToThePastureland extends Quest
{
	int VLADIMIR = 31302;
	int TUNATUN = 31537;

	int BEAST_MEAT = 7547;



	public _019_GoToThePastureland()
	{
		super(false);

		addStartNpc(VLADIMIR);

		addTalkId(VLADIMIR);
		addTalkId(TUNATUN);

		addQuestItem(BEAST_MEAT);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("trader_vladimir_q0019_0104.htm"))
		{
			st.giveItems(BEAST_MEAT, 1);
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		if(event.equals("beast_herder_tunatun_q0019_0201.htm"))
		{
			st.takeItems(BEAST_MEAT, -1);
			st.addExpAndSp(385040, 75250);
			st.giveItems(ADENA_ID, 147200);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == VLADIMIR)
		{
			if(cond == 0)
				if(st.getPlayer().getLevel() >= 63)
					htmltext = "trader_vladimir_q0019_0101.htm";
				else
				{
					htmltext = "trader_vladimir_q0019_0103.htm";
					st.exitCurrentQuest(true);
				}
			else
				htmltext = "trader_vladimir_q0019_0105.htm";
		}
		else if(npcId == TUNATUN)
			if(st.getQuestItemsCount(BEAST_MEAT) >= 1)
				htmltext = "beast_herder_tunatun_q0019_0101.htm";
			else
			{
				htmltext = "beast_herder_tunatun_q0019_0202.htm";
				st.exitCurrentQuest(true);
			}
		return htmltext;
	}
}