package ai.kamaloka;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * Босс 63й камалоки
 *
 * @author pchayka
 */
public class VenomousStorace extends Fighter
{
	private static final int _followerId = 18572;  // Follower of Storace
	private NpcInstance actor = getActor();

	private long _spawnTimer = 0L;
	private final static long _spawnInterval = 50000L;

	public VenomousStorace(NpcInstance actor)
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
			NpcInstance follower = NpcUtils.spawnSingle(_followerId, Location.findPointToStay(actor.getLoc(), 200, actor.getGeoIndex()), actor.getReflection());
			ChatUtils.say(actor, NpcString.COME_OUT_MY_SUBORDINATE_I_SUMMON_YOU_TO_DRIVE_THEM_OUT);
			follower.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getAttackTarget(), 1000);
			_spawnTimer = System.currentTimeMillis();
		}
		super.thinkAttack();
	}
}