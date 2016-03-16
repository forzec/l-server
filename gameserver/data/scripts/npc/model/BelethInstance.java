package npc.model;

import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.RaidBossInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import bosses.BelethManager;

public class BelethInstance extends RaidBossInstance
{
	public BelethInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	protected int getMinChannelSizeForLock()
	{
		return -1;
	}

	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);
		BelethManager.setBelethDead();
	}
}