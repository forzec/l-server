package ai.isle_of_prayer;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;

public class WaterDragonDetractor extends Fighter
{
	private static final int SPIRIT_OF_LAKE = 9689;
	private static final int BLUE_CRYSTAL = 9595;

	public WaterDragonDetractor(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if (killer != null)
		{
			final Player player = killer.getPlayer();
			if (player != null)
			{
				final NpcInstance actor = getActor();
				actor.dropItem(player, SPIRIT_OF_LAKE, 1, false);
				if (Rnd.chance(10))
					actor.dropItem(player, BLUE_CRYSTAL, 1, false);
			}
		}
		super.onEvtDead(killer);
	}
}