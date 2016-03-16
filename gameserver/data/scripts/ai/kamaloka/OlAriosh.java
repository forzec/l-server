package ai.kamaloka;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * Босс 26й камалоки
 *
 * @author pchayka
 */
public class OlAriosh extends Fighter
{
	private static final int _followerId = 18556;  // Follower of Ariosh
	private NpcInstance actor = getActor();
	private long _spawnTimer = 0L;
	private final static long _spawnInterval = 60000L;
	NpcInstance follower = null;

	public OlAriosh(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void thinkAttack()
	{
		if((follower == null || follower.isDead()) && _spawnTimer + _spawnInterval < System.currentTimeMillis())
		{
			follower = NpcUtils.spawnSingle(_followerId, Location.findPointToStay(actor.getLoc(), 200, actor.getGeoIndex()), actor.getReflection());
			follower.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getAttackTarget(), 1000);
			_spawnTimer = System.currentTimeMillis();
			ChatUtils.say(actor, NpcString.WHAT_ARE_YOU_DOING_HURRY_UP_AND_HELP_ME);
		}
		super.thinkAttack();
	}
}