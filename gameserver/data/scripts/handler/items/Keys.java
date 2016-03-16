package handler.items;

import gnu.trove.TIntHashSet;

import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.data.xml.holder.DoorHolder;
import org.mmocore.gameserver.model.GameObject;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.components.CustomMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.templates.DoorTemplate;


public class Keys extends ScriptItemHandler
{
	private int[] _itemIds = null;

	public Keys()
	{
		TIntHashSet keys = new TIntHashSet();
		for(DoorTemplate door : DoorHolder.getInstance().getDoors().values())
			if(door != null && door.getKey() > 0)
				keys.add(door.getKey());
		_itemIds = keys.toArray();
	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;
		Player player = playable.getPlayer();
		GameObject target = player.getTarget();
		if(target == null || !target.isDoor())
		{
			player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}
		DoorInstance door = (DoorInstance) target;
		if(door.isOpen())
		{
			player.sendPacket(SystemMsg.IT_IS_NOT_LOCKED);
			return false;
		}
		if(door.getKey() <= 0 || item.getItemId() != door.getKey()) // ключ не подходит к двери
		{
			player.sendPacket(SystemMsg.THIS_DOOR_CANNOT_BE_UNLOCKED);
			return false;
		}
		if(player.getDistance(door) > 300)
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_BECAUSE_YOU_ARE_TOO_FAR);
			return false;
		}
		if(!player.getInventory().destroyItem(item, 1L))
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return false;
		}
		player.sendPacket(SystemMessage.removeItems(item.getItemId(), 1));
		player.sendMessage(new CustomMessage("l2p.gameserver.skills.skillclasses.Unlock.Success"));
		door.openMe(player, true);
		return true;
	}

	@Override
	public int[] getItemIds()
	{
		return _itemIds;
	}
}