package handler.admincommands;

import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.GameObject;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import events.FunEvent;

/**
 * @author VISTALL
 * @date 18:45/07.06.2011
 */
public class AdminEvent extends ScriptAdminCommand
{
	enum Commands
	{
		admin_list_events,
		admin_start_event,
		admin_stop_event
	}
	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands c = (Commands)comm;
		switch(c)
		{
			case admin_start_event:
			case admin_stop_event:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE://" + wordList[0] + " ID");
					return false;
				}
				try
				{
					int id = Integer.parseInt(wordList[1]);

					FunEvent event = EventHolder.getInstance().getEvent(EventType.FUN_EVENT, id);
					if(comm == Commands.admin_start_event)
						event.startEvent();
					else
						event.forceStopEvent();
				}
				catch(NumberFormatException e)
				{
					activeChar.sendMessage("USAGE://" + wordList[0] + " ID");
					return false;
				}
				break;
			case admin_list_events:
				GameObject object = activeChar.getTarget();
				if(object == null)
					activeChar.sendPacket(SystemMsg.INVALID_TARGET);
				else
				{
					for(Event e : object.getEvents())
						activeChar.sendMessage("- " + e.toString());
				}
				break;
		}
		return false;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
