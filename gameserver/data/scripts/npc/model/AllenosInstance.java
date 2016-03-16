package npc.model;

import org.mmocore.gameserver.instancemanager.SoDManager;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Zone;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;

import zones.MonsterTrap;

/**
 * @author pchayka
 */

public final class AllenosInstance extends NpcInstance
{
	private static final int tiatIzId = 110;

	private static String[] zones = {
		"[SoD_trap_center]",
		"[SoD_trap_left]",
		"[SoD_trap_right]",
		"[SoD_trap_left_back]",
		"[SoD_trap_right_back]"
		};

	public AllenosInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("enter_seed"))
		{
			// Время открытого SoD прошло
			if(SoDManager.isAttackStage())
			{
				Reflection ref = ReflectionUtils.simpleEnterInstancedZone(player, tiatIzId);
				if (ref != null)
					for (String z : zones) // DS: жуткий кошмар, переделать позже
					{
						Zone zone = ref.getZone(z);
						if (zone != null)
							zone.addListener(MonsterTrap.getListener());
					}
			}
			else
				SoDManager.teleportIntoSeed(player);
		}
		else
			super.onBypassFeedback(player, command);
	}
}