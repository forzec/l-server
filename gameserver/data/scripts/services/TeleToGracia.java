package services;

import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.scripts.Functions;

/**
 * @author Bonux
 **/
public class TeleToGracia
{
	@Bypass("services.TeleToGracia:tele")
	public void tele(Player player, NpcInstance npc, String[] arg)
	{
		if(player != null && npc != null)
			if(player.getLevel() < 75)
				Functions.show("teleporter/" + npc.getNpcId() + "-4.htm", player, npc);
			else if(player.reduceAdena(150000, true))
				player.teleToLocation(-149406, 255247, -80);
			else
				Functions.show("teleporter/" + npc.getNpcId() + "-2.htm", player, npc);
	}
}
