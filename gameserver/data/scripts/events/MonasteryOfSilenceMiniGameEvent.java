package events;

import java.util.List;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.objects.SpawnExObject;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author VISTALL
 * @date 23:38/02.05.2012
 * Sumiel 32758
 */
public class MonasteryOfSilenceMiniGameEvent extends Event
{
	public static enum State
	{
		NONE,
		START,
		SELECT,
		FAILED
	}

	public static final int NEED_ITEM_ID = 15540;
	public static final int GIVE_ITEM_ID = 15485;

	public static final String NPC = "npc";
	public static final String TARGETS = "targets";
	public static final String CHEST = "chest";

	protected State _state = State.NONE;
	private long _startTime;

	private Player _player;

	private MonasteryOfSilenceMiniGameFireEvent _fireEvent;
	protected long _lastWinTime;

	public MonasteryOfSilenceMiniGameEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	@Override
	public void initEvent()
	{
		super.initEvent();

		_fireEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, getId() + 1);

		SpawnExObject first = getFirstObject(NPC);
		NpcInstance firstNpc = first.getFirstSpawned();
		if (firstNpc == null) // для запуска сервера без npc
			return;

		firstNpc.addEvent(this);
		SpawnExObject furnaces = getFirstObject(TARGETS);
		List<NpcInstance> npcs = furnaces.getAllSpawned();
		for(NpcInstance npc : npcs)
			npc.addEvent(this);
	}

	@Override
	public void startEvent()
	{
		super.startEvent();

		ItemFunctions.deleteItem(_player, NEED_ITEM_ID, 1);
		ItemFunctions.addItem(_player, GIVE_ITEM_ID, 1);

		_fireEvent._failCount = 0;

		_state = State.START;
	}

	@Override
	public void stopEvent()
	{
		super.stopEvent();

		clearActions();

		_player = null;
		_fireEvent.clearActions();
		_fireEvent.stopEvent();

		_state = State.NONE;
	}

	@Override
	public void action(String name, boolean start)
	{
		if(name.equals("fire_event"))
			_fireEvent.reCalcNextTime(false);
		else
			super.action(name, start);
	}

	@Override
	public void reCalcNextTime(boolean onInit)
	{
		if(onInit)
			return;

		_startTime = System.currentTimeMillis();

		registerActions();
	}

	@Override
	public EventType getType()
	{
		return EventType.MAIN_EVENT;
	}

	@Override
	protected long startTimeMillis()
	{
		return _startTime;
	}

	@Override
	public boolean isInProgress()
	{
		return _state != State.NONE;
	}

	@Override
	public NpcInstance getNpcByNpcId(int npcId)
	{
		SpawnExObject first = getFirstObject(NPC);
		return first.getFirstSpawned();
	}

	public void fireFurnace(NpcInstance npc)
	{
		_fireEvent.fireFurnace(npc);
	}

	public boolean isFailed()
	{
		return _state == State.FAILED;
	}

	public int getFailCount()
	{
		return _fireEvent.getFailCount();
	}

	public void restart()
	{
		_state = State.START;

		_fireEvent.reCalcNextTime(false);
	}

	public Player getPlayer()
	{
		return _player;
	}

	public void setPlayer(Player player)
	{
		_player = player;
	}

	public long getLastWinTime()
	{
		return _lastWinTime;
	}
}
