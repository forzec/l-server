package npc.model;

import org.mmocore.gameserver.instancemanager.ReflectionManager;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public final class KamalokaGuardInstance extends NpcInstance
{
	public KamalokaGuardInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(checkForDominionWard(player))
			return;

		if(command.startsWith("kamaloka"))
		{
			int val = Integer.parseInt(command.substring(9));
			if ((val >= 57 && val <= 79) || val == 134)
				ReflectionUtils.simpleEnterInstancedZone(player, val);
		}
		else if(command.startsWith("escape"))
		{
			if(player.getParty() == null || !player.getParty().isLeader(player))
			{
				showChatWindow(player, "not_party_leader.htm");
				return;
			}
			int refId = player.getReflection().getInstancedZoneId();
			if ((refId >= 57 && refId <= 79) || refId == 134)
				player.getReflection().collapse();
		}
		else if(command.startsWith("return"))
		{
			Reflection r = player.getReflection();
			if(r.getReturnLoc() != null)
				player.teleToLocation(r.getReturnLoc(), ReflectionManager.DEFAULT);
			else
				player.setReflection(ReflectionManager.DEFAULT);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if(checkForDominionWard(player))
			return;

		super.showChatWindow(player, val);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom;
		if(val == 0)
			pom = "" + npcId;
		else
			pom = npcId + "-" + val;

		return "instance/kamaloka/" + pom + ".htm";
	}
}