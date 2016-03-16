package handler.items;

import org.mmocore.gameserver.model.CommandChannel;
import org.mmocore.gameserver.model.GameObject;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.items.ItemInstance;

/**
 * @author VISTALL
 * @date 23:28/18.04.2012
 */
public class StrategyGuide extends ScriptItemHandler
{

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(!playable.isPlayer())
			return false;

		GameObject gameObject = playable.getTarget();

		Player player = CommandChannel.checkAndAskToCreateChannel((Player)playable, (gameObject == null || !gameObject.isPlayer()) ? null : gameObject.getPlayer(), true);
		return player != null;
	}

	@Override
	public int[] getItemIds()
	{
		return new int[] {CommandChannel.STRATEGY_GUIDE_ID};
	}
}
