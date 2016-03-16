package npc.model.residences.clanhall;

import org.mmocore.gameserver.dao.SiegeClanDAO;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.events.impl.ClanHallMiniGameEvent;
import org.mmocore.gameserver.model.entity.events.objects.CMGSiegeClanObject;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.TimeUtils;

/**
 * @author VISTALL
 * @date 8:01/07.05.2011
 */
public class RainbowMessengerInstance extends NpcInstance
{
	public static final int ITEM_ID = 8034;

	public RainbowMessengerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(final Player player, final String command)
	{
		if(!canBypassCheck(player, this))
			return;

		ClanHall clanHall = getClanHall();
		ClanHallMiniGameEvent miniGameEvent = clanHall.getSiegeEvent();
		if(command.equalsIgnoreCase("register"))
		{
			if(miniGameEvent.isRegistrationOver())
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti014.htm");
				return;
			}

			Clan clan = player.getClan();
			if(clan == null || clan.getLevel() < 3 || clan.getAllSize() <= 5)
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti011.htm");
				return;
			}
			if(clan.isPlacedForDisband())
			{
				player.sendPacket(SystemMsg.YOU_HAVE_ALREADY_REQUESTED_THE_DISSOLUTION_OF_YOUR_CLAN);
				return;
			}
			if(clan.getLeaderId() != player.getObjectId())
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti010.htm");
				return;
			}
			if(clan.getHasHideout() > 0)
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti012.htm");
				return;
			}

			if(miniGameEvent.getSiegeClan(ClanHallMiniGameEvent.ATTACKERS, clan) != null)
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti013.htm");
				return;
			}

			long count = player.getInventory().getCountOf(ITEM_ID);
			if(count == 0)
				showChatWindow(player, "residence2/clanhall/messenger_yetti008.htm");
			else
			{
				if(!player.consumeItem(ITEM_ID, count))
					return;

				CMGSiegeClanObject siegeClanObject = new CMGSiegeClanObject(ClanHallMiniGameEvent.ATTACKERS, clan, count);
				miniGameEvent.addObject(ClanHallMiniGameEvent.ATTACKERS, siegeClanObject);
				SiegeClanDAO.getInstance().insert(clanHall, siegeClanObject);

				showChatWindow(player, "residence2/clanhall/messenger_yetti009.htm");
			}
		}
		else if(command.equalsIgnoreCase("cancel"))
		{
			if(miniGameEvent.isRegistrationOver())
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti017.htm");
				return;
			}

			Clan clan = player.getClan();
			if(clan == null || clan.getLevel() < 3)
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti011.htm");
				return;
			}
			if(clan.getLeaderId() != player.getObjectId())
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti010.htm");
				return;
			}

			SiegeClanObject siegeClanObject = miniGameEvent.getSiegeClan(ClanHallMiniGameEvent.ATTACKERS, clan);
			if(siegeClanObject == null)
				showChatWindow(player, "residence2/clanhall/messenger_yetti016.htm");
			else
			{
				miniGameEvent.removeObject(ClanHallMiniGameEvent.ATTACKERS, siegeClanObject);
				SiegeClanDAO.getInstance().delete(clanHall, siegeClanObject);

				ItemFunctions.addItem(player, ITEM_ID, siegeClanObject.getParam() / 2L);

				showChatWindow(player, "residence2/clanhall/messenger_yetti005.htm");
			}
		}
		else if(command.equalsIgnoreCase("refund"))
		{
			if(miniGameEvent.isRegistrationOver())
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti010.htm");
				return;
			}

			Clan clan = player.getClan();
			if(clan == null || clan.getLevel() < 3)
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti011.htm");
				return;
			}
			if(clan.getLeaderId() != player.getObjectId())
			{
				showChatWindow(player, "residence2/clanhall/messenger_yetti010.htm");
				return;
			}

			SiegeClanObject siegeClanObject = miniGameEvent.getSiegeClan(ClanHallMiniGameEvent.REFUND, clan);
			if(siegeClanObject == null)
				showChatWindow(player, "residence2/clanhall/messenger_yetti020.htm");
			else
			{
				miniGameEvent.removeObject(ClanHallMiniGameEvent.REFUND, siegeClanObject);
				SiegeClanDAO.getInstance().delete(clanHall, siegeClanObject);

				ItemFunctions.addItem(player, ITEM_ID, siegeClanObject.getParam());

				showChatWindow(player, "residence2/clanhall/messenger_yetti019.htm");
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		ClanHall clanHall = getClanHall();
		Clan clan = clanHall.getOwner();
		HtmlMessage msg = new HtmlMessage(this);
		if(clan != null)
		{
			msg.setFile("residence2/clanhall/messenger_yetti001.htm");
			msg.replace("%owner_name%", clan.getName());
		}
		else
			msg.setFile("residence2/clanhall/messenger_yetti001a.htm");
		msg.replace("%siege_date%", TimeUtils.toSimpleFormat(clanHall.getSiegeDate()));

		player.sendPacket(msg);
	}
}
