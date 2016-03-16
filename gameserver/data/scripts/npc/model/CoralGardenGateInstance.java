package npc.model;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;
import instances.CrystalCaverns;

/**
 * @author pchayka
 */
public class CoralGardenGateInstance extends NpcInstance
{
	public CoralGardenGateInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("request_coralg"))
		{
			ReflectionUtils.simpleEnterInstancedZone(player, CrystalCaverns.class, 10);
		}
		else
			super.onBypassFeedback(player, command);
	}
}
