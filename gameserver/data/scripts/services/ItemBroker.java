package services;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.data.xml.holder.ItemHolder;
import org.mmocore.gameserver.data.xml.holder.RecipeHolder;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.GameObjectsStorage;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Recipe;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.items.ManufactureItem;
import org.mmocore.gameserver.model.items.TradeItem;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.s2c.CharInfo;
import org.mmocore.gameserver.network.l2.s2c.ExShowTrace;
import org.mmocore.gameserver.network.l2.s2c.RadarControl;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.item.ItemTemplate.ItemClass;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.Util;

public class ItemBroker
{
	private static final int MAX_ITEMS_PER_PAGE = 10;
	private static final int MAX_PAGES_PER_LIST = 9;

	private static Map<Integer, NpcInfo> _npcInfos = new ConcurrentHashMap<Integer, NpcInfo>();

	public class NpcInfo
	{
		public long lastUpdate;
		public TreeMap<String, TreeMap<Long, Item>> bestSellItems;
		public TreeMap<String, TreeMap<Long, Item>> bestBuyItems;
		public TreeMap<String, TreeMap<Long, Item>> bestCraftItems;
	}

	public class Item
	{
		public final int itemId;
		public final int itemObjId;
		public final int type;
		public final long price;
		public final long count;
		public final int enchant;
		public final boolean rare;
		public final String name;
		public final int merchantObjectId;
		public final String merchantName;
		public final Location player;
		public final TradeItem item;
		public final boolean isPackage;

		public Item(int itemId, int type, long price, long count, int enchant, String itemName, int mobjectId, String merchantName, Location player, int itemObjId, TradeItem item, boolean isPkg)
		{
			this.itemId = itemId;
			this.type = type;
			this.price = price;
			this.count = count;
			this.enchant = enchant;
			this.rare = org.apache.commons.lang3.ArrayUtils.contains(ItemBrokerRareItems.RARE_ITEMS, itemId);

			StringBuilder out = new StringBuilder(70);
			if (enchant > 0)
			{
				if (rare)
					out.append("<font color=\"FF0000\">+");
				else
					out.append("<font color=\"7CFC00\">+");
				out.append(enchant);
				out.append(" ");
			}
			else if (rare)
				out.append("<font color=\"0000FF\">Rare ");
			else
				out.append("<font color=\"LEVEL\">");

			out.append(itemName);
			out.append("</font>]");

			if (item != null)
			{
				if(item.getAttackElement() != Element.NONE.getId())
				{
					out.append(" &nbsp;<font color=\"7CFC00\">+");
					out.append(item.getAttackElementValue());
					switch(item.getAttackElement())
					{
						case ItemTemplate.ATTRIBUTE_FIRE:
							out.append(" Fire");
							break;
						case ItemTemplate.ATTRIBUTE_WATER:
							out.append(" Water");
							break;
						case ItemTemplate.ATTRIBUTE_WIND:
							out.append(" Wind");
							break;
						case ItemTemplate.ATTRIBUTE_EARTH:
							out.append(" Earth");
							break;
						case ItemTemplate.ATTRIBUTE_HOLY:
							out.append(" Holy");
							break;
						case ItemTemplate.ATTRIBUTE_DARK:
							out.append(" Unholy");
							break;
					}
					out.append("</font>");
				}
				else
				{
					final int fire = item.getDefenceFire();
					final int water = item.getDefenceWater();
					final int wind = item.getDefenceWind();
					final int earth = item.getDefenceEarth();
					final int holy = item.getDefenceHoly();
					final int unholy = item.getDefenceUnholy();
					if (fire + water + wind + earth + holy + unholy > 0)
					{
						out.append("&nbsp;<font color=\"7CFC00\">");
						if(fire > 0)
						{
							out.append("+");
							out.append(fire);
							out.append(" Fire ");
						}
						if (water > 0)
						{
							out.append("+");
							out.append(water);
							out.append(" Water ");
						}
						if (wind > 0)
						{
							out.append("+");
							out.append(wind);
							out.append(" Wind ");
						}
						if (earth > 0)
						{
							out.append("+");
							out.append(earth);
							out.append(" Earth ");
						}
						if (holy > 0)
						{
							out.append("+");
							out.append(holy);
							out.append(" Holy ");
						}
						if (unholy > 0)
						{
							out.append("+");
							out.append(unholy);
							out.append(" Unholy ");
						}
						out.append("</font>");
					}
				}
			}

			name = out.toString();

			this.merchantObjectId = mobjectId;
			this.merchantName = merchantName;
			this.player = player;
			this.itemObjId = itemObjId;
			this.item = item;
			this.isPackage = isPkg;
		}
	}

