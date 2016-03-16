package quests;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public class _10284_AcquisionOfDivineSword extends Quest
{
	private static final int Rafforty = 32020;
	private static final int Jinia = 32760;
	private static final int Krun = 32653;
	private static final int ColdResistancePotion = 15514;
	private static final int InjKegor = 18846;
	private static final int MithrilMillipede = 22766;
	int _count = 0;

	public _10284_AcquisionOfDivineSword()
	{
		super(false);
		addStartNpc(Rafforty);
		addTalkId(Jinia, Krun, InjKegor);
		addKillId(MithrilMillipede);
		addQuestItem(ColdResistancePotion);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("rafforty_q10284_02.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("enterinstance"))
		{
			st.setCond(2);
			ReflectionUtils.simpleEnterInstancedZone(st.getPlayer(), 140);
			return null;
		}
		else if(event.equalsIgnoreCase("jinia_q10284_03.htm"))
		{
			if(!st.getPlayer().getReflection().isDefault())
			{
				st.getPlayer().getReflection().startCollapseTimer(60 * 1000L);
				st.getPlayer().sendPacket(new SystemMessage(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(1));
			}
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("leaveinstance"))
		{
			st.getPlayer().getReflection().collapse();
			return null;
		}
		else if(event.equalsIgnoreCase("entermines"))
		{
			st.setCond(4);
			if(st.getQuestItemsCount(ColdResistancePotion) < 1)
				st.giveItems(ColdResistancePotion, 1);
			ReflectionUtils.simpleEnterInstancedZone(st.getPlayer(), 138);
			return null;
		}
		else if(event.equalsIgnoreCase("leavemines"))
		{
			st.giveItems(ADENA_ID, 296425);
			st.addExpAndSp(921805, 82230);
			st.playSound(SOUND_FINISH);
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
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
				QuestState qs = st.getPlayer().getQuestState(10283);
				if(st.getPlayer().getLevel() >= 82 && qs != null && qs.isCompleted())
					htmltext = "rafforty_q10284_01.htm";
				else
				{
					htmltext = "rafforty_q10284_00.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(cond == 1 || cond == 2)
				htmltext = "rafforty_q10284_02.htm";
		}
		else if(npcId == Jinia)
		{
			if(cond == 2)
				htmltext = "jinia_q10284_01.htm";
			else if(cond == 3)
				htmltext = "jinia_q10284_02.htm";
		}
		else if(npcId == Krun)
		{
			if(cond == 3 || cond == 4 || cond == 5)
				htmltext = "krun_q10284_01.htm";
		}
		else if(npcId == InjKegor)
		{
			if(cond == 4)
			{
				st.takeAllItems(ColdResistancePotion);
				st.setCond(5);
				htmltext = "kegor_q10284_01.htm";
				for(int i = 0; i < 4; i++)
				{
					NpcInstance mob = st.getPlayer().getReflection().addSpawnWithoutRespawn(MithrilMillipede, Location.findPointToStay(st.getPlayer(), 50, 100), st.getPlayer().getGeoIndex());
					mob.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, st.getPlayer(), null, 300);
				}
			}
			else if(cond == 5)
				htmltext = "kegor_q10284_02.htm";
			else if(cond == 6)
				htmltext = "kegor_q10284_03.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(cond == 5 && npcId == MithrilMillipede)
		{
			if(_count < 3)
				_count++;
			else
			{
				st.setCond(6);
				st.getPlayer().getReflection().startCollapseTimer(3 * 60 * 1000L);
				st.getPlayer().sendPacket(new SystemMessage(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(3));
			}
		}
		return null;
	}
}