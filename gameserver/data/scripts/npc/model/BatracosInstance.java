package npc.model;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public final class BatracosInstance extends NpcInstance
{
	private static final int urogosIzId = 505;

	public BatracosInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if(val == 0)
		{
			String htmlpath = null;
			if(getReflection().isDefault())
				htmlpath = "default/32740.htm";
			else
				htmlpath = "default/32740-4.htm";
			player.sendPacket(new HtmlMessage(this, htmlpath));
		}
		else
			super.showChatWindow(player, val);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("request_seer"))
		{
			ReflectionUtils.simpleEnterInstancedZone(player, urogosIzId);
		}
		else if(command.equalsIgnoreCase("leave"))
		{
			if(!getReflection().isDefault())
				getReflection().collapse();
		}
		else
			super.onBypassFeedback(player, command);
	}
}