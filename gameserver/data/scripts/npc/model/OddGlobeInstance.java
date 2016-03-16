package npc.model;

import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Zone;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.s2c.EventTrigger;
import org.mmocore.gameserver.network.l2.s2c.ExStartScenePlayer;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public final class OddGlobeInstance extends NpcInstance
{
	private static final int instancedZoneId = 151;

	public OddGlobeInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("monastery_enter"))
		{
			Reflection newRef = ReflectionUtils.simpleEnterInstancedZone(player, instancedZoneId);
			if (newRef != null)
			{
				ZoneListener zoneL = new ZoneListener();
				newRef.getZone("[ssq_holy_burial_ground]").addListener(zoneL);
				ZoneListener2 zoneL2 = new ZoneListener2();
				newRef.getZone("[ssq_holy_seal]").addListener(zoneL2);				
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		private boolean done = false;

		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			Player player = cha.getPlayer();
			if(player == null || !cha.isPlayer() || done)
				return;
			done = true;
			player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_HOLY_BURIAL_GROUND_OPENING);
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
		}
	}

	public class ZoneListener2 implements OnZoneEnterLeaveListener
	{
		private boolean done = false;

		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			Player player = cha.getPlayer();
			if(player == null || !cha.isPlayer())
				return;
			player.broadcastPacket(new EventTrigger(21100100, true));
			if(!done)
			{
				done = true;
				player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_SOLINA_TOMB_OPENING);
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
		}
	}
}