package ai.hellbound;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.GameObjectsStorage;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * Darion Challenger 7го этажа Tully Workshop
 * @author pchayka, доработка VAVAN.
 */
public class DarionChallenger extends Fighter
{
	private static final int TeleportCube = 32467;

	public DarionChallenger(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if(checkAllDestroyed())
			NpcUtils.spawnSingle(TeleportCube, new Location(-12527, 279714, -11622, 16384), 600000L);

		super.onEvtDead(killer);
	}

	private static boolean checkAllDestroyed()
	{
		if(!GameObjectsStorage.getAllByNpcId(25600, true).isEmpty())
			return false;
		if(!GameObjectsStorage.getAllByNpcId(25601, true).isEmpty())
			return false;
		if(!GameObjectsStorage.getAllByNpcId(25602, true).isEmpty())
			return false;

		return true;
	}
} 