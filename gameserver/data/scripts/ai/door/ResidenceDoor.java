package ai.door;

import org.mmocore.gameserver.ai.DoorAI;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.listener.actor.player.OnAnswerListener;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.ConfirmDlg;

/**
 * @author VISTALL
 * @date 15:32/11.07.2011
 */
public class ResidenceDoor extends DoorAI
{
	public ResidenceDoor(DoorInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtTwiceClick(final Player player)
	{
		final DoorInstance door = getActor();

		Residence residence = ResidenceHolder.getInstance().getResidence(door.getTemplate().getAIParams().getInteger("residence_id"));
		if(residence.getOwner() != null && player.getClan() != null && player.getClan() == residence.getOwner() && (player.getClanPrivileges() & Clan.CP_CS_ENTRY_EXIT) == Clan.CP_CS_ENTRY_EXIT)
		{
			SystemMsg msg = door.isOpen() ? SystemMsg.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE : SystemMsg.WOULD_YOU_LIKE_TO_OPEN_THE_GATE;
			player.ask(new ConfirmDlg(msg, 0), new OnAnswerListener()
			{
				@Override
				public void sayYes(Player player)
				{
					if(door.isOpen())
						door.closeMe(player, true);
					else
						door.openMe(player, true);
				}

				@Override
				public void sayNo(Player player)
				{
					//
				}
			});
		}
	}
}
