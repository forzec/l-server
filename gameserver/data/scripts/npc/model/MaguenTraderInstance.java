package npc.model;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * @author pchayka
 */
public final class MaguenTraderInstance extends NpcInstance
{
	public MaguenTraderInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("request_collector"))
		{
			if(ItemFunctions.getItemCount(player, 15487) > 0)
				showChatWindow(player, "default/32735-2.htm");
			else
				ItemFunctions.addItem(player, 15487, 1);
		}
		else if(command.equalsIgnoreCase("request_maguen"))
		{
			NpcUtils.spawnSingle(18839, Location.findPointToStay(getSpawnedLoc(), 40, 100, getGeoIndex()), getReflection()); // wild maguen
			showChatWindow(player, "default/32735-3.htm");
		}
		else
			super.onBypassFeedback(player, command);
	}
}