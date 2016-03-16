package handler.items;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.s2c.ExAutoSoulShot;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillUse;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate.WeaponType;
import handler.items.ScriptItemHandler;

public class SoulShots extends ScriptItemHandler
{
	private static final int[] _itemIds = { 5789, 1835, 1463, 1464, 1465, 1466, 1467, 13037, 13045, 13055, 22082, 22083, 22084, 22085, 22086 };
	private static final int[] _skillIds = { 2039, 2150, 2151, 2152, 2153, 2154 };

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;
		Player player = (Player) playable;

		WeaponTemplate weaponItem = player.getActiveWeaponItem();

		ItemInstance weaponInst = player.getActiveWeaponInstance();
		int SoulshotId = item.getItemId();
		boolean isAutoSoulShot = false;

		if(player.getAutoSoulShot().contains(SoulshotId))
			isAutoSoulShot = true;

		if(weaponInst == null)
		{
			if(!isAutoSoulShot)
				player.sendPacket(SystemMsg.CANNOT_USE_SOULSHOTS);
			return false;
		}

		// soulshot is already active
		if(weaponInst.getChargedSoulshot() != ItemInstance.CHARGED_NONE)
			return false;

		int grade = weaponItem.getCrystalType().externalOrdinal;
		int soulShotConsumption = weaponItem.getSoulShotCount();

		if(soulShotConsumption == 0)
		{
			// Can't use soulshots
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(SoulshotId);
				player.sendPacket(new ExAutoSoulShot(SoulshotId, false), new SystemMessage(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addItemName(SoulshotId));
				return false;
			}
			player.sendPacket(SystemMsg.CANNOT_USE_SOULSHOTS);
			return false;
		}

		if(grade == 0 && SoulshotId != 5789 && SoulshotId != 1835 // NG
				|| grade == 1 && SoulshotId != 1463 && SoulshotId != 22082 && SoulshotId != 13037 // D
				|| grade == 2 && SoulshotId != 1464 && SoulshotId != 22083 && SoulshotId != 13045 // C
				|| grade == 3 && SoulshotId != 1465 && SoulshotId != 22084 // B
				|| grade == 4 && SoulshotId != 1466 && SoulshotId != 22085 && SoulshotId != 13055 // A
				|| grade == 5 && SoulshotId != 1467 && SoulshotId != 22086 // S
		)
		{
			// wrong grade for weapon
			if(isAutoSoulShot)
				return false;
			player.sendPacket(SystemMsg.THE_SOULSHOT_YOU_ARE_ATTEMPTING_TO_USE_DOES_NOT_MATCH_THE_GRADE_OF_YOUR_EQUIPPED_WEAPON);
			return false;
		}

		if(weaponItem.getItemType() == WeaponType.BOW || weaponItem.getItemType() == WeaponType.CROSSBOW)
		{
			int newSS = (int) player.calcStat(Stats.SS_USE_BOW, soulShotConsumption, null, null);
			if(newSS < soulShotConsumption && Rnd.chance(player.calcStat(Stats.SS_USE_BOW_CHANCE, soulShotConsumption, null, null)))
				soulShotConsumption = newSS;
		}

		//if(!player.getInventory().destroyItem(item, soulShotConsumption))
		//{
		//	player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SOULSHOTS_FOR_THAT);
		//	return false;
		//}

		weaponInst.setChargedSoulshot(ItemInstance.CHARGED_SOULSHOT);
		player.sendPacket(SystemMsg.YOUR_SOULSHOTS_ARE_ENABLED);
		player.broadcastPacket(new MagicSkillUse(player, player, _skillIds[grade], 1, 0, 0));
		return true;
	}

	@Override
	public final int[] getItemIds()
	{
		return _itemIds;
	}
}