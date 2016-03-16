package ai.kamaloka;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * Босс 36й камалоки
 *
 * @author pchayka
 */
public class SeerFlouros extends Mystic
{
	private static final int _followerId = 18560;  // Follower of Flouros
	private NpcInstance actor = getActor();

	private long _spawnTimer = 0L;
	private int _spawnCounter = 0;
	private final static long _spawnInterval = 60000L;
	private final static int _spawnLimit = 10;

	public SeerFlouros(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void thinkAttack()
	{
		if(_spawnTimer == 0)
			_spawnTimer = System.currentTimeMillis();
		if(_spawnCounter < _spawnLimit && _spawnTimer + _spawnInterval < System.currentTimeMillis())
		{
			NpcInstance follower = NpcUtils.spawnSingle(_followerId, Location.findPointToStay(actor.getLoc(), 200, actor.getGeoIndex()), actor.getReflection());
			follower.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getAttackTarget(), 1000);
			_spawnTimer = System.currentTimeMillis();
			_spawnCounter++;
		}
		super.thinkAttack();
	}
}