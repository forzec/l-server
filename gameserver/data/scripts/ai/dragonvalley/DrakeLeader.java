package ai.dragonvalley;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;

import ai.moveroute.Fighter;

public class DrakeLeader extends Fighter
{
	public DrakeLeader(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		int warriors = 0; int scouts = 0; int mages = 0;
		for (int i = 0; i < 4; i++)
			switch (Rnd.get(3))
			{
				case 0:
					warriors++;
					break;
				case 1:
					scouts++;
					break;
				case 2:
					mages++;
					break;
			}

		if (_moveRoute != null)
			warriors += 2;

		// DS: миньоны с тем же id удаляются при спавне, поэтому спавним всех за один раз
		if (warriors > 0)
			getActor().getMinionList().spawnMinion(22849, warriors, true); // drake_warrior
		if (scouts > 0)
			getActor().getMinionList().spawnMinion(22850, scouts, true); // drake_scout
		if (mages > 0)
			getActor().getMinionList().spawnMinion(22851, mages, true); // drake_mage
	}
}