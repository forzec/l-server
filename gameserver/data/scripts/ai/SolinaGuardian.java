package ai;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author pchayka
 */
public class SolinaGuardian extends Fighter
{

	public SolinaGuardian(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getSkillEntry(6371, 1));
	}
}