	private TreeMap<String, TreeMap<Long, Item>> getItems(Player player, NpcInstance npc, int type)
	{
		if(player == null || npc == null)
			return null;
		updateInfo(player, npc);
		NpcInfo info = _npcInfos.get(npc.getObjectId());
		if(info == null)
			return null;
		switch(type)
		{
			case Player.STORE_PRIVATE_SELL:
				return info.bestSellItems;
			case Player.STORE_PRIVATE_BUY:
				return info.bestBuyItems;
			case Player.STORE_PRIVATE_MANUFACTURE:
				return info.bestCraftItems;
		}
		return null;
	}

	@Bypass("services.ItemBroker:main")
	public void main(Player player, NpcInstance npc, String[] var)
	{
		Integer type = Integer.parseInt(var[0]);

		HtmlMessage htmlMessage = new HtmlMessage(npc);
		htmlMessage.setFile("scripts/services/itembroker/main.htm");
		htmlMessage.addVar("type", type);

		player.sendPacket(htmlMessage);
	}

	@Bypass("services.ItemBroker:equipment")
	public void equipment(Player player, NpcInstance npc, String[] var)
	{
		Integer type = Integer.parseInt(var[0]);

		HtmlMessage htmlMessage = new HtmlMessage(npc);
		htmlMessage.setFile("scripts/services/itembroker/equipment.htm");
		htmlMessage.addVar("type", type);

		player.sendPacket(htmlMessage);
	}

	@Bypass("services.ItemBroker:equipment4")
	public void equipment4(Player player, NpcInstance npc, String[] var)
	{
		Integer type = Integer.parseInt(var[0]);

		HtmlMessage htmlMessage = new HtmlMessage(npc);
		htmlMessage.setFile("scripts/services/itembroker/equipment4.htm");
		htmlMessage.addVar("type", type);

		player.sendPacket(htmlMessage);
	}

