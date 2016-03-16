package npc.model;

import org.mmocore.gameserver.model.instances.BossInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class CoreInstance extends BossInstance
{
	public CoreInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	protected int getMinChannelSizeForLock()
	{
		return 36;
	}

	@Override
	protected void onChannelLock(String leaderName)
	{
		broadcastPacket(new ExShowScreenMessage(NpcString.CORE_S1_COMMAND_CHANNEL_HAS_LOOTING_RIGHTS, 4000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, false, leaderName));		
	}

	@Override
	protected void onChannelUnlock()
	{
		broadcastPacket(new ExShowScreenMessage(NpcString.CORE_LOOTING_RULES_ARE_NO_LONGER_ACTIVE, 4000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, false));
	}
}