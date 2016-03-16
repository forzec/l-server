package handler.items;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.items.ItemInstance;

public class NevitVoice extends SimpleItemHandler
{
	private static final int[] ITEM_IDS = new int[] { 17094 };

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}

	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		int itemId = item.getItemId();

		if(!useItem(player, item, 1))
			return false;

		switch(itemId)
		{
			case 17094:
				player.addRecomHave(10);
				break;
			default:
				return false;
		}

		return true;
	}
}
