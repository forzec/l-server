package npc.model;

import org.mmocore.gameserver.model.CommandChannel;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import bosses.ValakasManager;

/**
 * @author pchayka
 */

public final class ValakasGatekeeperInstance extends NpcInstance
{
	private static final int FLOATING_STONE = 7267;
	private static final Location TELEPORT_POSITION1 = new Location(183831, -115457, -3296);
	private static final Location TELEPORT_POSITION2 = new Location(203940, -111840, 66);

	public ValakasGatekeeperInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("request_passage"))
		{
			if(ItemFunctions.getItemCount(player, FLOATING_STONE) < 1)
			{
				showChatWindow(player, 3);
				return;
			}
			showChatWindow(player, 4);
		}
		else if(command.equalsIgnoreCase("request_passage2"))
		{
			player.teleToLocation(TELEPORT_POSITION1);
		}
		else if(command.equalsIgnoreCase("request_valakas"))
		{
			CommandChannel cc = null;
			if(player.isInParty() && player.getParty().isInCommandChannel())
				cc = player.getParty().getCommandChannel();
			boolean isLeader = cc != null && player == cc.getChannelLeader();
			switch(ValakasManager.checkNestEntranceCond(isLeader ? cc.getMemberCount() : 1))
			{
				case NOT_AVAILABLE:
					showChatWindow(player, 3);
					break;
				case ALREADY_ATTACKED:
					showChatWindow(player, 2);
					break;
				case LIMIT_EXCEEDED:
					showChatWindow(player, 1);
					break;
				case ALLOW:
					// if player is CC leader - use CC teleport and count check, otherwise - player alone.
					if(isLeader)
					{
						for(Player member : cc)
							if(member.isInRange(this, 500) && !member.isCursedWeaponEquipped())
								member.teleToLocation(TELEPORT_POSITION2);
					}
					else
						player.teleToLocation(TELEPORT_POSITION2);

					//notify Manager
					ValakasManager.notifyEntrance();
					break;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}