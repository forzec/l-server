package ai;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * AI для Karul Bugbear ID: 20438
 *
 * @author Diamond
 */
public class OlMahumGeneral extends Fighter
{
	private boolean _firstTimeAttacked = true;

	public OlMahumGeneral(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();
		if(_firstTimeAttacked)
		{
			_firstTimeAttacked = false;
			if(Rnd.chance(25))
				Functions.npcSay(actor, NpcString.WE_SHALL_SEE_ABOUT_THAT);
		}
		else if(Rnd.chance(10))
			Functions.npcSay(actor, NpcString.I_WILL_DEFINITELY_REPAY_THIS_HUMILIATION);
		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_firstTimeAttacked = true;
		super.onEvtDead(killer);
	}
}