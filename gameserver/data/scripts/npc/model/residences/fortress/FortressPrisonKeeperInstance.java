package npc.model.residences.fortress;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;
import instances.FortressPrison;
import instances.RimPailaka;

/**
 * @author pchayka
 */

public final class FortressPrisonKeeperInstance extends NpcInstance
{

	public FortressPrisonKeeperInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		Fortress fortress = getFortress();
		if(!canBypassCheck(player, this) || fortress == null)
			return;

		if(command.equalsIgnoreCase("rimentrance"))
		{
			int rimIzId = RimPailaka.getRimPailakaId(fortress.getId());
			if(rimIzId != 0)
			{
				if (player.getActiveReflection() == null) // first enter
				{
					String message = checkConditions(player);
					if(message != null)
					{
						showChatWindow(player, message);
						return;
					}
				}
				ReflectionUtils.simpleEnterInstancedZone(player, RimPailaka.class, rimIzId);
			}
		}
		else if(command.equalsIgnoreCase("prisonentrance"))
		{
			int prisonIzId = FortressPrison.getPrisonId(fortress.getId());
			if(prisonIzId != 0)
			{
				if (player.getActiveReflection() == null) // first enter
				{
					String message = checkConditions(player);
					if(message != null)
					{
						showChatWindow(player, message);
						return;
					}
				}
				ReflectionUtils.simpleEnterInstancedZone(player, FortressPrison.class, prisonIzId);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		String filename;
		if(val == 0)
			filename = "residence2/fortress/fortress_prison_keeper00.htm";
		else
			filename = "residence2/fortress/fortress_prison_keeper0" + val + ".htm";

		HtmlMessage html = new HtmlMessage(this);
		html.setFile(filename);
		player.sendPacket(html);
	}

	private String checkConditions(Player p)
	{
		Fortress fortress = getFortress();
		if(!p.isInParty())
			return "residence2/fortress/fortress_prison_keeper03.htm";   // 2 ppl min limit
		for(Player member : p.getParty().getPartyMembers())
			if(member.getClan() == null || member.getClan().getResidenceId(Fortress.class) == 0)
				return "residence2/fortress/fortress_prison_keeper01.htm";   // one member is not your clan
		if(fortress.getPrisonReuseTime() > System.currentTimeMillis())
			return "residence2/fortress/fortress_prison_keeper02.htm"; // 4 hours reuse imposed
		if(fortress.getContractState() != 1)
			return "residence2/fortress/fortress_prison_keeper04.htm"; // only independant fortress may use dungeon
		long timeToSiege = fortress.getSiegeDate().getTimeInMillis() - System.currentTimeMillis();
		if(fortress.getSiegeEvent().isInProgress() || (timeToSiege > 0 && timeToSiege < 7200 * 1000L))
			return "residence2/fortress/fortress_prison_keeper07.htm"; // cannot enter within 2 hours before the siege or during the siege

		return null;
	}
}