package npc.model;

import java.util.List;
import java.util.StringTokenizer;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;


/**
 * Данный инстанс используется в городе-инстансе на Hellbound как точка выхода
 * @author SYS
 */
public final class MoonlightTombstoneInstance extends NpcInstance
{
	private static final int KEY_ID = 9714;
	private final static long COLLAPSE_TIME = 5; // 5 мин
	private boolean _activated = false;

	public MoonlightTombstoneInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command);
		if(st.nextToken().equals("insertKey"))
		{
			if(player.getParty() == null)
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
				return;
			}

			if(!player.getParty().isLeader(player))
			{
				player.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER);
				return;
			}

			List<Player> partyMembers = player.getParty().getPartyMembers();
			for(Player partyMember : partyMembers)
				if(!isInRange(partyMember, INTERACTION_DISTANCE * 2))
				{
					// Члены партии слишком далеко
					showChatWindow(player, "default/32343-3.htm");
					return;
				}

			if(_activated)
			{
				// Уже активировано
				showChatWindow(player, "default/32343-1.htm");
				return;
			}

			if(ItemFunctions.deleteItem(player, KEY_ID, 1))
			{
				player.getReflection().startCollapseTimer(COLLAPSE_TIME * 60 * 1000L);
				_activated = true;
				broadcastPacketToOthers(new SystemMessage(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(COLLAPSE_TIME));
				player.getReflection().setReturnLoc(new Location(16262, 283651, -9700)); // coreLoc установлено при входе
				showChatWindow(player, "default/32343-1.htm");
				return;
			}
			// Нет ключа
			showChatWindow(player, "default/32343-2.htm");
			return;
		}
		super.onBypassFeedback(player, command);
	}
}