	@Bypass("services.ItemBroker:list")
	public void list(Player player, NpcInstance npc, String[] var)
	{
		if(player == null || npc == null)
			return;

		if(var.length != 5)
		{
			Functions.show("Некорректная длина данных", player, npc);
			return;
		}

		int type;
		int itemType;
		int currentPage;
		int minEnchant;
		int rare;

		try
		{
			type = Integer.valueOf(var[0]);
			itemType = Integer.valueOf(var[1]);
			currentPage = Integer.valueOf(var[2]);
			minEnchant = Integer.valueOf(var[3]);
			rare = Integer.valueOf(var[4]);
		}
		catch(Exception e)
		{
			Functions.show("Некорректные данные", player, npc);
			return;
		}

		ItemClass itemClass = itemType >= ItemClass.values().length ? null : ItemClass.values()[itemType];

		TreeMap<String, TreeMap<Long, Item>> allItems = getItems(player, npc, type);
		if(allItems == null)
		{
			Functions.show("Ошибка - предметов такого типа не найдено", player, npc);
			return;
		}

		List<Item> items = new ArrayList<Item>(allItems.size() * 10);
		for(TreeMap<Long, Item> tempItems : allItems.values())
		{
			TreeMap<Long, Item> tempItems2 = new TreeMap<Long, Item>();
			for(Entry<Long, Item> entry : tempItems.entrySet())
			{
				Item tempItem = entry.getValue();
				if(tempItem == null)
					continue;
				if(tempItem.enchant < minEnchant)
					continue;
				ItemTemplate temp = tempItem.item != null ? tempItem.item.getItem() : ItemHolder.getInstance().getTemplate(tempItem.itemId);
				if(temp == null || (rare > 0 && !tempItem.rare))
					continue;
				if(itemClass == null ? !temp.isCommonItem() : temp.isCommonItem())
					continue;
				if(itemClass != null && itemClass != ItemClass.ALL && temp.getItemClass() != itemClass)
					continue;
				tempItems2.put(entry.getKey(), tempItem);
			}
			if(tempItems2.isEmpty())
				continue;

			Item item = type == Player.STORE_PRIVATE_BUY ? tempItems2.lastEntry().getValue() : tempItems2.firstEntry().getValue();
			if(item != null)
				items.add(item);
		}

		StringBuilder out = new StringBuilder(200);
		out.append("[npc_%objectId%_services.ItemBroker:main ");
		out.append(type);
		out.append("|««]&nbsp;&nbsp;");

		int totalPages = items.size();
		totalPages = totalPages / MAX_ITEMS_PER_PAGE + (totalPages % MAX_ITEMS_PER_PAGE > 0 ? 1 : 0);
		totalPages = Math.max(1, totalPages);
		currentPage = Math.min(totalPages, Math.max(1, currentPage));

		if(totalPages > 1)
		{
			int page = Math.max(1, Math.min(totalPages - MAX_PAGES_PER_LIST + 1, currentPage - MAX_PAGES_PER_LIST / 2));

			// линк на первую страницу
			if (page > 1)
				listPageNum(out, type, itemType, 1, minEnchant, rare, "1");
			// линк на страницу - 10
			if (currentPage > 11)
				listPageNum(out, type, itemType, currentPage - 10, minEnchant, rare, String.valueOf(currentPage - 10));
			// линк на предыдущую страницу
			if (currentPage > 1)
				listPageNum(out, type, itemType, currentPage - 1, minEnchant, rare, "<");

			for (int count = 0; count < MAX_PAGES_PER_LIST && page <= totalPages; count++, page++)
			{
				if (page == currentPage)
					out.append(page).append("&nbsp;");
				else
					listPageNum(out, type, itemType, page, minEnchant, rare, String.valueOf(page));
			}

			// линк на следующую страницу
			if (currentPage < totalPages)
				listPageNum(out, type, itemType, currentPage + 1, minEnchant, rare, ">");
			// линк на страницу + 10
			if (currentPage < totalPages - 10)
				listPageNum(out, type, itemType, currentPage + 10, minEnchant, rare, String.valueOf(currentPage + 10));
			// линк на последнюю страницу
			if (page <= totalPages)
				listPageNum(out, type, itemType, totalPages, minEnchant, rare, String.valueOf(totalPages));
		}

		out.append("<table width=100%>");

		if(items.size() > 0)
		{
			int count = 0;
			ListIterator<Item> iter = items.listIterator((currentPage - 1) * MAX_ITEMS_PER_PAGE);
			while (iter.hasNext() && count < MAX_ITEMS_PER_PAGE)
			{
				Item item = iter.next();
				ItemTemplate temp = item.item != null ? item.item.getItem() : ItemHolder.getInstance().getTemplate(item.itemId);
				if(temp == null)
					continue;

				out.append("<tr><td>");
				out.append(temp.getIcon32());
				out.append("</td><td><table width=100%><tr><td>[npc_%objectId%_services.ItemBroker:listForItem ");
				out.append(type);
				out.append(" ");
				out.append(item.itemId);
				out.append(" ");
				out.append(minEnchant);
				out.append(" ");
				out.append(rare);
				out.append(" ");
				out.append(itemType);
				out.append(" 1 ");
				out.append(currentPage);
				out.append("|");
				out.append(item.name);
				out.append("</td></tr><tr><td>price: ");
				out.append(Util.formatAdena(item.price));
				if (item.isPackage)
					out.append(" (Package)");
				if (temp.isStackable())
					out.append(", count: ").append(Util.formatAdena(item.count));
				out.append("</td></tr></table></td></tr>");
				count++;
			}
		}
		else if(player.isLangRus())
			out.append("<tr><td colspan=2>Ничего не найдено.</td></tr>");
		else
			out.append("<tr><td colspan=2>Nothing found.</td></tr>");

		out.append("</table><br>&nbsp;");

		Functions.show(out.toString(), player, npc);
	}

	private void listPageNum(StringBuilder out, int type, int itemType, int page, int minEnchant, int rare, String letter)
	{
		out.append("[npc_%objectId%_services.ItemBroker:list ");
		out.append(type);
		out.append(" ");
		out.append(itemType);
		out.append(" ");
		out.append(page);
		out.append(" ");
		out.append(minEnchant);
		out.append(" ");
		out.append(rare);
		out.append("|");
		out.append(letter);
		out.append("]&nbsp;");
	}

