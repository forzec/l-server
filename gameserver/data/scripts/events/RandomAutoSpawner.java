package events;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.instancemanager.ReflectionManager;
import org.mmocore.gameserver.listener.actor.npc.OnSpawnListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.model.actor.listener.CharListenerList;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.NpcUtils;

public class RandomAutoSpawner implements OnInitScriptListener
{
	private int _npcId = 1561;
	private double _chance = 0.8;
	private SpawnListener _listener = new SpawnListener();

	@Override
	public void onInit()
	{
		CharListenerList.addGlobal(_listener);
	}

	public final class SpawnListener implements OnSpawnListener
	{
		@Override
		public void onSpawn(NpcInstance actor)
		{
			if (!actor.isMonster() || actor.isRaid() || actor.isBoss() || actor.isFlying())
				return;
			if (actor.getReflection() != ReflectionManager.DEFAULT)
				return;

			if (!Rnd.chance(_chance))
				return;

			final Location loc = Location.findPointToStay(actor, 300);
			Log.debug("Rudolf spawned at " + loc);
			NpcUtils.spawnSingle(_npcId, loc);
		}		
	}
}