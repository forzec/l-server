package ai.custom;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;

/**
 * @author pchayka
 */
public class SSQAnakimMinion extends Fighter
{
	private final int[] _enemies = { 32717, 32716 };

	public SSQAnakimMinion(NpcInstance actor)
	{
		super(actor);
		actor.setHasChatWindow(false);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		ThreadPoolManager.getInstance().schedule(new Attack(), 3000);
	}

	public class Attack extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(getEnemy() != null)
				getActor().getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, getEnemy(), null, 10000000);
		}
	}

	private NpcInstance getEnemy()
	{
		List<NpcInstance> around = getActor().getAroundNpc(1000, 300);
		if(around != null && !around.isEmpty())
			for(NpcInstance npc : around)
				if(ArrayUtils.contains(_enemies, npc.getNpcId()))
					return npc;
		return null;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}