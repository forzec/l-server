package services;

import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.utils.ItemFunctions;

public class EchoCrystals
{
	private static final int PRICE = 200;
	private static final int[][] CRYSTALS = { { 4411, 4410 }, { 4412, 4409 }, { 4413, 4408 }, { 4414, 4420 }, { 4415, 4421 }, { 4417, 4419 }, { 4416, 4418 } };

	@Bypass("services.EchoCrystals:MakeEchoCrystal")
	public void MakeEchoCrystal(Player player, NpcInstance npc, String[] param)
	{
		if(player == null)
			return;

		if(!NpcInstance.canBypassCheck(player, player.getLastNpc()))
			return;

		if(param.length < 1)
			return;

		final int idx = Integer.parseInt(param[0]);
		if (idx < 0 || idx >= CRYSTALS.length)
			return;

		if(ItemFunctions.getItemCount(player, CRYSTALS[idx][1]) == 0)
		{
			player.getLastNpc().onBypassFeedback(player, "Chat 1");
			return;
		}

		if (!player.reduceAdena(PRICE, true))
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		ItemFunctions.addItem(player, CRYSTALS[idx][0], 1);
	}
}