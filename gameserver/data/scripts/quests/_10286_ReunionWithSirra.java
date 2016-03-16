package quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public class _10286_ReunionWithSirra extends Quest
{
	private static final int Rafforty = 32020;
	private static final int Jinia = 32760;
	private static final int Jinia2 = 32781;
	private static final int Sirra = 32762;

	public _10286_ReunionWithSirra()
	{
		super(false);
		addStartNpc(Rafforty);
		addTalkId(Jinia, Jinia2, Sirra);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("rafforty_q10286_02.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("enterinstance"))
		{
			st.setCond(2);
			ReflectionUtils.simpleEnterInstancedZone(st.getPlayer(), 141);
			return null;
		}
		else if(event.equalsIgnoreCase("sirraspawn"))
		{
			st.setCond(3);
			NpcInstance sirra = st.getPlayer().getReflection().addSpawnWithoutRespawn(Sirra, new Location(-23848, -8744, -5413, 49152), 0);
			Functions.npcSay(sirra, "Вы с таким энтузиазмом отправились в путь и это все, чего Вы добиись? Хе-хе-хе...");
			return null;
		}
		else if(event.equalsIgnoreCase("sirra_q10286_04.htm"))
		{
			st.giveItems(15470, 5);
			st.setCond(4);
			npc.deleteMe();
		}
		else if(event.equalsIgnoreCase("leaveinstance"))
		{
			st.setCond(5);
			st.getPlayer().getReflection().collapse();
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
		if(npcId == Rafforty)
		{
			if(cond == 0)
			{
				QuestState qs = st.getPlayer().getQuestState(10285);
				if(st.getPlayer().getLevel() >= 82 && qs != null && qs.isCompleted())
					htmltext = "rafforty_q10286_01.htm";
				else
				{
					htmltext = "rafforty_q10286_00.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(cond == 1 || cond == 2 || cond == 3 || cond == 4)
				htmltext = "rafforty_q10286_03.htm";
		}
		else if(npcId == Jinia)
		{
			if(cond == 2)
				htmltext = "jinia_q10286_01.htm";
			else if(cond == 3)
				htmltext = "jinia_q10286_01a.htm";
			else if(cond == 4)
				htmltext = "jinia_q10286_05.htm";
		}
		else if(npcId == Sirra)
		{
			if(cond == 3)
				htmltext = "sirra_q10286_01.htm";
		}
		else if(npcId == Jinia2)
		{
			if(cond == 5)
				htmltext = "jinia2_q10286_01.htm";
			else if(cond == 6)
				htmltext = "jinia2_q10286_04.htm";
			else if(cond == 7)
			{
				htmltext = "jinia2_q10286_05.htm";
				st.addExpAndSp(2152200, 181070);
				st.setState(COMPLETED);
				st.exitCurrentQuest(false);
			}
		}
		return htmltext;
	}
}