package services;

import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author pchayka
 */
public class Misc
{
	@Bypass("services.Misc:assembleAntharasCrystal")
	public void assembleAntharasCrystal(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null || npc == null || !NpcInstance.canBypassCheck(player, npc))
			return;

		if(ItemFunctions.getItemCount(player, 17266) < 1 || ItemFunctions.getItemCount(player, 17267) < 1)
		{
			Functions.show("teleporter/32864-2.htm", player, npc);
			return;
		}
		if(ItemFunctions.deleteItem(player, 17266, 1) && ItemFunctions.deleteItem(player, 17267, 1))
		{
			ItemFunctions.addItem(player, 17268, 1);
			Functions.show("teleporter/32864-3.htm", player, npc);
		}
	}
}