package events;

import java.util.List;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.geometry.Polygon;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.listener.actor.OnDeathFromUndyingListener;
import org.mmocore.gameserver.model.GameObject;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.base.SpecialEffectState;
import org.mmocore.gameserver.model.entity.events.objects.DuelSnapshotObject;
import org.mmocore.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 15:49/22.08.2011
 */
public class TeamVsTeamEvent extends CustomInstantTeamEvent
{
	private OnDeathFromUndyingListener _onDeathFromUndyingListener = new OnDeathFromUndyingListenerImpl();

	private Territory[] _teleportLocs = new Territory[]
	{
		new Territory().add(new Polygon().add(149878, 47505).add(150262, 47513).add(150502, 47233).add(150507, 46300).add(150256, 46002).add(149903, 46005).setZmin(-3408).setZmax(-3308)),
		new Territory().add(new Polygon().add(149027, 46005).add(148686, 46003).add(148448, 46302).add(148449, 47231).add(148712, 47516).add(149014, 47527).setZmin(-3408).setZmax(-3308))
	};

	public TeamVsTeamEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	@Override
	protected int getInstantId()
	{
		return 500;
	}

	@Override
	protected Location getTeleportLoc(TeamType team)
	{
		return _teleportLocs[team.ordinalWithoutNone()].getRandomLoc(_reflection.getGeoIndex());
	}

	@Override
	public void onAddEvent(GameObject o)
	{
		if(o.isPlayer())
			o.getPlayer().addListener(_onDeathFromUndyingListener);
	}

	@Override
	public void onRemoveEvent(GameObject o)
	{
		super.onRemoveEvent(o);
		if(o.isPlayer())
			o.getPlayer().removeListener(_onDeathFromUndyingListener);
	}

	@Override
	public void onDie(Player player)
	{
		TeamType team = player.getTeam();
		if(team == TeamType.NONE)
			return;

		player.stopAttackStanceTask();
		player.startFrozen();
		player.setTeam(TeamType.NONE);

		for(Player $player : World.getAroundPlayers(player))
		{
			$player.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, player);
			if(player.getServitor() != null)
				$player.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, player.getServitor());
		}
		player.sendChanges();

		List<DuelSnapshotObject> objs = getObjects(team);
		for(DuelSnapshotObject obj : objs)
			if(obj.getPlayer() == player)
				obj.setDead();

		checkForWinner();
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

			boolean allDead = true;
			for(DuelSnapshotObject d : objects)
			{
				if(!d.isDead())
					allDead = false;
			}

			if(allDead)
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
		return true;
	}

	@Override
	protected void onTeleportOrExit(List<DuelSnapshotObject> objects, DuelSnapshotObject duelSnapshotObject, boolean exit)
	{
		duelSnapshotObject.setDead();

		if(exit)
			duelSnapshotObject.clear();
	}

	@Override
	protected void actionUpdate(boolean start, Player player)
	{
		player.setUndying(start ? SpecialEffectState.TRUE : SpecialEffectState.FALSE);
	}
}
