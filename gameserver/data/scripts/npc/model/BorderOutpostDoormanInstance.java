package npc.model;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.model.instances.GuardInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author VISTALL
 * @date 10:26/24.06.2011
 */
public class BorderOutpostDoormanInstance extends GuardInstance
{
	public BorderOutpostDoormanInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equals("openDoor"))
		{
			DoorInstance door = ReflectionUtils.getDoor(24170001);
			door.openMe();
		}
		else if(command.equals("closeDoor"))
		{
			DoorInstance door = ReflectionUtils.getDoor(24170001);
			door.closeMe();
		}
		else
			super.onBypassFeedback(player, command);
	}
}
