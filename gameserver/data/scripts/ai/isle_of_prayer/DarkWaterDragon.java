package ai.isle_of_prayer;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * AI моба Dark Water Dragon для Isle of Prayer.<br>
 * - Если был атакован, спавнится 5 миньонов Shade двух видов.<br>
 * - Если осталось меньше половины HP, спавнится еще 5 таких же миньонов.<br>
 * - После смерти, спавнит второго дракона, Fafurion Kindred<br>
 * - Не используют функцию Random Walk, если были заспавнены "миньоны"<br>
 * @author SYS & Diamond
 */
public class DarkWaterDragon extends Fighter
{
	private int _mobsSpawned = 0;
	private static final int FAFURION = 18482;
	private static final int SHADE1 = 22268;
	private static final int SHADE2 = 22269;
	private static final int MOBS[] = { SHADE1, SHADE2 };
	private static final int MOBS_COUNT = 5;
	private static final int RED_CRYSTAL = 9596;

	public DarkWaterDragon(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();
		if(!actor.isDead())
			switch(_mobsSpawned)
			{
				case 0:
					_mobsSpawned = 1;
					spawnShades(attacker);
					break;
				case 1:
					if(actor.getCurrentHp() < actor.getMaxHp() / 2)
					{
						_mobsSpawned = 2;
						spawnShades(attacker);
					}
					break;
			}

		super.onEvtAttacked(attacker, skill, damage);
	}

	private void spawnShades(Creature attacker)
	{
		NpcInstance actor = getActor();
		for(int i = 0; i < MOBS_COUNT; i++)
			try
			{
				NpcInstance npc = NpcUtils.spawnSingle(MOBS[Rnd.get(MOBS.length)], Location.findPointToStay(actor, 100, 120));
				npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_mobsSpawned = 0;
		NpcInstance actor = getActor();
		try
		{
			NpcUtils.spawnSingle(FAFURION, Location.findPointToStay(actor, 100, 120));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if (killer != null)
		{
			final Player player = killer.getPlayer();
			if (player != null)
				if (Rnd.chance(77))
					actor.dropItem(player, RED_CRYSTAL, 1, false);
		}
		super.onEvtDead(killer);
	}

	@Override
	protected boolean randomWalk()
	{
		return _mobsSpawned == 0;
	}
}