package quests;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.instancemanager.ServerVariables;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.GameObjectsStorage;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _610_MagicalPowerofWater2 extends Quest
{
	// NPC
	private static final int ASEFA = 31372;
	private static final int VARKAS_HOLY_ALTAR = 31560;

	// Quest items
	private static final int GREEN_TOTEM = 7238;
	int ICE_HEART_OF_ASHUTAR = 7239;

	private static final int Reward_First = 4589;
	private static final int Reward_Last = 4594;

	private static final int SoulOfWaterAshutar = 25316;
	private NpcInstance SoulOfWaterAshutarSpawn = null;



	public _610_MagicalPowerofWater2()
	{
		super(true);

		addStartNpc(ASEFA);

		addTalkId(VARKAS_HOLY_ALTAR);

		addKillId(SoulOfWaterAshutar);

		addQuestItem(ICE_HEART_OF_ASHUTAR);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		NpcInstance isQuest = GameObjectsStorage.getByNpcId(SoulOfWaterAshutar);
		String htmltext = event;
		if(event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "shaman_asefa_q0610_0104.htm";
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("610_1"))
		{
			if(ServerVariables.getLong(_610_MagicalPowerofWater2.class.getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
				htmltext = "totem_of_barka_q0610_0204.htm";
			else if(st.getQuestItemsCount(GREEN_TOTEM) >= 1 && isQuest == null)
			{
				st.takeItems(GREEN_TOTEM, 1);
				SoulOfWaterAshutarSpawn = st.addSpawn(SoulOfWaterAshutar, 104825, -36926, -1136);
				SoulOfWaterAshutarSpawn.addListener(new DeathListener());
				st.playSound(SOUND_MIDDLE);
			}
			else
				htmltext = "totem_of_barka_q0610_0203.htm";
		}
		else if(event.equalsIgnoreCase("610_3"))
			if(st.getQuestItemsCount(ICE_HEART_OF_ASHUTAR) >= 1)
			{
				st.takeItems(ICE_HEART_OF_ASHUTAR, -1);
				st.giveItems(Rnd.get(Reward_First, Reward_Last), 5, true);
				st.addExpAndSp(10000, 0);
				st.playSound(SOUND_FINISH);
				htmltext = "shaman_asefa_q0610_0301.htm";
				st.exitCurrentQuest(true);
			}
			else
				htmltext = "shaman_asefa_q0610_0302.htm";
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		NpcInstance isQuest = GameObjectsStorage.getByNpcId(SoulOfWaterAshutar);
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == ASEFA)
		{
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() >= 75)
				{
					if(st.getQuestItemsCount(GREEN_TOTEM) >= 1)
						htmltext = "shaman_asefa_q0610_0101.htm";
					else
					{
						htmltext = "shaman_asefa_q0610_0102.htm";
						st.exitCurrentQuest(true);
					}
				}
				else
				{
					htmltext = "shaman_asefa_q0610_0103.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(cond == 1)
				htmltext = "shaman_asefa_q0610_0105.htm";
			else if(cond == 2)
				htmltext = "shaman_asefa_q0610_0202.htm";
			else if(cond == 3 && st.getQuestItemsCount(ICE_HEART_OF_ASHUTAR) >= 1)
				htmltext = "shaman_asefa_q0610_0201.htm";
		}
		else if(npcId == VARKAS_HOLY_ALTAR)
			if(!npc.isBusy())
			{
				if(ServerVariables.getLong(_610_MagicalPowerofWater2.class.getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
					htmltext = "totem_of_barka_q0610_0204.htm";
				else if(cond == 1)
					htmltext = "totem_of_barka_q0610_0101.htm";
				else if(cond == 2 && isQuest == null)
				{
					SoulOfWaterAshutarSpawn = st.addSpawn(SoulOfWaterAshutar, 104825, -36926, -1136);
					SoulOfWaterAshutarSpawn.addListener(new DeathListener());
					htmltext = "totem_of_barka_q0610_0204.htm";
				}
			}
			else
				htmltext = "totem_of_barka_q0610_0202.htm";
		return htmltext;
	}

	private static class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature actor, Creature killer)
		{
			ServerVariables.set(_610_MagicalPowerofWater2.class.getSimpleName(), String.valueOf(System.currentTimeMillis()));
		}
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(ICE_HEART_OF_ASHUTAR) == 0 && npc.getNpcId() == SoulOfWaterAshutar)
		{
			st.giveItems(ICE_HEART_OF_ASHUTAR, 1);
			st.setCond(3);
			if(SoulOfWaterAshutarSpawn != null)
				SoulOfWaterAshutarSpawn.deleteMe();
			SoulOfWaterAshutarSpawn = null;
		}
		return null;
	}
}