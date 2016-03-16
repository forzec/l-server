package npc.model.events;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.dom4j.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.commons.data.xml.AbstractHolder;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.Announcements;
import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.xml.holder.ItemHolder;
import org.mmocore.gameserver.instancemanager.SpawnManager;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.ChatType;
import org.mmocore.gameserver.network.l2.components.CustomChatMessage;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.components.IBroadcastPacket;
import org.mmocore.gameserver.network.l2.s2c.L2GameServerPacket;
import org.mmocore.gameserver.network.l2.s2c.NpcInfo;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.GmListTable;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.templates.spawn.PeriodOfDay;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;

import events.FunEvent;

public class MorpherNpcInstance extends NpcInstance
{
	private static final RandomPointsHolder HOLDER = new RandomPointsHolder();
	private static int LAST_TOWN = -1;
	private static final int MIN_VISIBILITY_RANGE = 750;
	private static final int MAX_VISIBILITY_RANGE = 1000;

	private static final String SEARCH_TITLE = "gatekeeper";
	/*private static final int GATEKEEPERS[] = new int[] {
		30006, 30059, 30080, 30134, 30146, 30162, 30177, 30233,
		30256, 30320, 30427, 30429, 30483, 30484, 30485, 30486,
		30487, 30540, 30576, 30716, 30719, 30722, 30727, 30836,
		30848, 30878, 30899, 31275, 31320, 31376, 31383, 31698,
		31699, 31964, 32163, 32181, 32184, 32186, 32378, 32614,
		32714, 32740
	};*/

	private final List<Integer> _knownPlayers = new ArrayList<Integer>(100);
	private ScheduledFuture<?> _knownPlayersUpdateTask = null;

	private volatile boolean _found = false;

	public MorpherNpcInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();

		LAST_TOWN = HOLDER.getRandomTownId(LAST_TOWN);
		final Location teleTo = HOLDER.getRandomPointInTown(LAST_TOWN);
		if (teleTo == null)
			return;

		NpcTemplate nearest = null;
		double minDist = Double.MAX_VALUE;

		for(Spawner spawn : SpawnManager.getInstance().getSpawners(PeriodOfDay.NONE.name()))
			if (spawn != null)
			{
				NpcInstance npc = spawn.getLastSpawn();
				if (npc != null && npc.isVisible() && SEARCH_TITLE.equalsIgnoreCase(npc.getTitle()))
				{
					double dist = npc.getDistance(teleTo);
					if (dist < minDist)
					{
						minDist = dist;
						nearest = npc.getTemplate();
					}
				}
			}

		if (nearest == null)
			return;

