package ai.kamaloka;

import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * Босс 43й камалоки
 *
 * @author pchayka
 */
public class BladeOtis extends Mystic
{
	private static final int _followerId = 18563;  // Follower of Otis
	private NpcInstance actor = getActor();

	private long _spawnTimer = 0L;
	private int _spawnCounter = 0;
	private final static long _spawnInterval = 60000L;
	private final static int _spawnLimit = 10;

	public BladeOtis(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void thinkAttack()
	{
		if(_spawnTimer == 0)
			_spawnTimer = System.currentTimeMillis();
		if(actor.getCurrentHpPercents() < 60 && _spawnCounter < _spawnLimit && _spawnTimer + _spawnInterval < System.currentTimeMillis())
		{
			NpcUtils.spawnSingle(_followerId, Location.findPointToStay(actor.getLoc(), 200, actor.getGeoIndex()), actor.getReflection());
			_spawnTimer = System.currentTimeMillis();
			_spawnCounter++;
			ChatUtils.say(actor, NpcString.IF_YOU_THOUGHT_THAT_MY_SUBORDINATES_WOULD_BE_SO_FEW_YOU_ARE_MISTAKEN);
		}
		super.thinkAttack();
	}
}