package quests;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.tables.SkillTable;

public class _367_ElectrifyingRecharge extends Quest
{
	//NPCs
	private static int LORAIN = 30673;
	//Mobs
	private static int CATHEROK = 21035;
	//Quest Items
	private static int Titan_Lamp_First = 5875;
	private static int Titan_Lamp_Last = 5879;
	private static int Broken_Titan_Lamp = 5880;
	//Chances
	private static int broke_chance = 3;
	private static int uplight_chance = 7;

	public _367_ElectrifyingRecharge()
	{
		super(false);
		addStartNpc(LORAIN);
		addKillId(CATHEROK);
		for(int Titan_Lamp_id = Titan_Lamp_First; Titan_Lamp_id <= Titan_Lamp_Last; Titan_Lamp_id++)
			addQuestItem(Titan_Lamp_id);
		addQuestItem(Broken_Titan_Lamp);
	}

	private static boolean takeAllLamps(QuestState st)
	{
		boolean result = false;
		for(int Titan_Lamp_id = Titan_Lamp_First; Titan_Lamp_id <= Titan_Lamp_Last; Titan_Lamp_id++)
			if(st.getQuestItemsCount(Titan_Lamp_id) > 0)
			{
				result = true;
				st.takeItems(Titan_Lamp_id, -1);
			}
		if(st.getQuestItemsCount(Broken_Titan_Lamp) > 0)
		{
			result = true;
			st.takeItems(Broken_Titan_Lamp, -1);
		}
		return result;
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int _state = st.getState();
		if(event.equalsIgnoreCase("30673-03.htm") && _state == CREATED)
		{
			takeAllLamps(st);
			st.giveItems(Titan_Lamp_First, 1);
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("30673-07.htm") && _state == STARTED)
		{
			takeAllLamps(st);
			st.giveItems(Titan_Lamp_First, 1);
		}
		else if(event.equalsIgnoreCase("30673-08.htm") && _state == STARTED)
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		if(npc.getNpcId() != LORAIN)
			return htmltext;
		int _state = st.getState();

		if(_state == CREATED)
		{
			if(st.getPlayer().getLevel() < 37)
			{
				htmltext = "30673-02.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "30673-01.htm";
				st.setCond(0);
			}
		}
		else if(_state == STARTED)
			if(st.getQuestItemsCount(Titan_Lamp_Last) > 0)
			{
				htmltext = "30673-06.htm";
				takeAllLamps(st);
				st.giveItems(4553 + Rnd.get(12), 1);
				st.playSound(SOUND_MIDDLE);
			}
			else if(st.getQuestItemsCount(Broken_Titan_Lamp) > 0)
			{
				htmltext = "30673-05.htm";
				takeAllLamps(st);
				st.giveItems(Titan_Lamp_First, 1);
			}
			else
				htmltext = "30673-04.htm";

		return htmltext;
	}

	@Override
	public String onAttack(NpcInstance npc, QuestState qs)
	{
		if(qs.getState() != STARTED)
			return null;
		if(qs.getQuestItemsCount(Broken_Titan_Lamp) > 0)
			return null;

		if(Rnd.chance(uplight_chance))
			for(int Titan_Lamp_id = Titan_Lamp_First; Titan_Lamp_id < Titan_Lamp_Last; Titan_Lamp_id++)
				if(qs.getQuestItemsCount(Titan_Lamp_id) > 0)
				{
					int Titan_Lamp_Next = Titan_Lamp_id + 1;
					takeAllLamps(qs);
					qs.giveItems(Titan_Lamp_Next, 1);
					if(Titan_Lamp_Next == Titan_Lamp_Last)
					{
						qs.setCond(2);
						qs.playSound(SOUND_MIDDLE);
					}
					else
						qs.playSound(SOUND_ITEMGET);
					npc.doCast(SkillTable.getInstance().getSkillEntry(4072, 4), qs.getPlayer(), true);
					return null;
				}
				else if(Rnd.chance(broke_chance))
					if(takeAllLamps(qs))
						qs.giveItems(Broken_Titan_Lamp, 1);

		return null;
	}


}