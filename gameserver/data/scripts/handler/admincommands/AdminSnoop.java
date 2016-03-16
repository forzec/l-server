package handler.admincommands;

import org.mmocore.commons.listener.Listener;
import org.mmocore.gameserver.listener.actor.player.impl.SnoopPlayerSayListener;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.GameObjectsStorage;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.network.l2.components.SystemMsg;

/**
 * @author VISTALL
 * @date 20:49/15.09.2011
 */
public class AdminSnoop extends ScriptAdminCommand
{
	enum Commands
	{
		admin_snoop
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player player)
	{
		if(!player.getPlayerAccess().CanSnoop)
			return false;
		if(wordList.length != 3 || (!wordList[2].equalsIgnoreCase("on") && !wordList[2].equalsIgnoreCase("off")))
		{
			player.sendMessage("USAGE: //snoop [TARGET_NAME] [on|off]");
			return false;
		}

		boolean on = wordList[2].equalsIgnoreCase("on");
		if(on)
		{
			String currentSnoop = player.getVar(Player.SNOOP_TARGET);
			if(currentSnoop == null)
			{
				Player target = GameObjectsStorage.getPlayer(wordList[1]);
				if(target == null)
				{
					player.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
					return false;
				}

				target.addListener(new SnoopPlayerSayListener(player));
				player.getVars().set(Player.SNOOP_TARGET, wordList[1]);

				player.sendMessage("SNOOP: you snoop target: " + wordList[1]);
				return true;
			}
			else
				player.sendMessage("SNOOP: you already snooped target: " + currentSnoop);
		}
		else
		{
			String currentSnoop = (String)player.getVars().remove(Player.SNOOP_TARGET);
			if(currentSnoop == null)
				player.sendMessage("SNOOP: you not snoop any target");
			else
			{
				Player target = GameObjectsStorage.getPlayer(currentSnoop);
				if(target == null)
					player.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
				else
					for(Listener<Creature> $listener : target.getListeners().getListeners())
					{
						if($listener instanceof SnoopPlayerSayListener)
						{
							SnoopPlayerSayListener listener = (SnoopPlayerSayListener)$listener;

							if(listener.getOwner() == player)
							{
								target.removeListener($listener);
								break;
							}
						}
					}
				player.sendMessage("SNOOP: you cancel snoop for target: " + currentSnoop);
			}
		}

		return false;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
