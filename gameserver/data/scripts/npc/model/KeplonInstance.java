package npc.model;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author pchayka
 */

public final class KeplonInstance extends NpcInstance
{
	public KeplonInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(checkForDominionWard(player))
			return;

		if(command.equalsIgnoreCase("buygreen"))
		{
			if(ItemFunctions.deleteItem(player, 57, 10000))
			{
				ItemFunctions.addItem(player, 4401, 1);
				return;
			}
			else
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		}
		else if(command.startsWith("buyblue"))
		{
			if(ItemFunctions.deleteItem(player, 57, 10000))
			{
				ItemFunctions.addItem(player, 4402, 1);
				return;
			}
			else
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		}
		else if(command.startsWith("buyred"))
		{
			if(ItemFunctions.deleteItem(player, 57, 10000))
			{
				ItemFunctions.addItem(player, 4403, 1);
				return;
			}
			else
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}