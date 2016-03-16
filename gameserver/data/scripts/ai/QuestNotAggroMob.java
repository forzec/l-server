package ai;

import java.util.List;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.QuestEventType;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author VISTALL
 * @date 8:44/10.06.2011
 */
public class QuestNotAggroMob extends DefaultAI
{
	public QuestNotAggroMob(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean thinkActive()
	{
		return false;
	}

	@Override
	public  void onEvtAttacked(Creature attacker, SkillEntry skill, int dam)
	{
		NpcInstance actor = getActor();
		Player player = attacker.getPlayer();

		if(player != null)
		{
			List<QuestState> quests = player.getQuestsForEvent(actor, QuestEventType.ATTACKED_WITH_QUEST);
			if(quests != null)
				for(QuestState qs : quests)
					qs.getQuest().notifyAttack(actor, qs);
		}
	}

	@Override
	public  void onEvtAggression(Creature attacker, int d)
	{
		//
	}
}
