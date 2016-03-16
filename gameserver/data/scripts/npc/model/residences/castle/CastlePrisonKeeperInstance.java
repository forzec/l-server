package npc.model.residences.castle;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;
import instances.CastlePrison;
import instances.RimPailaka;

/**
 * @author pchayka
 */

public final class CastlePrisonKeeperInstance extends NpcInstance
{

	public CastlePrisonKeeperInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		Castle castle = getCastle();

		if(!canBypassCheck(player, this) || castle == null)
			return;

		if(command.equalsIgnoreCase("rimentrance"))
		{
			int rimIzId = RimPailaka.getRimPailakaId(castle.getId());
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
			int prisonIzId = CastlePrison.getPrisonId(castle.getId());
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
				ReflectionUtils.simpleEnterInstancedZone(player, CastlePrison.class, prisonIzId);
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
			filename = "residence2/castle/castle_prison_keeper00.htm";
		else
			filename = "residence2/castle/castle_prison_keeper0" + val + ".htm";

		HtmlMessage html = new HtmlMessage(this);
		html.setFile(filename);
		player.sendPacket(html);
	}


	private String checkConditions(Player p)
	{
		Castle castle = getCastle();

		if(!p.isInParty())
			return "residence2/castle/castle_prison_keeper03.htm";   // 2 ppl min limit
		for(Player member : p.getParty().getPartyMembers())
			if(member.getClan() == null || member.getClan().getResidenceId(Castle.class) == 0)
				return "residence2/castle/castle_prison_keeper01.htm";   // one member is not your clan
		if(castle.getPrisonReuseTime() > System.currentTimeMillis())
			return "residence2/castle/castle_prison_keeper02.htm"; // 4 hours reuse imposed
		if(castle.getDomainFortressContract() == 0)
			return "residence2/castle/castle_prison_keeper04.htm"; // cannot enter without fortress contract
		if(castle.getSiegeDate().getTimeInMillis() == 0)
			return "residence2/castle/castle_prison_keeper07.htm"; //cannot enter if siege date isn't defined
		long timeToSiege = castle.getSiegeDate().getTimeInMillis() - System.currentTimeMillis();
		long timeToTWSiege = castle.getDominion().getSiegeDate().getTimeInMillis() - System.currentTimeMillis();
		if(castle.getSiegeEvent().isInProgress() || castle.getDominion().getSiegeEvent().isInProgress() || (timeToSiege > 0 && timeToSiege < 7200 * 1000L) || (timeToTWSiege > 0 && timeToTWSiege < 7200 * 1000L))
			return "residence2/castle/castle_prison_keeper06.htm"; // cannot enter within 2 hours before the siege or during the siege

		return null;
	}
}