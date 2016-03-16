package npc.model.beastfarm;

import org.mmocore.gameserver.ai.CharacterAI;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import ai.beastfarm.BeastAI;

/**
 * @author VISTALL
 * @date 15:22/30.10.2011
 */
public class FeedableBeastInstance extends MonsterInstance
{
	// 0 - drop, 1 - exp
	private int[] _growNpcIds;
	private int[] _lastGrowNpcIds;
	private int _tamedNpcId;
	private int _step;

	private int _feederObjectId;

	public FeedableBeastInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);

		_step = getParameter("step", 0);
		_tamedNpcId = getParameter("tamed_npc_id", 0);
		_growNpcIds = getParameters().getIntegerArray("grow_npcs");
		_lastGrowNpcIds = getParameters().getIntegerArray("last_grow_npcs");
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();
		_feederObjectId = 0;
	}

	@Override
	public CharacterAI getAI()
	{
		if(_ai == null)
			synchronized (this)
			{
				if(_ai == null)
					_ai = new BeastAI(this);
			}

		return _ai;
	}

	@Override
	public void endDecayTask()
	{
		if(_decayTask != null)
		{
			_decayTask.cancel(false);
			_decayTask = null;
		}
		onDecay();
	}

	public int getTamedNpcId()
	{
		return _tamedNpcId;
	}

	public int[] getLastGrowNpcIds()
	{
		return _lastGrowNpcIds;
	}

	public int[] getGrowNpcIds()
	{
		return _growNpcIds;
	}

	public int getStep()
	{
		return _step;
	}

	public int getFeederObjectId()
	{
		return _feederObjectId;
	}

	public void setFeederObjectId(int feederObjectId)
	{
		_feederObjectId = feederObjectId;
	}
}
