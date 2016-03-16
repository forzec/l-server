import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.instancemanager.ReflectionManager;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;

public class Util
{
	@Bypass("Util:Gatekeeper")
	public void Gatekeeper(Player player, NpcInstance npc, String[] param)
	{
		if(param.length < 4)
			throw new IllegalArgumentException();

		if(player == null)
			return;

		if(!NpcInstance.canBypassCheck(player, player.getLastNpc()))
			return;

		if (player.isTerritoryFlagEquipped())
		{
			player.sendPacket(new HtmlMessage(null).setFile("flagman.htm"));
			return;
		}

		long price = Long.parseLong(param[param.length - 1]);

		if(price > 0 && player.getAdena() < price)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		if(player.getMountType() == 2)
		{
			player.sendMessage("Телепортация верхом на виверне невозможна.");
			return;
		}

		/* Затычка, npc Mozella не ТПшит чаров уровень которых превышает заданный в конфиге
		 * Off Like >= 56 lvl, данные по ограничению lvl'a устанавливаются в altsettings.properties.
		 */
		NpcInstance lastNpc = player.getLastNpc();
		if(npc != null)
		{
			int npcId = lastNpc.getNpcId();
			switch(npcId)
			{
				case 30483:
					if(player.getLevel() >= Config.CRUMA_GATEKEEPER_LVL)
					{
						Functions.show("teleporter/30483-no.htm", player, lastNpc);
						return;
					}
					break;
				case 32864:
				case 32865:
				case 32866:
				case 32867:
				case 32868:
				case 32869:
				case 32870:
					if(player.getLevel() < 80)
					{
						Functions.show("teleporter/"+npcId+"-no.htm", player, lastNpc);
						return;
					}
					break;
			}
		}

		int x = Integer.parseInt(param[0]);
		int y = Integer.parseInt(param[1]);
		int z = Integer.parseInt(param[2]);
		int castleId = param.length > 4 ? Integer.parseInt(param[3]) : 0;

		if(player.getReflection().isDefault())
		{
			Castle castle = castleId > 0 ? ResidenceHolder.getInstance().getResidence(Castle.class, castleId) : null;
			// Нельзя телепортироваться в города, где идет осада
			if(castle != null && castle.getSiegeEvent().isInProgress())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
				return;
			}
		}

		Location pos = Location.findPointToStay(x, y, z, 30, 80, player.getGeoIndex());

