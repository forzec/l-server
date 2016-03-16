package services;

import java.util.ArrayList;
import java.util.List;

import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.instancemanager.ReflectionManager;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Zone;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.PositionUtils;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeleToGH implements OnInitScriptListener
{
	private static final Logger _log = LoggerFactory.getLogger(TeleToGH.class);

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			// обрабатывать вход в зону не надо, только выход
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
			Player player = cha.getPlayer();
			if(player != null)
				if(Config.SERVICES_GIRAN_HARBOR_ENABLED && player.getReflection() == ReflectionManager.GIRAN_HARBOR && player.isVisible())
				{
					double angle = PositionUtils.convertHeadingToDegree(cha.getHeading()); // угол в градусах
					double radian = Math.toRadians(angle - 90); // угол в радианах
					cha.teleToLocation((int) (cha.getX() + 50 * Math.sin(radian)), (int) (cha.getY() - 50 * Math.cos(radian)), cha.getZ());
				}
		}
	}

	private static List<NpcInstance> _spawns = new ArrayList<NpcInstance>();
	private static Zone _zone = ReflectionUtils.getZone("[giran_harbor_offshore]");
	private static ZoneListener _zoneListener;

	@Override
	public void onInit()
	{
		if(!Config.SERVICES_GIRAN_HARBOR_ENABLED)
			return;

		ReflectionManager.GIRAN_HARBOR.setCoreLoc(new Location(47416, 186568, -3480));

		// spawn wh keeper
		_spawns.add(NpcUtils.spawnSingle(30086, new Location(48059, 186791, -3512, 42000), ReflectionManager.GIRAN_HARBOR));
		// spawn grocery trader
		_spawns.add(NpcUtils.spawnSingle(32169, new Location(48146, 186753, -3512, 42000), ReflectionManager.GIRAN_HARBOR));
		// spawn gk
		_spawns.add(NpcUtils.spawnSingle(13129, new Location(47984, 186832, -3512, 42000), ReflectionManager.GIRAN_HARBOR));
		// spawn Orion the Cat
		_spawns.add(NpcUtils.spawnSingle(31860, new Location(48129, 186828, -3512, 45452), ReflectionManager.GIRAN_HARBOR));
		// spawn blacksmith (Pushkin)
		_spawns.add(NpcUtils.spawnSingle(30300, new Location(48102, 186772, -3512, 42000), ReflectionManager.GIRAN_HARBOR));
		// spawn Item Broker
		_spawns.add(NpcUtils.spawnSingle(32320, new Location(47772, 186905, -3480, 42000), ReflectionManager.GIRAN_HARBOR));
		_spawns.add(NpcUtils.spawnSingle(32320, new Location(46360, 187672, -3480, 42000), ReflectionManager.GIRAN_HARBOR));
		_spawns.add(NpcUtils.spawnSingle(32320, new Location(49016, 185960, -3480, 42000), ReflectionManager.GIRAN_HARBOR));

		_zoneListener = new ZoneListener();
		_zone.addListener(_zoneListener);
		_zone.setReflection(ReflectionManager.GIRAN_HARBOR);
		_zone.setActive(true);
		Zone zone = ReflectionUtils.getZone("[giran_harbor_peace_alt]");
		zone.setReflection(ReflectionManager.GIRAN_HARBOR);
		zone.setActive(true);
		zone = ReflectionUtils.getZone("[giran_harbor_no_trade]");
		zone.setReflection(ReflectionManager.GIRAN_HARBOR);
		zone.setActive(true);

		_log.info("Loaded Service: Teleport to Giran Harbor");
	}

	@Bypass("services.TeleToGH:toGH")
	public void toGH(Player player, NpcInstance npc, String... arg)
	{
		if(player == null || npc == null)
			return;

		if(!NpcInstance.canBypassCheck(player, npc) || npc.checkForDominionWard(player))
			return;

		player.setVar("backCoords", player.getLoc().toXYZString(), -1);
		player.teleToLocation(Location.findPointToStay(_zone.getSpawn(), 30, 200, ReflectionManager.GIRAN_HARBOR.getGeoIndex()), ReflectionManager.GIRAN_HARBOR);
	}
}