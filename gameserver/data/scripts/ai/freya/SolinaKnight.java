package ai.freya;

import java.util.List;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;

public class SolinaKnight extends Fighter
{
	private NpcInstance scarecrow = null;

	public SolinaKnight(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean thinkActive()
	{
		if(scarecrow == null)
		{
			List<NpcInstance> around = getActor().getAroundNpc(300, 100);
			if(around != null && !around.isEmpty())
				for(NpcInstance npc : around)
					if(npc.getNpcId() == 18912)
						if(scarecrow == null || getActor().getDistance3D(npc) < getActor().getDistance3D(scarecrow))
							scarecrow = npc;
		}

		if(scarecrow != null)
		{
			getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, scarecrow, 1);
			return true;
		}

		return false;
	}
}