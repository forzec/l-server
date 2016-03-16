package ai;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * AI мобов Prison Guard на Isle of Prayer.<br>
 * - Не используют функцию Random Walk<br>
 * - Ругаются на атаковавших чаров без эффекта Event Timer<br>
 * - Ставят в петрификацию атаковавших чаров без эффекта Event Timer<br>
 * - Не могут быть убиты чарами без эффекта Event Timer<br>
 * - Не проявляют агресии к чарам без эффекта Event Timer<br>
 * ID: 18367, 18368
 *
 * @author SYS
 */
public class PrisonGuard extends Fighter
{
	private static final int RACE_STAMP = 10013;

	public PrisonGuard(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean checkAggression(Creature target)
	{
		// 18367 не агрятся
		NpcInstance actor = getActor();
		if(actor.isDead() || actor.getNpcId() == 18367)
			return false;

		if(target.getEffectList().getEffectsCountForSkill(Skill.SKILL_EVENT_TIMER) == 0)
			return false;

		return super.checkAggression(target);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return;
		if(attacker.isServitor())
			attacker = attacker.getPlayer();
		if(attacker.getEffectList().getEffectsCountForSkill(Skill.SKILL_EVENT_TIMER) == 0)
		{
			if(actor.getNpcId() == 18367)
				Functions.npcSay(actor, NpcString.ITS_NOT_EASY_TO_OBTAIN);
			else if(actor.getNpcId() == 18368)
				Functions.npcSay(actor, NpcString.YOURE_OUT_OF_YOUR_MIND_COMING_HERE);

			SkillEntry petrification = SkillTable.getInstance().getSkillEntry(4578, 1); // Petrification
			actor.doCast(petrification, attacker, true);
			if(attacker.getServitor() != null)
				actor.doCast(petrification, attacker.getServitor(), true);

			return;
		}

		// 18367 не отвечают на атаку, но зовут друзей
		if(actor.getNpcId() == 18367)
		{
			notifyFriends(attacker, skill, damage);
			return;
		}

		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		if(actor == null)
			return;

		if(actor.getNpcId() == 18367 && killer.getPlayer().getEffectList().getEffectsBySkillId(Skill.SKILL_EVENT_TIMER) != null)
			ItemFunctions.addItem(killer.getPlayer(), RACE_STAMP, 1);

		super.onEvtDead(killer);
	}
}