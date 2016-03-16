package handler.items;

import java.util.Collection;

import org.mmocore.gameserver.dao.CastleHiredGuardDAO;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.instancemanager.ReflectionManager;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Zone;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.model.pledge.Privilege;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.ActionFail;
import org.mmocore.gameserver.templates.item.support.MerchantGuard;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.PositionUtils;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;

/**
 * @author VISTALL
 */
public class MercTicket extends ScriptItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		// [VISTALL] old style on click - not used
		return false;
	}

	@Override
	public boolean dropItem(Player player, ItemInstance item, long count, Location loc)
	{
		if(!player.hasPrivilege(Privilege.CS_FS_MERCENARIES) || player.getClan().getCastle() == 0)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_POSITION_MERCENARIES, ActionFail.STATIC);
			return false;
		}

		Castle castle = player.getCastle();
		MerchantGuard guard = castle.getMerchantGuard(item.getItemId());
		if(guard == null || !castle.checkIfInZone(loc, ReflectionManager.DEFAULT) || player.isActionBlocked(Zone.BLOCKED_ACTION_DROP_MERCHANT_GUARD))
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_POSITION_MERCENARIES_HERE, ActionFail.STATIC);
			return false;
		}

		if(castle.getSiegeEvent().isInProgress() || !guard.isValidSSQPeriod())
		{
			player.sendPacket(SystemMsg.A_MERCENARY_CAN_BE_ASSIGNED_TO_A_POSITION_FROM_THE_BEGINNING_OF_THE_SEAL_VALIDATION_PERIOD_UNTIL_THE_TIME_WHEN_A_SIEGE_STARTS, ActionFail.STATIC);
			return false;
		}

		int countOfGuard = 0;
		for(ItemInstance $item : castle.getSpawnMerchantTickets())
		{
			if(PositionUtils.getDistance($item.getLoc(), loc) < 200)
			{
				player.sendPacket(SystemMsg.POSITIONING_CANNOT_BE_DONE_HERE_BECAUSE_THE_DISTANCE_BETWEEN_MERCENARIES_IS_TOO_SHORT, ActionFail.STATIC);
				return false;
			}
			if($item.getItemId() == guard.getItemId())
				countOfGuard ++;
		}

		if(countOfGuard >= guard.getMax())
		{
			player.sendPacket(SystemMsg.THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE, ActionFail.STATIC);
			return false;
		}

		castle.getSpawnMerchantTickets().add(item);
		CastleHiredGuardDAO.getInstance().insert(castle, item.getItemId(), item.getLoc());

		return true;
	}

	@Override
	public boolean pickupItem(Playable playable, ItemInstance item)
	{
		if(!playable.isPlayer())
			return false;

		Player player = (Player)playable;
		if(!player.hasPrivilege(Privilege.CS_FS_MERCENARIES) || player.getClan().getCastle() == 0)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_CANCEL_MERCENARY_POSITIONING);
			return false;
		}

		Castle castle = player.getCastle();
		if(!castle.getSpawnMerchantTickets().contains(item))
		{
			player.sendPacket(SystemMsg.THIS_IS_NOT_A_MERCENARY_OF_A_CASTLE_THAT_YOU_OWN_AND_SO_YOU_CANNOT_CANCEL_ITS_POSITIONING);
			return false;
		}

		if(castle.getSiegeEvent().isInProgress())
		{
			player.sendPacket(SystemMsg.A_MERCENARY_CAN_BE_ASSIGNED_TO_A_POSITION_FROM_THE_BEGINNING_OF_THE_SEAL_VALIDATION_PERIOD_UNTIL_THE_TIME_WHEN_A_SIEGE_STARTS, ActionFail.STATIC);
			return false;
		}

		castle.getSpawnMerchantTickets().remove(item);
		CastleHiredGuardDAO.getInstance().delete(castle, item);

		return true;
	}

	@Override
	public final int[] getItemIds()
	{
		IntSet set = new HashIntSet(100);
		Collection<Castle> castles = ResidenceHolder.getInstance().getResidenceList(Castle.class);
		for(Castle c : castles)
			set.addAll(c.getMerchantGuards().keySet());
		return set.toArray();
	}
}