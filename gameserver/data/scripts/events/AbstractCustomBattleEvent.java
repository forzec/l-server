package events;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.mmocore.commons.collections.JoinedIterator;
import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.base.RestartType;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.tables.PetDataTable;
import org.mmocore.gameserver.utils.Location;

public abstract class AbstractCustomBattleEvent extends AbstractCustomObservableEvent implements Iterable<CustomPlayerSnapshotObject>
{
	private AbstractCustomStarterEvent _parent = null;
	private boolean _isArena = false;
	private boolean _allowPets = false;
	private boolean _olympiadMode = false;

	private ScheduledFuture<?> _winnerCheckTask = null;
	protected TeamType _winner = TeamType.NONE;
	protected State _state = State.NONE;
	private boolean _canReEnter = false;

	public static enum State
	{
		NONE,
		TELEPORT_PLAYERS,
		STARTED
	}

	private final class CheckWinnerTask implements Runnable
	{
		public void run()
		{
			if (checkWinnerInProgress())
				stopEvent();
		}
	}

	public AbstractCustomBattleEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	protected AbstractCustomBattleEvent(int id, int type, AbstractCustomStarterEvent parent, String player1, String player2)
	{
		super(id, 1, type, player1, player2);

		_parent = parent;
		_isArena = parent.isArena();
		_allowPets = parent.allowPets();
		_olympiadMode = parent.isOlympiadMode();
	}

	@Override
	public void initEvent()
	{
		//
	}

	@Override
	public void startEvent()
	{
		_state = State.STARTED;
		getArena()._status = 2;
		_winnerCheckTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CheckWinnerTask(), 1000L, 1000L);

		for (CustomPlayerSnapshotObject s : this)
			s.onStart();

		super.startEvent();
	}

	@Override
	public void stopEvent()
	{
		if (_state != State.STARTED)
			return;

		_state = State.NONE;
		getArena()._status = 0;

		if (_winnerCheckTask != null)
		{
			_winnerCheckTask.cancel(false);
			_winnerCheckTask = null;
		}

		for (CustomPlayerSnapshotObject s : this)
			s.teleportBack(5000L);

		removeObjects(TeamType.BLUE);
		removeObjects(TeamType.RED);

		if (!getReflection().isDefault())
			getReflection().startCollapseTimer(10000L);

		super.stopEvent();

		_parent.unRegisterBattle(this);
		_parent = null;
	}

	@Override
	public boolean isInProgress()
	{
		return _state != State.NONE;
	}

	@Override
	public void reCalcNextTime(boolean onInit)
	{
		if(onInit)
			return;

		registerActions();
	}

	@Override
	public void action(String name, boolean start)
	{
		if (name.equalsIgnoreCase("reenter"))
		{
			_canReEnter = start;
			return;
		}
		else if (name.equalsIgnoreCase("heal"))
		{
			for (CustomPlayerSnapshotObject s : this)
				s.heal();
			return;
		}

		super.action(name, start);
	}

	@Override
	public SystemMsg checkForAttack(Creature target, Creature attacker, Skill skill, boolean force)
	{
		if(!canAttack(target, attacker, skill, force, false))
			return SystemMsg.INVALID_TARGET;

		return null;
	}

	@Override
	public boolean canAttack(Creature target, Creature attacker, Skill skill, boolean force, boolean nextAttackCheck)
	{
		if (attacker.getTeam() == TeamType.NONE || target.getTeam() == TeamType.NONE)
			return false;

		if (!isBattleStarted() && (_olympiadMode || attacker.getTeam() != target.getTeam()))
			return false;

		if (attacker.getTeam() == target.getTeam())
		{
			if (!force)
				return false;
			if (!_isArena && skill != null && skill.isPvpSkill())
				return false;
		}

		if (_isArena)
			return true;

		final Player targetPlayer = target.getPlayer();
		if (targetPlayer != null)
		{
			if (targetPlayer.getPvpFlag() == 0 && skill != null && skill.isPvpSkill())
				return false;

			return !nextAttackCheck;
		}

		return true;
	}

	@Override
	public SystemMsg canUseItem(Player player, ItemInstance item)
	{
		if (!_allowPets && PetDataTable.isPetControlItem(item))
			return SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME;

		return null;
	}

	@Override
	public boolean checkPvPFlag(Creature target)
	{
		return super.checkPvPFlag(target) || !_isArena || _state == State.NONE;
	}

	@Override
	public Iterator<CustomPlayerSnapshotObject> iterator()
	{
		return new JoinedIterator<CustomPlayerSnapshotObject>(getObjects(TeamType.BLUE).iterator(), getObjects(TeamType.RED).iterator());
	}

	@Override
	public void teleportPlayers(String name)
	{
		_state = State.TELEPORT_PLAYERS;

		Location loc = getReflection().getInstancedZone().getTeleportCoords().get(0);
		List<CustomPlayerSnapshotObject> team;
		team = getObjects(TeamType.BLUE);
		for (CustomPlayerSnapshotObject m : team)
			m.teleport(loc, getReflection());

		loc = getReflection().getInstancedZone().getTeleportCoords().get(1);
		team = getObjects(TeamType.RED);
		for (CustomPlayerSnapshotObject m : team)
			m.teleport(loc, getReflection());
	}

	@Override
	public Location getRestartLoc(Player player, RestartType type)
	{
		if (player.getReflection() != getReflection() || type != RestartType.TO_VILLAGE)
			return null;

		return player._stablePoint;
	}

	public boolean isBattleStarted()
	{
		return _state == State.STARTED;
	}

	public boolean canReEnter(Player player)
	{
		return _canReEnter;
	}

	public boolean allowPets()
	{
		return _allowPets;
	}

	protected abstract boolean checkWinnerInProgress();
}