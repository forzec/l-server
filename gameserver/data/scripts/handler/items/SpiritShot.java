package handler.items;

import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.s2c.ExAutoSoulShot;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillUse;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import handler.items.ScriptItemHandler;

public class SpiritShot extends ScriptItemHandler
{
	// all the items ids that this handler knowns
	private static final int[] _itemIds = { 5790, 2509, 2510, 2511, 2512, 2513, 2514, 22077, 22078, 22079, 22080, 22081 };
	private static final int[] _skillIds = { 2061, 2155, 2156, 2157, 2158, 2159 };

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;
		Player player = (Player) playable;

		ItemInstance weaponInst = player.getActiveWeaponInstance();
		WeaponTemplate weaponItem = player.getActiveWeaponItem();
		int SoulshotId = item.getItemId();
		boolean isAutoSoulShot = false;

		if(player.getAutoSoulShot().contains(SoulshotId))
			isAutoSoulShot = true;

		if(weaponInst == null)
		{
			if(!isAutoSoulShot)
				player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_SPIRITSHOTS);
			return false;
		}

		// spiritshot is already active
		if(weaponInst.getChargedSpiritshot() != ItemInstance.CHARGED_NONE)
			return false;

		int SpiritshotId = item.getItemId();
		int grade = weaponItem.getCrystalType().externalOrdinal;
		int soulSpiritConsumption = weaponItem.getSpiritShotCount();
		long count = item.getCount();

		if(soulSpiritConsumption == 0)
		{
			// Can't use Spiritshots
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(SoulshotId);
				player.sendPacket(new ExAutoSoulShot(SoulshotId, false), new SystemMessage(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addItemName(SoulshotId));
				return false;
			}
			player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_SPIRITSHOTS);
			return false;
		}

		if(grade == 0 && SpiritshotId != 5790 && SpiritshotId != 2509 // NG
				|| grade == 1 && SpiritshotId != 2510 && SpiritshotId != 22077 // D
				|| grade == 2 && SpiritshotId != 2511 && SpiritshotId != 22078 // C
				|| grade == 3 && SpiritshotId != 2512 && SpiritshotId != 22079 // B
				|| grade == 4 && SpiritshotId != 2513 && SpiritshotId != 22080 // A
				|| grade == 5 && SpiritshotId != 2514 && SpiritshotId != 22081 // S
		)
		{
			// wrong grade for weapon
			if(isAutoSoulShot)
				return false;
			player.sendPacket(SystemMsg.YOUR_SPIRITSHOT_DOES_NOT_MATCH_THE_WEAPONS_GRADE);
			return false;
		}

		if(count < soulSpiritConsumption)
		{
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(SoulshotId);
				player.sendPacket(new ExAutoSoulShot(SoulshotId, false), new SystemMessage(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addItemName(SoulshotId));
				return false;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SPIRITSHOT_FOR_THAT);
			return false;
		}

		//if(player.getInventory().destroyItem(item, soulSpiritConsumption))
		//{
		//	weaponInst.setChargedSpiritshot(ItemInstance.CHARGED_SPIRITSHOT);
		//	player.sendPacket(SystemMsg.YOUR_SPIRITSHOT_HAS_BEEN_ENABLED);
		//	player.broadcastPacket(new MagicSkillUse(player, player, _skillIds[grade], 1, 0, 0));
		//}
		return true;
	}

	@Override
	public final int[] getItemIds()
	{
		return _itemIds;
	}
}