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
public class LabyrinthLostBeholder extends Fighter
{
	NpcInstance actor = getActor();
	Reflection r = actor.getReflection();
	SkillEntry md_down = SkillTable.getInstance().getSkillEntry(5700, 7);

	public LabyrinthLostBeholder(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance captain = findLostCaptain();
		if(!r.isDefault() && checkMates(actor.getNpcId()) && captain != null)
			captain.altOnMagicUseTimer(captain, md_down);
		super.onEvtDead(killer);
	}

	private boolean checkMates(int id)
	{
		for(NpcInstance n : r.getNpcs())
			if(n.getNpcId() == id && !n.isDead())
				return false;
		return true;
	}

	private NpcInstance findLostCaptain()
	{
		for(NpcInstance n : r.getNpcs())
			if(n instanceof ReflectionBossInstance)
				return n;
		return null;
	}
}