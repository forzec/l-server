package services.petevolve;

import org.mmocore.commons.dao.JdbcEntityState;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Servitor;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.tables.PetDataTable;
import org.mmocore.gameserver.tables.PetDataTable.L2Pet;

/**
 * User: darkevil
 * Date: 02.06.2008
 * Time: 12:19:36
 */
public class fenrir
{
	private static final int GREAT_WOLF = PetDataTable.GREAT_WOLF_ID;
	private static final int GREAT_WOLF_NECKLACE = L2Pet.GREAT_WOLF.getControlItemId();
	private static final int FENRIR_NECKLACE = L2Pet.FENRIR_WOLF.getControlItemId();

	@Bypass("services.petevolve.fenrir:evolve")
	public void evolve(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null || npc == null)
			return;
		if(player.getInventory().getItemByItemId(GREAT_WOLF_NECKLACE) == null)
		{
			Functions.show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		Servitor pl_pet = player.getServitor();
		if(pl_pet == null || pl_pet.isDead())
		{
			Functions.show("scripts/services/petevolve/evolve_no.htm", player, npc);
			return;
		}
		if(pl_pet.getNpcId() != GREAT_WOLF)
		{
			Functions.show("scripts/services/petevolve/no_wolf.htm", player, npc);
			return;
		}
		if(pl_pet.getLevel() < 70)
		{
			Functions.show("scripts/services/petevolve/no_level_gw.htm", player, npc);
			return;
		}

		int controlItemId = player.getServitor().getControlItemObjId();
		player.getServitor().unSummon(false, false);

		ItemInstance control = player.getInventory().getItemByObjectId(controlItemId);
		control.setItemId(FENRIR_NECKLACE);
		control.setEnchantLevel(L2Pet.FENRIR_WOLF.getMinLevel());
		control.setJdbcState(JdbcEntityState.UPDATED);
		control.update();
		player.sendItemList(false);

		Functions.show("scripts/services/petevolve/yes_wolf.htm", player, npc);
	}
}