package events;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.handler.voicecommands.VoicedCommandHandler;
import org.mmocore.gameserver.model.GameObject;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Request;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.network.l2.components.IBroadcastPacket;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.ExDuelAskStart;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;

public class CustomPvPEvent extends AbstractCustomStarterEvent
{
	private static int _id = 0; // TODO: List
	private static EventCommandHandler voiceHandler = null;

	private static final class EventCommandHandler implements IVoicedCommandHandler
	{
		private final String[] _commandList = new String[] { "pvp" };

		@Override
		public boolean useVoicedCommand(String command, Player player, String args)
		{
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

			CustomPvPEvent event = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, _id);
			if (event == null)
				return false;

			if (!event.canDuel(player, targetPlayer, true))
				return false;

			if (targetPlayer.isBusy())
			{
				player.sendPacket(new SystemMessage(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(targetPlayer));
				return false;
			}

			event.askDuel(player, targetPlayer, 0);
			return true;
		}

		@Override
		public String[] getVoicedCommandList()
		{
			return _commandList;
		}	
	}

	public CustomPvPEvent(MultiValueSet<String> set)
	{
		super(set);

		if (voiceHandler == null)
		{
			voiceHandler = new EventCommandHandler();
			VoicedCommandHandler.getInstance().registerVoicedCommandHandler(voiceHandler);
		}
		_id = getId();
	}

	@Override
	public boolean canDuel(Player player, Player targetPlayer, boolean first)
	{
		if (player == targetPlayer)
			return false;

		IBroadcastPacket result;

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

		return true;
	}

	@Override
	public void askDuel(Player player, Player targetPlayer, int arenaId)
	{
		Request request = new Request(Request.L2RequestType.DUEL, player, targetPlayer).setTimeout(10000L);
		request.set("duelType", 0);
		request.set("eventId", getId());
		request.set("arenaId", arenaId);

		player.sendPacket(new SystemMessage(SystemMsg.C1_HAS_BEEN_CHALLENGED_TO_A_DUEL).addName(targetPlayer));
		targetPlayer.sendPacket(new SystemMessage(SystemMsg.C1_HAS_CHALLENGED_YOU_TO_A_DUEL).addName(player), new ExDuelAskStart(player.getName(), 0));
	}

	@Override
	public void createDuel(Player player, Player targetPlayer, int arenaId)
	{
		AbstractCustomBattleEvent battleEvent = getBattleEvent(player, targetPlayer, arenaId);
		if (battleEvent == null)
			return;

		final CustomPvPBattleEvent newEvent = new CustomPvPBattleEvent(battleEvent.getId(), this, player.getName(), targetPlayer.getName());

		addParticipant(newEvent, player, TeamType.RED);
		addParticipant(newEvent, targetPlayer, TeamType.BLUE);

		newEvent.reCalcNextTime(false);
	}
}