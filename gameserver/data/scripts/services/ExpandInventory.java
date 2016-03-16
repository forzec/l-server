package services;

import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

public class ExpandInventory
{
	@Bypass("services.ExpandInventory:get")
	public void get(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null)
			return;

		if(!Config.SERVICES_EXPAND_INVENTORY_ENABLED)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
			return;
		}

		if(player.getInventoryLimit() >= Config.SERVICES_EXPAND_INVENTORY_MAX)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/expand_inventory_max.htm"));
			return;
		}

		if(ItemFunctions.deleteItem(player, Config.SERVICES_EXPAND_INVENTORY_ITEM, Config.SERVICES_EXPAND_INVENTORY_PRICE))
		{
			player.setExpandInventory(player.getExpandInventory() + 1);
			player.setVar("ExpandInventory", String.valueOf(player.getExpandInventory()), -1);
		}
		else if(Config.SERVICES_EXPAND_INVENTORY_ITEM == ItemTemplate.ITEM_ID_ADENA)
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);

		show(player, npc, arg);
	}

	@Bypass("services.ExpandInventory:show")
	public void show(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null)
			return;

		if(!Config.SERVICES_EXPAND_INVENTORY_ENABLED)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
			return;
		}

		HtmlMessage msg = new HtmlMessage(5).setFile("scripts/services/expand_inventory.htm");
		msg.replace("%inven_cap_now%", String.valueOf(player.getInventoryLimit()));
		msg.replace("%inven_limit%", String.valueOf(Config.SERVICES_EXPAND_INVENTORY_MAX));
		msg.replace("%inven_exp_price%", String.valueOf(Config.SERVICES_EXPAND_INVENTORY_PRICE));
		msg.replace("%inven_exp_item%", String.valueOf(Config.SERVICES_EXPAND_INVENTORY_ITEM));

		player.sendPacket(msg);
	}
}