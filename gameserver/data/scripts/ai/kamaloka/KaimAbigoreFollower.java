package ai.kamaloka;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * Минион босса 63й камалоки
 *
 * @author pchayka
 */
public class KaimAbigoreFollower extends Fighter
{
	private NpcInstance actor = getActor();
	final SkillEntry s_self_explosion = SkillTable.getInstance().getSkillEntry(4614, 6);  // Explosion

	public KaimAbigoreFollower(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void thinkAttack()
	{
		Creature target = getAttackTarget();
		if(actor.getDistance(target) < 50)
		{
			actor.doCast(s_self_explosion, target, true);
			actor.doDie(null);
		}
		super.thinkAttack();
	}
}