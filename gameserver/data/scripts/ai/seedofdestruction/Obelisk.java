package ai.seedofdestruction;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;

/**
 * @author pchayka
 */
public class Obelisk extends DefaultAI
{
	private static final int[] MOBS = { 22541, 22544, 22543 };
	private boolean _firstTimeAttacked = true;

	public Obelisk(NpcInstance actor)
	{
		super(actor);
		actor.block();
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_firstTimeAttacked = true;
		NpcInstance actor = getActor();
		actor.broadcastPacket(new ExShowScreenMessage("Obelisk has collapsed. Don't let the enemies jump around wildly anymore!!!!", 3000, ScreenMessageAlign.MIDDLE_CENTER, false));
		actor.stopDecay();
		for(NpcInstance n : actor.getReflection().getNpcs())
			if(n.getNpcId() == 18777)
				n.stopDamageBlocked();
		super.onEvtDead(killer);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();
		if(_firstTimeAttacked)
		{
			_firstTimeAttacked = false;
			for(int i = 0; i < 8; i++)
				for(int mobId : MOBS)
				{
					NpcInstance npc = actor.getReflection().addSpawnWithoutRespawn(mobId, Location.findPointToStay(actor, 400, 1000), 0);
					Creature randomHated = actor.getAggroList().getRandomHated();
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, randomHated != null ? randomHated : attacker, Rnd.get(1, 100));
				}
		}
		super.onEvtAttacked(attacker, skill, damage);
	}
}