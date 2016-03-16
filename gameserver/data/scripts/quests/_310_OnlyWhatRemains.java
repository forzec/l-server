package quests;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * @author VAVAN
 * @corrected n0nam3
 */

public class _310_OnlyWhatRemains extends Quest
{


	// NPC's
	private static final int KINTAIJIN = 32640;
	// ITEMS's
	private static final int DIRTYBEAD = 14880;
	private static final int ACCELERATOR = 14832;
	private static final int JEWEL = 14835;

	public _310_OnlyWhatRemains()
	{
		super(false);
		addStartNpc(KINTAIJIN);
		addTalkId(KINTAIJIN);
		addQuestItem(DIRTYBEAD);
		for (int id = 22617; id < 22634; id++)
			addKillId(id);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("32640-3.htm"))
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
		int id = st.getState();
		int cond = st.getCond();
		if(id == COMPLETED)
			htmltext = "32640-10.htm";
		else if(id == CREATED)
		{
			QuestState ImTheOnlyOneYouCanTrust = st.getPlayer().getQuestState(240);
			if(st.getPlayer().getLevel() >= 81 && ImTheOnlyOneYouCanTrust != null && ImTheOnlyOneYouCanTrust.isCompleted())
				htmltext = "32640-1.htm";
			else
			{
				htmltext = "32640-0.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(cond == 1)
			htmltext = "32640-8.htm";
		else if(cond == 2)
		{
			st.takeItems(DIRTYBEAD, 500);
			st.giveItems(ACCELERATOR, 1);
			st.giveItems(JEWEL, 1);
			st.exitCurrentQuest(true);
			st.playSound(SOUND_FINISH);
			htmltext = "32640-9.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1 && Rnd.chance(66))
		{
			st.giveItems(DIRTYBEAD, 1);
			if(st.getQuestItemsCount(DIRTYBEAD) >= 500)
			{
				st.setCond(2);
				st.playSound(SOUND_MIDDLE);
			}
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}