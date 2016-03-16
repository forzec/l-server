package npc.model;

import org.mmocore.gameserver.model.instances.BossInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;

public class AntharasInstance extends BossInstance
{
	public AntharasInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	protected int getMinChannelSizeForLock()
	{
		return 0;
	}

	@Override
	public void notifyMinionDied(NpcInstance minion)
	{
		// spawned from ai
	}

	@Override
	public Location getRndMinionPosition()
	{
		return Location.findPointToStay(this, 400, 700);
	}
}