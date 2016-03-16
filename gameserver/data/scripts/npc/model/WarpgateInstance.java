package npc.model;

import org.mmocore.gameserver.instancemanager.HellboundManager;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author pchayka
 */
public class WarpgateInstance extends NpcInstance
{
	public WarpgateInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("enter_hellbound"))
		{
			if(HellboundManager.getHellboundLevel() != 0 && (player.isQuestCompleted(130) || player.isQuestCompleted(133)))
				player.teleToLocation(-11272, 236464, -3248);
			else
				showChatWindow(player, "default/32318-1.htm");
		}
		else
			super.onBypassFeedback(player, command);
	}
}