package npc.model.residences;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author VISTALL
 * @date 13:00/31.03.2011
 */
public abstract class DoormanInstance extends NpcInstance
{
	protected static final int COND_OWNER = 0;
	protected static final int COND_SIEGE = 1;
	protected static final int COND_FAIL = 2;

	protected String _siegeDialog;
	protected String _mainDialog;
	protected String _failDialog;

	protected int[] _doors;

	public DoormanInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);

		setDialogs();

		_doors = template.getAIParams().getIntegerArray("doors");
	}

	public void setDialogs()
	{
		_siegeDialog = getTemplate().getAIParams().getString("siege_dialog");
		_mainDialog = getTemplate().getAIParams().getString("main_dialog");
		_failDialog = getTemplate().getAIParams().getString("fail_dialog");
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
						ReflectionUtils.getDoor(i).openMe();
				else if (command.equalsIgnoreCase("closeDoors"))
					for(int i : _doors)
						ReflectionUtils.getDoor(i).closeMe();
				break;
			case COND_SIEGE:
				player.sendPacket(new HtmlMessage(this).setFile(_siegeDialog));
				break;
			case COND_FAIL:
				player.sendPacket(new HtmlMessage(this).setFile(_failDialog));
				break;
		}
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		String filename = null;
		int cond = getCond(player);
		switch(cond)
		{
			case COND_OWNER:
				filename = _mainDialog;
				break;
			case COND_SIEGE:
				filename = _siegeDialog;
				break;
			case COND_FAIL:
				filename = _failDialog;
				break;
		}
		player.sendPacket(new HtmlMessage(this, filename));
	}

	protected int getCond(Player player)
	{
		Residence residence = getResidence();
		Clan residenceOwner = residence.getOwner();
		if(residenceOwner != null && player.getClan() == residenceOwner && (player.getClanPrivileges() & getOpenPriv()) == getOpenPriv())
		{
			if(residence.getSiegeEvent().isInProgress())
				return COND_SIEGE;
			else
				return COND_OWNER;
		}
		else
			return COND_FAIL;
	}

	public abstract int getOpenPriv();

	public abstract Residence getResidence();
}
