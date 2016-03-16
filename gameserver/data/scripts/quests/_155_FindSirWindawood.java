package quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _155_FindSirWindawood extends Quest
{
	int OFFICIAL_LETTER = 1019;
	int HASTE_POTION = 734;



	public _155_FindSirWindawood()
	{
		super(false);

		addStartNpc(30042);

		addTalkId(30042);
		addTalkId(30311);

		addQuestItem(OFFICIAL_LETTER);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("30042-04.htm"))
		{
			st.giveItems(OFFICIAL_LETTER, 1);
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getCond();
		if(npcId == 30042)
		{
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() >= 3)
				{
					htmltext = "30042-03.htm";
					return htmltext;
				}
				htmltext = "30042-02.htm";
				st.exitCurrentQuest(true);
			}
			else if(cond == 1 && st.getQuestItemsCount(OFFICIAL_LETTER) == 1)
				htmltext = "30042-05.htm";
		}
		else if(npcId == 30311 && cond == 1 && st.getQuestItemsCount(OFFICIAL_LETTER) == 1)
		{
			htmltext = "30311-01.htm";
			st.takeItems(OFFICIAL_LETTER, -1);
			st.giveItems(HASTE_POTION, 1);
			st.setCond(0);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}
}