package handler.items;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.Dice;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;

public class RollingDice extends ScriptItemHandler
{
	// all the items ids that this handler knowns
	private static final int[] _itemIds = { 4625, 4626, 4627, 4628 };

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;
		Player player = (Player) playable;

		int itemId = item.getItemId();

		if(player.isInOlympiadMode())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_USE_THAT_ITEM_IN_A_GRAND_OLYMPIAD_MATCH);
			return false;
		}

		if(player.isSitting())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
			return false;
		}

		int number = Rnd.get(1, 6);
		if(number == 0)
		{
			player.sendPacket(SystemMsg.YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIME_TRY_AGAIN_LATER);
			return false;
		}

		player.broadcastPacket(new Dice(player.getObjectId(), itemId, number, player.getX() - 30, player.getY() - 30, player.getZ()), new SystemMessage(SystemMsg.C1_HAS_ROLLED_A_S2).addName(player).addNumber(number));

		return true;
	}


	@Override
	public final int[] getItemIds()
	{
		return _itemIds;
	}
}