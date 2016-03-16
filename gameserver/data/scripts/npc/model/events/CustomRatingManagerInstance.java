package npc.model.events;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.mmocore.gameserver.Announcements;
import org.mmocore.gameserver.data.xml.holder.ItemHolder;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.ChatType;
import org.mmocore.gameserver.network.l2.components.CustomChatMessage;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;

import events.FunEvent;

public class CustomRatingManagerInstance extends NpcInstance
{
	private static final int DEFAULT_EVENT_ITEM_ID = 5757;
	private static final int DEFAULT_REWARD_ITEM_ID = 57;
	private static final int DEFAULT_REWARD_ITEM_COUNT = 10;

	private static final int LINES_IN_LIST = 10;

	private static Lock _lock = new ReentrantLock();
	private static List<RatingObject> _rating = new ArrayList<RatingObject>();
	private static String _names = "";

	private static int _eventItemId = DEFAULT_EVENT_ITEM_ID;
	private static String _eventItemName = "";
	private static int _rewardItemId = DEFAULT_REWARD_ITEM_ID;
	private static int _rewardItemCount = DEFAULT_REWARD_ITEM_COUNT;
	private static String _rewardItemName = "";
	private static AdditionalRewardObject[] _additionalReward = null;

	private static volatile boolean _inProgress = false;
	private static volatile boolean _rewarded = false;
	private static volatile RatingObject _first = null;
	private static volatile RatingObject _second = null;
	private static volatile RatingObject _third = null;

	private static int _firstItemId = DEFAULT_REWARD_ITEM_ID;
	private static int _firstItemCount = DEFAULT_REWARD_ITEM_COUNT;
	private static String _firstItemName = "";
	private static int _secondItemId = DEFAULT_REWARD_ITEM_ID;
	private static int _secondItemCount = DEFAULT_REWARD_ITEM_COUNT;
	private static String _secondItemName = "";
	private static int _thirdItemId = DEFAULT_REWARD_ITEM_ID;
	private static int _thirdItemCount = DEFAULT_REWARD_ITEM_COUNT;
	private static String _thirdItemName = "";

