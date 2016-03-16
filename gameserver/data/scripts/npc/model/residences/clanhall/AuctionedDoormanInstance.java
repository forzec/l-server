package npc.model.residences.clanhall;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.Privilege;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.HtmlUtils;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author VISTALL
 * @date 10:50/20.06.2011
 */
public class AuctionedDoormanInstance extends NpcInstance
{
	private int[] _doors;
	private boolean _elite;

	public AuctionedDoormanInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);

		_doors = template.getAIParams().getIntegerArray("doors", ArrayUtils.EMPTY_INT_ARRAY);
		_elite = template.getAIParams().getBool("elite", false);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		ClanHall clanHall = getClanHall();
		if(command.equalsIgnoreCase("openDoors"))
		{
			if(player.hasPrivilege(Privilege.CH_ENTER_EXIT) && player.getClan().getHasHideout() == clanHall.getId())
			{
				for(int d : _doors)
					ReflectionUtils.getDoor(d).openMe();
				showChatWindow(player, "residence2/clanhall/agitafterdooropen.htm");
			}
			else
				showChatWindow(player, "residence2/clanhall/noAuthority.htm");
		}
		else if(command.equalsIgnoreCase("closeDoors"))
		{
			if(player.hasPrivilege(Privilege.CH_ENTER_EXIT) && player.getClan().getHasHideout() == clanHall.getId())
			{
				for(int d : _doors)
					ReflectionUtils.getDoor(d).closeMe(player, true);
				showChatWindow(player, "residence2/clanhall/agitafterdoorclose.htm");
			}
			else
				showChatWindow(player, "residence2/clanhall/noAuthority.htm");
		}
		else if(command.equalsIgnoreCase("banish"))
		{
			if(player.hasPrivilege(Privilege.CH_DISMISS))
			{
				clanHall.banishForeigner();
				showChatWindow(player, "residence2/clanhall/agitafterbanish.htm");
			}
			else
				showChatWindow(player, "residence2/clanhall/noAuthority.htm");
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		ClanHall clanHall = getClanHall();
		if (clanHall != null)
		{
			Clan playerClan = player.getClan();
			if(playerClan != null && playerClan.getHasHideout() == clanHall.getId())
				showChatWindow(player, _elite ? "residence2/clanhall/WyvernAgitJanitorHi.htm" : "residence2/clanhall/AgitJanitorHi.htm", "%owner%", playerClan.getName());
			else
			{
				if(playerClan != null && playerClan.getCastle() > 0)
				{
					Castle castle = ResidenceHolder.getInstance().getResidence(playerClan.getCastle());
					HtmlMessage html = new HtmlMessage(this);
					html.setFile("merchant/territorystatus.htm");
					html.replace("%castlename%", HtmlUtils.htmlResidenceName(castle.getId()));
					html.replace("%taxpercent%", String.valueOf(castle.getTaxPercent()));
					html.replace("%clanname%", playerClan.getName());
					html.replace("%clanleadername%", playerClan.getLeaderName());
					player.sendPacket(html);
				}
				else
					showChatWindow(player, "residence2/clanhall/noAgitInfo.htm");
			}
		}
		else
			showChatWindow(player, "residence2/clanhall/noAgitInfo.htm");
	}
}
