package handler.items;

import gnu.trove.TIntHashSet;

import org.mmocore.gameserver.ai.PlayableAI.nextAction;
import org.mmocore.gameserver.data.xml.holder.ItemHolder;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.components.IBroadcastPacket;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate.WeaponType;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author VISTALL
 * @date 7:27/17.03.2011
 */
public class EquipableItem extends ScriptItemHandler
{
	private int[] _itemIds;

	public EquipableItem()
	{
		TIntHashSet set = new TIntHashSet();
		for(ItemTemplate template : ItemHolder.getInstance().getAllTemplates())
		{
			if(template == null)
				continue;
			if(template.isEquipable())
				set.add(template.getItemId());
		}
		_itemIds = set.toArray();
	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(!playable.isPlayer())
			return false;
		Player player = playable.getPlayer();

		// Нельзя снимать/одевать любое снаряжение при этих условиях
		if(player.isStunned() || player.isSleeping() || player.isParalyzed() || player.isAlikeDead() || player.isFakeDeath())
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return false;
		}

		int bodyPart = item.getBodyPart();

		if(bodyPart == ItemTemplate.SLOT_LR_HAND || bodyPart == ItemTemplate.SLOT_L_HAND || bodyPart == ItemTemplate.SLOT_R_HAND)
		{
			// Нельзя снимать/одевать оружие, сидя на пете
			// Нельзя снимать/одевать проклятое оружие и флаги
			// Нельзя одевать/снимать оружие/щит/сигил, управляя кораблем
			// Дисарм
			if(player.isMounted() || player.isCursedWeaponEquipped() || player.getActiveWeaponFlagAttachment() != null || player.isClanAirShipDriver() || (player.isWeaponEquipBlocked() && item.getItemType() != WeaponType.NONE))
			{
				player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
				return false;
			}
		}

		// Нельзя снимать/одевать проклятое оружие
		if(item.isCursed())
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return false;
		}

		if (player.isAttackingNow() || player.isCastingNow())
		{
			player.getAI().setNextAction(nextAction.EQUIP, item, null, ctrl, false);
			player.sendActionFailed();
			return false;
		}

		if(item.isEquipped())
		{
			ItemInstance weapon = player.getActiveWeaponInstance();
			if(item == weapon)
			{
				player.abortAttack(true, true);
				player.abortCast(true, true);
			}
			player.sendDisarmMessage(item);
			player.getInventory().unEquipItem(item);
			return false;
		}

		IBroadcastPacket p = ItemFunctions.checkIfCanEquip(player, item);
		if(p != null)
		{
			player.sendPacket(p);
			return false;
		}

		player.getInventory().equipItem(item);
		if(!item.isEquipped())
		{
			player.sendActionFailed();
			return false;
		}

		SystemMessage sm;
		if(item.getEnchantLevel() > 0)
		{
			sm = new SystemMessage(SystemMsg.EQUIPPED_S1_S2);
			sm.addNumber(item.getEnchantLevel());
			sm.addItemName(item.getItemId());
		}
		else
			sm = new SystemMessage(SystemMsg.YOU_HAVE_EQUIPPED_YOUR_S1).addItemName(item.getItemId());

		player.sendPacket(sm);
		return true;
	}

	@Override
	public int[] getItemIds()
	{
		return _itemIds;
	}
}
