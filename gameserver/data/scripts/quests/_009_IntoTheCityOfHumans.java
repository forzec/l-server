package quests;

import org.mmocore.gameserver.model.base.Race;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _009_IntoTheCityOfHumans extends Quest
{
	//NPC
	public final int PETUKAI = 30583;
	public final int TANAPI = 30571;
	public final int TAMIL = 30576;
	//Items
	public final int SCROLL_OF_ESCAPE_GIRAN = 7126;
	//Quest Item
	public final int MARK_OF_TRAVELER = 7570;



	public _009_IntoTheCityOfHumans()
	{
		super(false);

		addStartNpc(PETUKAI);

		addTalkId(PETUKAI);
		addTalkId(TANAPI);
		addTalkId(TAMIL);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("centurion_petukai_q0009_0104.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("seer_tanapi_q0009_0201.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("gatekeeper_tamil_q0009_0301.htm"))
		{
			st.giveItems(SCROLL_OF_ESCAPE_GIRAN, 1);
			st.giveItems(MARK_OF_TRAVELER, 1);
			st.unset("cond");
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
		if(npcId == PETUKAI)
		{
			if(cond == 0)
			{
				if(st.getPlayer().getRace() == Race.orc && st.getPlayer().getLevel() >= 3)
					htmltext = "centurion_petukai_q0009_0101.htm";
				else
				{
					htmltext = "centurion_petukai_q0009_0102.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(cond == 1)
				htmltext = "centurion_petukai_q0009_0105.htm";
		}
		else if(npcId == TANAPI)
		{
			if(cond == 1)
				htmltext = "seer_tanapi_q0009_0101.htm";
			else if(cond == 2)
				htmltext = "seer_tanapi_q0009_0202.htm";
		}
		else if(npcId == TAMIL)
			if(cond == 2)
				htmltext = "gatekeeper_tamil_q0009_0201.htm";
		return htmltext;
	}
}