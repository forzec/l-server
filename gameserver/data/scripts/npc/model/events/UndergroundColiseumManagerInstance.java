package npc.model.events;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.mmocore.commons.collections.CollectionUtils;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.Party;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.UndergroundColiseumEvent;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 15:40/12.07.2011
 */
public class UndergroundColiseumManagerInstance extends UndergroundColiseumHelperInstance
{
	private String _startHtm;

	public UndergroundColiseumManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);

		_startHtm = getParameter("start_htm", StringUtils.EMPTY);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		UndergroundColiseumEvent coliseumEvent = getEvent(UndergroundColiseumEvent.class);
		if(coliseumEvent == null)
			return;

		List<Player> leaders = coliseumEvent.getObjects(UndergroundColiseumEvent.REGISTERED_LEADERS);

		if(command.equals("register"))
		{
			Party party = player.getParty();
			if(party == null)
				showChatWindow(player, "events/kerthang_manager008.htm");
			else if(party.getPartyLeader() != player)
				showChatWindow(player, "events/kerthang_manager004.htm");
			else if(party.getMemberCount() < UndergroundColiseumEvent.PARTY_SIZE)
				showChatWindow(player, "events/kerthang_manager010.htm");
			else
			{
				for(int i = 3; i <= 7; i++)
				{
					UndergroundColiseumEvent $event = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, i);
					if($event == null)
						continue;

					List<Player> $leaders = coliseumEvent.getObjects(UndergroundColiseumEvent.REGISTERED_LEADERS);

					for(Player object : $leaders)
						if(object == player)
						{
							showChatWindow(player, "events/kerthang_manager009.htm");
							return;
						}
				}

				for(Player $player : party)
				{
					if($player.getEffectList().getEffectsBySkillId(5661) != null)
					{
						showChatWindow(player, "events/kerthang_manager021.htm", "%name%", $player.getName());
						return;
					}

					if($player.getLevel() < coliseumEvent.getMinLevel() || $player.getLevel() > coliseumEvent.getMaxLevel())
					{
						showChatWindow(player, "events/kerthang_manager011.htm", "%name%", $player.getName());
						return;
					}

					if($player.getDistance(this) > 400)
					{
						showChatWindow(player, "events/kerthang_manager012.htm");
						return;
					}
				}

				if(leaders.size() >= 5)
				{
					showChatWindow(player, "events/kerthang_manager013.htm");
					return;
				}

				coliseumEvent.addObject(UndergroundColiseumEvent.REGISTERED_LEADERS, player);

				showChatWindow(player, "events/kerthang_manager014.htm");
			}
		}
		else if(command.equals("viewMostWins"))
		{
			Pair<String, Integer> mostWin = coliseumEvent.getTopWinner();
			if(mostWin == null)
				showChatWindow(player, "events/kerthang_manager020.htm");
			else
				showChatWindow(player, "events/kerthang_manager019.htm", "%name%", mostWin.getKey(), "%count%", mostWin.getValue());
		}
		else if(command.equals("cancel"))
		{
			Party party = player.getParty();
			if(party == null)
				showChatWindow(player, "events/kerthang_manager008.htm");
			else if(party.getPartyLeader() != player)
				showChatWindow(player, "events/kerthang_manager004.htm");
			else
			{
				for(Player temp : leaders)
					if(temp == player)
					{
						leaders.remove(player);

						showChatWindow(player, "events/kerthang_manager005.htm");
						return;
					}

				showChatWindow(player, "events/kerthang_manager006.htm");
			}
		}
		else if(command.equals("viewTeams"))
		{
			HtmlMessage msg = new HtmlMessage(this);
			msg.setFile("events/kerthang_manager003.htm");
			for(int i = 0; i < UndergroundColiseumEvent.REGISTER_COUNT; i++)
			{
				Player team = CollectionUtils.safeGet(leaders, i);

				msg.replace("%team" + i + "%", team == null ? StringUtils.EMPTY : team.getName());
			}

			player.sendPacket(msg);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... ar)
	{
		showChatWindow(player, _startHtm);
	}
}
