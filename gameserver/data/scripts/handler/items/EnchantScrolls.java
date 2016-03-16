package handler.items;

import org.mmocore.gameserver.data.xml.holder.EnchantItemHolder;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.s2c.ChooseInventoryItem;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;
import handler.items.ScriptItemHandler;

public class EnchantScrolls extends ScriptItemHandler
{
	private static final int[] _itemIds = {729,
			730,
			731,
			732,
			947,
			948,
			949,
			950,
			951,
			952,
			953,
			954,
			955,
			956,
			957,
			958,
			959,
			960,
			961,
			962,
			6569,
			6570,
			6571,
			6572,
			6573,
			6574,
			6575,
			6576,
			6577,
			6578,
			13540,
			22006,
			22007,
			22008,
			22009,
			22010,
			22011,
			22012,
			22013,
			22014,
			22015,
			22016,
			22017,
			22018,
			22019,
			22020,
			22021,
			20517,
			20518,
			20519,
			20520,
			20521,
			20522,
			21581,
			21582,
			22221,
			22222,
			22223,
			22224,
			22225,
			22226,
			22227,
			22228,
			22229,
			22230};

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;
		Player player = (Player) playable;

		if(player.getEnchantScroll() != null)
			return false;

		player.setEnchantScroll(item);
		player.sendPacket(new ChooseInventoryItem(item.getItemId()));
		return true;
	}

	@Override
	public final int[] getItemIds()
	{
		int[] enchantScrolls = EnchantItemHolder.getInstance().getEnchantScrolls();
		IntSet set = new HashIntSet(_itemIds.length + enchantScrolls.length);
		for(int i : _itemIds)
			set.add(i);
		for(int i : enchantScrolls)
			set.add(i);
		return set.toArray();
	}
}