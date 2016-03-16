package events;

import java.util.List;

import npc.model.events.CustomObservationManagerInstance;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.GameObject;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.olympiad.CompType;
import org.mmocore.gameserver.network.l2.components.CustomMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.ExOlympiadMatchEnd;
import org.mmocore.gameserver.network.l2.s2c.ExOlympiadMode;
import org.mmocore.gameserver.network.l2.s2c.ExOlympiadSpelledInfo;
import org.mmocore.gameserver.network.l2.s2c.ExOlympiadUserInfo;
import org.mmocore.gameserver.network.l2.s2c.ExReceiveOlympiad;
import org.mmocore.gameserver.network.l2.s2c.PlaySound;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;

public class CustomPvPBattleEvent extends AbstractCustomBattleEvent
{
	public CustomPvPBattleEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	protected CustomPvPBattleEvent(int id, CustomPvPEvent parent, String player1, String player2)
	{
		super(id, CompType.NON_CLASSED.ordinal(), parent, player1, player2);
		CustomObservationManagerInstance.broadcast(new CustomMessage("CustomPvPBattleEvent.BattleBegin").addNumber(getArenaId() + 1).addString(player1).addString(player2));
	}

	@Override
	protected long startTimeMillis()
	{
		return System.currentTimeMillis() + 60000L;
	}

	@Override
	public void startEvent()
	{
		super.startEvent();

		sendPackets(PlaySound.B04_S01, SystemMsg.LET_THE_DUEL_BEGIN);

		for (CustomPlayerSnapshotObject s : this)
		{
			Player p = s.getPlayer();
			if (p != null)
				onStatusUpdate(p);
		}
	}

	@Override
	public void stopEvent()
	{
		if (_state != State.STARTED)
			return;

		checkWinner();

		final String player1 = getArena()._name1;
		final String player2 = getArena()._name2;
		String winner = null;
		ExReceiveOlympiad.MatchResult result;
		switch(_winner)
		{
			case RED:
				winner = player1;
				break;
			case BLUE:
				winner = player2;
				break;
		}
		if(winner != null)
		{
			sendPackets(new SystemMessage(SystemMsg.C1_HAS_WON_THE_DUEL).addString(winner));
			CustomObservationManagerInstance.broadcast(new CustomMessage("CustomPvPBattleEvent.BattleEnd").addString(player1).addString(player2).addString(winner));
			result = new ExReceiveOlympiad.MatchResult(false, winner);
		}
		else
		{
			sendPackets(SystemMsg.THE_DUEL_HAS_ENDED_IN_A_TIE);
			CustomObservationManagerInstance.broadcast(new CustomMessage("CustomPvPBattleEvent.BattleTie").addString(player1).addString(player2));						
			result = new ExReceiveOlympiad.MatchResult(true, "");
		}

		List<CustomPlayerSnapshotObject> team;
		team = getObjects(TeamType.BLUE);
		for (CustomPlayerSnapshotObject s : team)
			result.addPlayer(TeamType.BLUE, s.getMatchRecord().name, s.getClanName(), s.getClassId(), 0, 0, (int)s.getDamage());

		team = getObjects(TeamType.RED);
		for (CustomPlayerSnapshotObject s : team)
			result.addPlayer(TeamType.RED, s.getMatchRecord().name, s.getClanName(), s.getClassId(), 0, 0, (int)s.getDamage());

		sendPacket(result);

		super.stopEvent(); // после вывода результатов, там очистка команд
	}

	@Override
	public void announce(int i)
	{
		sendPackets(new SystemMessage(SystemMsg.THE_DUEL_WILL_BEGIN_IN_S1_SECONDS).addNumber(i));
	}

	@Override
	public void addObserver(Player player)
	{
		if (_state != State.STARTED)
			return;

		for (CustomPlayerSnapshotObject s : this)
		{
			Player p = s.getPlayer();
			if (p != null)
				player.sendPacket(new ExOlympiadUserInfo(p, p.getTeam().ordinal()));
		}
	}

	@Override
	public void onStatusUpdate(Player player)
	{
		if (_state != State.STARTED)
			return;

		sendPacket(new ExOlympiadUserInfo(player, player.getTeam().ordinal()));
	}

	@Override
	public void onEffectIconsUpdate(Player player, Effect[] effects)
	{
		if (_state != State.STARTED)
			return;

		final ExOlympiadSpelledInfo packet = new ExOlympiadSpelledInfo();
		for (Effect e : effects)
			if (e.isInUse())
				e.addOlympiadSpelledIcon(player, packet);

		sendPacketToObservers(packet);
	}

	@Override
	public void onRemoveEvent(GameObject o)
	{
		super.onRemoveEvent(o);

		if (o.isPlayer())
			((Player)o).sendPacket(new ExOlympiadMode(0), new ExOlympiadMatchEnd());
	}

	@Override
	protected boolean checkWinnerInProgress()
	{
		if (!isInProgress())
			return false;

		boolean blueDead = true;
		boolean redDead = true;
		List<CustomPlayerSnapshotObject> team;

		team = getObjects(TeamType.BLUE);
		for (CustomPlayerSnapshotObject s : team)
			if (!s.isDead())
				blueDead = false;

		team = getObjects(TeamType.RED);
		for (CustomPlayerSnapshotObject s : team)
			if (!s.isDead())
				redDead = false;

		if (blueDead || redDead)
		{
			if (blueDead && redDead)
				_winner = TeamType.NONE;
			else
				_winner = blueDead ? TeamType.RED : TeamType.BLUE;

			return true;
		}

		return false;
	}

	private void checkWinner()
	{
		if (_winner != TeamType.NONE)
			return;

		boolean aliveBlue = false;
		boolean aliveRed = false;
		double damageBlue = 0;
		double damageRed = 0;
		List<CustomPlayerSnapshotObject> team;

		team = getObjects(TeamType.BLUE);
		for (CustomPlayerSnapshotObject s : team)
		{
			damageBlue += s.getDamage();
			if (!s.isDead())
				aliveBlue = true;
		}

		team = getObjects(TeamType.RED);
		for (CustomPlayerSnapshotObject s : team)
		{
			damageRed += s.getDamage();
			if (!s.isDead())
				aliveRed = true;
		}

		if (aliveBlue ^ aliveRed)
			_winner = aliveRed ? TeamType.RED : TeamType.BLUE; // только одна из команд жива
		else
		{
			if (aliveRed && damageBlue != damageRed)
				_winner = damageBlue > damageRed ? TeamType.RED : TeamType.BLUE;
			else
				_winner = TeamType.NONE; // обе команды мертвы или дамаг одинаков
		}
	}
}