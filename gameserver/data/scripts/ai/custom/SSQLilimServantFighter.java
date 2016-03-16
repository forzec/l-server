package ai.custom;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * AI Lilim Servants: 27371-27379
 *
 * @author pchayka
 */
public class SSQLilimServantFighter extends Fighter
{
	private boolean _attacked = false;

	public SSQLilimServantFighter(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		if(Rnd.chance(30) && !_attacked)
		{
			Functions.npcSay(getActor(), Rnd.chance(50) ? "Those who are afraid should get away and those who are brave should fight!" : "This place once belonged to Lord Shilen.");
			_attacked = true;
		}
		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if(Rnd.chance(30))
			Functions.npcSay(getActor(), Rnd.chance(50) ? "Why are you getting in our way?" : "Shilen... our Shilen!");
		super.onEvtDead(killer);
	}
}