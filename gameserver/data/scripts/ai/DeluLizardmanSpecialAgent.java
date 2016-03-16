package ai;

import java.util.List;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Ranger;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * AI для Delu Lizardman Special Agent ID: 21105
 *
 * @author Diamond
 */
public class DeluLizardmanSpecialAgent extends Ranger
{
	private boolean _firstTimeAttacked = true;

	public DeluLizardmanSpecialAgent(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		_firstTimeAttacked = true;
		super.onEvtSpawn();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();
		if(_firstTimeAttacked)
		{
			_firstTimeAttacked = false;
			if(Rnd.chance(25))
			{
				ChatUtils.say(actor, NpcString.S1_HOW_DARE_YOU_INTERRUPT_OUR_FIGHT_HEY_GUYS_HELP, attacker.getName());
				List<NpcInstance> around = actor.getAroundNpc(8000, 300);
				if(around != null && !around.isEmpty())
					for(NpcInstance npc : around)
						if(npc.isMonster())
							npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 5000);
			}

		}
		else if(Rnd.chance(10))
			ChatUtils.say(actor, NpcString.S1_HEY_WERE_HAVING_A_DUEL_HERE, attacker.getName());
		super.onEvtAttacked(attacker, skill, damage);
	}
}