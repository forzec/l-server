package quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _650_ABrokenDream extends Quest
{
	// NPC
	private static final int RailroadEngineer = 32054;
	// mobs
	private static final int ForgottenCrewman = 22027;
	private static final int VagabondOfTheRuins = 22028;
	// QuestItem
	private static final int RemnantsOfOldDwarvesDreams = 8514;



	public _650_ABrokenDream()
	{
		super(false);
		addStartNpc(RailroadEngineer);

		addKillId(ForgottenCrewman);
		addKillId(VagabondOfTheRuins);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "ghost_of_railroadman_q0650_0103.htm";
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("650_4"))
		{
			htmltext = "ghost_of_railroadman_q0650_0205.htm";
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
			st.unset("cond");
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		String htmltext = "noquest";
		if(cond == 0)
		{
			QuestState OceanOfDistantStar = st.getPlayer().getQuestState(117);
			if(OceanOfDistantStar != null)
			{
				if(OceanOfDistantStar.isCompleted())
				{
					if(st.getPlayer().getLevel() < 39)
					{
						st.exitCurrentQuest(true);
						htmltext = "ghost_of_railroadman_q0650_0102.htm";
					}
					else
						htmltext = "ghost_of_railroadman_q0650_0101.htm";
				}
				else
				{
					htmltext = "ghost_of_railroadman_q0650_0104.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
			{
				htmltext = "ghost_of_railroadman_q0650_0104.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(cond == 1)
			htmltext = "ghost_of_railroadman_q0650_0202.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		st.rollAndGive(RemnantsOfOldDwarvesDreams, 1, 1, 68);
		return null;
	}

}