package npc.model.residences.fortress.siege;

import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.base.SpecialEffectState;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * Данный инстанс используется NPC Ballista на осадах фортов
 * @author SYS
 */
public class BallistaInstance extends NpcInstance
{
	public BallistaInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setUndying(SpecialEffectState.TRUE);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);

		if(killer == null || !killer.isPlayer())
			return;

		Player player = killer.getPlayer();
		if(player.getClan() == null)
			return;

		player.getClan().incReputation(30, false, "Ballista " + getTitle());
		player.sendPacket(new SystemMessage(SystemMsg.THE_BALLISTA_HAS_BEEN_SUCCESSFULLY_DESTROYED));
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return true;
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}
}