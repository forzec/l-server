package npc.model;

import java.util.StringTokenizer;

import org.mmocore.gameserver.model.CommandChannel;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

import bosses.BelethManager;

/**
 * @author pchayka
 */

public final class BelethCoffinInstance extends NpcInstance
{
	private static final int RING = 10314;

	public BelethCoffinInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command);
		if(st.nextToken().equals("request_ring"))
		{
			if(!BelethManager.isRingAvailable())
			{
				player.sendPacket(new HtmlMessage(this).setHtml("Stone Coffin:<br><br>Ring is not available. Get lost!"));
				return;
			}
			if(player.getParty() == null || player.getParty().getCommandChannel() == null)
			{
				player.sendPacket(new HtmlMessage(this).setHtml("Stone Coffin:<br><br>You are not allowed to take the ring. Are are not the group or Command Channel."));
				return;
			}
			if(player.getParty().getCommandChannel().getChannelLeader() != player)
			{
				player.sendPacket(new HtmlMessage(this).setHtml("Stone Coffin:<br><br>You are not leader or the Command Channel."));
				return;
			}

			CommandChannel channel = player.getParty().getCommandChannel();

			ItemFunctions.addItem(player, RING, 1);

			SystemMessage smsg = new SystemMessage(SystemMsg.C1_HAS_OBTAINED_S2);
			smsg.addName(player);
			smsg.addItemName(RING);
			channel.broadCast(smsg);

			BelethManager.setRingAvailable(false);
			deleteMe();

		}
		else
			super.onBypassFeedback(player, command);
	}
}