	@Bypass("services.ItemBroker:listForItem")
	public void listForItem(Player player, NpcInstance npc, String[] var)
	{
		if(player == null || npc == null)
			return;

		if(var.length < 7 || var.length > 12)
		{
			Functions.show("Некорректная длина данных", player, npc);
			return;
		}

		int type;
		int itemId;
		int minEnchant;
		int rare;
		// нужны только для запоминания, на какую страницу возвращаться
		int itemType;
		int currentPage;
		int returnPage;
		String[] search = null;

		try
		{
			type = Integer.valueOf(var[0]);
			itemId = Integer.valueOf(var[1]);
			minEnchant = Integer.valueOf(var[2]);
			rare = Integer.valueOf(var[3]);
			itemType = Integer.valueOf(var[4]);
			currentPage = Integer.valueOf(var[5]);
			returnPage = Integer.valueOf(var[6]);
			if (var.length > 7)
			{
				search = new String[var.length - 7];
				System.arraycopy(var, 7, search, 0, search.length);
			}
		}
		catch(Exception e)
		{
			Functions.show("Некорректные данные", player, npc);
			return;
		}

		ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
		if(template == null)
		{
			Functions.show("Ошибка - itemId не определен.", player, npc);
			return;
		}

		TreeMap<String, TreeMap<Long, Item>> tmpItems = getItems(player, npc, type);
		if(tmpItems == null)
		{
			Functions.show("Ошибка - такой тип предмета отсутствует.", player, npc);
			return;
		}

		TreeMap<Long, Item> allItems = tmpItems.get(template.getName());
		if(allItems == null)
		{
			Functions.show("Ошибка - предметов с таким названием не найдено.", player, npc);
			return;
		}

		StringBuilder out = new StringBuilder(200);
		if (search == null) // возврат в список
			listPageNum(out, type, itemType, returnPage, minEnchant, rare, "««");
		else // возврат в поиск
			findPageNum(out, type, returnPage, search, "««");
		out.append("&nbsp;&nbsp;");

		NavigableMap<Long, Item> sortedItems = type == Player.STORE_PRIVATE_BUY ? allItems.descendingMap() : allItems;
		if(sortedItems == null)
		{
			Functions.show("Ошибка - ничего не найдено.", player, npc);
			return;
		}

		List<Item> items = new ArrayList<Item>(sortedItems.size());
		for (Item item : sortedItems.values())
		{
			if(item == null || item.enchant < minEnchant || (rare > 0 && !item.rare))
				continue;

			items.add(item);
		}

		int totalPages = items.size();
		totalPages = totalPages / MAX_ITEMS_PER_PAGE + (totalPages % MAX_ITEMS_PER_PAGE > 0 ? 1 : 0);
		totalPages = Math.max(1, totalPages);
		currentPage = Math.min(totalPages, Math.max(1, currentPage));

		if(totalPages > 1)
		{
			int page = Math.max(1, Math.min(totalPages - MAX_PAGES_PER_LIST + 1, currentPage - MAX_PAGES_PER_LIST / 2));

			// линк на первую страницу
			if (page > 1)
				listForItemPageNum(out, type, itemId, minEnchant, rare, itemType, 1, returnPage, search, "1");
			// линк на страницу - 10
			if (currentPage > 11)
				listForItemPageNum(out, type, itemId, minEnchant, rare, itemType, currentPage - 10, returnPage, search, String.valueOf(currentPage - 10));
			// линк на предыдущую страницу
			if (currentPage > 1)
				listForItemPageNum(out, type, itemId, minEnchant, rare, itemType, currentPage - 1, returnPage, search, "<");

			for (int count = 0; count < MAX_PAGES_PER_LIST && page <= totalPages; count++, page++)
			{
				if (page == currentPage)
					out.append(page).append("&nbsp;");
				else
					listForItemPageNum(out, type, itemId, minEnchant, rare, itemType, page, returnPage, search, String.valueOf(page));
			}

			// линк на следующую страницу
			if (currentPage < totalPages)
				listForItemPageNum(out, type, itemId, minEnchant, rare, itemType, currentPage + 1, returnPage, search, ">");
			// линк на страницу + 10
			if (currentPage < totalPages - 10)
				listForItemPageNum(out, type, itemId, minEnchant, rare, itemType, currentPage + 10, returnPage, search, String.valueOf(currentPage + 10));
			// линк на последнюю страницу
			if (page <= totalPages)
				listForItemPageNum(out, type, itemId, minEnchant, rare, itemType, totalPages, returnPage, search, String.valueOf(totalPages));
		}

		out.append("<table width=100%>");

		if (items.size() > 0)
		{
			int count = 0;
			ListIterator<Item> iter = items.listIterator((currentPage - 1) * MAX_ITEMS_PER_PAGE);
			while (iter.hasNext() && count < MAX_ITEMS_PER_PAGE)
			{
				Item item = iter.next();
				ItemTemplate temp = item.item != null ? item.item.getItem() : ItemHolder.getInstance().getTemplate(item.itemId);
				if(temp == null)
					continue;

				out.append("<tr><td>");
				out.append(temp.getIcon32());
				out.append("</td><td><table width=100%><tr><td>[npc_%objectId%_services.ItemBroker:path ");
				out.append(type);
				out.append(" ");
				out.append(item.itemId);
				out.append(" ");
				out.append(item.itemObjId);
				out.append("|");
				out.append(item.name);
				out.append("</td></tr><tr><td>price: ");
				out.append(Util.formatAdena(item.price));
				if (item.isPackage)
					out.append(" (Package)");
				if (temp.isStackable())
					out.append(", count: ").append(Util.formatAdena(item.count));
				out.append(", owner: ").append(item.merchantName);
				out.append("</td></tr></table></td></tr>");
				count++;
			}
		}
		else if(player.isLangRus())
			out.append("<tr><td colspan=2>Ничего не найдено.</td></tr>");
		else
			out.append("<tr><td colspan=2>Nothing found.</td></tr>");

		out.append("</table><br>&nbsp;");

		Functions.show(out.toString(), player, npc);
	}

