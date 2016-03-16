package ai.hellbound;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;

/**
 * AI Steel Citadel Keymaster в городе-инстанте на Hellbound<br>
 * - кричит когда его атакуют первый раз
 * - портает к себе Amaskari, если был атакован
 * - не использует random walk
 * 
 * @author SYS
 */
public class SteelCitadelKeymaster extends Fighter
{
	private boolean _firstTimeAttacked = true;
	private static final int AMASKARI_ID = 22449;

	public SteelCitadelKeymaster(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return;

		if(_firstTimeAttacked)
		{
			_firstTimeAttacked = false;
			Functions.npcSay(actor, "You have done well in finding me, but I cannot just hand you the key!");
			for(NpcInstance npc : World.getAroundNpc(actor))
				if(npc.getNpcId() == AMASKARI_ID && npc.getReflectionId() == actor.getReflectionId() && !npc.isDead())
				{
					npc.teleToLocation(Location.findPointToStay(actor, 150, 200));
					break;
				}
		}

		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_firstTimeAttacked = true;
		super.onEvtDead(killer);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}