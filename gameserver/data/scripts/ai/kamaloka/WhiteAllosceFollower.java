package ai.kamaloka;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.base.SpecialEffectState;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Location;

/**
 * Минион босса 73й камалоки
 *
 * @author pchayka
 */
public class WhiteAllosceFollower extends Mystic
{
	private NpcInstance actor = getActor();
	private long _skillTimer = 0L;
	private final static long _skillInterval = 15000L;
	final SkillEntry s_soul_bind = SkillTable.getInstance().getSkillEntry(5624, 1);  // Soul Confinement

	public WhiteAllosceFollower(NpcInstance actor)
	{
		super(actor);
		actor.setInvul(SpecialEffectState.TRUE);
	}

	@Override
	protected boolean thinkActive()
	{
		if(_skillTimer + _skillInterval < System.currentTimeMillis())
		{
			for(Creature p : actor.getAroundCharacters(1000, 200))
				if(p.isPlayer() && !p.isDead() && !p.isInvisible())
					actor.getAggroList().addDamageHate(p, 0, 10);

			Creature target = actor.getAggroList().getRandomHated();
			if(target != null)
				actor.doCast(s_soul_bind, target, false);
			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			addTaskMove(Location.findAroundPosition(actor, 400), false);
			_skillTimer = System.currentTimeMillis() + Rnd.get(1L, 5000L);
		}
		return super.thinkActive();
	}

	@Override
	protected void thinkAttack()
	{
		// do not attack
		setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}
}