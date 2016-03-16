package npc.model;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.model.CommandChannel;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import bosses.AntharasManager;

/**
 * @author pchayka
 */

public final class HeartOfWardingInstance extends NpcInstance
{
	public HeartOfWardingInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("enter_lair"))
		{
			// Has an item (leader only)
			if(ItemFunctions.getItemCount(player, 3865) < 1)
			{
				showChatWindow(player, 4);
				return;
			}

			CommandChannel cc = null;
			if(player.isInParty() && player.getParty().isInCommandChannel())
				cc = player.getParty().getCommandChannel();
			// If in CC, is CC leader
			if(cc != null && player != cc.getChannelLeader())
			{
				showChatWindow(player, 5);
				return;
			}
			switch(AntharasManager.checkNestEntranceCond(cc != null ? cc.getMemberCount() : 1))
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
					if(cc != null)
					{
						for(Player member : cc)
							if(member.isInRange(this, 1000) && !member.isTerritoryFlagEquipped() && !member.isCursedWeaponEquipped())
								member.teleToLocation(179700 + Rnd.get(200), 113800 + Rnd.get(500), -7709);
					}
					else
						player.teleToLocation(179700 + Rnd.get(700), 113800 + Rnd.get(2100), -7709);

					//notify Manager
					AntharasManager.notifyEntrance();
					break;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}