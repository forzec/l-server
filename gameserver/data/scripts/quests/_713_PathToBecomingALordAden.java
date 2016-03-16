package quests;

import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.utils.ChatUtils;

public class _713_PathToBecomingALordAden extends Quest
{
	private static final int Logan = 35274;
	private static final int Orven = 30857;
	private static final int[] Orcs = {
			20669,
			20665
	};

	private static final int AdenCastle = 5;

	public _713_PathToBecomingALordAden()
	{
		super(false);
		addStartNpc(Logan);
		addTalkId(Orven);
		addKillId(Orcs);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Castle castle = ResidenceHolder.getInstance().getResidence(AdenCastle);
		if(castle.getOwner() == null)
			return "Castle has no lord";
		Player castleOwner = castle.getOwner().getLeader().getPlayer();

		if(event.equals("logan_q713_02.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equals("orven_q713_03.htm"))
		{
			st.setCond(2);
		}
		else if(event.equals("logan_q713_05.htm"))
		{
			ChatUtils.say(npc, NpcString.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_ADEN, st.getPlayer().getName());
			castle.getDominion().changeOwner(castleOwner.getClan());
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		Castle castle = ResidenceHolder.getInstance().getResidence(AdenCastle);
		if(castle.getOwner() == null)
			return "Castle has no lord";
		Player castleOwner = castle.getOwner().getLeader().getPlayer();

		if(npcId == Logan)
		{
			if(cond == 0)
			{
				if(castleOwner == st.getPlayer())
				{
					if(castle.getDominion().getLordObjectId() != st.getPlayer().getObjectId())
						htmltext = "logan_q713_01.htm";
					else
					{
						htmltext = "logan_q713_00.htm";
						st.exitCurrentQuest(true);
					}
				}
				else
				{
					htmltext = "logan_q713_00a.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(cond == 1)
				htmltext = "logan_q713_03.htm";
			else if(cond == 7)
				htmltext = "logan_q713_04.htm";
		}
		else if(npcId == Orven)
		{
			if(cond == 1)
				htmltext = "orven_q713_01.htm";
			else if(cond == 2)
				htmltext = "orven_q713_04.htm";
			else if(cond == 4)
				htmltext = "orven_q713_05.htm";
			else if(cond == 5)
			{
				st.setCond(7);
				htmltext = "orven_q713_06.htm";
			}
			else if(cond == 7)
				htmltext = "orven_q713_06.htm";
		}

		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 4)
		{
			int mobs = st.getInt("mobs") + 1;
			if(mobs < 100)
				st.set("mobs", mobs);
			else
				st.setCond(5);
		}
		return null;
	}
}