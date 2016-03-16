package services;

import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.utils.ItemFunctions;

public class NickColor
{
	@Bypass("services.NickColor:list")
	public void list(Player player, NpcInstance npc, String[] arg)
	{
		if(!Config.SERVICES_CHANGE_NICK_COLOR_ENABLED)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
			return;
		}

		HtmlMessage msg = new HtmlMessage(5).setFile("scripts/services/change_nick_color.htm");
		msg.replace("%item_id%", String.valueOf(Config.SERVICES_CHANGE_NICK_COLOR_ITEM));
		msg.replace("%item_count%", String.valueOf(Config.SERVICES_CHANGE_NICK_COLOR_PRICE));

		StringBuilder sb = new StringBuilder();
		for(String color : Config.SERVICES_CHANGE_NICK_COLOR_LIST)
			sb.append("<br><a action=\"bypass -h htmbypass_services.NickColor:change ").append(color).append("\"><font color=\"").append(color.substring(4, 6) + color.substring(2, 4) + color.substring(0, 2)).append("\">").append(color.substring(4, 6) + color.substring(2, 4) + color.substring(0, 2)).append("</font></a>");

		msg.replace("%list%", sb.toString());
		player.sendPacket(msg);
	}

	@Bypass("services.NickColor:change")
	public void change(Player player, NpcInstance npc, String[] arg)
	{
		if(arg == null || arg.length < 1)
			return;

		if(!Config.SERVICES_CHANGE_NICK_COLOR_ENABLED)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
			return;
		}

		if(arg[0].equalsIgnoreCase("FFFFFF"))
		{
			player.setNameColor(Integer.decode("0xFFFFFF"));
			player.broadcastUserInfo(true);
			return;
		}

		if(ItemFunctions.deleteItem(player, Config.SERVICES_CHANGE_NICK_COLOR_ITEM, Config.SERVICES_CHANGE_NICK_COLOR_PRICE))
		{
			player.setNameColor(Integer.decode("0x" + arg[0]));
			player.broadcastUserInfo(true);
		}
		else if(Config.SERVICES_CHANGE_NICK_COLOR_ITEM == 57)
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
	}
}