package services.petevolve;

import org.mmocore.commons.dao.JdbcEntityState;
import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.data.xml.holder.ItemHolder;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Servitor;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.InventoryUpdate;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.tables.PetDataTable;
import org.mmocore.gameserver.tables.PetDataTable.L2Pet;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Util;

public class exchange
{
	/** Билеты для обмена **/
	private static final int PEticketB = 7583;
	private static final int PEticketC = 7584;
	private static final int PEticketK = 7585;

	/** Дудки для вызова петов **/
	private static final int BbuffaloP = 6648;
	private static final int BcougarC = 6649;
	private static final int BkookaburraO = 6650;

	@Bypass("services.petevolve.exchange:exch_1")
	public void exch_1(Player player, NpcInstance npc, String[] args)
	{
		if(ItemFunctions.deleteItem(player, PEticketB, 1))
		{
			ItemFunctions.addItem(player, BbuffaloP, 1);
			return;
		}
		Functions.show("scripts/services/petevolve/exchange_no.htm", player, npc);
	}

	@Bypass("services.petevolve.exchange:exch_2")
	public void exch_2(Player player, NpcInstance npc, String[] args)
	{
		if(ItemFunctions.deleteItem(player, PEticketC, 1))
		{
			ItemFunctions.addItem(player, BcougarC, 1);
			return;
		}
		Functions.show("scripts/services/petevolve/exchange_no.htm", player, npc);
	}

	@Bypass("services.petevolve.exchange:exch_3")
	public void exch_3(Player player, NpcInstance npc, String[] args)
	{
		if(ItemFunctions.deleteItem(player, PEticketK, 1))
		{
			ItemFunctions.addItem(player, BkookaburraO, 1);
			return;
		}
		Functions.show("scripts/services/petevolve/exchange_no.htm", player, npc);
	}

	@Bypass("services.petevolve.exchange:showBabyPetExchange")
	public void showBabyPetExchange(Player player, NpcInstance npc, String[] args)
	{
		if(!Config.SERVICES_EXCHANGE_BABY_PET_ENABLED)
		{
			Functions.show("Сервис отключен.", player, npc);
			return;
		}
		ItemTemplate item = ItemHolder.getInstance().getTemplate(Config.SERVICES_EXCHANGE_BABY_PET_ITEM);
		String out = "";
		out += "<html><body>Вы можете в любое время обменять вашего Improved Baby пета на другой вид, без потери опыта. Пет при этом должен быть вызван.";
		out += "<br>Стоимость обмена: " + Util.formatAdena(Config.SERVICES_EXCHANGE_BABY_PET_PRICE) + " " + item.getName();
		out += "<br><button width=250 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h npc_%objectId%_services.petevolve.exchange:exToCougar\" value=\"Обменять на Improved Cougar\">";
		out += "<br1><button width=250 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h npc_%objectId%_services.petevolve.exchange:exToBuffalo\" value=\"Обменять на Improved Buffalo\">";
		out += "<br1><button width=250 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h npc_%objectId%_services.petevolve.exchange:exToKookaburra\" value=\"Обменять на Improved Kookaburra\">";
		out += "</body></html>";
		Functions.show(out, player, npc);
	}

	@Bypass("services.petevolve.exchange:showErasePetName")
	public void showErasePetName(Player player, NpcInstance npc, String[] args)
	{
		if(!Config.SERVICES_CHANGE_PET_NAME_ENABLED)
		{
			Functions.show("Сервис отключен.", player, npc);
			return;
		}
		ItemTemplate item = ItemHolder.getInstance().getTemplate(Config.SERVICES_CHANGE_PET_NAME_ITEM);
		String out = "";
		out += "<html><body>Вы можете обнулить имя у пета, для того чтобы назначить новое. Пет при этом должен быть вызван.";
		out += "<br>Стоимость обнуления: " + Util.formatAdena(Config.SERVICES_CHANGE_PET_NAME_PRICE) + " " + item.getName();
		out += "<br><button width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h htmbypass_services.petevolve.exchange:erasePetName\" value=\"Обнулить имя\">";
		out += "</body></html>";
		Functions.show(out, player, npc);
	}

	@Bypass("services.petevolve.exchange:erasePetName")
	public void erasePetName(Player player, NpcInstance npc, String[] args)
	{
		if(!Config.SERVICES_CHANGE_PET_NAME_ENABLED)
		{
			Functions.show("Сервис отключен.", player, npc);
			return;
		}
		Servitor pl_pet = player.getServitor();
		if(pl_pet == null || !pl_pet.isPet())
		{
			Functions.show("Питомец должен быть вызван.", player, npc);
			return;
		}
		if(ItemFunctions.deleteItem(player, Config.SERVICES_CHANGE_PET_NAME_ITEM, Config.SERVICES_CHANGE_PET_NAME_PRICE))
		{
			pl_pet.setName(pl_pet.getTemplate().name);
			pl_pet.broadcastCharInfo();

			PetInstance _pet = (PetInstance) pl_pet;
			ItemInstance control = _pet.getControlItem();
			if(control != null)
			{
				control.setCustomType2(1);
				control.setJdbcState(JdbcEntityState.UPDATED);
				control.update();
				player.sendPacket(new InventoryUpdate().addModifiedItem(control));
			}
			Functions.show("Имя стерто.", player, npc);
		}
		else if(Config.SERVICES_CHANGE_PET_NAME_ITEM == ItemTemplate.ITEM_ID_ADENA)
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
	}

