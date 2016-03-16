package quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _10276_MutatedKaneusGludio extends Quest
{
	// NPCs
	private static final int Bathis = 30332;
	private static final int Rohmer = 30344;

	// MOBs
	private static final int TomlanKamos = 18554;
	private static final int OlAriosh = 18555;

	// Items
	private static final int Tissue1 = 13830;
	private static final int Tissue2 = 13831;

	public _10276_MutatedKaneusGludio()
	{
		super(true);
		addStartNpc(Bathis);
		addTalkId(Rohmer);
		addKillId(TomlanKamos, OlAriosh);
		addQuestItem(Tissue1, Tissue2);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30332-03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("30344-02.htm"))
		{
			st.giveItems(57, 8500);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		if(id == COMPLETED)
		{
			if(npcId == Bathis)
				htmltext = "30332-0a.htm";
		}
		else if(id == CREATED && npcId == Bathis)
		{
			if(st.getPlayer().getLevel() >= 18)
				htmltext = "30332-01.htm";
			else
				htmltext = "30332-00.htm";
		}
		else if(npcId == Bathis)
		{
			if(cond == 1)
			{
				if(st.getQuestItemsCount(Tissue1) < 1 && st.getQuestItemsCount(Tissue2) < 1)
					htmltext = "30332-04.htm";
				else
					htmltext = "30332-05.htm";
			}
		}
		else if(npcId == Rohmer)
			if(cond == 1)
			{
				if(st.getQuestItemsCount(Tissue1) < 1 && st.getQuestItemsCount(Tissue2) < 1)
					htmltext = "30344-01a.htm";
				else
					htmltext = "30344-01.htm";
			}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getState() == STARTED && st.getCond() == 1)
		{
			if(npc.getNpcId() == TomlanKamos && st.getQuestItemsCount(Tissue1) < 1)
				st.giveItems(Tissue1, 1);
			else if(npc.getNpcId() == OlAriosh && st.getQuestItemsCount(Tissue2) < 1)
				st.giveItems(Tissue2, 1);
		}
		return null;
	}
}