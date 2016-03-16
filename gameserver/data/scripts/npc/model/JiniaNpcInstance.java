package npc.model;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.ReflectionUtils;
import instances.FreyaHard;
import instances.FreyaNormal;

/**
 * @author pchayka
 */

public final class JiniaNpcInstance extends NpcInstance
{
	private static final int normalFreyaIzId = 139;
	private static final int extremeFreyaIzId = 144;

	public JiniaNpcInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("request_normalfreya"))
		{
			ReflectionUtils.simpleEnterInstancedZone(player, FreyaNormal.class, normalFreyaIzId);
		}
		else if(command.equalsIgnoreCase("request_extremefreya"))
		{
			ReflectionUtils.simpleEnterInstancedZone(player, FreyaHard.class, extremeFreyaIzId);
		}
		else if(command.equalsIgnoreCase("request_stone"))
		{
			if(player.getInventory().getCountOf(15469) > 0 || player.getInventory().getCountOf(15470) > 0)
				showChatWindow(player, 4);
			else if(player.getQuestState(10286) == null || !player.getQuestState(10286).isCompleted())
			{
				ItemFunctions.addItem(player, 15470, 1);
				showChatWindow(player, 5);
			}
			else
			{
				ItemFunctions.addItem(player, 15469, 1);
				showChatWindow(player, 5);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}