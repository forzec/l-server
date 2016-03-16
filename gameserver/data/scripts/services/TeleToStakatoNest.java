package services;

import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Party;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.utils.Location;
import quests._240_ImTheOnlyOneYouCanTrust;

public class TeleToStakatoNest
{
	private final static Location[] teleports =
	{
			new Location(80456, -52322, -5640),
			new Location(88718, -46214, -4640),
			new Location(87464, -54221, -5120),
			new Location(80848, -49426, -5128),
			new Location(87682, -43291, -4128)
	};

	@Bypass("services.TeleToStakatoNest:list")
	public void list(Player player, NpcInstance npc, String[] args)
	{
		if(player == null || npc == null)
			return;

		QuestState qs = player.getQuestState(240);
		if(qs == null || !qs.isCompleted())
		{
			Functions.show("scripts/services/TeleToStakatoNest-no.htm", player, npc);
			return;
		}

		Functions.show("scripts/services/TeleToStakatoNest.htm", player, npc);
	}

	@Bypass("services.TeleToStakatoNest:teleTo")
	public void teleTo(Player player, NpcInstance npc, String[] args)
	{
		if(player == null || npc == null)
			return;
		if(args.length != 1)
			return;

		Location loc = teleports[Integer.parseInt(args[0]) - 1];
		Party party = player.getParty();
		if(party == null)
			player.teleToLocation(loc);
		else
			for(Player member : party.getPartyMembers())
				if(member != null && member.isInRange(player, 1000))
					member.teleToLocation(loc);
	}
}