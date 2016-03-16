package npc.model.residences.castle;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.s2c.CastleSiegeInfo;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class CastleMessengerInstance extends NpcInstance
{
	public CastleMessengerInstance(int objectID, NpcTemplate template)
	{
		super(objectID, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		Castle castle = getCastle();

		if(player.isCastleLord(castle.getId()))
		{
			if(castle.getSiegeEvent().isInProgress())
				showChatWindow(player, "residence2/castle/sir_tyron021.htm");
			else
				showChatWindow(player, "residence2/castle/sir_tyron007.htm");
		}
		else if(castle.getSiegeEvent().isInProgress() || castle.getDominion().getSiegeEvent().isInProgress())
			showChatWindow(player, "residence2/castle/sir_tyron021.htm");
		else
			player.sendPacket(new CastleSiegeInfo(castle, player));
	}
}