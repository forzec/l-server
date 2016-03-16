package handler.items;

import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.tables.PetDataTable.L2Pet;
import org.mmocore.gameserver.tables.SkillTable;

public class PetSummon extends ScriptItemHandler
{
	// all the items ids that this handler knowns
	private static final int _skillId = 2046;

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;
		Player player = (Player) playable;

		player.setPetControlItem(item);
		player.getAI().Cast(SkillTable.getInstance().getSkillEntry(_skillId, 1), player, false, true);
		return true;
	}

	@Override
	public final int[] getItemIds()
	{
		return L2Pet.PET_CONTROL_ITEMS;
	}
}