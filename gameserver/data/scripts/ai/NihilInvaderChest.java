package ai;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillUse;
import org.mmocore.gameserver.skills.SkillEntry;

public class NihilInvaderChest extends DefaultAI
{
	private static int[] _firstLevelItems = {4039, 4040, 4041, 4042, 4043, 4044};
	private static int[] _secondLevelItems = {9628, 9629, 9630};

	public NihilInvaderChest(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();
		if(actor.getNpcId() == 18820)
		{
			if(Rnd.chance(40))
			{
				actor.broadcastPacket(new MagicSkillUse(actor, actor, 2025, 1, 0, 10));
				actor.dropItem(attacker.getPlayer(), _firstLevelItems[Rnd.get(0, _firstLevelItems.length - 1)], Rnd.get(10, 20), false);
				actor.doDie(null);
			}
		}
		else if(actor.getNpcId() == 18823)
		{
			if(Rnd.chance(40))
			{
				actor.broadcastPacket(new MagicSkillUse(actor, actor, 2025, 1, 0, 10));
				actor.dropItem(attacker.getPlayer(), _secondLevelItems[Rnd.get(0, _secondLevelItems.length - 1)], Rnd.get(10, 20), false);
				actor.doDie(null);
			}
		}
		for(NpcInstance npc : actor.getReflection().getNpcs())
			if(npc.getNpcId() == actor.getNpcId())
				npc.deleteMe();

		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
	}

}