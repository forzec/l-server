package handler.petition;

import org.mmocore.gameserver.handler.petition.IPetitionHandler;
import org.mmocore.gameserver.model.Player;

/**
 * @author VISTALL
 * @date 22:28/25.07.2011
 *
 * Простой пример хендлера петиций
 * Пишет в чат игроку то что он написал
 */
public class SimplePetitionHandler implements IPetitionHandler
{
	public SimplePetitionHandler()
	{
		//
	}

	@Override
	public void handle(Player player, int id, String txt)
	{
		player.sendMessage(txt);
	}
}
