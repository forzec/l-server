package ai.den_of_evil;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.scripts.Functions;

/**
 * @author VISTALL
 * @date 19:24/28.08.2011
 * Npc Id: 32026
 * Кричит в чат - если лвл ниже чем 37 включно
 */
public class HestuiGuard extends DefaultAI
{
	public HestuiGuard(NpcInstance actor)
	{
		super(actor);
		AI_TASK_DELAY_CURRENT = AI_TASK_ACTIVE_DELAY = AI_TASK_ATTACK_DELAY = 10000L;
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();

		for(Player player : World.getAroundPlayers(actor))
		{
			if(player.getLevel() <= 37)
				Functions.npcSay(actor, NpcString.THIS_PLACE_IS_DANGEROUS_S1__PLEASE_TURN_BACK, player.getName());
		}

		return false;
	}
}
