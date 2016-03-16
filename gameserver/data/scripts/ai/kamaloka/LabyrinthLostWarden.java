package ai.kamaloka;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.ReflectionBossInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author pchayka
 */
public class LabyrinthLostWarden extends Fighter
{
	NpcInstance actor = getActor();
	Reflection r = actor.getReflection();
	SkillEntry pa_down = SkillTable.getInstance().getSkillEntry(5701, 7);

	public LabyrinthLostWarden(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance captain = findLostCaptain();
		if(!r.isDefault() && captain != null)
			captain.altOnMagicUseTimer(captain, pa_down);
		super.onEvtDead(killer);
	}

	private NpcInstance findLostCaptain()
	{
		for(NpcInstance n : r.getNpcs())
			if(n instanceof ReflectionBossInstance)
				return n;
		return null;
	}
}