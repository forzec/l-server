package ai.queen_ant;

import java.util.List;

import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.base.SpecialEffectState;
import org.mmocore.gameserver.model.instances.NpcInstance;

public class GuardAnt extends Fighter
{
	public GuardAnt(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean checkAggression(Creature target)
	{
		return !checkAndParalyze(target) && super.checkAggression(target);
	}

	@Override
	protected void thinkAttack()
	{
		super.thinkAttack();

		if (getActor().isDead())
			return;

		// Продолжаем сканировать окружение даже во время атаки
		final long now = System.currentTimeMillis();
		if (now - _checkAggroTimestamp > Config.AGGRO_CHECK_INTERVAL)
		{
			_checkAggroTimestamp = now;

			List<Playable> targets = World.getAroundPlayables(getActor());
			if (!targets.isEmpty())
				for (Playable target : targets)
					checkAggression(target);
		}
	}

	private final boolean checkAndParalyze(Creature target)
	{
		if (!Config.PARALIZE_ON_RAID_DIFF)
			return false;
		if (!target.isPlayable())
			return false;
		if (getActor().isDead())
			return false;
		if (target.isPlayer() && target.getInvisible() == SpecialEffectState.GM)
			return false;
		if (((Playable) target).getNonAggroTime() > System.currentTimeMillis())
			return false;
		if (!target.isInRange(getActor(), getActor().getAggroRange()))
			return false;
		if (!getActor().paralizeOnAttack(target))
			return false;
		
		((Playable)target).paralizeMe(getActor(), Skill.SKILL_RAID_CURSE);
		((Playable)target).setNonAggroTime(System.currentTimeMillis() + 121000L); // one second to leave
		return true;
	}
}