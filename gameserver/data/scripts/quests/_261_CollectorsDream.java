package quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;

public class _261_CollectorsDream extends Quest
{
	int GIANT_SPIDER_LEG = 1087;



	public _261_CollectorsDream()
	{
		super(false);

		addStartNpc(30222);

		addTalkId(30222);

		addKillId(20308);
		addKillId(20460);
		addKillId(20466);

		addQuestItem(GIANT_SPIDER_LEG);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.intern().equalsIgnoreCase("moneylender_alshupes_q0261_03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		if(cond == 0)
		{
			if(st.getPlayer().getLevel() >= 15)
			{
				htmltext = "moneylender_alshupes_q0261_02.htm";
				return htmltext;
			}
			htmltext = "moneylender_alshupes_q0261_01.htm";
			st.exitCurrentQuest(true);
		}
		else if(cond == 1 || st.getQuestItemsCount(GIANT_SPIDER_LEG) < 8)
			htmltext = "moneylender_alshupes_q0261_04.htm";
		else if(cond == 2 && st.getQuestItemsCount(GIANT_SPIDER_LEG) >= 8)
		{
			st.takeItems(GIANT_SPIDER_LEG, -1);

			st.giveItems(ADENA_ID, 1000);
			st.addExpAndSp(2000, 0);

			if(st.getPlayer().getClassId().getLevel() == 1 && !st.getPlayer().getVarB("p1q4"))
			{
				st.getPlayer().setVar("p1q4", "1", -1);
				st.getPlayer().sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide.", 5000, ScreenMessageAlign.TOP_CENTER, true));
			}

			htmltext = "moneylender_alshupes_q0261_05.htm";
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1 && st.getQuestItemsCount(GIANT_SPIDER_LEG) < 8)
		{
			st.giveItems(GIANT_SPIDER_LEG, 1);
			if(st.getQuestItemsCount(GIANT_SPIDER_LEG) == 8)
			{
				st.playSound(SOUND_MIDDLE);
				st.setCond(2);
			}
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}