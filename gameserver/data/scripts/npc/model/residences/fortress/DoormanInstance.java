package npc.model.residences.fortress;

import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author VISTALL
 * @date 13:47/02.04.2011
 */
public class DoormanInstance extends npc.model.residences.DoormanInstance
{
	private static final long serialVersionUID = 5259832732646127200L;

	private Location _loc;

	public DoormanInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		String loc = template.getAIParams().getString("tele_loc", null);
		if(loc != null)
			_loc = Location.parseLoc(loc);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		int cond = getCond(player);
		switch(cond)
		{
			case COND_OWNER:
				if(command.equalsIgnoreCase("openDoors"))
					for(int i : _doors)
						ReflectionUtils.getDoor(i).openMe(player, true);
				else if (command.equalsIgnoreCase("closeDoors"))
					for(int i : _doors)
						ReflectionUtils.getDoor(i).closeMe(player, true);
				break;
			case COND_SIEGE:
				if(command.equalsIgnoreCase("tele"))
					player.teleToLocation(_loc);
				break;
			case COND_FAIL:
				player.sendPacket(new HtmlMessage(this).setFile(_failDialog));
				break;
		}
	}

	@Override
	protected int getCond(Player player)
	{
		final int result = super.getCond(player);
		if (result == COND_OWNER)
		{
			final DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
			if (runnerEvent.isInProgress())
				return COND_SIEGE;
		}
		return result;
	}

	@Override
	public void setDialogs()
	{
		 _mainDialog = "residence2/fortress/fortress_doorkeeper001.htm";
		 _failDialog = "residence2/fortress/fortress_doorkeeper002.htm";
		 _siegeDialog = "residence2/fortress/fortress_doorkeeper003.htm";
	}

	@Override
	public int getOpenPriv()
	{
		return Clan.CP_CS_ENTRY_EXIT;
	}

	@Override
	public Residence getResidence()
	{
		return getFortress();
	}
}
