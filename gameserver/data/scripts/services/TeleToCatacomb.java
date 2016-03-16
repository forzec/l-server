package services;

import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;

public class TeleToCatacomb
{
	@Bypass("services.TeleToCatacomb:list")
	public void list(Player player, NpcInstance npc, String[] var)
	{
		if(!Config.ALT_TELE_TO_CATACOMBS)
			return;

		HtmlMessage message = new HtmlMessage(npc);
		message.setFile(player.getLevel() <= Config.GATEKEEPER_FREE ? "scripts/services/tele_catacomb_free.htm" : "scripts/services/tele_catacomb.htm");
		player.sendPacket(message);
	}
}