package npc.model.monasteryofsilence;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import events.MonasteryOfSilenceMiniGameEvent;

/**
 * @author VISTALL
 * @date 16:51/30.04.2012
 */
public class SumielInstance extends NpcInstance
{
	public SumielInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		MonasteryOfSilenceMiniGameEvent event = getEvent(MonasteryOfSilenceMiniGameEvent.class);

		if(command.equals("start"))
		{
			if(event.isInProgress())
			{
				if(player != event.getPlayer())
				{
					showChatWindow(player, "Town/Goddard/MonasteryOfSilence/minigame_instructor004.htm");
					return;
				}

				if(event.isFailed())
				{
					if(event.getFailCount() == 1)
						showChatWindow(player, "Town/Goddard/MonasteryOfSilence/minigame_instructor002.htm");
					else
						showChatWindow(player, "Town/Goddard/MonasteryOfSilence/minigame_instructor003.htm");

					return;
				}
			}

			if(event.getLastWinTime() > System.currentTimeMillis())
			{
				showChatWindow(player, "Town/Goddard/MonasteryOfSilence/minigame_instructor008.htm");
				return;
			}

			if(ItemFunctions.getItemCount(player, MonasteryOfSilenceMiniGameEvent.NEED_ITEM_ID) == 0)
			{
				showChatWindow(player, "Town/Goddard/MonasteryOfSilence/minigame_instructor005.htm");
				return;
			}

			event.setPlayer(player);
			event.reCalcNextTime(false);
		}
		else if(command.equals("restart"))
		{
			if(!event.isInProgress() || event.getPlayer() != player || event.getLastWinTime() > System.currentTimeMillis())
				return;

			event.restart();
		}
		else if(command.equals("outLoc"))
			player.teleToLocation(Location.parseLoc(getParameter("outLoc", StringUtils.EMPTY)));
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		MonasteryOfSilenceMiniGameEvent event = getEvent(MonasteryOfSilenceMiniGameEvent.class);
		if(event.isInProgress())
		{
			if(player != event.getPlayer())
				showChatWindow(player, "Town/Goddard/MonasteryOfSilence/minigame_instructor004.htm");
			else
			{
				if(event.isFailed())
				{
					if(event.getFailCount() == 1)
						showChatWindow(player, "Town/Goddard/MonasteryOfSilence/minigame_instructor002.htm");
					else
						showChatWindow(player, "Town/Goddard/MonasteryOfSilence/minigame_instructor003.htm");
				}
				else
					showChatWindow(player, "Town/Goddard/MonasteryOfSilence/minigame_instructor007.htm");
			}
		}
		else
		{
			if(event.getLastWinTime() > System.currentTimeMillis())
				showChatWindow(player, "Town/Goddard/MonasteryOfSilence/minigame_instructor008.htm");
			else
				showChatWindow(player, "Town/Goddard/MonasteryOfSilence/minigame_instructor001.htm");
		}
	}
}