		player.teleToLocation(pos);
		if(price > 0)
			player.reduceAdena(price, true);
	}

	@Bypass("Util:SSGatekeeper")
	public void SSGatekeeper(Player player, NpcInstance npc, String[] param)
	{
		if(param.length < 4)
			throw new IllegalArgumentException();

		if(player == null)
			return;

		int type = Integer.parseInt(param[3]);

		if(!NpcInstance.canBypassCheck(player, player.getLastNpc()))
			return;

		if(type > 0)
		{
			int player_cabal = SevenSigns.getInstance().getPlayerCabal(player);
			int period = SevenSigns.getInstance().getCurrentPeriod();
			if(period == SevenSigns.PERIOD_COMPETITION && player_cabal == SevenSigns.CABAL_NULL)
			{
				player.sendPacket(SystemMsg.THIS_MAY_ONLY_BE_USED_DURING_THE_QUEST_EVENT_PERIOD);
				return;
			}

			int winner;
			if(period == SevenSigns.PERIOD_SEAL_VALIDATION && (winner = SevenSigns.getInstance().getCabalHighestScore()) != SevenSigns.CABAL_NULL)
			{
				if(winner != player_cabal)
					return;
				if(type == 1 && SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE) != player_cabal)
					return;
				if(type == 2 && SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS) != player_cabal)
					return;
			}
		}

		player.teleToLocation(Integer.parseInt(param[0]), Integer.parseInt(param[1]), Integer.parseInt(param[2]));
	}

	@Bypass("Util:QuestGatekeeper")
	public void QuestGatekeeper(Player player, NpcInstance npc, String[] param)
	{
		if(param.length < 5)
			throw new IllegalArgumentException();

		if(player == null)
			return;

		long count = Long.parseLong(param[3]);
		int item = Integer.parseInt(param[4]);

		if(!NpcInstance.canBypassCheck(player, player.getLastNpc()))
			return;

		if(count > 0)
		{
			if(!ItemFunctions.deleteItem(player, item, count))
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				return;
			}
		}

		int x = Integer.parseInt(param[0]);
		int y = Integer.parseInt(param[1]);
		int z = Integer.parseInt(param[2]);

		Location pos = Location.findPointToStay(x, y, z, 20, 70, player.getGeoIndex());

		player.teleToLocation(pos);
	}

	/**
	 * Используется для телепортации за Newbie Token, проверяет уровень и передает
	 * параметры в QuestGatekeeper
	 */
	@Bypass("Util:TokenJump")
	public void TokenJump(Player player, NpcInstance npc, String[] param)
	{
		if(player == null)
			return;
		if(player.getLevel() <= 19)
			QuestGatekeeper(player, npc, param);
		else
			Functions.show("Only for newbies", player, npc);
	}

	@Bypass("Util:NoblessTeleport")
	public void NoblessTeleport(Player player, NpcInstance npc, String[] param)
	{
		if(player == null)
			return;
		if(player.isNoble() || Config.ALLOW_NOBLE_TP_TO_ALL)
			Functions.show("scripts/noble.htm", player, npc);
		else
			Functions.show("scripts/nobleteleporter-no.htm", player, npc);
	}

	@Bypass("Util:PayPage")
	public void PayPage(Player player, NpcInstance npc, String[] param)
	{
		if(param.length < 2)
			throw new IllegalArgumentException();

		if(player == null)
			return;

		String page = param[0];
		int item = Integer.parseInt(param[1]);
		long price = Long.parseLong(param[2]);

		if(ItemFunctions.getItemCount(player, item) < price)
		{
			player.sendPacket(item == 57 ? SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA : SystemMsg.INCORRECT_ITEM_COUNT);
			return;
		}

		ItemFunctions.deleteItem(player, item, price);
		Functions.show(page, player, npc);
	}

	@Bypass("Util:TakeNewbieWeaponCoupon")
	public void TakeNewbieWeaponCoupon(Player player, NpcInstance npc, String[] param)
	{
		if(!Config.ALT_ALLOW_SHADOW_WEAPONS)
		{
			player.sendPacket(SystemMsg.THIS_FUNCTION_IS_INACCESSIBLE_RIGHT_NOW);
			return;
		}
		if(player.getLevel() > 19 || player.getClassId().getLevel() > 1)
		{
			Functions.show("Your level is too high!", player, npc);
			return;
		}
		if(player.getLevel() < 6)
		{
			Functions.show("Your level is too low!", player, npc);
			return;
		}
		if(player.getVarB("newbieweapon"))
		{
			Functions.show("Your already got your newbie weapon!", player, npc);
			return;
		}
		ItemFunctions.addItem(player, 7832, 5);
		player.setVar("newbieweapon", "true", -1);
	}

	@Bypass("Util:TakeAdventurersArmorCoupon")
	public void TakeAdventurersArmorCoupon(Player player, NpcInstance npc, String[] param)
	{
		if(!Config.ALT_ALLOW_SHADOW_WEAPONS)
		{
			player.sendPacket(SystemMsg.THIS_FUNCTION_IS_INACCESSIBLE_RIGHT_NOW);
			return;
		}
		if(player.getLevel() > 39 || player.getClassId().getLevel() > 2)
		{
			Functions.show("Your level is too high!", player, npc);
			return;
		}
		if(player.getLevel() < 20 || player.getClassId().getLevel() < 2)
		{
			Functions.show("Your level is too low!", player, npc);
			return;
		}
		if(player.getVarB("newbiearmor"))
		{
			Functions.show("Your already got your newbie weapon!", player, npc);
			return;
		}
		ItemFunctions.addItem(player, 7833, 1);
		player.setVar("newbiearmor", "true", -1);
	}

	@Bypass("Util:enter_dc")
	public void enter_dc(Player player, NpcInstance npc, String[] param)
	{
		if(player == null || npc == null)
			return;

		if(!NpcInstance.canBypassCheck(player, npc))
			return;

		player.setVar("DCBackCoords", player.getLoc().toXYZString(), -1);
		player.teleToLocation(-114582, -152635, -6742);
	}

	@Bypass("Util:exit_dc")
	public void exit_dc(Player player, NpcInstance npc, String[] param)
	{
		if(player == null || npc == null)
			return;

		if(!NpcInstance.canBypassCheck(player, npc))
			return;

		String var = player.getVar("DCBackCoords");
		if(var == null || var.isEmpty())
		{
			player.teleToLocation(new Location(43768, -48232, -800), 0);
			return;
		}
		player.teleToLocation(Location.parseLoc(var), 0);
		player.unsetVar("DCBackCoords");
	}

	@Bypass("Util:teleToBackCoors")
	public void teleToBackCoors(Player player, NpcInstance npc, String[] param)
	{
		if(player == null || npc == null)
			return;

		if(!NpcInstance.canBypassCheck(player, npc))
			return;

		if(player.getReflection() != ReflectionManager.GIRAN_HARBOR && player.getReflection() != ReflectionManager.PARNASSUS)
			return;

		String var = player.getVar("backCoords");
		if(StringUtils.isEmpty(var))
		{
			player.teleToLocation(46776, 185784, -3528, 0);
			return;
		}

		player.teleToLocation(Location.parseLoc(var), ReflectionManager.DEFAULT);
	}
}