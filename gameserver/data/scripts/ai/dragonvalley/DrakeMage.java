package ai.dragonvalley;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;

public class DrakeMage extends Mystic
{
	public DrakeMage(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);

		final Player player = killer.getPlayer();
		if (player != null && player.isMageClass())
			if (Rnd.chance(70))
				getActor().dropItem(player, 8603, 1, false);
			else
				getActor().dropItem(player, 8604, 1, false);
	}
}