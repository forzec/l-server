package ai;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.Ranger;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * AI для Karul Bugbear ID: 20600
 *
 * @author Diamond
 */
public class KarulBugbear extends Ranger
{
	private boolean _firstTimeAttacked = true;

	public KarulBugbear(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		_firstTimeAttacked = true;
		super.onEvtSpawn();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();
		if(_firstTimeAttacked)
		{
			_firstTimeAttacked = false;
			if(Rnd.chance(25))
				Functions.npcSay(actor, NpcString.YOUR_REAR_IS_PRACTICALLY_UNGUARDED);
		}
		else if(Rnd.chance(10))
			Functions.npcSay(actor, NpcString.S1_WATCH_YOUR_BACK, attacker.getPlayer().getName());
		super.onEvtAttacked(attacker, skill, damage);
	}
}