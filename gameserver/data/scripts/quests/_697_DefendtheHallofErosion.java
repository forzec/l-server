package quests;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.instancemanager.SoIManager;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * @author pchayka
 */

public class _697_DefendtheHallofErosion extends Quest
{
	private static final int TEPIOS = 32603;
	private static final int VesperNobleEnhanceStone = 14052;

	public _697_DefendtheHallofErosion()
	{
		super(PARTY_ALL);
		addStartNpc(TEPIOS);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;

		if(event.equalsIgnoreCase("tepios_q697_3.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		Player player = st.getPlayer();
		int cond = st.getCond();

		if(npcId == TEPIOS)
		{
			if(cond == 0)
			{
				if(player.getLevel() < 75)
				{
					st.exitCurrentQuest(true);
					return "tepios_q697_0.htm";
				}
				if(SoIManager.getCurrentStage() != 4)
				{
					st.exitCurrentQuest(true);
					return "tepios_q697_0a.htm";
				}
				htmltext = "tepios_q697_1.htm";
			}
			else if(cond == 1 && st.getInt("defenceDone") == 0)
				htmltext = "tepios_q697_4.htm";
			else if(cond == 1 && st.getInt("defenceDone") != 0)
			{
				st.giveItems(VesperNobleEnhanceStone, Rnd.get(12, 20));
				htmltext = "tepios_q697_5.htm";
				st.playSound(SOUND_FINISH);
				st.unset("defenceDone");
				st.exitCurrentQuest(true);
			}

		}
		return htmltext;
	}



}