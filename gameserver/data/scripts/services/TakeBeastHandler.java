package services;

import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.ItemFunctions;

public class TakeBeastHandler
{
	private static final int BEAST_WHIP = 15473;

	@Bypass("services.TakeBeastHandler:show")
	public void show(Player player, NpcInstance npc, String[] arg)
	{
		String htmltext;
		if(player.getLevel() < 82)
			htmltext = npc.getNpcId() + "-1.htm";
		else if(ItemFunctions.getItemCount(player, BEAST_WHIP) > 0)
			htmltext = npc.getNpcId() + "-2.htm";
		else
		{
			ItemFunctions.addItem(player, BEAST_WHIP, 1);
			htmltext = npc.getNpcId() + "-3.htm";
		}

		npc.showChatWindow(player, "default/" + htmltext);
	}
}
