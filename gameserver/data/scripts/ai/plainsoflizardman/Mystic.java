package ai.plainsoflizardman;

import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;

/**
 * @author VISTALL
 * @date 11:42/01.05.2012
 */
public class Mystic extends org.mmocore.gameserver.ai.Mystic
{
	public Mystic(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);
		if(killer != null && killer.isPlayable())
			HerbHelper.give(getActor(), killer.getPlayer());
	}
}
