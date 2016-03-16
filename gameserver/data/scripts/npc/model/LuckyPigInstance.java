package npc.model;

import java.util.concurrent.Future;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * @author VISTALL
 * @date 16:00/25.04.2012
 */
public class LuckyPigInstance extends NpcInstance
{
	private int _pickCount;

	private Future<?> _task;

	public LuckyPigInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equals("stop"))
		{
			if(_pickCount < 3)
				showChatWindow(player, "luckpi_003.htm");
			else
			{
				ChatUtils.say(this, NpcString.LUCKPY_NO_MORE_ADENA_OH);

				final int pickCount = _pickCount;

				onDecay();

				PrizeLuckyPigInstance luckyPigInstance = NpcUtils.newInstance(pickCount >= 5 ? 2503 : 2502);
				luckyPigInstance.setPickCount(pickCount);
				luckyPigInstance.setPigLevel(getLevel());
				luckyPigInstance.setSpawnedLoc(getLoc());
				luckyPigInstance.spawnMe(luckyPigInstance.getSpawnedLoc());
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		showChatWindow(player, "luckpi_001.htm");
	}

	@Override
	public void spawnMe(Location loc)
	{
		if(!Rnd.chance(getParameter("randRate", 100)))
		{
			onDecay();
			return;
		}

		spawnMe0(loc, null);

		_task = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				onDecay();
			}
		}, 600000L);
	}

	@Override
	protected void onDecay()
	{
		_pickCount = 0;
		if(_task != null)
			_task.cancel(true);

		super.onDecay();
	}

	public int getPickCount()
	{
		return _pickCount;
	}

	public void incPickCount()
	{
		_pickCount ++;
	}

	public void decPickCount()
	{
		_pickCount--;
	}

	public void meFull()
	{
		ChatUtils.say(this, NpcString.LUCKPY_IM_FULL_THANKS_FOR_THE_YUMMY_ADENA_OH);

		onDecay();
	}
}
