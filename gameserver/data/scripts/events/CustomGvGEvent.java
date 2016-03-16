package events;

import java.util.Iterator;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.collections.JoinedIterator;
import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.handler.voicecommands.VoicedCommandHandler;
import org.mmocore.gameserver.model.GameObject;
import org.mmocore.gameserver.model.GameObjectsStorage;
import org.mmocore.gameserver.model.Party;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Request;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.network.l2.components.CustomMessage;
import org.mmocore.gameserver.network.l2.components.IBroadcastPacket;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.ExDuelAskStart;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;

public class CustomGvGEvent extends AbstractCustomStarterEvent
{
	private static int _id = 0; // TODO: List
	private static EventCommandHandler voiceHandler = null;

	private final int[] _administrators;

	private static final class EventCommandHandler implements IVoicedCommandHandler
	{
		private final String[] _commandList = new String[] { "gvg" };

		@Override
		public boolean useVoicedCommand(String command, Player player, String args)
		{
			final CustomGvGEvent event = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, _id);
			if (event == null)
				return false;

			int arenaId = 0;
			if (args != null && !args.isEmpty())
			{
				final String[] param = args.split(" ");
				if (param.length == 1)
				{
					try
					{
						arenaId = Integer.parseInt(param[0]);
					}
					catch (Exception e)
					{
						player.sendPacket(new CustomMessage("CustomBattleEvent.InvalidArena"));
						return false;
					}
				}
				else if (event._administrators != null && ArrayUtils.contains(event._administrators, player.getObjectId()) || player.isGM())
				{
					if (param.length < 2)
					{
						player.sendPacket(SystemMsg.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);					
						return false;
					}

					final Player player1 = GameObjectsStorage.getPlayer(param[0]);
					if (player1 == null)
					{
						player.sendPacket(SystemMsg.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);
						return false;
					}

					final Player player2 = GameObjectsStorage.getPlayer(param[1]);
					if (player2 == null)
					{
						player.sendPacket(SystemMsg.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);
						return false;
					}

					if (player1.isBusy())
					{
						player.sendPacket(new SystemMessage(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(player1));
						return false;
					}

					if (player2.isBusy())
					{
						player.sendPacket(new SystemMessage(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(player2));
						return false;
					}

					if (!event.canDuel(player1, player2, true))
						return false;

					if (param.length > 2)
					{
						try
						{
							arenaId = Integer.parseInt(param[2]);
						}
						catch (Exception e)
						{
							player.sendPacket(new CustomMessage("CustomBattleEvent.InvalidArena"));
							return false;
						}
					}

					event.createDuel(player1, player2, arenaId);
					return true;
				}
				else
				{
					player.sendPacket(new CustomMessage("CustomBattleEvent.InvalidArena"));
					return false;
				}
			}

			final GameObject target = player.getTarget();
			if (target == null || !target.isPlayer())
			{
				player.sendPacket(SystemMsg.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);
				return false;
			}
			final Player targetPlayer = (Player)target;
			if (targetPlayer == player)
			{
				player.sendPacket(SystemMsg.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);
				return false;
			}

			if (!event.canDuel(player, targetPlayer, true))
				return false;

			if (targetPlayer.isBusy())
			{
				player.sendPacket(new SystemMessage(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(targetPlayer));
				return false;
			}

			event.askDuel(player, targetPlayer, arenaId);
			return true;
		}

		@Override
		public String[] getVoicedCommandList()
		{
			return _commandList;
		}	
	}

	public CustomGvGEvent(MultiValueSet<String> set)
	{
		super(set);

		if (voiceHandler == null)
		{
			voiceHandler = new EventCommandHandler();
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(voiceHandler);
		}
		_id = getId();
		_administrators = set.getIntegerArray("administrators", null);
	}

	@Override
	public boolean canDuel(Player player, Player targetPlayer, boolean first)
	{
		if (player == targetPlayer)
			return false;

		IBroadcastPacket result;
		final Party party1 = player.getParty();
		final Party party2 = targetPlayer.getParty();

		if (party1 == null && party2 == null && player.isGM()) // дебаг
		{
			result = checkPlayer(player, player);
			if (result != null)
			{
				player.sendPacket(result);
				return false;
			}
			result = checkPlayer(player, targetPlayer);
			if (result != null)
			{
				player.sendPacket(result);
				return false;
			}				
		}
		else
		{
			if (party1 == null)
			{
				player.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_REQUEST_A_TEAM_MATCH);
				return false;
			}

			if (party2 == null)
			{
				player.sendPacket(new SystemMessage(SystemMsg.SINCE_THE_PERSON_YOU_CHALLENGED_IS_NOT_CURRENTLY_IN_A_PARTY_THEY_CANNOT_DUEL_AGAINST_YOUR_PARTY));
				return false;
			}

			if(player != party1.getPartyLeader() || targetPlayer != party2.getPartyLeader())
			{
				player.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_REQUEST_A_TEAM_MATCH);
				targetPlayer.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_REQUEST_A_TEAM_MATCH);
				return false;
			}

			if (first) // сопартийцев проверяем после ответа
				return true;

			Iterator<Player> iterator = new JoinedIterator<Player>(party1.iterator(), party2.iterator());
			while(iterator.hasNext())
			{
				Player member = iterator.next();
				if((result = checkPlayer(player, member)) != null)
				{
					player.sendPacket(result);
					targetPlayer.sendPacket(result);
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public void askDuel(Player player, Player targetPlayer, int arenaId)
	{
		Request request = new Request(Request.L2RequestType.DUEL, player, targetPlayer).setTimeout(10000L);
		request.set("duelType", 1);
		request.set("eventId", getId());
		request.set("arenaId", arenaId);

		player.sendPacket(new SystemMessage(SystemMsg.C1S_PARTY_HAS_BEEN_CHALLENGED_TO_A_DUEL).addName(targetPlayer));
		targetPlayer.sendPacket(new SystemMessage(SystemMsg.C1S_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL).addName(player), new ExDuelAskStart(player.getName(), 1));
	}

	@Override
	public void createDuel(Player player, Player targetPlayer, int arenaId)
	{
		AbstractCustomBattleEvent battleEvent = getBattleEvent(player, targetPlayer, arenaId);
		if (battleEvent == null)
			return;

		final Party party1 = player.getParty();
		final Party party2 = targetPlayer.getParty();
		final CustomGvGBattleEvent newEvent = new CustomGvGBattleEvent(battleEvent.getId(), this, player.getName(), targetPlayer.getName());

		if (party1 == null || party2 == null) // проверка в checkDuel
		{
			addParticipant(newEvent, player, TeamType.RED);
			addParticipant(newEvent, targetPlayer, TeamType.BLUE);
		}
		else
		{
			for (Player member : party1)
				addParticipant(newEvent, member, TeamType.RED);
			for (Player member : party2)
				addParticipant(newEvent, member, TeamType.BLUE);
		}

		newEvent.reCalcNextTime(false);
	}
}