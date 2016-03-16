package events;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.GameObject;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.base.RestartType;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.events.objects.DuelSnapshotObject;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.skills.skillclasses.Resurrect;
import org.mmocore.gameserver.utils.Location;
import org.napile.pair.primitive.IntObjectPair;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import events.impl.ctf.CtfBaseObject;
import events.impl.ctf.CtfFlagObject;

/**
 * @author VISTALL
 * @date 15:08/03.04.2012
 */
public class CaptureTeamFlagEvent extends CustomInstantTeamEvent
{
	private class RessurectTask extends RunnableImpl
	{
		private Player _player;
		private int _seconds = 11;

		public RessurectTask(Player player)
		{
			_player = player;
		}

		@Override
		public void runImpl()
		{
			_seconds -= 1;
			if(_seconds == 0)
			{
				_deadList.remove(_player.getObjectId());

				if (_player.getTeam() == TeamType.NONE) // Он уже не на эвенте.
					return;

				_player.teleToLocation(getTeleportLoc(_player.getTeam()));
				_player.doRevive();
			}
			else
			{
				_player.sendPacket(new SystemMessage(SystemMsg.RESURRECTION_WILL_TAKE_PLACE_IN_THE_WAITING_ROOM_AFTER_S1_SECONDS).addNumber(_seconds));
				ScheduledFuture<?> f = ThreadPoolManager.getInstance().schedule(this, 1000L);

				_deadList.put(_player.getObjectId(), f);
			}
		}
	}

	private class OnDeathListenerImpl implements OnDeathListener
	{
		@Override
		public void onDeath(Creature actor, Creature killer)
		{
			_deadList.put(actor.getObjectId(), ThreadPoolManager.getInstance().schedule(new RessurectTask(actor.getPlayer()), 1000L));
		}
	}

	public static final String FLAGS = "flags";
	public static final String BASES = "bases";
	public static final String UPDATE_ARROW = "update_arrow";
	private OnDeathListener _deathListener = new OnDeathListenerImpl();
	private IntObjectMap<ScheduledFuture<?>> _deadList = new CHashIntObjectMap<ScheduledFuture<?>>();

	public CaptureTeamFlagEvent(MultiValueSet<String> set)
	{
		super(set);

		Resurrect.GLOBAL.add(this);

		addObject(BASES, new CtfBaseObject(35426, new Location(-187608, 205272, -9542), TeamType.BLUE));
		addObject(BASES, new CtfBaseObject(35423, new Location(-173720, 218088, -9536), TeamType.RED));
		addObject(FLAGS, new CtfFlagObject(new Location(-187752, 206072, -9454), TeamType.BLUE));
		addObject(FLAGS, new CtfFlagObject(new Location(-174264, 218424, -9577), TeamType.RED));
	}

	private void updateArrowInPlayers()
	{
		List<CtfFlagObject> flagObjects = getObjects(FLAGS);

		for(int i = 0; i < TeamType.VALUES.length; i++)
		{
			TeamType teamType = TeamType.VALUES[i];

			CtfFlagObject selfFlag = flagObjects.get(teamType.ordinalWithoutNone());
			CtfFlagObject enemyFlag = flagObjects.get(teamType.revert().ordinalWithoutNone());

			List<DuelSnapshotObject> objects = getObjects(teamType);

			for(DuelSnapshotObject object : objects)
			{
				Player player = object.getPlayer();
				if(player == null)
					continue;

				Location location = null;
				// у тя чужой флаг в руках, посылаем к базе
				if(enemyFlag.getOwner() == player)
				{
					List<CtfBaseObject> bases = getObjects(BASES);

					location = bases.get(i).getLoc();
				}
				// свой флаг потерян, посылаем к овнеру
				else if(selfFlag.getOwner() != null)
					location = selfFlag.getOwner().getLoc();
				// иначе посылаем к чужом флагу
				else
					location = enemyFlag.getLocation();

				player.addRadar(location.getX(), location.getY(), location.getZ());
			}
		}
	}

	public void setWinner(TeamType teamType)
	{
		if(_winner != TeamType.NONE)
			return;

		_winner = teamType;

		stopEvent();
	}

	//region Implementation & Override
	@Override
	public void stopEvent()
	{
		for(IntObjectPair<ScheduledFuture<?>> pair : _deadList.entrySet())
			pair.getValue().cancel(true);

		_deadList.clear();

		super.stopEvent();
	}

	@Override
	protected void actionUpdate(boolean start, Player player)
	{
		if(!start)
			player.removeRadar();
	}

	@Override
	public void action(String name, boolean start)
	{
		if(name.equals(UPDATE_ARROW))
			updateArrowInPlayers();
		else
			super.action(name, start);
	}

	@Override
	public int getInstantId()
	{
		return 600;
	}

	@Override
	protected Location getTeleportLoc(TeamType team)
	{
		List<CtfBaseObject> objects = getObjects(BASES);

		return Location.findAroundPosition(objects.get(team.ordinalWithoutNone()).getLoc(), 100, 200, _reflection.getGeoIndex());
	}

	@Override
	public synchronized void checkForWinner()
	{
		if(_state == State.NONE)
			return;

		TeamType winnerTeam = null;
		for(TeamType team : TeamType.VALUES)
		{
			List<DuelSnapshotObject> objects = getObjects(team);

			if(objects.isEmpty())
			{
				winnerTeam = team.revert();
				break;
			}
		}

		if(winnerTeam != null)
		{
			_winner = winnerTeam;

			stopEvent();
		}
	}

	@Override
	protected boolean canWalkInWaitTime()
	{
		return false;
	}

	@Override
	protected void onTeleportOrExit(List<DuelSnapshotObject> objects, DuelSnapshotObject duelSnapshotObject, boolean exit)
	{
		objects.remove(duelSnapshotObject);
	}

	@Override
	public void checkRestartLocs(Player player, Map<RestartType, Boolean> r)
	{
		r.clear();
	}

	@Override
	public boolean canResurrect(Creature active, Creature target, boolean force, boolean quiet)
	{
		CaptureTeamFlagEvent cubeEvent = target.getEvent(CaptureTeamFlagEvent.class);
		if(cubeEvent == this)
		{
			if(!quiet)
				active.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}
		else
			return true;
	}

	@Override
	public void onAddEvent(GameObject o)
	{
		super.onAddEvent(o);
		if(o.isPlayer())
			o.getPlayer().addListener(_deathListener);
	}

	@Override
	public void onRemoveEvent(GameObject o)
	{
		super.onRemoveEvent(o);
		if(o.isPlayer())
			o.getPlayer().removeListener(_deathListener);
	}
	//endregion
}