	public CustomRatingManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void addEvent(Event event)
	{
		super.addEvent(event);

		if (!(event instanceof FunEvent))
			return;

		if (isVisible())
		{
			// already spawned, refresh action executed
			if (_rewarded)
				return;

			_rewarded = true;
			_inProgress = false;
			reward();
			return;
		}

		if (_inProgress)
			return;

		_inProgress = true;

		ItemTemplate it;

		_eventItemId = ((FunEvent)event).getParameters().getInteger("event_item_id", DEFAULT_EVENT_ITEM_ID);
		it = ItemHolder.getInstance().getTemplate(_eventItemId);
		if (it != null)
			_eventItemName = it.getName();

		_rewardItemId = ((FunEvent)event).getParameters().getInteger("reward_item_id", DEFAULT_REWARD_ITEM_ID);
		_rewardItemCount = ((FunEvent)event).getParameters().getInteger("reward_item_count", DEFAULT_REWARD_ITEM_COUNT);
		it = ItemHolder.getInstance().getTemplate(_rewardItemId);
		if (it != null)
			_rewardItemName = (_rewardItemCount > 1 ? String.valueOf(_rewardItemCount) + " " : "") +  it.getName();

		_firstItemId = ((FunEvent)event).getParameters().getInteger("first_item_id", DEFAULT_REWARD_ITEM_ID);
		_firstItemCount = ((FunEvent)event).getParameters().getInteger("first_item_count", DEFAULT_REWARD_ITEM_COUNT);
		it = ItemHolder.getInstance().getTemplate(_firstItemId);
		if (it != null)
			_firstItemName = (_firstItemCount > 1 ? String.valueOf(_firstItemCount) + " " : "") +  it.getName();

		_secondItemId = ((FunEvent)event).getParameters().getInteger("second_item_id", DEFAULT_REWARD_ITEM_ID);
		_secondItemCount = ((FunEvent)event).getParameters().getInteger("second_item_count", DEFAULT_REWARD_ITEM_COUNT);
		it = ItemHolder.getInstance().getTemplate(_secondItemId);
		if (it != null)
			_secondItemName = (_secondItemCount > 1 ? String.valueOf(_secondItemCount) + " " : "") +  it.getName();

		_thirdItemId = ((FunEvent)event).getParameters().getInteger("third_item_id", DEFAULT_REWARD_ITEM_ID);
		_thirdItemCount = ((FunEvent)event).getParameters().getInteger("third_item_count", DEFAULT_REWARD_ITEM_COUNT);
		it = ItemHolder.getInstance().getTemplate(_thirdItemId);
		if (it != null)
			_thirdItemName = (_thirdItemCount > 1 ? String.valueOf(_thirdItemCount) + " " : "") +  it.getName();

		final int[] addReward = ((FunEvent)event).getParameters().getIntegerArray("additional_reward", null);
		if (addReward != null)
		{
			if ((addReward.length % 3) == 0)
			{
				_additionalReward = new AdditionalRewardObject[addReward.length / 3];
				int idx = 0;
				for (int i = 0; i < addReward.length; i += 3, idx++)
					_additionalReward[idx] = new AdditionalRewardObject(addReward[i], addReward[i + 1], addReward[i + 2]);
			}
			else
				Log.debug("Invalid additional_reward format");
		}

		_lock.lock();
		try
		{
			_rating.clear();
			_names = "";
			_first = null; _second = null; _third = null;
			_rewarded = false;
		}
		finally
		{
			_lock.unlock();
		}

		Announcements.getInstance().announceToAll(new CustomChatMessage("RatingEvent.Started", ChatType.ANNOUNCEMENT));
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (checkForDominionWard(player))
			return;

		if (!canBypassCheck(player, this))
			return;

		if (command.startsWith("Info"))
		{
			if (!_inProgress)
			{
				player.sendPacket(new HtmlMessage(this, getTemplate().getHtmRoot() + "notinprogress.htm"));
				return;				
			}
			
			player.sendPacket(new HtmlMessage(this, getTemplate().getHtmRoot() + "info.htm").replace("%item%", _eventItemName).replace("%reward%", _rewardItemName).replace("%first%", _firstItemName).replace("%second%", _secondItemName).replace("%third%", _thirdItemName));
			return;
		}
		else if (command.startsWith("Add"))
		{
			if (!_inProgress)
			{
				player.sendPacket(new HtmlMessage(this, getTemplate().getHtmRoot() + "notinprogress.htm"));
				return;				
			}

			final long count = player.getInventory().getCountOf(_eventItemId);
			if (count == 0)
			{
				player.sendPacket(new HtmlMessage(this, getTemplate().getHtmRoot() + "noitems.htm").replace("%item%", _eventItemName));
				return;
			}

			if (!ItemFunctions.deleteItem(player, _eventItemId, count))
				return;

			ItemFunctions.addItem(player, _rewardItemId, count * _rewardItemCount);

			final ResultObject result = add(player, count);

			if (_additionalReward != null)
				for (AdditionalRewardObject ar : _additionalReward)
					if (ar.requiredAmount > result.previousAmount && ar.requiredAmount <= result.amount)
						ItemFunctions.addItem(player, ar.itemId, ar.count);

			player.sendPacket(new HtmlMessage(this, getTemplate().getHtmRoot() + "done.htm").replace("%pos%", String.valueOf(result.pos)).replace("%total%", String.valueOf(result.amount)));
			return;
		}
		else if (command.startsWith("Check"))
		{
			RatingObject current;
			for (int i = 0; i < _rating.size(); i++)
			{
				current = _rating.get(i);
				if (current.objectId == player.getObjectId())
				{
					player.sendPacket(new HtmlMessage(this, getTemplate().getHtmRoot() + "now.htm").replace("%pos%", String.valueOf(i + 1)).replace("%total%", String.valueOf(current.amount)));
					return;
				}
			}
			player.sendPacket(new HtmlMessage(this, getTemplate().getHtmRoot() + "notfound.htm"));
			return;
		}
		else if (command.startsWith("Reward"))
		{
			if (!_rewarded)
			{
				player.sendPacket(new HtmlMessage(this, getTemplate().getHtmRoot() + "inprogress.htm"));
				return;				
			}

			if (_first != null && _first.objectId == player.getObjectId())
			{
				_first = null;
				ItemFunctions.addItem(player, _firstItemId, _firstItemCount);
			}
			else if (_second != null && _second.objectId == player.getObjectId())
			{
				_second = null;
				ItemFunctions.addItem(player, _secondItemId, _secondItemCount);
			}
			else if (_third != null && _third.objectId == player.getObjectId())
			{
				_third = null;
				ItemFunctions.addItem(player, _thirdItemId, _thirdItemCount);
			}
			else
			{
				player.sendPacket(new HtmlMessage(this, getTemplate().getHtmRoot() + "winnernotfound.htm"));
				return;
			}

			player.sendPacket(new HtmlMessage(this, getTemplate().getHtmRoot() + "congratulations.htm"));
			return;								
		}
		else if (command.startsWith("ShowRating"))
			player.sendPacket(new HtmlMessage(this, getTemplate().getHtmRoot() + "rating.htm").replace("%list%", _names));
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... ar)
	{
		if(checkForDominionWard(player))
			return;

		String fileName;
		if(val > 0)
			fileName = getTemplate().getHtmRoot() + "manager-" + val + ".htm";
		else
			fileName = getTemplate().getHtmRoot() + "manager.htm";

		player.sendPacket(new HtmlMessage(this, fileName));
	}

