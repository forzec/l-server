package npc.model;

import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 20:32/16.05.2011
 */
public class EnergySeedInstance extends NpcInstance
{
	public EnergySeedInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public double getRewardRate(Player player)
	{
		return Config.RATE_DROP_ENERGY_SEED;
	}
}
