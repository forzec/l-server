package events;

import java.util.ArrayList;
import java.util.List;

import npc.model.events.CustomObservationManagerInstance;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.olympiad.CompType;
import org.mmocore.gameserver.network.l2.components.CustomMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.ExPVPMatchRecord;
import org.mmocore.gameserver.network.l2.s2c.ExPVPMatchUserDie;
import org.mmocore.gameserver.network.l2.s2c.PlaySound;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.network.l2.s2c.ExPVPMatchRecord.Member;

public class CustomGvGBattleEvent extends AbstractCustomBattleEvent
{
	private int _blueKills = 0;
	private int _redKills = 0;
	private int _blueDeadTimer = 0;
	private int _redDeadTimer = 0;

	public CustomGvGBattleEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	protected CustomGvGBattleEvent(int id, CustomGvGEvent parent, String player1, String player2)
	{
		super(id, CompType.TEAM.ordinal(), parent, player1, player2);
		CustomObservationManagerInstance.broadcast(new CustomMessage("CustomGvGBattleEvent.BattleBegin").addNumber(getArenaId() + 1).addString(player1).addString(player2));
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

//		broadcastRecord(ExPVPMatchRecord.START, TeamType.NONE);
//		broadcastRecord(ExPVPMatchRecord.UPDATE, TeamType.NONE);
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
			sendPackets(new SystemMessage(SystemMsg.C1S_PARTY_HAS_WON_THE_DUEL).addString(winner));
			CustomObservationManagerInstance.broadcast(new CustomMessage("CustomGvGBattleEvent.BattleEnd").addString(player1).addString(player2).addString(winner));			
		}
		else
		{
			sendPackets(SystemMsg.THE_DUEL_HAS_ENDED_IN_A_TIE);
			CustomObservationManagerInstance.broadcast(new CustomMessage("CustomGvGBattleEvent.BattleTie").addString(player1).addString(player2));						
		}

		// отсылка START нужна для очистки предыдущих результатов.
		broadcastRecord(ExPVPMatchRecord.START, TeamType.NONE);
		broadcastRecord(ExPVPMatchRecord.FINISH, _winner);

		super.stopEvent(); // после вывода результатов, там очистка команд
	}

	@Override
	public void announce(int i)
	{
		sendPackets(new SystemMessage(SystemMsg.THE_DUEL_WILL_BEGIN_IN_S1_SECONDS).addNumber(i));
	}

	@Override
	public void onDie(Player player)
	{
		switch (player.getTeam())
		{
			case NONE:
				return;
			case BLUE:
				_redKills++;
				break;
			case RED:
				_blueKills++;
				break;
		}

//		sendPacket(new ExPVPMatchUserDie(_blueKills, _redKills));
	}

	private void broadcastRecord(int type, TeamType winner)
	{
		List<Member> blueTeam;
		List<Member> redTeam;
		List<CustomPlayerSnapshotObject> team;

		team = getObjects(TeamType.BLUE);
		blueTeam = new ArrayList<Member>(team.size());
		for (CustomPlayerSnapshotObject s : team)
			blueTeam.add(s.getMatchRecord());

		team = getObjects(TeamType.RED);
		redTeam = new ArrayList<Member>(team.size());
		for (CustomPlayerSnapshotObject s : team)
			redTeam.add(s.getMatchRecord());

		if (type == ExPVPMatchRecord.UPDATE)
			sendPackets(new ExPVPMatchRecord(type, winner, _blueKills, _redKills, blueTeam, redTeam), new ExPVPMatchUserDie(_blueKills, _redKills));
		else
			sendPacket(new ExPVPMatchRecord(type, winner, _blueKills, _redKills, blueTeam, redTeam));
	}

	@Override
	protected boolean checkWinnerInProgress()
	{
		if (!isInProgress())
			return false;

		boolean allBlueDead = true;
		boolean allRedDead = true;
		boolean allBlueExited = true;
		boolean allRedExited = true;
		List<CustomPlayerSnapshotObject> team;

		team = getObjects(TeamType.BLUE);
		for (CustomPlayerSnapshotObject s : team)
		{
			if (!s.isDead())
				allBlueDead = false;
			if (!s.isExited())
				allBlueExited = false;
		}

		team = getObjects(TeamType.RED);
		for (CustomPlayerSnapshotObject s : team)
		{
			if (!s.isDead())
				allRedDead = false;
			if (!s.isExited())
				allRedExited = false;
		}

		if (allBlueExited || allRedExited)
		{
			if (allBlueExited && allRedExited)
				_winner = TeamType.NONE;
			else
				_winner = allBlueExited ? TeamType.RED : TeamType.BLUE;

			return true;
		}

		if (allBlueDead)
			_blueDeadTimer++;
		else
			_blueDeadTimer = 0;

		if (allRedDead)
			_redDeadTimer++;
		else
			_redDeadTimer = 0;

		if (_blueDeadTimer > 60 || _redDeadTimer > 60)
		{
			if (_blueDeadTimer != _redDeadTimer)
				_winner = (_blueDeadTimer > _redDeadTimer) ? TeamType.RED : TeamType.BLUE;
			else
				_winner = TeamType.NONE;

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
		List<CustomPlayerSnapshotObject> team;

		team = getObjects(TeamType.BLUE);
		for (CustomPlayerSnapshotObject s : team)
			if (!s.isDead())
				aliveBlue = true;

		team = getObjects(TeamType.RED);
		for (CustomPlayerSnapshotObject s : team)
			if (!s.isDead())
				aliveRed = true;

		if (aliveBlue == aliveRed) // все умерли или все живы
		{
			if (_blueKills == _redKills) // одинаково фрагов - ничья
				_winner = TeamType.NONE;
			else
				_winner = _blueKills > _redKills ? TeamType.BLUE : TeamType.RED;
		}
		else
			_winner = aliveRed ? TeamType.RED : TeamType.BLUE; // одна из команд полностью мертвая
	}
}