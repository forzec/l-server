package npc.model.residences.clanhall;

import java.util.List;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.entity.events.impl.ClanHallTeamBattleEvent;
import org.mmocore.gameserver.model.entity.events.objects.CTBTeamObject;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 15:13/27.04.2011
 */
public class MatchMassTeleporterInstance extends NpcInstance
{
	private int _flagId;
	private long _timeout;

	public MatchMassTeleporterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		_flagId = template.getAIParams().getInteger("flag");
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		ClanHall clanHall = getClanHall();
		ClanHallTeamBattleEvent siegeEvent = clanHall.getSiegeEvent();

		if(_timeout > System.currentTimeMillis())
		{
			showChatWindow(player, "residence2/clanhall/agit_mass_teleporter001.htm");
			return;
		}

		if(isInRange(player, INTERACTION_DISTANCE))
		{
			_timeout = System.currentTimeMillis() + 60000L;

			List<CTBTeamObject> locs = siegeEvent.getObjects(ClanHallTeamBattleEvent.TRYOUT_PART);

			CTBTeamObject object = locs.get(_flagId);
			if(object.getFlag() != null)
			{
				for(Player $player : World.getAroundPlayers(this, 400, 100))
					$player.teleToLocation(Location.findPointToStay(object.getFlag(), 100, 125));
			}
		}
	}
}
