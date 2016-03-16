package services.petevolve;

import org.mmocore.commons.dao.JdbcEntityState;
import org.mmocore.gameserver.Config;
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
 * Date: 07.06.2008
 * Time: 0:37:55
 */
public class ibkookaburra
{
	private static final int BABY_KOOKABURRA = PetDataTable.BABY_KOOKABURRA_ID;
	private static final int BABY_KOOKABURRA_OCARINA = L2Pet.BABY_KOOKABURRA.getControlItemId();
	private static final int IN_KOOKABURRA_OCARINA = L2Pet.IMPROVED_BABY_KOOKABURRA.getControlItemId();

	@Bypass("services.petevolve.ibkookaburra:evolve")
	public void evolve(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null || npc == null)
			return;
		Servitor pet = player.getServitor();
		if(player.getInventory().getItemByItemId(BABY_KOOKABURRA_OCARINA) == null)
		{
			Functions.show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		else if(pet == null || pet.isDead())
		{
			Functions.show("scripts/services/petevolve/evolve_no.htm", player, npc);
			return;
		}
		if(pet.getNpcId() != BABY_KOOKABURRA)
		{
			Functions.show("scripts/services/petevolve/no_pet.htm", player, npc);
			return;
		}
		if(Config.ALT_IMPROVED_PETS_LIMITED_USE && !player.isMageClass())
		{
			Functions.show("scripts/services/petevolve/no_class_m.htm", player, npc);
			return;
		}
		if(pet.getLevel() < 55)
		{
			Functions.show("scripts/services/petevolve/no_level.htm", player, npc);
			return;
		}

		int controlItemId = player.getServitor().getControlItemObjId();
		player.getServitor().unSummon(false, false);

		ItemInstance control = player.getInventory().getItemByObjectId(controlItemId);
		control.setItemId(IN_KOOKABURRA_OCARINA);
		control.setEnchantLevel(L2Pet.IMPROVED_BABY_KOOKABURRA.getMinLevel());
		control.setJdbcState(JdbcEntityState.UPDATED);
		control.update();
		player.sendItemList(false);

		Functions.show("scripts/services/petevolve/yes_pet.htm", player, npc);
	}
}