	private static final void reward()
	{
		_lock.lock();
		try
		{
			final int size = _rating.size();
			if (size <= 0)
			{
				Announcements.getInstance().announceToAll(new CustomChatMessage("RatingEvent.NoWinner", ChatType.ANNOUNCEMENT));
				return;
			}
			_first = _rating.get(0);
			Announcements.getInstance().announceToAll(new CustomChatMessage("RatingEvent.First", ChatType.ANNOUNCEMENT).addString(_first.name).addNumber(_first.amount));
			if (size <= 1)
				return;
			_second = _rating.get(1);
			Announcements.getInstance().announceToAll(new CustomChatMessage("RatingEvent.Second", ChatType.ANNOUNCEMENT).addString(_second.name).addNumber(_second.amount));
			if (size <= 2)
				return;
			_third = _rating.get(2);
			Announcements.getInstance().announceToAll(new CustomChatMessage("RatingEvent.Third", ChatType.ANNOUNCEMENT).addString(_third.name).addNumber(_third.amount));
			Announcements.getInstance().announceToAll(new CustomChatMessage("RatingEvent.Reward", ChatType.ANNOUNCEMENT));
		}
		finally
		{
			_lock.unlock();
		}
	}

	private static final ResultObject add(Player player, long count)
	{
		final ResultObject result =  add(player.getObjectId(), player.getName(), count);
		if (result.pos <= LINES_IN_LIST)
		{
			StringBuilder str = new StringBuilder(120 * LINES_IN_LIST + 100);
			str.append("<table width=270 border=0 bgcolor=\"000000\">");
			for (int i = 0; i < LINES_IN_LIST && i < _rating.size(); i++)
			{
				str.append("<tr><td fixwidth=100 align=center>");
				str.append(i + 1);
				str.append("</td><td fixwidth=170 align=center>");
				str.append(_rating.get(i).name);
				str.append("</td></tr>");
			}
			str.append("</table>");
			_names = str.toString();
		}
		return result;
	}

	private static final ResultObject add(int objectId, String name, long count)
	{
		RatingObject current;

		_lock.lock();
		try
		{
			int idx = _rating.size();
			while (--idx >= 0)
			{
				current = _rating.get(idx);
				if (current.objectId == objectId) // ищем существующую запись
				{
					long previousAmount = current.amount;
					current.amount += count; // нашли, обновляем
					if (idx > 0 && _rating.get(idx - 1).amount <= current.amount) // если не на первой позиции и стало больше чем предыдыдущая запись
					{
						_rating.remove(idx); // удаляем и ищем новое место
						while (--idx >= 0)
							if (_rating.get(idx).amount > current.amount)
								break;

						idx++;
						_rating.add(idx, current);
					}

					return new ResultObject(idx + 1, current.amount, previousAmount);
				}
			}

			idx = _rating.size(); // новая запись, ищем место для нее
			while (--idx >= 0)
				if (_rating.get(idx).amount > count)
					break;

			idx++;
			_rating.add(idx, new RatingObject(objectId, name, count));

			return new ResultObject(idx + 1, count, 0);					
		}
		finally
		{
			_lock.unlock();
		}
	}

	private static final class RatingObject
	{
		private final int objectId;
		private final String name;
		private long amount;

		public RatingObject(int objId, String n, long cnt)
		{
			objectId = objId;
			name = n;
			amount = cnt;
		}
	}

	private static final class ResultObject
	{
		private final int pos;
		private final long amount;
		private final long previousAmount;

		public ResultObject(int p, long ca, long pa)
		{
			pos = p;
			amount = ca;
			previousAmount = pa;
		}
	}

	private static final class AdditionalRewardObject
	{
		private final long requiredAmount;
		private final int itemId;
		private final long count;

		public AdditionalRewardObject(long ra, int id, long c)
		{
			requiredAmount = ra;
			itemId = id;
			count = c;
		}
	}
}