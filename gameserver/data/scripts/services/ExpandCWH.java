package services;

import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

public class ExpandCWH
{
	@Bypass("services.ExpandCWH:get")
	public void get(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null)
			return;

		if(!Config.SERVICES_EXPAND_CWH_ENABLED)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
			return;
		}

		if(player.getClan() == null)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/expand_cwh_clanrestriction.htm"));
			return;
		}

		if(ItemFunctions.deleteItem(player, Config.SERVICES_EXPAND_CWH_ITEM, Config.SERVICES_EXPAND_CWH_PRICE))
		{
			player.getClan().setWhBonus(player.getClan().getWhBonus() + 1);
			player.sendMessage("Warehouse capacity is now " + (Config.WAREHOUSE_SLOTS_CLAN + player.getClan().getWhBonus()));
		}
		else if(Config.SERVICES_EXPAND_CWH_ITEM == ItemTemplate.ITEM_ID_ADENA)
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);

		show(player,  npc, arg);
	}

	@Bypass("services.ExpandCWH:show")
	public void show(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null)
			return;

		if(!Config.SERVICES_EXPAND_WAREHOUSE_ENABLED)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
			return;
		}

		if(player.getClan() == null)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/expand_cwh_clanrestriction.htm"));
			return;
		}

		HtmlMessage msg = new HtmlMessage(5).setFile("scripts/services/expand_cwh.htm");
		msg.replace("%cwh_cap_now%", String.valueOf(Config.WAREHOUSE_SLOTS_CLAN + player.getClan().getWhBonus()));
		msg.replace("%cwh_exp_price%", String.valueOf(Config.SERVICES_EXPAND_CWH_PRICE));
		msg.replace("%cwh_exp_item%", String.valueOf(Config.SERVICES_EXPAND_CWH_ITEM));

		player.sendPacket(msg);
	}
}