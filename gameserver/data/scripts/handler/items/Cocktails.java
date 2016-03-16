package handler.items;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillUse;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.tables.SkillTable;

public class Cocktails extends SimpleItemHandler
{
	private static final int[] ITEM_IDS = new int[] { 10178, 15356, 20393, 10179, 15357, 20394, 14739, 21726 };

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}

	// Sweet Fruit Cocktail
	private static final int[] sweet_list = { 2404, // Might
			2405, // Shield
			2406, // Wind Walk
			2407, // Focus
			2408, // Death Whisper
			2409, // Guidance
			2410, // Bless Shield
			2411, // Bless Body
			2412, // Haste
			2413, // Vampiric Rage
	};

	// Fresh Fruit Cocktail
	private static final int[] fresh_list = { 2414, // Berserker Spirit
			2411, // Bless Body
			2415, // Magic Barrier
			2405, // Shield
			2406, // Wind Walk
			2416, // Bless Soul
			2417, // Empower
			2418, // Acumen
			2419, // Clarity
	};

	//Event - Fresh Milk
	private static final int[] milk_list = { 2873, 2874, 2875, 2876, 2877, 2878, 2879, 2885, 2886, 2887, 2888, 2889, 2890, };

	// Angel Cat's Blessing
	private static final int[] angel_blessing_list = { 22256, 22257, 22258, 22259, 22260, 22261, 22262, 22263, 22264, 22265, 22266, 22267, 22268, 22253};

	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		int itemId = item.getItemId();

		if(player.isInOlympiadMode())
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
			return false;
		}

		if(!useItem(player, item, 1))
			return false;

		switch(itemId)
		{
			// Sweet Fruit Cocktail
			case 10178:
			case 15356:
			case 20393:
				for(int skill : sweet_list)
				{
					player.broadcastPacket(new MagicSkillUse(player, player, skill, 1, 0, 0));
					player.altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(skill, 1));
				}
				break;
			// Fresh Fruit Cocktail				
			case 10179:
			case 15357:
			case 20394:
				for(int skill : fresh_list)
				{
					player.broadcastPacket(new MagicSkillUse(player, player, skill, 1, 0, 0));
					player.altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(skill, 1));
				}
				break;
			//Event - Fresh Milk				
			case 14739:
				player.broadcastPacket(new MagicSkillUse(player, player, 2873, 1, 0, 0));
				player.altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(2891, 6));
				for(int skill : milk_list)
				{
					player.broadcastPacket(new MagicSkillUse(player, player, skill, 1, 0, 0));
					player.altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(skill, 1));
				}
				break;
			case 21726:
				player.broadcastPacket(new MagicSkillUse(player, player, 2873, 1, 0, 0));
				player.altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(22269, 6));
				for(int skill : angel_blessing_list)
				{
					player.broadcastPacket(new MagicSkillUse(player, player, skill, 1, 0, 0));
					player.altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(skill, 1));
				}
				break;
			default:
				return false;
		}

		return true;
	}
}