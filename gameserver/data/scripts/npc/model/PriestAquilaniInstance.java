package npc.model;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;

/**
 * @author pchayka
 */
public class PriestAquilaniInstance extends NpcInstance
{

	public PriestAquilaniInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if(player.getQuestState(10288) != null && player.getQuestState(10288).isCompleted())
		{
			player.sendPacket(new HtmlMessage(this, "default/32780-1.htm"));
			return;
		}
		else
		{
			player.sendPacket(new HtmlMessage(this, "default/32780.htm"));
			return;
		}
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("teleport"))
		{
			player.teleToLocation(new Location(118833, -80589, -2688));
			return;
		}
		else
			super.onBypassFeedback(player, command);
	}
}