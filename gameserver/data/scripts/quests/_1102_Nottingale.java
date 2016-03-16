package quests;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.l2.s2c.RadarControl;

public class _1102_Nottingale extends Quest
{
	private final static int Nottingale = 32627;



	public _1102_Nottingale()
	{
		super(false);
		addFirstTalkId(Nottingale);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();
		QuestState qs = player.getQuestState(10273);
		if(qs == null || qs.getState() != COMPLETED)
		{
			player.sendPacket(new RadarControl(2, 2, 0, 0, 0));
			player.sendPacket(new RadarControl(0, 2, -184545, 243120, 1581));
			htmltext = "32627.htm";
		}
		else if(event.equalsIgnoreCase("32627-3.htm"))
		{
			player.sendPacket(new RadarControl(2, 2, 0, 0, 0));
			player.sendPacket(new RadarControl(0, 2, -192361, 254528, 3598));
		}
		else if(event.equalsIgnoreCase("32627-4.htm"))
		{
			player.sendPacket(new RadarControl(2, 2, 0, 0, 0));
			player.sendPacket(new RadarControl(0, 2, -174600, 219711, 4424));
		}
		else if(event.equalsIgnoreCase("32627-5.htm"))
		{
			player.sendPacket(new RadarControl(2, 2, 0, 0, 0));
			player.sendPacket(new RadarControl(0, 2, -181989, 208968, 4424));
		}
		else if(event.equalsIgnoreCase("32627-6.htm"))
		{
			player.sendPacket(new RadarControl(2, 2, 0, 0, 0));
			player.sendPacket(new RadarControl(0, 2, -252898, 235845, 5343));
		}
		else if(event.equalsIgnoreCase("32627-8.htm"))
		{
			player.sendPacket(new RadarControl(2, 2, 0, 0, 0));
			player.sendPacket(new RadarControl(0, 2, -212819, 209813, 4288));
		}
		else if(event.equalsIgnoreCase("32627-9.htm"))
		{
			player.sendPacket(new RadarControl(2, 2, 0, 0, 0));
			player.sendPacket(new RadarControl(0, 2, -246899, 251918, 4352));
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		QuestState qs = player.getQuestState(this);
		if(qs == null)
			newQuestState(player, STARTED);
		return "";
	}
}