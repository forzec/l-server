package quests;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.utils.ChatUtils;

public class _185_NikolasCooperationConsideration extends Quest
{
	private static final int Lorain = 30673;
	private static final int Nikola = 30621;
	private static final int Device = 32366;
	private static final int Alarm = 32367;

	private static final int Certificate = 10362;
	private static final int Metal = 10359;
	private static final int BrokenMetal = 10360;
	private static final int NicolasMap = 10361;



	public _185_NikolasCooperationConsideration()
	{
		super(false);

		// Нет стартового NPC, чтобы квест не появлялся в списке раньше времени
		addTalkId(Lorain, Nikola, Device, Alarm);
		addQuestItem(NicolasMap, BrokenMetal, Metal);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("30621-01.htm"))
		{
			if(player.getLevel() < 40)
				htmltext = "30621-00.htm";
		}
		else if(event.equalsIgnoreCase("30621-04.htm"))
		{
			st.playSound(SOUND_ACCEPT);
			st.setCond(1);
			st.giveItems(NicolasMap, 1);
		}
		else if(event.equalsIgnoreCase("30673-03.htm"))
		{
			st.playSound(SOUND_MIDDLE);
			st.setCond(2);
			st.takeItems(NicolasMap, -1);
		}
		else if(event.equalsIgnoreCase("30673-05.htm"))
		{
			st.playSound(SOUND_MIDDLE);
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("30673-09.htm"))
		{
			if(st.getQuestItemsCount(BrokenMetal) > 0)
				htmltext = "30673-10.htm";
			else if(st.getQuestItemsCount(Metal) > 0)
				st.giveItems(Certificate, 1);
			st.giveItems(ADENA_ID, 72527);
			st.addExpAndSp(203717, 14032);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		else if(event.equalsIgnoreCase("32366-02.htm"))
		{
			NpcInstance alarm = st.addSpawn(Alarm, 16491, 113563, -9064, 121000);
			st.set("step", "1");
			st.playSound("ItemSound3.sys_siren");
			st.startQuestTimer("1", 60000, alarm);
			ChatUtils.shout(alarm, NpcString.INTRUDER_ALERT_THE_ALARM_WILL_SELFDESTRUCT_IN_2_MINUTES_);
		}
		else if(event.equalsIgnoreCase("32366-05.htm"))
		{
			st.unset("step");
			st.playSound(SOUND_MIDDLE);
			st.setCond(5);
			st.giveItems(BrokenMetal, 1);
		}
		else if(event.equalsIgnoreCase("32366-06.htm"))
		{
			st.unset("step");
			st.playSound(SOUND_MIDDLE);
			st.setCond(4);
			st.giveItems(Metal, 1);
		}
		else if(event.equalsIgnoreCase("32367-02.htm"))
			st.set("pass", "0");
		else if(event.startsWith("correct"))
		{
			st.set("pass", str(st.getInt("pass") + 1));
			htmltext = event.substring(8);
			if(htmltext.equals("32367-07.htm"))
				if(st.getInt("pass") == 4)
				{
					st.set("step", "3");
					st.cancelQuestTimer("1");
					st.cancelQuestTimer("2");
					st.cancelQuestTimer("3");
					st.cancelQuestTimer("4");
					st.unset("pass");
					npc.deleteMe();
				}
				else
					htmltext = "32367-06.htm";
		}
		else if(event.equalsIgnoreCase("1"))
		{
			ChatUtils.shout(npc, NpcString.THE_ALARM_WILL_SELFDESTRUCT_IN_60_SECONDS_);
			st.startQuestTimer("2", 30000, npc);
			return null;
		}
		else if(event.equalsIgnoreCase("2"))
		{
			ChatUtils.shout(npc, NpcString.THE_ALARM_WILL_SELFDESTRUCT_IN_30_SECONDS_);
			st.startQuestTimer("3", 20000, npc);
			return null;
		}
		else if(event.equalsIgnoreCase("3"))
		{
			ChatUtils.shout(npc, NpcString.THE_ALARM_WILL_SELFDESTRUCT_IN_10_SECONDS_);
			st.startQuestTimer("4", 10000, npc);
			return null;
		}
		else if(event.equalsIgnoreCase("4"))
		{
			ChatUtils.shout(npc, NpcString.RECORDER_CRUSHED);
			st.set("step", "2");
			return null;
		}

		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if(st.isStarted())
			if(npcId == Nikola)
			{
				if(cond == 0)
					if(st.getPlayer().getLevel() < 40)
						htmltext = "30621-00.htm";
					else
						htmltext = "30621-01.htm";
				else if(cond == 1)
					htmltext = "30621-05.htm";
			}
			else if(npcId == Lorain)
			{
				if(cond == 1)
					htmltext = "30673-01.htm";
				else if(cond == 2)
					htmltext = "30673-04.htm";
				else if(cond == 3)
					htmltext = "30673-06.htm";
				else if(cond == 4 || cond == 5)
					htmltext = "30673-07.htm";
			}
			else if(npcId == Device)
			{
				int step = st.getInt("step");
				if(cond == 3)
					if(step == 0)
						htmltext = "32366-01.htm";
					else if(step == 1)
						htmltext = "32366-02.htm";
					else if(step == 2)
						htmltext = "32366-04.htm";
					else if(step == 3)
						htmltext = "32366-03.htm";
			}
			else if(npcId == Alarm)
				htmltext = "32367-01.htm";

		return htmltext;
	}
}