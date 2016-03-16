package npc.model;

import java.util.StringTokenizer;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;
import instances.CrystalCaverns;

/**
 * @author pchayka
 */
public class SteamCorridorControllerInstance extends NpcInstance
{
	public SteamCorridorControllerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("move_next"))
		{
			if(getReflection().getInstancedZoneId() == 10)
				((CrystalCaverns) getReflection()).notifyNextLevel(this);
		}
		else
			super.onBypassFeedback(player, command);
	}
}
