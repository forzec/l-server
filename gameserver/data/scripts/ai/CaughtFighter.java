package ai;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.scripts.Functions;

public class CaughtFighter extends Fighter
{
	private static final int TIME_TO_LIVE = 60000;
	private final long TIME_TO_DIE = System.currentTimeMillis() + TIME_TO_LIVE;

	public CaughtFighter(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		if(Rnd.chance(75))
			Functions.npcSay(getActor(), NpcString.YOUR_BAIT_WAS_TOO_DELICIOUS_NOW_I_WILL_KILL_YOU);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if(Rnd.chance(75))
			Functions.npcSay(getActor(), NpcString.I_WILL_TELL_FISH_NOT_TO_TAKE_YOUR_BAIT);

		super.onEvtDead(killer);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if(actor != null && System.currentTimeMillis() >= TIME_TO_DIE)
		{
			actor.deleteMe();
			return false;
		}
		return super.thinkActive();
	}
}