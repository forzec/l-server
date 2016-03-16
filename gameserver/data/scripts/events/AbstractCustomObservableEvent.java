package events;

import java.util.List;

import npc.model.events.CustomObservationManagerInstance;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.data.xml.holder.InstantZoneHolder;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.SingleMatchEvent;
import org.mmocore.gameserver.model.entity.events.objects.DoorObject;
import org.mmocore.gameserver.model.entity.events.objects.SpawnExObject;
import org.mmocore.gameserver.network.l2.components.IBroadcastPacket;
import org.mmocore.gameserver.network.l2.s2c.ExReceiveOlympiad.MatchList.ArenaInfo;
import org.mmocore.gameserver.templates.InstantZone;
import org.mmocore.gameserver.utils.Location;

public abstract class AbstractCustomObservableEvent extends SingleMatchEvent
{
	private static final String DOORS = "doors";
	private static final String SPAWNS = "spawns";

	private final int _instanceId;

	private Reflection _reflection;
	private ArenaInfo _arena = null;
	private int _arenaId = 0;
	private Location _observerCoords = null;

	public AbstractCustomObservableEvent(MultiValueSet<String> set)
	{
		super(set);
		_instanceId = set.getInteger("instance_id", getId());
	}

	protected AbstractCustomObservableEvent(int id, int status, int type, String player1, String player2)
	{
		super(id, StringUtils.EMPTY);
		final AbstractCustomObservableEvent source = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, id);
		source.cloneTo(this);

		// копируем двери потому что в каждом новом рефлекте они свои
		final List<DoorObject> doorList = source.getObjects(DOORS);
		if (!doorList.isEmpty())
			for (DoorObject door : doorList)
				addObject(DOORS, new DoorObject(door.getId()));

		final List<SpawnExObject> spawnList = source.getObjects(SPAWNS);
		if (!spawnList.isEmpty())
			for (SpawnExObject spawn : spawnList)
				addObject(SPAWNS, new SpawnExObject(spawn));

		_instanceId = source.getInstanceId();
		final InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(_instanceId);
		_reflection = new Reflection();
		_reflection.init(instantZone);
		_observerCoords = instantZone.getTeleportCoords().size() > 1 ? instantZone.getTeleportCoords().get(2) : instantZone.getTeleportCoord();

		_arenaId = CustomObservationManagerInstance.registerBattle(this);
		_arena =  new ArenaInfo(_arenaId, status, type, player1, player2);
	}

	public int getInstanceId()
	{
		return _instanceId;
	}

	public ArenaInfo getArena()
	{
		return _arena;
	}

	public int getArenaId()
	{
		return _arenaId;
	}

	public Location getObserverCoords()
	{
		return _observerCoords;
	}

	public void addObserver(Player player)
	{
		//
	}

	public void removeObserver(Player player)
	{
		//
	}

	@Override
	public void stopEvent()
	{
		super.stopEvent();
		CustomObservationManagerInstance.unRegisterBattle(this);
	}

	@Override
	public EventType getType()
	{
		return EventType.PVP_EVENT;
	}

	@Override
	public Reflection getReflection()
	{
		return _reflection;
	}

	@Override
	public void sendPacket(IBroadcastPacket packet)
	{
		for (Creature c : _reflection.getPlayersAndObservers())
			c.sendPacket(packet);
	}

	@Override
	public void sendPackets(IBroadcastPacket... packets)
	{
		for (Creature c : _reflection.getPlayersAndObservers())
			c.sendPacket(packets);
	}

	public void sendPacketToObservers(IBroadcastPacket packet)
	{
		for (Creature c : _reflection.getObservers())
			c.sendPacket(packet);		
	}
}