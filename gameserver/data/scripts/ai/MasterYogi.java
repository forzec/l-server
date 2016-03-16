package ai;

import java.util.List;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.skills.SkillEntry;


public class MasterYogi extends DefaultAI
{
	private long wait_timeout1 = 0;
	private long wait_timeout2 = 0;
	private int range = 0;

	public MasterYogi(NpcInstance actor)
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
		NpcInstance actor = getActor();

		//Вычисляем радиус, на котором нпс будет разговаривать
		if(range <= 0)
		{
			List<NpcInstance> around = actor.getAroundNpc(6000, 300);
			if(around != null && !around.isEmpty())
			{
				double distance;
				for(NpcInstance npc : around)
					if(npc.getNpcId() == 32599)
					{
						distance = actor.getDistance(npc) * 0.50;
						if(range > 0 && distance < range || range == 0)
							range = (int) distance;
					}
			}
			else
				range = 3000;
		}

		if(System.currentTimeMillis() > wait_timeout1)
		{
			wait_timeout1 = System.currentTimeMillis() + 180000;
			Functions.npcSayInRange(actor, range, NpcString.DONT_PASS_UP_THE_CHANCE_TO_WIN_AN_S80_WEAPON);
			return true;
		}
		if(System.currentTimeMillis() > wait_timeout2)
		{
			wait_timeout2 = System.currentTimeMillis() + 300000;
			Functions.npcSayInRange(actor, range, NpcString.CARE_TO_CHALLENGE_FATE_AND_TEST_YOUR_LUCK);
			return true;
		}

		if(randomAnimation())
			return true;

		return false;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{}
}