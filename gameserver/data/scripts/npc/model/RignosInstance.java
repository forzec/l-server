package npc.model;

import java.util.concurrent.Future;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author VISTALL
 * @date 17:41/30.08.2011
 */
public class RignosInstance extends NpcInstance
{
	private class EndRaceTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_raceTask = null;
		}
	}

	private static final SkillEntry SKILL_EVENT_TIMER = SkillTable.getInstance().getSkillEntry(5239, 5);
	private static final int RACE_STAMP = 10013;
	private static final int SECRET_KEY = 9694;

	private Future<?> _raceTask;

	public RignosInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("startRace"))
		{
			if(_raceTask != null)
				return;

			altUseSkill(SKILL_EVENT_TIMER, player);
			ItemFunctions.deleteItem(player, RACE_STAMP, ItemFunctions.getItemCount(player, RACE_STAMP));
			_raceTask = ThreadPoolManager.getInstance().schedule(new EndRaceTask(), 30 * 60 * 1000L);
		}
		else if(command.equalsIgnoreCase("endRace"))
		{
			if(_raceTask == null)
				return;

			long count = ItemFunctions.getItemCount(player, RACE_STAMP);
			if(count >= 4)
			{
				ItemFunctions.deleteItem(player, RACE_STAMP, count);
				ItemFunctions.addItem(player, SECRET_KEY, 3);
				player.getEffectList().stopEffect(SKILL_EVENT_TIMER);
				_raceTask.cancel(false);
				_raceTask = null;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if(ItemFunctions.getItemCount(player, RACE_STAMP) >= 4)
			showChatWindow(player, "race_start001a.htm");
		else if(player.getLevel() >= 78 && _raceTask == null)
			showChatWindow(player, "race_start001.htm");
		else
			showChatWindow(player, "race_start002.htm");
	}
}
