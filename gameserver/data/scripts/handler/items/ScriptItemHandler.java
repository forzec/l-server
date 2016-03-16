package handler.items;

import org.mmocore.gameserver.handler.items.IItemHandler;
import org.mmocore.gameserver.handler.items.ItemHandler;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 21:09/12.07.2011
 */
public abstract class ScriptItemHandler implements OnInitScriptListener, IItemHandler
{
	@Override
	public boolean dropItem(Player player, ItemInstance item, long count, Location loc)
	{
		return true;
	}

	@Override
	public boolean pickupItem(Playable playable, ItemInstance item)
	{
		return true;
	}

	@Override
	public void onInit()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}
}
