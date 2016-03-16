package ai;

import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * @author VISTALL
 */
public class Quest024Mystic extends Mystic
{
	public Quest024Mystic(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		for(Player player : World.getAroundPlayers(getActor(), 300, 200))
		{
			QuestState questState = player.getQuestState(24);
			if(questState != null && questState.getCond() == 3)
				questState.getQuest().notifyEvent("seePlayer", questState, getActor());
		}
		return super.thinkActive();
	}
}