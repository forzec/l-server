package ai;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;

public class HekatonPrime extends Fighter
{
	private long _lastTimeAttacked;

	public HekatonPrime(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		_lastTimeAttacked = System.currentTimeMillis();
	}

	@Override
	protected boolean thinkActive()
	{
		if(_lastTimeAttacked + 600000 < System.currentTimeMillis())
		{
			if(getActor().hasMinions())
				getActor().getMinionList().deleteMinions();
			getActor().deleteMe();
			return true;
		}
		return false;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		_lastTimeAttacked = System.currentTimeMillis();
		super.onEvtAttacked(attacker, skill, damage);
	}
}