package ai;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

public class EtisEtina extends Fighter
{
	private boolean summonsReleased = false;
	private NpcInstance summon1;
	private NpcInstance summon2;

	public EtisEtina(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();
		if(actor.getCurrentHpPercents() < 70 && !summonsReleased)
		{
			summonsReleased = true;
			summon1 = NpcUtils.spawnSingle(18950, Location.findAroundPosition(actor, 150), actor.getReflection());
			summon2 = NpcUtils.spawnSingle(18951, Location.findAroundPosition(actor, 150), actor.getReflection());
		}
		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if(summon1 != null && !summon1.isDead())
			summon1.decayMe();
		if(summon2 != null && !summon2.isDead())
			summon2.decayMe();
		super.onEvtDead(killer);
	}
}