package events.Christmas;

import org.mmocore.commons.collections.CollectionUtils;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.Announcements;
import org.mmocore.gameserver.handler.admincommands.AdminCommandHandler;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.instancemanager.ServerVariables;
import org.mmocore.gameserver.instancemanager.SpawnManager;
import org.mmocore.gameserver.listener.actor.player.OnPlayerEnterListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.actor.listener.PlayerListenerList;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.reward.RewardData;
import org.mmocore.gameserver.model.reward.RewardGroup;
import org.mmocore.gameserver.network.l2.components.CustomMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class Christmas implements IAdminCommandHandler, OnInitScriptListener
{
	private static final Logger _log = LoggerFactory.getLogger(Christmas.class);

	private static final OnPlayerEnterListener _eventAnnouncer = new OnPlayerEnterListenerImpl();

	//private static final RewardList DROP_DATA;

	static
	{
		final RewardGroup eventDrop = new RewardGroup(20000.);
		eventDrop.setNotRate(true);
		eventDrop.addData(new RewardData(5556, 1, 1, 200000.)); // Star Ornament
		eventDrop.addData(new RewardData(5557, 1, 1, 200000.)); // Bead Ornament
		eventDrop.addData(new RewardData(5558, 1, 1, 450000.)); // Fir Tree Branch
		eventDrop.addData(new RewardData(5559, 1, 1, 50000.)); // Flower Pot
		eventDrop.addData(new RewardData(3886, 1, 1, 20000.)); // Letter 'S'
		eventDrop.addData(new RewardData(3875, 1, 1, 40000.)); // Letter 'A'
		eventDrop.addData(new RewardData(3883, 1, 1, 20000.)); // Letter 'N'
		eventDrop.addData(new RewardData(3887, 1, 1, 20000.)); // Letter 'T'

		//DROP_DATA = new RewardList(RewardType.EVENT, true);
	//	DROP_DATA.add(eventDrop);
	}

	private static enum Commands
	{
		admin_christmas
	}

	@Override
	public void onInit()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
		if(isActive())
		{
			startEvent();
			_log.info("Loaded Event: Christmas [state: activated]");
		}
		else
			_log.info("Loaded Event: Christmas [state: deactivated]");
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		if (!activeChar.getPlayerAccess().IsEventGm)
			return false;

		if (wordList.length == 2)
		{
			if (wordList[1].equalsIgnoreCase("on"))
			{
				if (isActive())
				{
					activeChar.sendMessage("Christmas event already started.");
					return false;
				}
				startEvent();
				ServerVariables.set("Christmas", "on");
				_log.info("Event: Christmas [state: activated]");
				Announcements.getInstance().announceToAll(new CustomMessage("scripts.events.Christmas.AnnounceEventStarted"));
				return true;
			}
			else if (wordList[1].equalsIgnoreCase("off"))
			{
				if (!isActive())
				{
					activeChar.sendMessage("Christmas event not started.");
					return false;
				}
				stopEvent();
				ServerVariables.set("Christmas", "off");
				_log.info("Event: Christmas [state: deactivated]");
				Announcements.getInstance().announceToAll(new CustomMessage("scripts.events.Christmas.AnnounceEventStoped"));
				return true;
			}
		}

		activeChar.sendMessage("USAGE: //christmas [on|off]");
		return false;
	}

	private static boolean isActive()
	{
		return ServerVariables.getString("Christmas", "off").equalsIgnoreCase("on");
	}

	private void startEvent()
	{
		SpawnManager.getInstance().spawn("christmas");
		//NpcHolder.getInstance().addEventDrop(DROP_DATA);
		PlayerListenerList.addGlobal(_eventAnnouncer);
	}

	private void stopEvent()
	{
		SpawnManager.getInstance().despawn("christmas");
	//	NpcHolder.getInstance().removeEventDrop();
		PlayerListenerList.removeGlobal(_eventAnnouncer);
	}

	@Bypass("events.Christmas:exchange")
	public void exchange(Player player, NpcInstance npc, String[] var)
	{
		if(!player.isQuestContinuationPossible(true))
			return;

		if(player.isActionsDisabled() || player.isSitting() || npc == null || !NpcInstance.canBypassCheck(player, npc))
			return;

		if(var[0].equalsIgnoreCase("0"))
		{
			if(ItemFunctions.getItemCount(player, 5556) >= 4 && ItemFunctions.getItemCount(player, 5557) >= 4 && ItemFunctions.getItemCount(player, 5558) >= 10 && ItemFunctions.getItemCount(player, 5559) >= 1)
			{
				ItemFunctions.deleteItem(player, 5556, 4);
				ItemFunctions.deleteItem(player, 5557, 4);
				ItemFunctions.deleteItem(player, 5558, 10);
				ItemFunctions.deleteItem(player, 5559, 1);
				ItemFunctions.addItem(player, 5560, 1); // Christmas Tree
				return;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
		else if(var[0].equalsIgnoreCase("1"))
		{
			if(ItemFunctions.getItemCount(player, 5560) >= 10)
			{
				ItemFunctions.deleteItem(player, 5560, 10);
				ItemFunctions.addItem(player, 5561, 1); // Special Christmas Tree
				return;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
		else if(var[0].equalsIgnoreCase("2"))
		{
			if(ItemFunctions.getItemCount(player, 5560) >= 100)
			{
				ItemFunctions.deleteItem(player, 5560, 100);
				ItemFunctions.addItem(player, 14613, 1); // Santa's Hat
				return;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
		else if(var[0].equalsIgnoreCase("3"))
		{
			if(ItemFunctions.getItemCount(player, 5560) >= 15)
			{
				ItemFunctions.deleteItem(player, 5560, 15);
				ItemFunctions.addItem(player, 20094, 1); // Agathion Seal Bracelet - Rudolph
				return;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
		else if(var[0].equalsIgnoreCase("4"))
		{
			if(ItemFunctions.getItemCount(player, 5560) >= 10)
			{
				ItemFunctions.deleteItem(player, 5560, 10);
				ItemFunctions.addItem(player, 14612, 1); // Red Sock
				return;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
		else if(var[0].equalsIgnoreCase("5"))
		{
			if(ItemFunctions.getItemCount(player, 3886) >= 1 && ItemFunctions.getItemCount(player, 3875) >= 2 && ItemFunctions.getItemCount(player, 3883) >= 1 && ItemFunctions.getItemCount(player, 3887) >= 1)
			{
				ItemFunctions.deleteItem(player, 3886, 1);
				ItemFunctions.deleteItem(player, 3875, 2);
				ItemFunctions.deleteItem(player, 3883, 1);
				ItemFunctions.deleteItem(player, 3887, 1);
				switch (Rnd.get(3))
				{
					case 0:
						ItemFunctions.addItem(player, 20900, 1); // Santa's Hat
						break;
					case 1:
						ItemFunctions.addItem(player, 20094, 1); // Agathion Seal Bracelet - Rudolph
						break;
					case 2:
						ItemFunctions.addItem(player, 14612, 1); // Red Sock
						break;
					case 3:
						ItemFunctions.addItem(player, 5561, 1); // Special Christmas Tree
						break;
				}
				return;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
		else if(var[0].equalsIgnoreCase("6"))
		{
			if(ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= 10000)
			{
				ItemFunctions.deleteItem(player, ItemTemplate.ITEM_ID_ADENA, 10000);
				ItemFunctions.addItem(player, 6643, 100); // Old Golden Spice
				return;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		}
		else if(var[0].equalsIgnoreCase("7"))
		{
			final NpcInstance tamed = CollectionUtils.safeGet(player.getTamedBeasts(), 0);
			if(tamed != null && tamed.isVisible())
			{
				tamed.deleteMe();
				ItemFunctions.addItem(player, 10606, 1); // Rudolf Agation
				return;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_MATERIALS_TO_PERFORM_THAT_ACTION);
		}
	}

	private static final class OnPlayerEnterListenerImpl implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			final CustomMessage cm = new CustomMessage("scripts.events.Christmas.AnnounceEventStarted");
			if (cm != null)
				player.sendPacket(cm);
		}
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}