	@Bypass("services.petevolve.exchange:exToCougar")
	public void exToCougar(Player player, NpcInstance npc, String[] args)
	{
		if(!Config.SERVICES_EXCHANGE_BABY_PET_ENABLED)
		{
			Functions.show("Сервис отключен.", player, npc);
			return;
		}
		Servitor pl_pet = player.getServitor();
		if(pl_pet == null || pl_pet.isDead() || !(pl_pet.getNpcId() == PetDataTable.IMPROVED_BABY_BUFFALO_ID || pl_pet.getNpcId() == PetDataTable.IMPROVED_BABY_KOOKABURRA_ID))
		{
			Functions.show("Пет должен быть вызван.", player, npc);
			return;
		}
		if(ItemFunctions.deleteItem(player, Config.SERVICES_EXCHANGE_BABY_PET_ITEM, Config.SERVICES_EXCHANGE_BABY_PET_PRICE))
		{
			ItemInstance control = player.getInventory().getItemByObjectId(player.getServitor().getControlItemObjId());
			control.setItemId(L2Pet.IMPROVED_BABY_COUGAR.getControlItemId());
			control.setJdbcState(JdbcEntityState.UPDATED);
			control.update();
			player.sendPacket(new InventoryUpdate().addModifiedItem(control));
			player.getServitor().unSummon(false, false);
			Functions.show("Пет изменен.", player, npc);
		}
		else if(Config.SERVICES_EXCHANGE_BABY_PET_ITEM == ItemTemplate.ITEM_ID_ADENA)
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
	}

	@Bypass("services.petevolve.exchange:exToBuffalo")
	public void exToBuffalo(Player player, NpcInstance npc, String[] args)
	{
		if(!Config.SERVICES_EXCHANGE_BABY_PET_ENABLED)
		{
			Functions.show("Сервис отключен.", player, npc);
			return;
		}
		Servitor pl_pet = player.getServitor();
		if(pl_pet == null || pl_pet.isDead() || !(pl_pet.getNpcId() == PetDataTable.IMPROVED_BABY_COUGAR_ID || pl_pet.getNpcId() == PetDataTable.IMPROVED_BABY_KOOKABURRA_ID))
		{
			Functions.show("Пет должен быть вызван.", player, npc);
			return;
		}
		if(Config.ALT_IMPROVED_PETS_LIMITED_USE && player.isMageClass())
		{
			Functions.show("Этот пет только для воинов.", player, npc);
			return;
		}
		if(ItemFunctions.deleteItem(player, Config.SERVICES_EXCHANGE_BABY_PET_ITEM, Config.SERVICES_EXCHANGE_BABY_PET_PRICE))
		{
			ItemInstance control = player.getInventory().getItemByObjectId(player.getServitor().getControlItemObjId());
			control.setItemId(L2Pet.IMPROVED_BABY_BUFFALO.getControlItemId());
			control.setJdbcState(JdbcEntityState.UPDATED);
			control.update();
			player.sendPacket(new InventoryUpdate().addModifiedItem(control));
			player.getServitor().unSummon(false, false);
			Functions.show("Пет изменен.", player, npc);
		}
		else if(Config.SERVICES_EXCHANGE_BABY_PET_ITEM == ItemTemplate.ITEM_ID_ADENA)
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
	}

	@Bypass("services.petevolve.exchange:exToKookaburra")
	public void exToKookaburra(Player player, NpcInstance npc, String[] args)
	{
		if(!Config.SERVICES_EXCHANGE_BABY_PET_ENABLED)
		{
			Functions.show("Сервис отключен.", player, npc);
			return;
		}
		Servitor pl_pet = player.getServitor();
		if(pl_pet == null || pl_pet.isDead() || !(pl_pet.getNpcId() == PetDataTable.IMPROVED_BABY_BUFFALO_ID || pl_pet.getNpcId() == PetDataTable.IMPROVED_BABY_COUGAR_ID))
		{
			Functions.show("Пет должен быть вызван.", player, npc);
			return;
		}
		if(Config.ALT_IMPROVED_PETS_LIMITED_USE && !player.isMageClass())
		{
			Functions.show("Этот пет только для магов.", player, npc);
			return;
		}
		if(ItemFunctions.deleteItem(player, Config.SERVICES_EXCHANGE_BABY_PET_ITEM, Config.SERVICES_EXCHANGE_BABY_PET_PRICE))
		{
			ItemInstance control = player.getInventory().getItemByObjectId(player.getServitor().getControlItemObjId());
			control.setItemId(L2Pet.IMPROVED_BABY_KOOKABURRA.getControlItemId());
			control.setJdbcState(JdbcEntityState.UPDATED);
			control.update();
			player.sendPacket(new InventoryUpdate().addModifiedItem(control));
			player.getServitor().unSummon(false, false);
			Functions.show("Пет изменен.", player, npc);
		}
		else if(Config.SERVICES_EXCHANGE_BABY_PET_ITEM == ItemTemplate.ITEM_ID_ADENA)
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
	}
}