	private void listForItemPageNum(StringBuilder out, int type, int itemId, int minEnchant, int rare, int itemType, int page, int returnPage, String[] search, String letter)
	{
		out.append("[npc_%objectId%_services.ItemBroker:listForItem ");
		out.append(type);
		out.append(" ");
		out.append(itemId);
		out.append(" ");
		out.append(minEnchant);
		out.append(" ");
		out.append(rare);
		out.append(" ");
		out.append(itemType);
		out.append(" ");
		out.append(page);
		out.append(" ");
		out.append(returnPage);
		if (search != null)
			for (int i = 0; i < search.length; i++)
			{
				out.append(" ");
				out.append(search[i]);
			}
		out.append("|");
		out.append(letter);
		out.append("]&nbsp;");
	}

	@Bypass("services.ItemBroker:path")
	public void path(Player player, NpcInstance npc, String[] var)
	{
		if(player == null || npc == null)
			return;

		if(var.length != 3)
		{
			Functions.show("Некорректная длина данных", player, npc);
			return;
		}

		int type;
		int itemId;
		int itemObjId;

		try
		{
			type = Integer.valueOf(var[0]);
			itemId = Integer.valueOf(var[1]);
			itemObjId = Integer.valueOf(var[2]);
		}
		catch(Exception e)
		{
			Functions.show("Некорректные данные", player, npc);
			return;
		}

		ItemTemplate temp = ItemHolder.getInstance().getTemplate(itemId);
		if(temp == null)
		{
			Functions.show("Ошибка - itemId не определен.", player, npc);
			return;
		}

		TreeMap<String, TreeMap<Long, Item>> allItems = getItems(player, npc, type);
		if(allItems == null)
		{
			Functions.show("Ошибка - предметов такого типа не найдено.", player, npc);
			return;
		}

		TreeMap<Long, Item> items = allItems.get(temp.getName());
		if(items == null)
		{
			Functions.show("Ошибка - предметов с таким именем не найдено.", player, npc);
			return;
		}

		Item item = null;
		for(Item i : items.values())
			if(i.itemObjId == itemObjId)
			{
				item = i;
				break;
			}

		if(item == null)
		{
			Functions.show("Ошибка - предмет не найден.", player, npc);
			return;
		}

		boolean found = false;
		Player trader = GameObjectsStorage.getPlayer(item.merchantObjectId);
		if (trader == null)
		{
			Functions.show("Торговец не найден, возможно он вышел из игры.", player, npc);
			return;
		}

		switch (type)
		{
			case Player.STORE_PRIVATE_SELL:
				if (trader.getSellList() != null)
				{
					if (trader.getPrivateStoreType() == Player.STORE_PRIVATE_SELL_PACKAGE)
					{
						if (item.isPackage)
						{
							long packagePrice = 0;
							for(TradeItem tradeItem : trader.getSellList())
							{
								packagePrice += tradeItem.getOwnersPrice() * tradeItem.getCount();
								if (tradeItem.getItemId() == item.itemId)
									found = true;
							}

							if (packagePrice != item.price)
								found = false;
						}
					}
					else
					{
						if (!item.isPackage)
							for(TradeItem tradeItem : trader.getSellList())
								if (tradeItem.getItemId() == item.itemId && tradeItem.getOwnersPrice() == item.price)
								{
									found = true;
									break;
								}
					}
				}
				break;
			case Player.STORE_PRIVATE_BUY:
				if (trader.getBuyList() != null)
					for(TradeItem tradeItem : trader.getBuyList())
						if (tradeItem.getItemId() == item.itemId && tradeItem.getOwnersPrice() == item.price)
						{
							found = true;
							break;
						}
				break;
			case Player.STORE_PRIVATE_MANUFACTURE:
				found = true; // not done
				break;
		}

		if (!found)
		{
			if (player.isLangRus())
				Functions.show("Внимание, цена или предмет изменились, будьте осторожны !", player, npc);
			else
				Functions.show("Caution, price or item was changed, please be careful !", player, npc);
		}

		ExShowTrace trace = new ExShowTrace();
		trace.addLine(item.player, item.player, 30, 60000);
		player.sendPacket(trace);
		RadarControl rc = new RadarControl(0, 1, item.player);
		player.sendPacket(rc);

		// Показываем игроку торговца, если тот скрыт
		if(player.getVarB(Player.NO_TRADERS_VAR))
		{
			player.sendPacket(new CharInfo(trader));
			player.sendPacket(trader.getPrivateStoreMsgPacket(player));
		}

		//Устанавливаем таргет на торговца для того, чтобы быстро до него дойти
		player.setTarget(trader);
	}

