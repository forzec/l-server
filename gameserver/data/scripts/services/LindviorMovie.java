package services;

import java.util.List;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Zone;
import org.mmocore.gameserver.network.l2.components.SceneMovie;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * Раз в 3 часа на всей территории базы альянса на Грации всем внутри зоны показывается мувик
 *
 * @author pchayka
 */
public class LindviorMovie implements OnInitScriptListener
{
	@Override
	public void onInit()
	{
		Zone zone = ReflectionUtils.getZone("[keucereus_alliance_base_town_peace]");
		zone.setActive(true);

		long movieDelay = 3 * 60 * 60 * 1000L;
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new ShowLindviorMovie(zone), movieDelay, movieDelay);
	}

	public class ShowLindviorMovie extends RunnableImpl
	{
		Zone _zone;

		public ShowLindviorMovie(Zone zone)
		{
			_zone = zone;
		}

		@Override
		public void runImpl() throws Exception
		{
			List<Player> insideZoners = _zone.getInsidePlayers();

			if(insideZoners != null && !insideZoners.isEmpty())
				for(Player player : insideZoners)
					if(!player.isInBoat() && !player.isInFlyingTransform())
						player.showQuestMovie(SceneMovie.LINDVIOR_SPAWN);
		}
	}
}
