package npc.model;

import instances.Frintezza;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public final class FrintezzaGatekeeperInstance extends NpcInstance
{
	private static final int frintezzaIzId = 136;

	public FrintezzaGatekeeperInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("request_frintezza"))
		{
			if (player.getActiveReflection() != null)
				ReflectionUtils.simpleEnterInstancedZone(player, Frintezza.class, frintezzaIzId);
			else if (ItemFunctions.getItemCount(player, 8073) > 0)
			{
				if (ReflectionUtils.simpleEnterInstancedZone(player, Frintezza.class, frintezzaIzId) != null)
					ItemFunctions.deleteItem(player, 8073, 1);
			}
			else
				player.sendPacket(new SystemMessage(SystemMsg.C1S_ITEM_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
		}
		else
			super.onBypassFeedback(player, command);
	}
}