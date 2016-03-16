package services;

import org.mmocore.gameserver.data.xml.holder.ItemHolder;
import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.items.Inventory;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.multisell.MultiSellEntry;
import org.mmocore.gameserver.templates.multisell.MultiSellIngredient;
import org.mmocore.gameserver.templates.multisell.MultiSellListContainer;

public class Pushkin
{
	@Bypass("services.Pushkin:doCrystallize")
	public void doCrystallize(Player player, NpcInstance npc, String[] arg)
	{
		NpcInstance merchant = npc;
		Castle castle = merchant != null ? merchant.getCastle(player) : null;

		MultiSellListContainer list = new MultiSellListContainer();
		list.setShowAll(false);
		list.setKeepEnchant(true);
		list.setNoTax(false);
		int entry = 0;
		final Inventory inv = player.getInventory();
		for(final ItemInstance itm : inv.getItems())
			if(itm.canBeCrystallized(player))
			{
				final ItemTemplate crystal = ItemHolder.getInstance().getTemplate(itm.getTemplate().getCrystalType().cry);
				final int crystalCount = itm.getTemplate().getCrystalCount(itm.getEnchantLevel(), false);
				MultiSellEntry possibleEntry = new MultiSellEntry(++entry, crystal.getItemId(), crystalCount, 0);
				possibleEntry.addIngredient(new MultiSellIngredient(itm.getItemId(), 1, itm.getEnchantLevel()));
				possibleEntry.addIngredient(new MultiSellIngredient(ItemTemplate.ITEM_ID_ADENA, Math.round(crystalCount * crystal.getReferencePrice() * 0.05), 0));
				list.addEntry(possibleEntry);
			}

		MultiSellHolder.getInstance().SeparateAndSend(list, player, merchant != null ? merchant.getObjectId() : -1, castle == null ? 0. : castle.getTaxRate());
	}
}