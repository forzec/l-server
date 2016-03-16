package handler.onshiftaction;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.utils.HtmlUtils;

/**
 * @author VISTALL
 * @date 2:51/19.08.2011
 */
public class OnShiftAction_PetInstance extends ScriptOnShiftActionHandler<PetInstance>
{
	@Override
	public Class<PetInstance> getClazz()
	{
		return PetInstance.class;
	}

	@Override
	public boolean call(PetInstance pet, Player player)
	{
		if(!player.getPlayerAccess().CanViewChar)
			return false;

		HtmlMessage msg = new HtmlMessage(0);
		msg.setFile("scripts/actions/admin.L2PetInstance.onActionShift.htm");

		msg.replace("%name%", HtmlUtils.htmlNpcName(pet.getNpcId()));
		msg.replace("%title%", String.valueOf(StringUtils.isEmpty(pet.getTitle()) ? "Empty" : pet.getTitle()));
		msg.replace("%level%", String.valueOf(pet.getLevel()));
		msg.replace("%class%", String.valueOf(pet.getClass().getSimpleName().replaceFirst("L2", "").replaceFirst("Instance", "")));
		msg.replace("%xyz%", pet.getLoc().x + " " + pet.getLoc().y + " " + pet.getLoc().z);
		msg.replace("%heading%", String.valueOf(pet.getLoc().h));

		msg.replace("%owner%", String.valueOf(pet.getPlayer().getName()));
		msg.replace("%ownerId%", String.valueOf(pet.getPlayer().getObjectId()));
		msg.replace("%npcId%", String.valueOf(pet.getNpcId()));
		msg.replace("%controlItemId%", String.valueOf(pet.getControlItem().getItemId()));

		msg.replace("%exp%", String.valueOf(pet.getExp()));
		msg.replace("%sp%", String.valueOf(pet.getSp()));

		msg.replace("%maxHp%", String.valueOf(pet.getMaxHp()));
		msg.replace("%maxMp%", String.valueOf(pet.getMaxMp()));
		msg.replace("%currHp%", String.valueOf((int) pet.getCurrentHp()));
		msg.replace("%currMp%", String.valueOf((int) pet.getCurrentMp()));

		msg.replace("%pDef%", String.valueOf(pet.getPDef(null)));
		msg.replace("%mDef%", String.valueOf(pet.getMDef(null, null)));
		msg.replace("%pAtk%", String.valueOf(pet.getPAtk(null)));
		msg.replace("%mAtk%", String.valueOf(pet.getMAtk(null, null)));
		msg.replace("%accuracy%", String.valueOf(pet.getAccuracy()));
		msg.replace("%evasionRate%", String.valueOf(pet.getEvasionRate(null)));
		msg.replace("%crt%", String.valueOf(pet.getCriticalHit(null, null)));
		msg.replace("%runSpeed%", String.valueOf(pet.getRunSpeed()));
		msg.replace("%walkSpeed%", String.valueOf(pet.getWalkSpeed()));
		msg.replace("%pAtkSpd%", String.valueOf(pet.getPAtkSpd(true)));
		msg.replace("%mAtkSpd%", String.valueOf(pet.getMAtkSpd()));
		msg.replace("%dist%", String.valueOf((int) pet.getRealDistance(player)));

		msg.replace("%STR%", String.valueOf(pet.getSTR()));
		msg.replace("%DEX%", String.valueOf(pet.getDEX()));
		msg.replace("%CON%", String.valueOf(pet.getCON()));
		msg.replace("%INT%", String.valueOf(pet.getINT()));
		msg.replace("%WIT%", String.valueOf(pet.getWIT()));
		msg.replace("%MEN%", String.valueOf(pet.getMEN()));

		player.sendPacket(msg);
		return true;
	}
}
