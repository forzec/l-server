package ai.seedofinfinity;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author pchayka
 */
public class WardofDeath extends DefaultAI
{
	private static final int[] mobs = {22516, 22520, 22522, 22524};

	public WardofDeath(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onIntentionAttack(Creature target)
	{
		NpcInstance actor = getActor();

		if(actor.getNpcId() == 18667) // trap skill
		{
			actor.doCast(SkillTable.getInstance().getSkillEntry(Rnd.get(5423, 5424), 9), actor, false);
			actor.doDie(null);
		}
		else if(actor.getNpcId() == 18668) // trap spawn
		{
			int count = Rnd.get(1, 4);
			for(int i = 0; i < count; i++)
				actor.getReflection().addSpawnWithoutRespawn(mobs[Rnd.get(mobs.length)], actor.getLoc(), 100);
			actor.doDie(null);
		}
	}

	@Override
	protected boolean checkAggression(Creature target)
	{
		NpcInstance actor = getActor();
		if(target.isInRange(actor, actor.getAggroRange()) && target.isPlayable() && !target.isDead() && !target.isInvisible())
			return true;
		return false;
	}
}