	public void updateInfo(Player player, NpcInstance npc)
	{
		NpcInfo info = _npcInfos.get(npc.getObjectId());
		if(info == null || info.lastUpdate < System.currentTimeMillis() - 100000)
		{
			info = new NpcInfo();
			info.lastUpdate = System.currentTimeMillis();
			info.bestBuyItems = new TreeMap<String, TreeMap<Long, Item>>();
			info.bestSellItems = new TreeMap<String, TreeMap<Long, Item>>();
			info.bestCraftItems = new TreeMap<String, TreeMap<Long, Item>>();

			int itemObjId = 0; // Обычный objId не подходит для покупаемых предметов

			long refresh = System.currentTimeMillis() - 60000L * Config.SERVICES_ITEM_BROKER_REFRESH_DELAY;

			for(Player pl : World.getAroundPlayers(npc))
			{
				if (pl.getPrivateStoreStartTime() > refresh) // торгаш сидит слишком мало
					continue;

				TreeMap<String, TreeMap<Long, Item>> items = null;
				List<TradeItem> tradeList = null;

				int type = pl.getPrivateStoreType();
				switch(type)
				{
					case Player.STORE_PRIVATE_SELL:
						items = info.bestSellItems;
						tradeList = pl.getSellList();

						for(TradeItem item : tradeList)
						{
							ItemTemplate temp = item.getItem();
							if(temp == null)
								continue;
							TreeMap<Long, Item> oldItems = items.get(temp.getName());
							if(oldItems == null)
							{
								oldItems = new TreeMap<Long, Item>();
								items.put(temp.getName(), oldItems);
							}
							Item newItem = new Item(item.getItemId(), type, item.getOwnersPrice(), item.getCount(), item.getEnchantLevel(), temp.getName(), pl.getObjectId(), pl.getName(), pl.getLoc(), item.getObjectId(), item, false);
							long key = newItem.price * 100;
							while(key < newItem.price * 100 + 100 && oldItems.containsKey(key))
								// До 100 предметов с одинаковыми ценами
								key++;
							oldItems.put(key, newItem);
						}

						break;
					case Player.STORE_PRIVATE_SELL_PACKAGE:
						items = info.bestSellItems;
						tradeList = pl.getSellList();

						long packagePrice = 0;
						for(TradeItem item : tradeList)
							packagePrice += item.getOwnersPrice() * item.getCount();

						for(TradeItem item : tradeList)
						{
							ItemTemplate temp = item.getItem();
							if(temp == null)
								continue;
							TreeMap<Long, Item> oldItems = items.get(temp.getName());
							if(oldItems == null)
							{
								oldItems = new TreeMap<Long, Item>();
								items.put(temp.getName(), oldItems);
							}
							Item newItem = new Item(item.getItemId(), type, packagePrice, item.getCount(), item.getEnchantLevel(), temp.getName(), pl.getObjectId(), pl.getName(), pl.getLoc(), item.getObjectId(), item, true);
							long key = newItem.price * 100;
							while(key < newItem.price * 100 + 100 && oldItems.containsKey(key))
								// До 100 предметов с одинаковыми ценами
								key++;
							oldItems.put(key, newItem);
						}

						break;
					case Player.STORE_PRIVATE_BUY:
						items = info.bestBuyItems;
						tradeList = pl.getBuyList();

						for(TradeItem item : tradeList)
						{
							ItemTemplate temp = item.getItem();
							if(temp == null)
								continue;
							TreeMap<Long, Item> oldItems = items.get(temp.getName());
							if(oldItems == null)
							{
								oldItems = new TreeMap<Long, Item>();
								items.put(temp.getName(), oldItems);
							}
							Item newItem = new Item(item.getItemId(), type, item.getOwnersPrice(), item.getCount(), item.getEnchantLevel(), temp.getName(), pl.getObjectId(), pl.getName(), pl.getLoc(), itemObjId++, item, false);
							long key = newItem.price * 100;
							while(key < newItem.price * 100 + 100 && oldItems.containsKey(key))
								// До 100 предметов с одинаковыми ценами
								key++;
							oldItems.put(key, newItem);
						}

						break;
					case Player.STORE_PRIVATE_MANUFACTURE:
						items = info.bestCraftItems;
						List<ManufactureItem> createList = pl.getCreateList();
						if(createList == null)
							continue;

						for(ManufactureItem mitem : createList)
						{
							int recipeId = mitem.getRecipeId();
							Recipe recipe = RecipeHolder.getInstance().getRecipeByRecipeId(recipeId);
							if(recipe == null)
								continue;

							ItemTemplate temp = ItemHolder.getInstance().getTemplate(recipe.getItemId());
							if(temp == null)
								continue;
							TreeMap<Long, Item> oldItems = items.get(temp.getName());
							if(oldItems == null)
							{
								oldItems = new TreeMap<Long, Item>();
								items.put(temp.getName(), oldItems);
							}
							Item newItem = new Item(recipe.getItemId(), type, mitem.getCost(), recipe.getCount(), 0, temp.getName(), pl.getObjectId(), pl.getName(), pl.getLoc(), itemObjId++, null, false);
							long key = newItem.price * 100;
							while(key < newItem.price * 100 + 100 && oldItems.containsKey(key))
								// До 100 предметов с одинаковыми ценами
								key++;
							oldItems.put(key, newItem);
						}

						break;
					default:
						continue;
				}
			}
			_npcInfos.put(npc.getObjectId(), info);
		}
	}

