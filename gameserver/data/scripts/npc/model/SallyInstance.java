package npc.model;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 10:17/24.06.2011
 */
public class SallyInstance extends NpcInstance
{
	public SallyInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equals("ask_about_rare_plants"))
		{
			QuestState qs = player.getQuestState(250);
			if(qs != null && qs.isCompleted())
				showChatWindow(player, 3);
			else
				showChatWindow(player, 2);
		}
		else
			super.onBypassFeedback(player, command);
	}
}
