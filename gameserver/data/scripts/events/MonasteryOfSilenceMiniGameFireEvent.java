package events;

import java.util.List;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.objects.SpawnExObject;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * @author VISTALL
 * @date 13:45/04.05.2012
 */
public class MonasteryOfSilenceMiniGameFireEvent extends Event
{
	private static class SetNpcStateTask extends RunnableImpl
	{
		private NpcInstance _npc;
		private int _state;

		private SetNpcStateTask(NpcInstance npc, int state)
		{
			_npc = npc;
			_state = state;
		}

		@Override
		public void runImpl() throws Exception
		{
			_npc.setNpcState(_state);
		}
	}

	public static final String NPC = MonasteryOfSilenceMiniGameEvent.NPC;
	public static final String TARGETS = MonasteryOfSilenceMiniGameEvent.TARGETS;
	public static final String CHEST = MonasteryOfSilenceMiniGameEvent.CHEST;

	private MonasteryOfSilenceMiniGameEvent _event;

	private long _startTime;
	private int[] _indexes = new int[9];
	private int _currentIndex;
	protected int _failCount;

	public MonasteryOfSilenceMiniGameFireEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	@Override
	public void initEvent()
	{
		super.initEvent();

		_event = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, getId() - 1);

		addObjects(MonasteryOfSilenceMiniGameEvent.NPC, _event.getObjects(MonasteryOfSilenceMiniGameEvent.NPC));
		addObjects(MonasteryOfSilenceMiniGameEvent.TARGETS, _event.getObjects(MonasteryOfSilenceMiniGameEvent.TARGETS));
		addObjects(MonasteryOfSilenceMiniGameEvent.CHEST, _event.getObjects(MonasteryOfSilenceMiniGameEvent.CHEST));

		for(NpcInstance npc : getFurnaces())
			npc.setTargetable(false, false);
	}

	@Override
	public void startEvent()
	{
		super.startEvent();

		for(int i = 0; i < _indexes.length; i++)
			_indexes[i] = Rnd.get(9);
	}

	@Override
	public void stopEvent()
	{
		for(NpcInstance npc : getFurnaces())
			npc.setTargetable(false, true);
	}

	private void startSelect()
	{
		_event._state = MonasteryOfSilenceMiniGameEvent.State.SELECT;
		_currentIndex = 0;

		for(NpcInstance npc : getFurnaces())
			npc.setTargetable(true, false);
	}

	@Override
	public void action(String name, boolean start)
	{
		List<NpcInstance> npcs = getFurnaces();

		if(name.equals("fire_all"))
		{
			for(NpcInstance npc : npcs)
				npc.setNpcState(start ? 1 : 2);
		}
		else if(name.equals("fire_select"))
			startSelect();
		else if(name.startsWith("fire_"))
		{
			NpcInstance npc = npcs.get(_indexes[Integer.parseInt(name.substring(5, name.length()))]);
			npc.setNpcState(start ? 1 : 2);
		}
		else
			super.action(name, start);
	}

	public void fireFurnace(final NpcInstance npc)
	{
		List<NpcInstance> npcs = getFurnaces();

		int index = npcs.indexOf(npc);
		if(index < -1)
			return;

		if(_indexes[_currentIndex] == index)
		{
			_currentIndex ++;

			npc.setNpcState(1);
			ThreadPoolManager.getInstance().schedule(new SetNpcStateTask(npc, 2), 2000L);

			if(_currentIndex == 9)
			{
				for(final NpcInstance temp : getFurnaces())
				{
					temp.setTargetable(false, true);
					temp.setNpcState(1);

					ThreadPoolManager.getInstance().schedule(new SetNpcStateTask(temp, 2), 2000L);
				}

				ChatUtils.say(getNpcByNpcId(-1), NpcString.OH_YOUVE_SUCCEEDED);

				_event._lastWinTime = System.currentTimeMillis() + 180000L;
				_event.stopEvent();

				spawnAction(CHEST, true);
				ThreadPoolManager.getInstance().schedule(new RunnableImpl()
				{
					@Override
					public void runImpl() throws Exception
					{
						spawnAction(CHEST, false);
					}
				}, 180000L);
			}
		}
		else
		{
			_failCount ++;
			_event._state = MonasteryOfSilenceMiniGameEvent.State.FAILED;

			for(final NpcInstance temp : getFurnaces())
			{
				temp.setTargetable(false, true);
				temp.setNpcState(1);

				ThreadPoolManager.getInstance().schedule(new SetNpcStateTask(temp, 2), 2000L);
			}

			ChatUtils.say(getNpcByNpcId(-1), _failCount == 3 ? NpcString.AH_IVE_FAILED : NpcString.AH_IS_THIS_FAILURE_BUT_IT_LOOKS_LIKE_I_CAN_KEEP_GOING);

			if(_failCount == 3)
				_event.stopEvent();
		}
	}

	@Override
	public void reCalcNextTime(boolean onInit)
	{
		if(onInit)
			return;

		clearActions();

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
	public NpcInstance getNpcByNpcId(int npcId)
	{
		SpawnExObject first = getFirstObject(MonasteryOfSilenceMiniGameEvent.NPC);
		return first.getFirstSpawned();
	}

	private List<NpcInstance> getFurnaces()
	{
		SpawnExObject furnaces = getFirstObject(TARGETS);
		return furnaces.getAllSpawned();
	}

	public int getFailCount()
	{
		return _failCount;
	}
}
