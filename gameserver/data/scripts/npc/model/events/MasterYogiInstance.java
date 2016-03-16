package npc.model.events;

import java.util.concurrent.ScheduledFuture;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.items.Inventory;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.ItemFunctions;

import events.FunEvent;

public final class MasterYogiInstance extends NpcInstance
{
	private static final int MASTER_YOGI_STAFF = 13539;
	private static final int MASTER_YOGI_SCROLL = 13540;

	private static final int[] HAT_SHADOW = new int[] { 13074, 13075, 13076 };
	private static final int[] HAT_EVENT = new int[] { 13518, 13519, 13522 };
	private static final int[] SOUL_CRYSTALL = new int[] { 10480, 10481, 10482 };

	private int _staffPrice = 1000;
	private int _scrollPrice = 77777;
	private int _timedScrollNumber = 24;
	private int _timedScrollHours = 6;
	private int _timedScrollPrice = 6000;

	private ScheduledFuture<?> _shoutTask = null;

	public MasterYogiInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public final void showChatWindow(Player player, int val, Object... replace)
	{
		showChatWindowImpl(player, getHtmlPath(getNpcId(), val, player));
	}

	@Override
	public final void showChatWindow(Player player, String filename, Object... replace)
	{
		showChatWindowImpl(player, getTemplate().getHtmRoot() + filename);
	}

	private final void showChatWindowImpl(Player player, String filename)
	{
		HtmlMessage packet = new HtmlMessage(this).setFile(filename);
		packet.replace("%num%", String.valueOf(_timedScrollNumber));
		packet.replace("%hours%", String.valueOf(_timedScrollHours));
		packet.replace("%price%", String.valueOf(_timedScrollPrice));
		packet.replace("%staff%", String.valueOf(_staffPrice));
		packet.replace("%scroll%", String.valueOf(_scrollPrice));
		player.sendPacket(packet);
	}

