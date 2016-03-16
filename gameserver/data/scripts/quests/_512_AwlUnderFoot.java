package quests;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _512_AwlUnderFoot extends Quest
{
	private final static int FragmentOfTheDungeonLeaderMark = 9798;
	private final static int RewardMarksCount = 1500;
	private final static int KnightsEpaulette = 9912;

	private static final int BeautifulAtrielle = 25563;
	private static final int NagenTheTomboy = 25566;
	private static final int JaxTheDestroyer = 25569;

	private static final int[] rewardBosses = new int[] { BeautifulAtrielle, NagenTheTomboy, JaxTheDestroyer };

	public _512_AwlUnderFoot()
	{
		super(PARTY_ALL);
		// Wardens
		addStartNpc(36403, 36404, 36405, 36406, 36407, 36408, 36409, 36410, 36411);
		addQuestItem(FragmentOfTheDungeonLeaderMark);
		addKillId(BeautifulAtrielle, NagenTheTomboy, JaxTheDestroyer);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("gludio_prison_keeper_q0512_03.htm") || event.equalsIgnoreCase("gludio_prison_keeper_q0512_05.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("exit"))
		{
			st.exitCurrentQuest(true);
			return null;
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if(!checkCastleOwner(st.getPlayer()))
		{
			st.exitCurrentQuest(true);
			return "gludio_prison_keeper_q0512_01a.htm";
		}
		if(st.getState() == CREATED)
			return "gludio_prison_keeper_q0512_01.htm";
		if(st.getQuestItemsCount(FragmentOfTheDungeonLeaderMark) > 0)
		{
			st.giveItems(KnightsEpaulette, st.getQuestItemsCount(FragmentOfTheDungeonLeaderMark));
			st.takeItems(FragmentOfTheDungeonLeaderMark, -1);
			st.playSound(SOUND_FINISH);
			return "gludio_prison_keeper_q0512_08.htm";
		}
		return "gludio_prison_keeper_q0512_09.htm";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		Reflection r = st.getPlayer().getReflection();
		if(!r.isDefault() && r == npc.getReflection() && ArrayUtils.contains(rewardBosses, npc.getNpcId()))
		{
			st.giveItems(FragmentOfTheDungeonLeaderMark, RewardMarksCount / r.getPlayerCount(), true);
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}

	private boolean checkCastleOwner(Player player)
	{
		Castle castle = ResidenceHolder.getInstance().getResidenceByObject(Castle.class, player);
		if(castle == null)
			return false;
		Clan clan = player.getClan();
		return clan != null && clan.getClanId() == castle.getOwnerId();
	}

}