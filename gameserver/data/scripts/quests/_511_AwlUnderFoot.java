package quests;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _511_AwlUnderFoot extends Quest
{
	private final static int DungeonLeaderMark = 9797;
	private final static int RewardMarksCount = 1256;
	private final static int KnightsEpaulette = 9912;

	private static final int BrandTheExile = 25589;
	private static final int CommanderKoenig = 25592;
	private static final int GergTheHunter = 25593;

	private static final int[] rewardBosses = new int[] { BrandTheExile, CommanderKoenig, GergTheHunter };

	public _511_AwlUnderFoot()
	{
		super(PARTY_ALL);

		// Detention Camp Wardens
		addStartNpc(35666, 35698, 35735, 35767, 35804, 35835, 35867, 35904, 35936, 35974, 36011, 36043, 36081, 36118, 36149, 36181, 36219, 36257, 36294, 36326, 36364);
		addQuestItem(DungeonLeaderMark);
		addKillId(BrandTheExile, CommanderKoenig, GergTheHunter);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("gludio_fort_a_campkeeper_q0511_03.htm") || event.equalsIgnoreCase("gludio_fort_a_campkeeper_q0511_06.htm"))
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
		if(!checkFortressOwner(st.getPlayer()))
		{
			st.exitCurrentQuest(true);
			return "gludio_fort_a_campkeeper_q0511_01a.htm";
		}
		if(st.getState() == CREATED)
			return "gludio_fort_a_campkeeper_q0511_01.htm";
		if(st.getQuestItemsCount(DungeonLeaderMark) > 0)
		{
			st.giveItems(KnightsEpaulette, st.getQuestItemsCount(DungeonLeaderMark));
			st.takeItems(DungeonLeaderMark, -1);
			st.playSound(SOUND_FINISH);
			return "gludio_fort_a_campkeeper_q0511_09.htm";
		}
		return "gludio_fort_a_campkeeper_q0511_10.htm";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		Reflection r = st.getPlayer().getReflection();
		if(!r.isDefault() && r == npc.getReflection() && ArrayUtils.contains(rewardBosses, npc.getNpcId()))
		{
			st.giveItems(DungeonLeaderMark, RewardMarksCount / r.getPlayerCount(), true);
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}

	private boolean checkFortressOwner(Player player)
	{
		Fortress fort = ResidenceHolder.getInstance().getResidenceByObject(Fortress.class, player);
		if(fort == null)
			return false;
		Clan clan = player.getClan();
		return clan != null && clan.getClanId() == fort.getOwnerId();
	}
}