	@Override
	public final void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if ("buy_staff".equalsIgnoreCase(command))
		{
			if (ItemFunctions.getItemCount(player, MASTER_YOGI_STAFF) == 0 && player.reduceAdena(_staffPrice, true))
			{
				ItemFunctions.addItem(player, MASTER_YOGI_STAFF, 1);
				showChatWindow(player, "32599-staffbuyed.htm");
			}
			else
				showChatWindow(player, "32599-staffcant.htm");
		}
		else if ("buy_scroll_lim".equalsIgnoreCase(command))
		{
			final long remainingTime = player.getVarLong("MasterOfEnch", 0) - System.currentTimeMillis();
			if (remainingTime <= 0)
			{
				if (player.reduceAdena(_timedScrollPrice, true))
				{
					ItemFunctions.addItem(player, MASTER_YOGI_SCROLL, _timedScrollNumber);
					player.setVar("MasterOfEnch", System.currentTimeMillis() + _timedScrollHours * 3600000L, -1);
					showChatWindow(player, "32599-scroll24.htm");
				}
				else
					showChatWindow(player, "32599-s24-no.htm");
			}
			else
			{
				int hours = (int) (remainingTime / 3600000L);
				int minutes = (int) (remainingTime % 3600000L / 60000);
				if(hours > 0)
				{
					SystemMessage sm = new SystemMessage(SystemMsg.THERE_ARE_S1_HOURSS_AND_S2_MINUTES_REMAINING_UNTIL_THE_ITEM_CAN_BE_PURCHASED_AGAIN);
					sm.addNumber(hours);
					sm.addNumber(minutes);
					player.sendPacket(sm);
					showChatWindow(player, "32599-scroll24.htm");
				}
				else if(minutes > 0)
				{
					SystemMessage sm = new SystemMessage(SystemMsg.THERE_ARE_S1_MINUTES_REMAINING_UNTIL_THE_ITEM_CAN_BE_PURCHASED_AGAIN);
					sm.addNumber(minutes);
					player.sendPacket(sm);
					showChatWindow(player, "32599-scroll24.htm");
				}
				else
					showChatWindow(player, "32599-s24-no.htm");
			}
		}
		else if ("buy_scroll_1".equalsIgnoreCase(command))
		{
			if(player.reduceAdena(_scrollPrice, true))
			{
				ItemFunctions.addItem(player, MASTER_YOGI_SCROLL, 1);
				showChatWindow(player, "32599-scroll-ok.htm");
			}
			else
				showChatWindow(player, "32599-s1-no.htm");
		}
		else if ("buy_scroll_10".equalsIgnoreCase(command))
		{
			if(player.reduceAdena(10 * _scrollPrice, true))
			{
				ItemFunctions.addItem(player, MASTER_YOGI_SCROLL, 10);
				showChatWindow(player, "32599-scroll-ok.htm");
			}
			else
				showChatWindow(player, "32599-s10-no.htm");
		}
		else if ("receive_reward".equalsIgnoreCase(command))
		{
			ItemInstance enchantedItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
			if(enchantedItem == null || enchantedItem.getItemId() != MASTER_YOGI_STAFF || enchantedItem.getEnchantLevel() < 4)
			{
				showChatWindow(player, "32599-rewardnostaff.htm");
				return;
			}

			if(!player.getInventory().destroyItem(enchantedItem))
				return;

			switch(enchantedItem.getEnchantLevel())
			{
				case 0:
				case 1:
				case 2:
				case 3:
					break;
				case 4:
					ItemFunctions.addItem(player, 6406, 1); // Firework
					break;
				case 5:
					ItemFunctions.addItem(player, 6406, 2); // Firework
					ItemFunctions.addItem(player, 6407, 1); // Large Firework
					break;
				case 6:
					ItemFunctions.addItem(player, 6406, 3); // Firework
					ItemFunctions.addItem(player, 6407, 2); // Large Firework
					break;
				case 7:
					ItemFunctions.addItem(player, HAT_SHADOW[Rnd.get(HAT_SHADOW.length)], 1); //Shadow Hat Accessory
					break;
				case 8:
					ItemFunctions.addItem(player, 955, 1); // Scroll: Enchant Weapon (D)
					break;
				case 9:
					ItemFunctions.addItem(player, 955, 1); // Scroll: Enchant Weapon (D)
					ItemFunctions.addItem(player, 956, 1); // Scroll: Enchant Armor (D)
					break;
				case 10:
					ItemFunctions.addItem(player, 951, 1); // Scroll: Enchant Weapon (C)
					break;
				case 11:
					ItemFunctions.addItem(player, 951, 1); // Scroll: Enchant Weapon (C)
					ItemFunctions.addItem(player, 952, 1); // Scroll: Enchant Armor (C)
					break;
				case 12:
					ItemFunctions.addItem(player, 948, 1); // Scroll: Enchant Armor (B)
					break;
				case 13:
					ItemFunctions.addItem(player, 729, 1); // Scroll: Enchant Weapon (A)
					break;
				case 14:
					ItemFunctions.addItem(player, HAT_EVENT[Rnd.get(HAT_EVENT.length)], 1); //Event Hat Accessory
					break;
				case 15:
					ItemFunctions.addItem(player, 13992, 1); // Grade S Accessory Chest (Event)
					break;
				case 16:
					ItemFunctions.addItem(player, 10486, 1); // Top-Grade Life Stone: level 76 > 82
					break;
				case 17:
					ItemFunctions.addItem(player, 959, 1); // Scroll: Enchant Weapon (S)
					break;
				case 18:
					ItemFunctions.addItem(player, 17073, 1); // Grade S Armor Chest (Event) > moirai
					break;
				case 19:
					ItemFunctions.addItem(player, 17070, 1); // Grade S Weapon Chest (Event) > icar
					break;
				case 20:
					ItemFunctions.addItem(player, SOUL_CRYSTALL[Rnd.get(SOUL_CRYSTALL.length)], 1); // Red/Blue/Green Soul Crystal - Stage 14 > 15
					break;
				case 21:
					ItemFunctions.addItem(player, 10486, 3); // Top-Grade Life Stone: level 76 > 82
					ItemFunctions.addItem(player, SOUL_CRYSTALL[Rnd.get(SOUL_CRYSTALL.length)], 1); // Red/Blue/Green Soul Crystal - Stage 14 > 15
					break;
				case 22:
					ItemFunctions.addItem(player, 22203, 1); // S80 Grade Armor Chest (Event)  > vorp
					break;
				case 23:
					ItemFunctions.addItem(player, 22202, 1); // S80 Grade Weapon Chest (Event) > mid84
					break;
				default:
					ItemFunctions.addItem(player, 22202, 1); // S80 Grade Weapon Chest (Event)
					break;
			}
			showChatWindow(player, "32599-rewardnostaff.htm");
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	protected final void onSpawn()
	{
		super.onSpawn();

		FunEvent evt = getEvent(FunEvent.class);
		if (evt != null)
		{
			MultiValueSet<String> params = evt.getParameters();
			_staffPrice = params.getInteger("staff_price", 1000);
			_scrollPrice = params.getInteger("scroll_price", 77777);
			_timedScrollNumber = params.getInteger("timed_scroll_number", 24);
			_timedScrollHours = params.getInteger("timed_scroll_hours", 6);
			_timedScrollPrice = params.getInteger("timed_scroll_price", 6000);
		}

		_shoutTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Shout(), 60000L, 60000L);
	}

	@Override
	protected final void onDelete()
	{
		super.onDelete();

		if (_shoutTask != null)
		{
			_shoutTask.cancel(false);
			_shoutTask = null;
		}
	}

	private final class Shout extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if (Rnd.get(5) < 1)
				ChatUtils.shout(MasterYogiInstance.this, Rnd.nextBoolean() ? NpcString.CARE_TO_CHALLENGE_FATE_AND_TEST_YOUR_LUCK : NpcString.DONT_PASS_UP_THE_CHANCE_TO_WIN_AN_S80_WEAPON);
		}
	}
}