	@Bypass("services.ItemBroker:find")
	public void find(Player player, NpcInstance npc, String[] var)
	{
		if(player == null || npc == null)
			return;

		if(var.length < 3 || var.length > 7)
		{
			if (player.isLangRus())
				Functions.show("Пожалуйста введите от 1 до 16 символов.<br>[npc_%objectId%_Chat 0|<font color=\"FF9900\">Назад</font>]", player, npc);
			else
				Functions.show("Please enter from 1 up to 16 symbols.<br>[npc_%objectId%_Chat 0|<font color=\"FF9900\">Back</font>]", player, npc);
			return;
		}

		int type;
		int currentPage;
		int minEnchant = 0;
		String[] search = null;

		try
		{
			type = Integer.valueOf(var[0]);
			currentPage = Integer.valueOf(var[1]);
			search = new String[var.length - 2];
			String line;
			for (int i = 0; i < search.length; i++)
			{
				line = var[i + 2].trim().toLowerCase();
				search[i] = line;
				if (line.length() > 1 && line.startsWith("+"))
					minEnchant = Integer.valueOf(line.substring(1));
			}
		}
		catch(Exception e)
		{
			Functions.show("Некорректные данные", player, npc);
			return;
		}

		TreeMap<String, TreeMap<Long, Item>> allItems = getItems(player, npc, type);
		if(allItems == null)
		{
			Functions.show("Ошибка - предметов с таким типом не найдено.", player, npc);
			return;
		}

		List<Item> items = new ArrayList<Item>();
		String line;
		TreeMap<Long, Item> itemMap;
		Item item;
		mainLoop: for(Entry<String, TreeMap<Long, Item>> entry : allItems.entrySet())
		{
			for(int i = 0; i < search.length; i++)
			{
				line = search[i];
				if (line.startsWith("+"))
					continue;
				if(entry.getKey().toLowerCase().indexOf(line) == -1)
					continue mainLoop;
			}

			itemMap = entry.getValue();
			item = null;
			for (Item itm : itemMap.values()) // Ищем первый подходящий предмет
				if (itm != null && itm.enchant >= minEnchant)
				{
					item = itm;
					break;
				}

			if(item != null)
				items.add(item);
		}

		StringBuilder out = new StringBuilder(200);
		out.append("[npc_%objectId%_services.ItemBroker:main ");
		out.append(type);
		out.append("|««]&nbsp;&nbsp;");

		int totalPages = items.size();
		totalPages = totalPages / MAX_ITEMS_PER_PAGE + (totalPages % MAX_ITEMS_PER_PAGE > 0 ? 1 : 0);
		totalPages = Math.max(1, totalPages);
		currentPage = Math.min(totalPages, Math.max(1, currentPage));

		if(totalPages > 1)
		{
			int page = Math.max(1, Math.min(totalPages - MAX_PAGES_PER_LIST + 1, currentPage - MAX_PAGES_PER_LIST / 2));

			// линк на первую страницу
			if (page > 1)
				findPageNum(out, type, 1, search, "1");
			// линк на страницу - 10
			if (currentPage > 11)
				findPageNum(out, type, currentPage - 10, search, String.valueOf(currentPage - 10));
			// линк на предыдущую страницу
			if (currentPage > 1)
				findPageNum(out, type, currentPage - 1, search, "<");

			for (int count = 0; count < MAX_PAGES_PER_LIST && page <= totalPages; count++, page++)
			{
				if (page == currentPage)
					out.append(page).append("&nbsp;");
				else
					findPageNum(out, type, page, search, String.valueOf(page));
			}

			// линк на следующую страницу
			if (currentPage < totalPages)
				findPageNum(out, type, currentPage + 1, search, ">");
			// линк на страницу + 10
			if (currentPage < totalPages - 10)
				findPageNum(out, type, currentPage + 10, search, String.valueOf(currentPage + 10));
			// линк на последнюю страницу
			if (page <= totalPages)
				findPageNum(out, type, totalPages, search, String.valueOf(totalPages));
		}

		out.append("<table width=100%>");

		if(items.size() > 0)
		{
			int count = 0;
			ListIterator<Item> iter = items.listIterator((currentPage - 1) * MAX_ITEMS_PER_PAGE);
			while (iter.hasNext() && count < MAX_ITEMS_PER_PAGE)
			{
				item = iter.next();
				ItemTemplate temp = item.item != null ? item.item.getItem() : ItemHolder.getInstance().getTemplate(item.itemId);
				if(temp == null)
					continue;

				out.append("<tr><td>");
				out.append(temp.getIcon32());
				out.append("</td><td><table width=100%><tr><td>[npc_%objectId%_services.ItemBroker:listForItem ");
				out.append(type);
				out.append(" ");
				out.append(item.itemId);
				out.append(" ");
				out.append(minEnchant);
				out.append(" 0 0 1 ");
				out.append(currentPage);
				if (search != null)
					for (int i = 0; i < search.length; i++)
					{
						out.append(" ");
						out.append(search[i]);
					}
				out.append("|");
				out.append("<font color=\"LEVEL\">");
				out.append(temp.getName()); // Здесь берем название из шаблона
				out.append("</font>]");
				out.append("</td></tr>");
				out.append("</table></td></tr>");
				count++;
			}
		}
		else if(player.isLangRus())
			out.append("<tr><td colspan=2>Ничего не найдено.</td></tr>");
		else
			out.append("<tr><td colspan=2>Nothing found.</td></tr>");
		out.append("</table><br>&nbsp;");

		Functions.show(out.toString(), player, npc);
	}

	private void findPageNum(StringBuilder out, int type, int page, String[] search, String letter)
	{
		out.append("[npc_%objectId%_services.ItemBroker:find ");
		out.append(type);
		out.append(" ");
		out.append(page);
		if (search != null)
			for (int i = 0; i < search.length; i++)
			{
				out.append(" ");
				out.append(search[i]);
			}
		out.append("|");
		out.append(letter);
		out.append("]&nbsp;");
	}
}