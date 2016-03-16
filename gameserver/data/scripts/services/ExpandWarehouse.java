package services;

import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

public class ExpandWarehouse
{
	@Bypass("services.ExpandWarehouse:get")
	public void get(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null)
			return;

		if(!Config.SERVICES_EXPAND_WAREHOUSE_ENABLED)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
			return;
		}

		if(ItemFunctions.deleteItem(player, Config.SERVICES_EXPAND_WAREHOUSE_ITEM, Config.SERVICES_EXPAND_WAREHOUSE_PRICE))
		{
			player.setExpandWarehouse(player.getExpandWarehouse() + 1);
			player.setVar("ExpandWarehouse", String.valueOf(player.getExpandWarehouse()), -1);
		}
		else if(Config.SERVICES_EXPAND_WAREHOUSE_ITEM == ItemTemplate.ITEM_ID_ADENA)
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);

		show(player, npc, arg);
	}

	@Bypass("services.ExpandWarehouse:show")
	public void show(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null)
			return;

		if(!Config.SERVICES_EXPAND_WAREHOUSE_ENABLED)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
			return;
		}

		HtmlMessage msg = new HtmlMessage(5).setFile("scripts/services/expand_warehouse.htm");
		msg.replace("%wh_limit%", String.valueOf(player.getWarehouseLimit()));
		msg.replace("%wh_exp_price%", String.valueOf(Config.SERVICES_EXPAND_WAREHOUSE_PRICE));
		msg.replace("%wh_exp_item%", String.valueOf(Config.SERVICES_EXPAND_WAREHOUSE_ITEM));

		player.sendPacket(msg);
	}
}