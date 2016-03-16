package ai.dragonvalley;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * @author pchayka
 */
public class ShadowSummoner extends DragonRaid
{
	private long _lastSpawnTime = 0;

	public ShadowSummoner(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void thinkAttack()
	{
		if(_actor.getCurrentHpPercents() < 50)
		{
			if(_lastSpawnTime + 60 * 1000L < System.currentTimeMillis())
			{
				_lastSpawnTime = System.currentTimeMillis();
				NpcInstance minion = NpcUtils.spawnSingle(25731, Location.findPointToStay(_actor, 250));
				minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getAttackTarget(), 5000);
			}
		}
		super.thinkAttack();
	}

	// Should be Mystic type
	@Override
	public int getRateDEBUFF()
	{
		return 15;
	}

	@Override
	public int getRateDAM()
	{
		return 50;
	}

}