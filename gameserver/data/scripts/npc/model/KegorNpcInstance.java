package npc.model;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import instances.FreyaHard;
import instances.FreyaNormal;

/**
 * @author pchayka
 */
public class KegorNpcInstance extends NpcInstance
{
	public KegorNpcInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String htmlpath = null;
		if(getReflection().isDefault())
			htmlpath = "default/32761-default.htm";
		else if(getNpcState() == 2)
			htmlpath = "default/32761-1.htm";
		else
			htmlpath = "default/32761.htm";
		return htmlpath;
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("eliminate"))
		{
			Reflection r = getReflection();
			if(!r.isDefault())
			{
				if(!player.isInParty() || player.getParty().getCommandChannel() == null || player.getParty().getCommandChannel().getChannelLeader() != player)
					showChatWindow(player, "default/32761-2.htm");
				else
				{
					if(r.getInstancedZoneId() == 139)
						((FreyaNormal) r).notifyElimination();
					else if(r.getInstancedZoneId() == 144)
						((FreyaHard) r).notifyElimination();
				}
			}
		}
		else if(command.equalsIgnoreCase("wait"))
		{
			// do nothing, close window
		}
		else
			super.onBypassFeedback(player, command);
	}
}