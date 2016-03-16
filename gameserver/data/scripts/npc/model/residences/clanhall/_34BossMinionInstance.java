package npc.model.residences.clanhall;

import org.mmocore.gameserver.model.Creature;
import npc.model.residences.SiegeGuardInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * @author VISTALL
 * @date 17:50/13.05.2011
 */
public abstract class _34BossMinionInstance extends SiegeGuardInstance implements _34SiegeGuard
{
	public _34BossMinionInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onDeath(Creature killer)
	{
		setCurrentHp(1, true);
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();

		ChatUtils.shout(this, spawnChatSay());
	}

	public abstract NpcString spawnChatSay();
	public abstract NpcString teleChatSay();
}
