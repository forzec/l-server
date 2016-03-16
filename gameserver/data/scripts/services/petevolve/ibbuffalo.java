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
 * Date: 04.06.2008
 * Time: 1:06:26
 */
public class ibbuffalo
{
	private static final int BABY_BUFFALO = PetDataTable.BABY_BUFFALO_ID;
	private static final int BABY_BUFFALO_PANPIPE = L2Pet.BABY_BUFFALO.getControlItemId();
	private static final int IN_BABY_BUFFALO_NECKLACE = L2Pet.IMPROVED_BABY_BUFFALO.getControlItemId();

	@Bypass("services.petevolve.ibbuffalo:evolve")
	public void evolve(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null || npc == null)
			return;
		Servitor pl_pet = player.getServitor();
		if(player.getInventory().getItemByItemId(BABY_BUFFALO_PANPIPE) == null)
		{
			Functions.show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		if(pl_pet == null || pl_pet.isDead())
		{
			Functions.show("scripts/services/petevolve/evolve_no.htm", player, npc);
			return;
		}
		if(pl_pet.getNpcId() != BABY_BUFFALO)
		{
			Functions.show("scripts/services/petevolve/no_pet.htm", player, npc);
			return;
		}
		if(Config.ALT_IMPROVED_PETS_LIMITED_USE && player.isMageClass())
		{
			Functions.show("scripts/services/petevolve/no_class_w.htm", player, npc);
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
		control.setItemId(IN_BABY_BUFFALO_NECKLACE);
		control.setEnchantLevel(L2Pet.IMPROVED_BABY_BUFFALO.getMinLevel());
		control.setJdbcState(JdbcEntityState.UPDATED);
		control.update();
		player.sendItemList(false);

		Functions.show("scripts/services/petevolve/yes_pet.htm", player, npc);
	}
}