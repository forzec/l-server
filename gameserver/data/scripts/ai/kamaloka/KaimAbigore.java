package ai.kamaloka;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * Босс 53й камалоки
 *
 * @author pchayka
 */
public class KaimAbigore extends Fighter
{
	private static final int _followerId = 18567;  // Follower of Abigore
	private NpcInstance actor = getActor();

	private long _spawnTimer = 0L;
	private final static long _spawnInterval = 60000L;

	public KaimAbigore(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void thinkAttack()
	{
		if(_spawnTimer == 0)
			_spawnTimer = System.currentTimeMillis();
		if(_spawnTimer + _spawnInterval < System.currentTimeMillis())
		{
			NpcInstance follower = NpcUtils.spawnSingle(_followerId, Location.findPointToStay(actor.getLoc(), 600, actor.getGeoIndex()), actor.getReflection());
			follower.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getAttackTarget(), 1000);
			_spawnTimer = System.currentTimeMillis();
		}
		super.thinkAttack();
	}
}