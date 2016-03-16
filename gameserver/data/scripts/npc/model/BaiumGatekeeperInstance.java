package npc.model;

import org.mmocore.gameserver.model.GameObjectsStorage;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;

import bosses.BaiumManager;

/**
 * @author pchayka
 */

public final class BaiumGatekeeperInstance extends NpcInstance
{
	private static final int Baium = 29020;
	private static final int BaiumNpc = 29025;
	private static final int BloodedFabric = 4295;
	private static final Location TELEPORT_POSITION = new Location(114077, 15882, 10078);

	public BaiumGatekeeperInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("request_entrance"))
		{
			if(ItemFunctions.getItemCount(player, BloodedFabric) > 0)
			{
				NpcInstance baiumBoss = GameObjectsStorage.getByNpcId(Baium);
				if(baiumBoss != null)
				{
					showChatWindow(player, "default/31862-1.htm");
					return;
				}
				NpcInstance baiumNpc = GameObjectsStorage.getByNpcId(BaiumNpc);
				if(baiumNpc == null)
				{
					showChatWindow(player, "default/31862-2.htm");
					return;
				}
				ItemFunctions.deleteItem(player, BloodedFabric, 1);
				player.setVar("baiumPermission", "granted", -1);
				player.teleToLocation(TELEPORT_POSITION);
			}
			else
			{
				showChatWindow(player, "default/31862-3.htm");
			}
		}
		else if(command.startsWith("request_wakeup"))
		{
			if(player.getVar("baiumPermission") == null || !player.getVar("baiumPermission").equalsIgnoreCase("granted"))
			{
				showChatWindow(player, "default/29025-1.htm");
				return;
			}
			if(isBusy())
			{
				showChatWindow(player, "default/29025-2.htm");
			}
			setBusy(true);
			ChatUtils.shout(this, NpcString.HOW_DARE_YOU_WAKE_ME__NOW_YOU_SHALL_DIE);
			BaiumManager.spawnBaium(this, player);
		}
		else
			super.onBypassFeedback(player, command);
	}
}