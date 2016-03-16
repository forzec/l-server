package ai;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillUse;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

public class PowderKeg extends DefaultAI
{
	private static final SkillEntry se = SkillTable.getInstance().getSkillEntry(5714, 1);
	private boolean _exploded = false;

	public PowderKeg(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		if(!_exploded)
		{
			_exploded = true;
			_actor.broadcastPacket(new MagicSkillUse(_actor, se.getId(), se.getLevel(), se.getTemplate().getHitTime(), 0));
			for(Creature c : _actor.getAroundCharacters(600, 200))
				if(!c.isPlayable())
					c.reduceCurrentHp(1700, _actor, se, 0, false, true, true, false, false, false, false, true);
			_actor.doDie(attacker);
		}
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
	}
}