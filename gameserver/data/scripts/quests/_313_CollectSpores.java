package quests;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _313_CollectSpores extends Quest
{
	//NPC
	public final int Herbiel = 30150;
	//Mobs
	public final int SporeFungus = 20509;
	//Quest Items
	public final int SporeSac = 1118;



	public _313_CollectSpores()
	{
		super(false);

		addStartNpc(Herbiel);
		addTalkId(Herbiel);
		addKillId(SporeFungus);
		addQuestItem(SporeSac);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("green_q0313_05.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		if(cond == 0)
		{
			if(st.getPlayer().getLevel() >= 8)
				htmltext = "green_q0313_03.htm";
			else
			{
				htmltext = "green_q0313_02.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(cond == 1)
			htmltext = "green_q0313_06.htm";
		else if(cond == 2)
			if(st.getQuestItemsCount(SporeSac) < 10)
			{
				st.setCond(1);
				htmltext = "green_q0313_06.htm";
			}
			else
			{
				st.takeItems(SporeSac, -1);
				st.giveItems(ADENA_ID, 3500, true);
				st.playSound(SOUND_FINISH);
				htmltext = "green_q0313_07.htm";
				st.exitCurrentQuest(true);
			}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(cond == 1 && npcId == SporeFungus && Rnd.chance(70))
		{
			st.giveItems(SporeSac, 1);
			if(st.getQuestItemsCount(SporeSac) < 10)
				st.playSound(SOUND_ITEMGET);
			else
			{
				st.playSound(SOUND_MIDDLE);
				st.setCond(2);
				st.setState(STARTED);
			}
		}
		return null;
	}
}