		_template = nearest;
		setName(getTemplate().name);
		setTitle(getTemplate().title);
		setLHandId(getTemplate().lhand);
		setRHandId(getTemplate().rhand);
		setCollisionHeight(getTemplate().collisionHeight);
		setCollisionRadius(getTemplate().collisionRadius);
		teleToLocation(teleTo);
	}

	@Override
	public void onDelete()
	{
		super.onDelete();

		if (_knownPlayersUpdateTask != null)
		{
			_knownPlayersUpdateTask.cancel(false);
			_knownPlayersUpdateTask = null;
		}

		if (!_found)
			Announcements.getInstance().announceToAll(new CustomChatMessage("SearchEvent.MorpherNpcDeleted", ChatType.ANNOUNCEMENT).addString(HOLDER.getTownName(LAST_TOWN)));
	}

	@Override
	public void onCastEndTime(SkillEntry se)
	{
		super.onCastEndTime(se);
		deleteMe();
	}

	@Override
	public boolean onTeleported()
	{
		if (!super.onTeleported())
			return false;

		if (_knownPlayersUpdateTask == null)
			_knownPlayersUpdateTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new KnownPlayersUpdater(), 100, 500);

		Announcements.getInstance().announceToAll(new CustomChatMessage("SearchEvent.MorpherNpcSpawned", ChatType.ANNOUNCEMENT));
		GmListTable.broadcastMessageToGMs("Morpher NPC spawned at "+getLoc().x+","+getLoc().y+","+getLoc().z+" in "+HOLDER.getTownName(LAST_TOWN));
		return true;
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("reward"))
		{
			if (!_found)
			{
				_found = true;
				final FunEvent event = getEvent(FunEvent.class);
				if (event != null)
				{
					final int requiredItemId = event.getParameters().getInteger("required_item_id", ItemTemplate.ITEM_ID_ADENA);
					final int requiredItemCount = event.getParameters().getInteger("required_item_count", 1000);
					final ItemTemplate it = ItemHolder.getInstance().getTemplate(requiredItemId);
					if (it != null && !ItemFunctions.deleteItem(player, requiredItemId, requiredItemCount, true))
					{
						_found = false;
						player.sendPacket(new HtmlMessage(this, "events/SearchEvent/no.htm").replace("%name%", it.getName()).replace("%count%", requiredItemCount > 1 ? String.valueOf(requiredItemCount) : ""));
						return;
					}
					int rewardItemId = event.getParameters().getInteger("reward_item_id", ItemTemplate.ITEM_ID_ADENA);
					int rewardItemCount = event.getParameters().getInteger("reward_item_count", 1);
					ItemFunctions.addItem(player, rewardItemId, rewardItemCount, true);
				}
				Announcements.getInstance().announceToAll(new CustomChatMessage("SearchEvent.MorpherNpcFound", ChatType.ANNOUNCEMENT).addString(player.getName()).addString(HOLDER.getTownName(LAST_TOWN)));
				player.sendPacket(new HtmlMessage(this, "events/SearchEvent/thanks.htm"));
				doCast(SkillTable.getInstance().getSkillEntry(1050, 1), this, false);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... ar)
	{
		if (!_found)
		{
			final FunEvent event = getEvent(FunEvent.class);
			if (event != null)
			{
				final int requiredItemId = event.getParameters().getInteger("required_item_id", ItemTemplate.ITEM_ID_ADENA);
				final int requiredItemCount = event.getParameters().getInteger("required_item_count", 1000);
				final ItemTemplate it = ItemHolder.getInstance().getTemplate(requiredItemId);
				if (it != null)
				{
					player.sendPacket(new HtmlMessage(this, "events/SearchEvent/found.htm").replace("%name%", it.getName()).replace("%count%", requiredItemCount > 1 ? String.valueOf(requiredItemCount) : ""));
					return;
				}
			}
			super.showChatWindow(player, val, ar);
		}
		else
			player.sendPacket(new HtmlMessage(this, "events/SearchEvent/already_found.htm"));
	}

	@Override
	public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper)
	{
		return Collections.emptyList();
	}

	@Override
	public List<L2GameServerPacket> deletePacketList(Player forPlayer)
	{
		if (!knowThisPlayer(forPlayer))
			return Collections.emptyList();

		return super.deletePacketList(forPlayer);
	}

	@Override
	public void broadcastCharInfoImpl()
	{
		for(Player player : World.getAroundObservers(this))
			if (knowThisPlayer(player))
				player.sendPacket(new NpcInfo(this, player).update());
	}

	@Override
	public void broadcastPacketToOthers(IBroadcastPacket... packets)
	{
		if(!isVisible() || packets.length == 0)
			return;

		List<Player> players = World.getAroundObservers(this);
		Player target;
		for(int i = 0; i < players.size(); i++)
		{
			target = players.get(i);
			if (knowThisPlayer(target))
				target.sendPacket(packets);
		}
	}

	@Override
	public void broadcastPacketToOthers(List<IBroadcastPacket> packets)
	{
		if(!isVisible() || packets.isEmpty())
			return;

		List<Player> players = World.getAroundObservers(this);
		Player target;
		for(int i = 0; i < players.size(); i++)
		{
			target = players.get(i);
			if (knowThisPlayer(target))
				target.sendPacket(packets);
		}
	}

	private final boolean knowThisPlayer(Player player)
	{
		return _knownPlayers.contains(player.getObjectId());
	}

	private final void addPlayer(Player player)
	{
		if (knowThisPlayer(player))
			return;

		_knownPlayers.add(player.getObjectId());
		player.sendPacket(super.addPacketList(player, null));
	}

	private final void forgetPlayer(Player player)
	{
		if (!knowThisPlayer(player))
			return;

		_knownPlayers.remove(Integer.valueOf(player.getObjectId()));
		player.sendPacket(super.deletePacketList(player));
	}

	private final class KnownPlayersUpdater implements Runnable
	{
		public final void run()
		{
			final List<Player> players = World.getAroundObservers(MorpherNpcInstance.this);
			final List<Integer> possiblyKnown = new ArrayList<Integer>(Math.max(_knownPlayers.size(), 4));
			Player player;
			int distance;
			for(int i = 0; i < players.size(); i++)
			{
				player = players.get(i);
				distance = (int)getDistance(player);
				if (distance > MAX_VISIBILITY_RANGE)
					forgetPlayer(player);
				else
				{
					if (distance < MIN_VISIBILITY_RANGE)
						addPlayer(player);					
					possiblyKnown.add(player.getObjectId());
				}
			}
			// cleanup
			Iterator<Integer> iter = _knownPlayers.iterator();
			while (iter.hasNext())
				if (!possiblyKnown.contains(iter.next()))
					iter.remove();
		}
	}

	private static final class RandomPointsHolder extends AbstractHolder
	{
		private final Map<Integer, ArrayList<Location>> POINTS = new HashMap<Integer, ArrayList<Location>>();
		private final Map<Integer, String> NAMES = new HashMap<Integer, String>();
		private int _size = 0;

		public RandomPointsHolder()
		{
			super();
			new RandomPointsParser(this);
		}

		@Override
		public final int size()
		{
			return _size;
		}

		@Override
		public final void clear()
		{
			//
		}

		protected final void addTown(int townId, String townName, ArrayList<Location> points)
		{
			POINTS.put(townId, points);
			NAMES.put(townId, townName);
			_size += points.size();
		}

		public final String getTownName(int townId)
		{
			return NAMES.get(townId);
		}

		public final int getRandomTownId(int exclude)
		{
			int value = -1;
			int index = Rnd.get(POINTS.size());
			for (int townId : POINTS.keySet())
				if (townId != exclude)
				{
					value = townId;
					if (index-- <= 0)
						break;
				}

			return value;
		}

		public final Location getRandomPointInTown(int townId)
		{
			ArrayList<Location> town = POINTS.get(townId);
			if (town == null)
				return null;

			return town.get(Rnd.get(town.size()));
		}
	}

	private static final class RandomPointsParser extends AbstractFileParser<RandomPointsHolder>
	{
		private final RandomPointsHolder _holder;

		public RandomPointsParser(RandomPointsHolder holder)
		{
			super(holder);
			_holder = holder;
			load();
		}

		@Override
		public final File getXMLFile()
		{
			return new File(Config.DATAPACK_ROOT, "data/random_town_points.xml");
		}

		@Override
		public final String getDTDFileName()
		{
			return "random_town_points.dtd";
		}

		@Override
		protected final void readData(Element rootElement) throws Exception
		{
			for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
			{
				Element townElement = (Element) iterator.next();
				int townId = Integer.parseInt(townElement.attributeValue("id"));
				String townName = townElement.attributeValue("name");

				ArrayList<Location> townPoints = new ArrayList<Location>();
				for(Iterator<Element> pointsIterator = townElement.elementIterator(); pointsIterator.hasNext();)
					townPoints.add(Location.parse(pointsIterator.next()));

				_holder.addTown(townId, townName, townPoints);
			}
		}
	}
}