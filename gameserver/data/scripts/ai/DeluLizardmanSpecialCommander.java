package ai;

import java.util.List;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * @author pchayka
 */
public class DeluLizardmanSpecialCommander extends Fighter
{
	private boolean _shouted = false;

	public DeluLizardmanSpecialCommander(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		_shouted = false;
		super.onEvtSpawn();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();

		if(Rnd.chance(40) && !_shouted)
		{
			_shouted = true;
			ChatUtils.say(actor, NpcString.FOUL_KILL_THE_COWARD);

			List<NpcInstance> around = actor.getAroundNpc(1000, 300);
			if(around != null && !around.isEmpty())
				for(NpcInstance npc : around)
					if(npc.isMonster())
						npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 5000);
		}
		super.onEvtAttacked(attacker, skill, damage);
	}
}