package handler.items;

import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Servitor;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillUse;

public class BeastShot extends ScriptItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;
		Player player = (Player) playable;

		boolean isAutoSoulShot = false;
		if(player.getAutoSoulShot().contains(item.getItemId()))
			isAutoSoulShot = true;

		Servitor pet = player.getServitor();
		if(pet == null)
		{
			if(!isAutoSoulShot)
				player.sendPacket(SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
			return false;
		}

		if(pet.isDead())
		{
			if(!isAutoSoulShot)
				player.sendPacket(SystemMsg.SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_PET_OR_SERVITOR);
			return false;
		}

		int consumption = 0;
		int skillid = 0;

		switch(item.getItemId())
		{
			case 6645:
			case 20332:
				if(pet.getChargedSoulShot())
					return false;
				consumption = pet.getSoulshotConsumeCount();
				//if(!player.getInventory().destroyItem(item, consumption))
				//{
				//	player.sendPacket(SystemMsg.YOU_DONT_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_PETSERVITOR);
				//	return false;
				//}
				pet.chargeSoulShot();
				skillid = 2033;
				break;
			case 6646:
			case 20333:
				if(pet.getChargedSpiritShot(false) > 0)
					return false;
				consumption = pet.getSpiritshotConsumeCount();
				//if(!player.getInventory().destroyItem(item, consumption))
				//{
				//	player.sendPacket(SystemMsg.YOU_DONT_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_PETSERVITOR);
				//	return false;
				//}
				pet.chargeSpiritShot(ItemInstance.CHARGED_SPIRITSHOT);
				skillid = 2008;
				break;
			case 6647:
			case 20334:
				if(pet.getChargedSpiritShot(false) > 1)
					return false;
				consumption = pet.getSpiritshotConsumeCount();
				//if(!player.getInventory().destroyItem(item, consumption))
				//{
				//	player.sendPacket(SystemMsg.YOU_DONT_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_PETSERVITOR);
				//	return false;
				//}
				pet.chargeSpiritShot(ItemInstance.CHARGED_BLESSED_SPIRITSHOT);
				skillid = 2009;
				break;
		}

		pet.broadcastPacket(new MagicSkillUse(pet, pet, skillid, 1, 0, 0));
		return true;
	}

	@Override
	public final int[] getItemIds()
	{
		return Servitor.BEAST_SHOTS;
	}
}