package instances;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.network.l2.s2c.ExSendUIEvent;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;

/**
 * Класс контролирует высшего дневного Закена
 *
 * @author pchayka
 */

public class ZakenDay83 extends Reflection
{
	private static final int Anchor = 32468;
	private static final int UltraDayZaken = 29181;
	private static Location[] zakenTp = {
			new Location(55272, 219112, -3496),
			new Location(56296, 218072, -3496),
			new Location(54232, 218072, -3496),
			new Location(54248, 220136, -3496),
			new Location(56296, 220136, -3496),
			new Location(55272, 219112, -3224),
			new Location(56296, 218072, -3224),
			new Location(54232, 218072, -3224),
			new Location(54248, 220136, -3224),
			new Location(56296, 220136, -3224),
			new Location(55272, 219112, -2952),
			new Location(56296, 218072, -2952),
			new Location(54232, 218072, -2952),
			new Location(54248, 220136, -2952),
			new Location(56296, 220136, -2952)
	};
	private long _savedTime = 0L;

	@Override
	protected void onCreate()
	{
		super.onCreate();
		addSpawnWithoutRespawn(Anchor, zakenTp[Rnd.get(zakenTp.length)], 0);
		_savedTime = System.currentTimeMillis();
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		super.onPlayerEnter(player);
		player.sendPacket(new ExSendUIEvent(player, false, true, (int) (System.currentTimeMillis() - _savedTime) / 1000, 0, NpcString.ELAPSED_TIME));
	}

	@Override
	public void onPlayerExit(Player player)
	{
		super.onPlayerExit(player);
		player.sendPacket(new ExSendUIEvent(player, true, true, 0, 0));
	}

	public void notifyZakenDeath(NpcInstance zaken)
	{
		if(zaken.isNpc() && zaken.getNpcId() == UltraDayZaken)
		{
			long _timePassed = System.currentTimeMillis() - _savedTime;
			for(Player p : getPlayers())
			{
				if(_timePassed < 5 * 60 * 1000)
				{
					if(Rnd.chance(50))
						ItemFunctions.addItem(p, 15763, 1);
				}
				else if(_timePassed < 10 * 60 * 1000)
				{
					if(Rnd.chance(30))
						ItemFunctions.addItem(p, 15764, 1);
				}
				else if(_timePassed < 15 * 60 * 1000)
				{
					if(Rnd.chance(25))
						ItemFunctions.addItem(p, 15763, 1);
				}
			}
			for(Player p : getPlayers())
				p.sendPacket(new ExSendUIEvent(p, true, true, 0, 0));
		}
	}
}