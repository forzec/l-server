package services.petevolve;

import org.mmocore.commons.dao.JdbcEntityState;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.tables.PetDataTable;
import org.mmocore.gameserver.tables.PetDataTable.L2Pet;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class clanhall
{
	// -- Pet ID --
	private static final int GREAT_WOLF = PetDataTable.GREAT_WOLF_ID;
	private static final int WHITE_WOLF = PetDataTable.WGREAT_WOLF_ID;
	private static final int FENRIR = PetDataTable.FENRIR_WOLF_ID;
	private static final int WHITE_FENRIR = PetDataTable.WFENRIR_WOLF_ID;
	private static final int WIND_STRIDER = PetDataTable.STRIDER_WIND_ID;
	private static final int RED_WIND_STRIDER = PetDataTable.RED_STRIDER_WIND_ID;
	private static final int STAR_STRIDER = PetDataTable.STRIDER_STAR_ID;
	private static final int RED_STAR_STRIDER = PetDataTable.RED_STRIDER_STAR_ID;
	private static final int TWILING_STRIDER = PetDataTable.STRIDER_TWILIGHT_ID;
	private static final int RED_TWILING_STRIDER = PetDataTable.RED_STRIDER_TWILIGHT_ID;

	// -- First Item ID --
	private static final int GREAT_WOLF_NECKLACE = L2Pet.GREAT_WOLF.getControlItemId();
	private static final int FENRIR_NECKLACE = L2Pet.FENRIR_WOLF.getControlItemId();
	private static final int WIND_STRIDER_ITEM = L2Pet.STRIDER_WIND.getControlItemId();
	private static final int STAR_STRIDER_ITEM = L2Pet.STRIDER_STAR.getControlItemId();
	private static final int TWILING_STRIDER_ITEM = L2Pet.STRIDER_TWILIGHT.getControlItemId();

	// -- Second Item ID --
	private static final int WHITE_WOLF_NECKLACE = L2Pet.WGREAT_WOLF.getControlItemId();
	private static final int WHITE_FENRIR_NECKLACE = L2Pet.WFENRIR_WOLF.getControlItemId();
	private static final int RED_WS_ITEM = L2Pet.RED_STRIDER_WIND.getControlItemId();
	private static final int RED_SS_ITEM = L2Pet.RED_STRIDER_STAR.getControlItemId();
	private static final int RED_TW_ITEM = L2Pet.RED_STRIDER_TWILIGHT.getControlItemId();

	@Bypass("services.petevolve.clanhall:greatsw")
	public void greatsw(Player player, NpcInstance npc, String[] direction)
	{
		if(player == null || npc == null)
			return;
		boolean fwd = Integer.parseInt(direction[0]) == 1;

		if(player.getInventory().getCountOf(fwd ? GREAT_WOLF_NECKLACE : WHITE_WOLF_NECKLACE) > 1)
		{
			Functions.show("scripts/services/petevolve/error_3.htm", player, npc);
			return;
		}
		if(player.getServitor() != null)
		{
			Functions.show("scripts/services/petevolve/error_4.htm", player, npc);
			return;
		}
		ItemInstance collar = player.getInventory().getItemByItemId(fwd ? GREAT_WOLF_NECKLACE : WHITE_WOLF_NECKLACE);
		if(collar == null)
		{
			Functions.show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		int npcId = PetDataTable.getSummonId(collar);
		if(npcId == 0)
			return;
		NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(npcId);
		if(petTemplate == null)
			return;
		PetInstance pet = PetInstance.restore(collar, petTemplate, player);

		if(npcId != (fwd ? GREAT_WOLF : WHITE_WOLF))
		{
			Functions.show("scripts/services/petevolve/error_2.htm", player, npc);
			return;
		}
		if(pet.getLevel() < 55)
		{
			Functions.show("scripts/services/petevolve/error_lvl_greatw.htm", player, npc);
			return;
		}

		collar.setItemId(fwd ? WHITE_WOLF_NECKLACE : GREAT_WOLF_NECKLACE);
		collar.setJdbcState(JdbcEntityState.UPDATED);
		collar.update();
		player.sendItemList(false);
		player.sendPacket(SystemMessage.obtainItems((fwd ? WHITE_WOLF_NECKLACE : GREAT_WOLF_NECKLACE), 1, 0));
		Functions.show("scripts/services/petevolve/end_msg3_gwolf.htm", player, npc);
	}

	@Bypass("services.petevolve.clanhall:fenrir")
	public void fenrir(Player player, NpcInstance npc, String[] direction)
	{
		if(player == null || npc == null)
			return;
		boolean fwd = Integer.parseInt(direction[0]) == 1;

		if(player.getInventory().getCountOf(fwd ? FENRIR_NECKLACE : WHITE_FENRIR_NECKLACE) > 1)
		{
			Functions.show("scripts/services/petevolve/error_3.htm", player, npc);
			return;
		}
		if(player.getServitor() != null)
		{
			Functions.show("scripts/services/petevolve/error_4.htm", player, npc);
			return;
		}
		ItemInstance collar = player.getInventory().getItemByItemId(fwd ? FENRIR_NECKLACE : WHITE_FENRIR_NECKLACE);
		if(collar == null)
		{
			Functions.show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		int npcId = PetDataTable.getSummonId(collar);
		if(npcId == 0)
			return;
		NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(npcId);
		if(petTemplate == null)
			return;
		PetInstance pet = PetInstance.restore(collar, petTemplate, player);

		if(npcId != (fwd ? FENRIR : WHITE_FENRIR))
		{
			Functions.show("scripts/services/petevolve/error_2.htm", player, npc);
			return;
		}
		if(pet.getLevel() < 55)
		{
			Functions.show("scripts/services/petevolve/error_lvl_fenrir.htm", player, npc);
			return;
		}

		collar.setItemId(fwd ? WHITE_FENRIR_NECKLACE : FENRIR_NECKLACE);
		collar.setJdbcState(JdbcEntityState.UPDATED);
		collar.update();
		player.sendItemList(false);
		player.sendPacket(SystemMessage.obtainItems((fwd ? WHITE_FENRIR_NECKLACE : FENRIR_NECKLACE), 1, 0));
		Functions.show("scripts/services/petevolve/end_msg2_fenrir.htm", player, npc);
	}

	@Bypass("services.petevolve.clanhall:fenrirW")
	public void fenrirW(Player player, NpcInstance npc, String[] direction)
	{
		if(player == null || npc == null)
			return;
		boolean fwd = Integer.parseInt(direction[0]) == 1;

		if(player.getInventory().getCountOf(fwd ? WHITE_WOLF_NECKLACE : WHITE_FENRIR_NECKLACE) > 1)
		{
			Functions.show("scripts/services/petevolve/error_3.htm", player, npc);
			return;
		}
		if(player.getServitor() != null)
		{
			Functions.show("scripts/services/petevolve/error_4.htm", player, npc);
			return;
		}
		ItemInstance collar = player.getInventory().getItemByItemId(fwd ? WHITE_WOLF_NECKLACE : WHITE_FENRIR_NECKLACE);
		if(collar == null)
		{
			Functions.show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		int npcId = PetDataTable.getSummonId(collar);
		if(npcId == 0)
			return;
		NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(npcId);
		if(petTemplate == null)
			return;
		PetInstance pet = PetInstance.restore(collar, petTemplate, player);

		if(npcId != (fwd ? WHITE_WOLF : WHITE_FENRIR))
		{
			Functions.show("scripts/services/petevolve/error_2.htm", player, npc);
			return;
		}
		if(pet.getLevel() < 70)
		{
			Functions.show("scripts/services/petevolve/no_level_gw.htm", player, npc);
			return;
		}

		collar.setItemId(fwd ? WHITE_FENRIR_NECKLACE : WHITE_WOLF_NECKLACE);
		collar.setJdbcState(JdbcEntityState.UPDATED);
		collar.update();
		player.sendItemList(false);
		player.sendPacket(SystemMessage.obtainItems((fwd ? WHITE_FENRIR_NECKLACE : WHITE_WOLF_NECKLACE), 1, 0));
		Functions.show("scripts/services/petevolve/yes_wolf.htm", player, npc);
	}

	@Bypass("services.petevolve.clanhall:wstrider")
	public void wstrider(Player player, NpcInstance npc, String[] direction)
	{
		if(player == null || npc == null)
			return;
		boolean fwd = Integer.parseInt(direction[0]) == 1;

		if(player.getInventory().getCountOf(fwd ? WIND_STRIDER_ITEM : RED_WS_ITEM) > 1)
		{
			Functions.show("scripts/services/petevolve/error_3.htm", player, npc);
			return;
		}
		if(player.getServitor() != null)
		{
			Functions.show("scripts/services/petevolve/error_4.htm", player, npc);
			return;
		}
		ItemInstance collar = player.getInventory().getItemByItemId(fwd ? WIND_STRIDER_ITEM : RED_WS_ITEM);
		if(collar == null)
		{
			Functions.show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		int npcId = PetDataTable.getSummonId(collar);
		if(npcId == 0)
			return;
		NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(npcId);
		if(petTemplate == null)
			return;
		PetInstance pet = PetInstance.restore(collar, petTemplate, player);

		if(npcId != (fwd ? WIND_STRIDER : RED_WIND_STRIDER))
		{
			Functions.show("scripts/services/petevolve/error_2.htm", player, npc);
			return;
		}
		if(pet.getLevel() < 55)
		{
			Functions.show("scripts/services/petevolve/error_lvl_strider.htm", player, npc);
			return;
		}

		collar.setItemId(fwd ? RED_WS_ITEM : WIND_STRIDER_ITEM);
		collar.setJdbcState(JdbcEntityState.UPDATED);
		collar.update();
		player.sendItemList(false);
		player.sendPacket(SystemMessage.obtainItems((fwd ? RED_WS_ITEM : WIND_STRIDER_ITEM), 1, 0));
		Functions.show("scripts/services/petevolve/end_msg_strider.htm", player, npc);
	}

	@Bypass("services.petevolve.clanhall:sstrider")
	public void sstrider(Player player, NpcInstance npc, String[] direction)
	{
		if(player == null || npc == null)
			return;
		boolean fwd = Integer.parseInt(direction[0]) == 1;

		if(player.getInventory().getCountOf(fwd ? STAR_STRIDER_ITEM : RED_SS_ITEM) > 1)
		{
			Functions.show("scripts/services/petevolve/error_3.htm", player, npc);
			return;
		}
		if(player.getServitor() != null)
		{
			Functions.show("scripts/services/petevolve/error_4.htm", player, npc);
			return;
		}
		ItemInstance collar = player.getInventory().getItemByItemId(fwd ? STAR_STRIDER_ITEM : RED_SS_ITEM);
		if(collar == null)
		{
			Functions.show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		int npcId = PetDataTable.getSummonId(collar);
		if(npcId == 0)
			return;
		NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(npcId);
		if(petTemplate == null)
			return;
		PetInstance pet = PetInstance.restore(collar, petTemplate, player);

		if(npcId != (fwd ? STAR_STRIDER : RED_STAR_STRIDER))
		{
			Functions.show("scripts/services/petevolve/error_2.htm", player, npc);
			return;
		}
		if(pet.getLevel() < 55)
		{
			Functions.show("scripts/services/petevolve/error_lvl_strider.htm", player, npc);
			return;
		}

		collar.setItemId(fwd ? RED_SS_ITEM : STAR_STRIDER_ITEM);
		collar.setJdbcState(JdbcEntityState.UPDATED);
		collar.update();
		player.sendItemList(false);
		player.sendPacket(SystemMessage.obtainItems((fwd ? RED_SS_ITEM : STAR_STRIDER_ITEM), 1, 0));
		Functions.show("scripts/services/petevolve/end_msg_strider.htm", player, npc);
	}

	@Bypass("services.petevolve.clanhall:tstrider")
	public void tstrider(Player player, NpcInstance npc, String[] direction)
	{
		if(player == null || npc == null)
			return;
		boolean fwd = Integer.parseInt(direction[0]) == 1;

		if(player.getInventory().getCountOf(fwd ? TWILING_STRIDER_ITEM : RED_TW_ITEM) > 1)
		{
			Functions.show("scripts/services/petevolve/error_3.htm", player, npc);
			return;
		}
		if(player.getServitor() != null)
		{
			Functions.show("scripts/services/petevolve/error_4.htm", player, npc);
			return;
		}
		ItemInstance collar = player.getInventory().getItemByItemId(fwd ? TWILING_STRIDER_ITEM : RED_TW_ITEM);
		if(collar == null)
		{
			Functions.show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		int npcId = PetDataTable.getSummonId(collar);
		if(npcId == 0)
			return;
		NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(npcId);
		if(petTemplate == null)
			return;
		PetInstance pet = PetInstance.restore(collar, petTemplate, player);

		if(npcId != (fwd ? TWILING_STRIDER : RED_TWILING_STRIDER))
		{
			Functions.show("scripts/services/petevolve/error_2.htm", player, npc);
			return;
		}
		if(pet.getLevel() < 55)
		{
			Functions.show("scripts/services/petevolve/error_lvl_strider.htm", player, npc);
			return;
		}

		collar.setItemId(fwd ? RED_TW_ITEM : TWILING_STRIDER_ITEM);
		collar.setJdbcState(JdbcEntityState.UPDATED);
		collar.update();
		player.sendItemList(false);
		player.sendPacket(SystemMessage.obtainItems((fwd ? RED_TW_ITEM : TWILING_STRIDER_ITEM), 1, 0));
		Functions.show("scripts/services/petevolve/end_msg_strider.htm", player, npc);
	}
}