package ai;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;

/**
 * @author Diamond
 */
public class EvasGiftBox extends Fighter
{
	private static final int[] KISS_OF_EVA = new int[] { 1073, 3141, 3252 };

	private static final int Red_Coral = 9692;
	private static final int Crystal_Fragment = 9693;

	public EvasGiftBox(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		if(killer != null)
		{
			Player player = killer.getPlayer();
			if(player != null && player.getEffectList().containEffectFromSkills(KISS_OF_EVA))
				actor.dropItem(player, Rnd.chance(50) ? Red_Coral : Crystal_Fragment, 1, false);
		}
		super.onEvtDead(killer);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}