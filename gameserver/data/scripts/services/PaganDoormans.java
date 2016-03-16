package services;

import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * Используется в локации Pagan Temple
 * @author SYS
 */
public class PaganDoormans
{
	private static final int MainDoorId = 19160001;
	private static final int SecondDoor1Id = 19160011;
	private static final int SecondDoor2Id = 19160010;

	@Bypass("services.PaganDoormans:openMainDoor")
	public void openMainDoor(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null || npc == null)
			return;

		if(!NpcInstance.canBypassCheck(player, npc))
			return;

		if(ItemFunctions.getItemCount(player, 8064) == 0 && ItemFunctions.getItemCount(player, 8067) == 0)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
			return;
		}

		openDoor(MainDoorId);
		Functions.show("default/32034-1.htm", player, npc);
	}

	@Bypass("services.PaganDoormans:openSecondDoor")
	public void openSecondDoor(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null || npc == null)
			return;

		if(!NpcInstance.canBypassCheck(player, npc))
			return;

		if(ItemFunctions.getItemCount(player, 8067) == 0)
		{
			Functions.show("default/32036-2.htm", player, npc);
			return;
		}

		openDoor(SecondDoor1Id);
		openDoor(SecondDoor2Id);
		Functions.show("default/32036-1.htm", player, npc);
	}

	@Bypass("services.PaganDoormans:pressSkull")
	public void pressSkull(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null || npc == null)
			return;

		if(!NpcInstance.canBypassCheck(player, npc))
			return;

		openDoor(MainDoorId);
		Functions.show("default/32035-1.htm", player, npc);
	}

	@Bypass("services.PaganDoormans:press2ndSkull")
	public void press2ndSkull(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null || npc == null)
			return;

		if(!NpcInstance.canBypassCheck(player, npc))
			return;

		openDoor(SecondDoor1Id);
		openDoor(SecondDoor2Id);
		Functions.show("default/32037-1.htm", player, npc);
	}

	private static void openDoor(int doorId)
	{
		DoorInstance door = ReflectionUtils.getDoor(doorId);
		door.openMe();
	}
}