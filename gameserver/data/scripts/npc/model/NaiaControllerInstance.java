package npc.model;

import org.mmocore.gameserver.instancemanager.naia.NaiaTowerManager;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillUse;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import ai.hellbound.NaiaLock;

/**
 * @author pchayka
 */
public class NaiaControllerInstance extends NpcInstance
{
	public NaiaControllerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("tryenter"))
		{
			if(NaiaLock.isEntranceActive())
			{
				//instance
				if(!player.isInParty())
				{
					player.sendPacket(SystemMsg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
					return;
				}
				if(!player.getParty().isLeader(player))
				{
					player.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER);
					return;
				}
				for(Player member : player.getParty().getPartyMembers())
				{
					if(member.getLevel() < 80)
					{
						player.sendPacket(new SystemMessage(SystemMsg.C1S_LEVEL_DOES_NOT_CORRESPOND_TO_THE_REQUIREMENTS_FOR_ENTRY).addName(member));
						return;
					}
					if(!member.isInRange(this, 500))
					{
						player.sendPacket(new SystemMessage(SystemMsg.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED).addName(member));
						return;
					}
				}
				NaiaTowerManager.getInstance().startNaiaTower(player, this);

				broadcastPacket(new MagicSkillUse(this, this, 5527, 1, 0, 0));
				doDie(null);
			}
			else
			{
				broadcastPacket(new MagicSkillUse(this, this, 5527, 1, 0, 0));
				doDie(null);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}