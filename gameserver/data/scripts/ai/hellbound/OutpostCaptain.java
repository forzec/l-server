package ai.hellbound;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.skills.SkillEntry;

public class OutpostCaptain extends Fighter
{
	private boolean _attacked = false;

	public OutpostCaptain(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		if(attacker == null || attacker.getPlayer() == null)
			return;

		for(NpcInstance minion : World.getAroundNpc(getActor(), 3000, 2000))
			if(minion.getNpcId() == 22358 || minion.getNpcId() == 22357)
				minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 5000);

		if(!_attacked)
		{
			Functions.npcSay(getActor(), "Fool, you and your friends will die! Attack!");
			_attacked = true;
		}
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

}