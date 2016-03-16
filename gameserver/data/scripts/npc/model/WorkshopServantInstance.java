package npc.model;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.model.Party;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillUse;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;

/**
 * Служебные НПЦ на 5, 6м, 7-м этаже Tully Workshop
 *
 * @author pchayka, доработка VAVAN.
 */
public class WorkshopServantInstance extends NpcInstance
{

	private static final int[] medals = {
			10427,
			//Tully's Platinum Medal
			10428,
			//Tully's Tin Medal
			10429,
			//Tully's Lead Medal
			10430,
			//Tully's Zinc Medal
			10431,
			//Tully's Copper Medal
	};
	private static final String[] phrases = {
			"We won't let you go with this knowledge! Die!",
			"Mysterious Agent has failed! Kill him!",
			"Mates! Attack those fools!",
	};

	public WorkshopServantInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("getmedals"))
		{
			for(int i = 0; i < medals.length; i++)
				if(ItemFunctions.getItemCount(player, medals[i]) != 0)
				{
					player.sendPacket(new HtmlMessage(this).setHtml("Ingenious Contraption:<br><br>You already have one of the medals. Cannot proceed."));
					return;
				}

			ItemFunctions.addItem(player, medals[Rnd.get(0, 4)], 1);
			player.sendPacket(new HtmlMessage(this).setHtml("Ingenious Contraption:<br><br>The medal for access to Anomic Founrdy has been given."));

		}
		else if(command.startsWith("requestteleport"))
			player.teleToLocation(-12220, 279713, -13595);
		else if(command.startsWith("teletoroof"))
			player.teleToLocation(22616, 244888, 11062);
		else if(command.startsWith("teleto6thfloor"))
			player.teleToLocation(-12176, 279696, -13596);
		else if(command.startsWith("teleto7thfloor"))
			player.teleToLocation(-12501, 281397, -11936);
		else if(command.startsWith("teleto8thfloor"))
			player.teleToLocation(-12176, 279696, -10492);
		else if(command.startsWith("acceptjob"))
		{
			broadcastPacket(new MagicSkillUse(this, player, 5526, 1, 0, 0));
			player.altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(5526, 1));
			player.teleToLocation(22616, 244888, 11062);
		}
		else if(command.startsWith("rejectjob"))
		{
			for(NpcInstance challenger : World.getAroundNpc(this, 600, 300))
			{
				challenger.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 5000);
				switch(challenger.getNpcId())
				{
					case 25600:
						Functions.npcSay(challenger, phrases[0]);
						break;
					case 25601:
						Functions.npcSay(challenger, phrases[1]);
						break;
					case 25602:
						Functions.npcSay(challenger, phrases[2]);
						break;
					default:
						break;
				}
			}
			Functions.npcSay(this, "Oh...");
			doDie(null);
		}
		else if(command.startsWith("tryanomicentry"))
		{
			if(!player.isInParty())
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
				return;
			}
			Party party = player.getParty();
			if(!party.isLeader(player))
			{
				player.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER);
				return;
			}
			for(Player p : party.getPartyMembers())
				if(!this.isInRange(p, 500))
				{
					player.sendPacket(SystemMsg.YOU_ARE_TOO_FAR_FROM_THE_NPC_FOR_THAT_TO_WORK);
					return;
				}
			for(int i = 0; i < medals.length; i++)
				if(!hasItem(party, medals[i]))
				{
					player.sendMessage("In order to enter the Anomic Foundry your party should be carrying all 5 medals of Tully");
					return;
				}
			party.Teleport(new Location(25512, 247240, -2656));
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom;
		if(val == 0)
			pom = String.valueOf(npcId);
		else
			pom = npcId + "-" + val;

		if(getNpcId() == 32372)
			if(this.isInZone("[tully7]"))
				return "default/32372-floor.htm";
		if(getNpcId() == 32467)
			if(this.isInZone("[tully6]"))
				return "default/32467-6floor.htm";
			if(this.isInZone("[tully8]"))
				return "default/32467-8floor.htm";
		return "default/" + pom + ".htm";
	}

	private boolean hasItem(Party party, int itemId)
	{
		for(Player p : party.getPartyMembers())
			if(ItemFunctions.getItemCount(p, itemId) != 0)
				return true;
		return false;
	}
}