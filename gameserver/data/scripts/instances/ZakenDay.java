package instances;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.Zone;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.network.l2.s2c.ExSendUIEvent;
import org.mmocore.gameserver.utils.Location;

/**
 * Класс контролирует дневного Закена
 *
 * @author pchayka
 */

public class ZakenDay extends Reflection
{
	private static final int Anchor = 32468;
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
	private ZoneListener _epicZoneListener = new ZoneListener();

	@Override
	protected void onCreate()
	{
		super.onCreate();
		addSpawnWithoutRespawn(Anchor, zakenTp[Rnd.get(zakenTp.length)], 0);
		_savedTime = System.currentTimeMillis();
		getZone("[zaken_day_epic]").addListener(_epicZoneListener);
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

	public void notifyZakenDeath()
	{
		for(Player p : getPlayers())
			p.sendPacket(new ExSendUIEvent(p, true, true, 0, 0));
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if(cha.isPlayable() && cha.getLevel() > 65)
			{
				Playable p = ((Playable) cha);
				p.paralizeMe(cha, Skill.SKILL_RAID_CURSE);
				p.teleToLocation(getInstancedZone().getTeleportCoord());
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
		}
	}


}