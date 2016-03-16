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
 * Date: 16.05.2008
 * Time: 12:19:36
 */
public class wolfevolve
{
	private static final int WOLF = PetDataTable.PET_WOLF_ID; //Чтоб было =), проверка на Wolf
	private static final int WOLF_COLLAR = L2Pet.WOLF.getControlItemId(); // Ошейник Wolf
	private static final int GREAT_WOLF_NECKLACE = L2Pet.GREAT_WOLF.getControlItemId(); // Ожерелье Great Wolf

	@Bypass("services.petevolve.wolfevolve:evolve")
	public void evolve(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null || npc == null)
			return;
		Servitor pl_pet = player.getServitor();
		if(player.getInventory().getItemByItemId(WOLF_COLLAR) == null)
		{
			Functions.show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		if(pl_pet == null || pl_pet.isDead())
		{
			Functions.show("scripts/services/petevolve/evolve_no.htm", player, npc);
			return;
		}
		if(pl_pet.getNpcId() != WOLF)
		{
			Functions.show("scripts/services/petevolve/no_wolf.htm", player, npc);
			return;
		}
		if(pl_pet.getLevel() < 55)
		{
			Functions.show("scripts/services/petevolve/no_level.htm", player, npc);
			return;
		}

		int controlItemId = player.getServitor().getControlItemObjId();
		player.getServitor().unSummon(false, false);

		ItemInstance control = player.getInventory().getItemByObjectId(controlItemId);
		control.setItemId(GREAT_WOLF_NECKLACE);
		control.setEnchantLevel(L2Pet.GREAT_WOLF.getMinLevel());
		control.setJdbcState(JdbcEntityState.UPDATED);
		control.update();
		player.sendItemList(false);

		Functions.show("scripts/services/petevolve/yes_wolf.htm", player, npc);
	}
}