package npc.model;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * Данный инстанс используется телепортерами из/в Pagan Temple
 * @author SYS
 */
public class TriolsMirrorInstance extends NpcInstance
{
	public TriolsMirrorInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if(getNpcId() == 32040)
			player.teleToLocation(-12766, -35840, -10856); //to pagan
		else if(getNpcId() == 32039)
			player.teleToLocation(35079, -49758, -760); //from pagan
	}
}