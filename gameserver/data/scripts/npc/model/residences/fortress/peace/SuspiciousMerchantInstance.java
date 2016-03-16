package npc.model.residences.fortress.peace;

import org.mmocore.gameserver.dao.SiegeClanDAO;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import org.mmocore.gameserver.model.entity.events.impl.FortressSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.Privilege;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class SuspiciousMerchantInstance extends NpcInstance
{
	public SuspiciousMerchantInstance(int objectID, NpcTemplate template)
	{
		super(objectID, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		Fortress fortress = getFortress();
		FortressSiegeEvent siegeEvent = fortress.getSiegeEvent();

		if(command.equalsIgnoreCase("register"))
		{
			Clan clan = player.getClan();
			if(clan == null)
			{
				showChatWindow(player, "residence2/fortress/fortress_ordery002.htm");
				return;
			}

			if(clan.isPlacedForDisband())
			{
				player.sendPacket(SystemMsg.YOU_HAVE_ALREADY_REQUESTED_THE_DISSOLUTION_OF_YOUR_CLAN);
				return;
			}

			if(clan.getHasFortress() == fortress.getId())
			{
				showChatWindow(player, "residence2/fortress/fortress_ordery014.htm", "%clan_name%", clan.getName());
				return;
			}

			if(!player.hasPrivilege(Privilege.CS_FS_SIEGE_WAR))
			{
				showChatWindow(player, "residence2/fortress/fortress_ordery012.htm");
				return;
			}

			if(clan.getCastle() > 0)
			{
				Castle relatedCastle = null;
				for(Castle castle : fortress.getRelatedCastles())
					if(castle.getId()  == clan.getCastle())
						relatedCastle = castle;

				if(relatedCastle != null)
				{
					if(fortress.getContractState() == Fortress.CONTRACT_WITH_CASTLE)
					{
						showChatWindow(player, "residence2/fortress/fortress_ordery022.htm");
						return;
					}

					if(relatedCastle.getSiegeEvent().isRegistrationOver())
					{
						showChatWindow(player, "residence2/fortress/fortress_ordery006.htm");
						return;
					}
				}
				else
				{
					showChatWindow(player, "residence2/fortress/fortress_ordery021.htm");
					return;
				}
			}


			SiegeClanObject siegeClan = siegeEvent.getSiegeClan(FortressSiegeEvent.ATTACKERS, clan);
			if(siegeClan !=  null)
			{
				showChatWindow(player, "residence2/fortress/fortress_ordery007.htm");
				return;
			}

			// 1 рега возможна всего
			for(Fortress $ : ResidenceHolder.getInstance().getResidenceList(Fortress.class))
				if($.getSiegeEvent().getSiegeClan(FortressSiegeEvent.ATTACKERS, clan) != null)
				{
					showChatWindow(player, "residence2/fortress/fortress_ordery006.htm");
					return;
				}

			if(clan.getLevel() < 4)
			{
				showChatWindow(player, "residence2/fortress/fortress_ordery006.htm");
				return;
			}

			// если у нас есть форт, запрещаем регатся на форт, если на носу осада своего форта(во избежания абуза, участия в 2 осадах)
			if(clan.getHasFortress() > 0)
			{
				Fortress clanFortress = ResidenceHolder.getInstance().getResidence(clan.getHasFortress());
				if(clanFortress.getSiegeDate().getTimeInMillis() > 0)
				{
					showChatWindow(player, "residence2/fortress/fortress_ordery006.htm");
					return;
				}
			}

			DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
			if(runnerEvent.isRegistrationOver() || siegeEvent.isRegistrationOver())
			{
				showChatWindow(player, "residence2/fortress/fortress_ordery006.htm");
				return;
			}

			int attackersSize = siegeEvent.getObjects(SiegeEvent.ATTACKERS).size();
			if(attackersSize == 0)
				if(!player.reduceAdena(250000L, true))
				{
					showChatWindow(player, "residence2/fortress/fortress_ordery003.htm");
					return;
				}

			siegeClan = new SiegeClanObject(FortressSiegeEvent.ATTACKERS, clan, 0);
			siegeEvent.addObject(FortressSiegeEvent.ATTACKERS, siegeClan);
			SiegeClanDAO.getInstance().insert(fortress, siegeClan);

			siegeEvent.reCalcNextTime(false);

			player.sendPacket(new SystemMessage(SystemMsg.YOUR_CLAN_HAS_BEEN_REGISTERED_TO_S1S_FORTRESS_BATTLE).addResidenceName(fortress));
			showChatWindow(player, "residence2/fortress/fortress_ordery005.htm");
		}
		else if(command.equalsIgnoreCase("cancel"))
		{
			Clan clan = player.getClan();
			if(clan == null || !player.hasPrivilege(Privilege.CS_FS_SIEGE_WAR))
			{
				showChatWindow(player, "residence2/fortress/fortress_ordery010.htm");
				return;
			}

			SiegeClanObject siegeClan = siegeEvent.getSiegeClan(FortressSiegeEvent.ATTACKERS, clan);
			if(siegeClan != null)
			{
				siegeEvent.removeObject(FortressSiegeEvent.ATTACKERS, siegeClan);
				SiegeClanDAO.getInstance().delete(fortress, siegeClan);

				siegeEvent.reCalcNextTime(false);

				showChatWindow(player, "residence2/fortress/fortress_ordery009.htm");
			}
			else
				showChatWindow(player, "residence2/fortress/fortress_ordery011.htm");
		}
		else if(command.equalsIgnoreCase("state"))
		{
			int attackersSize = siegeEvent.getObjects(SiegeEvent.ATTACKERS).size();
			if(attackersSize == 0)
				showChatWindow(player, "residence2/fortress/fortress_ordery019.htm");
			else
				showChatWindow(player, "residence2/fortress/fortress_ordery020.htm");
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		HtmlMessage html = new HtmlMessage(this);
		Fortress fortress = getFortress();
		if(fortress.getOwner() != null)
		{
			html.setFile("residence2/fortress/fortress_ordery001a.htm");
			html.replace("%clan_name%", fortress.getOwner().getName());
		}
		else
			html.setFile("residence2/fortress/fortress_ordery001.htm");

		player.sendPacket(html);
	}
}