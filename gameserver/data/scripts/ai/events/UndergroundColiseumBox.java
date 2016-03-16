package ai.events;

import java.util.concurrent.Future;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author VISTALL
 * @date 17:59/19.05.2012
 */
public class UndergroundColiseumBox extends DefaultAI
{
	private Future<?> _despawnTask;

	public UndergroundColiseumBox(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public void onEvtSpawn()
	{
		super.onEvtSpawn();

		_despawnTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			protected void runImpl() throws Exception
			{
				getActor().decayOrDelete();
			}
		}, 20000L);
	}

	@Override
	public void onEvtDeSpawn()
	{
		super.onEvtDeSpawn();

		cancel();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		cancel();
	}

	@Override
	protected void onEvtAggression(Creature attacker, int aggro)
	{
		//
	}

	private void cancel()
	{
		if(_despawnTask != null)
		{
			_despawnTask.cancel(false);
			_despawnTask = null;
		}
	}
}
