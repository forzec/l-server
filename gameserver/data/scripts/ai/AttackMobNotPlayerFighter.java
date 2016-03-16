package ai;

import java.util.List;

import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.QuestEventType;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * Квестовый NPC, атакующий мобов. Игнорирует игроков.
 * @author Diamond
 */
public class AttackMobNotPlayerFighter extends Fighter
{
	public AttackMobNotPlayerFighter(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();
		if(attacker == null)
			return;

		Player player = attacker.getPlayer();
		if(player != null)
		{
			List<QuestState> quests = player.getQuestsForEvent(actor, QuestEventType.ATTACKED_WITH_QUEST);
			if(quests != null)
				for(QuestState qs : quests)
					qs.getQuest().notifyAttack(actor, qs);
		}

		onEvtAggression(attacker, damage);
	}

	@Override
	protected void onEvtAggression(Creature attacker, int aggro)
	{
		NpcInstance actor = getActor();
		if(attacker == null)
			return;

		if(!actor.isRunning())
			startRunningTask(AI_TASK_ATTACK_DELAY);

		if(getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
	}
}