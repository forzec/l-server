package ai.hellbound;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.geodata.GeoEngine;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Location;

/**
 * AI NPC Sandstorm (ID: 32350)
 * - Используют randomWalk
 * - Все время бегают
 * - Если находят чара в радиусе 200, то на него сперва используют стан - 5435, затем "пинок" - 5494
 * - Цепляют даже тех, кто находится в режиме SilentMove
 * - Никогда и никого не атакуют
 * @author SYS & Diamond
 */
public class Sandstorm extends DefaultAI
{
	private static final int AGGRO_RANGE = 200;
	private static final SkillEntry SKILL1 = SkillTable.getInstance().getSkillEntry(5435, 1);
	private static final SkillEntry SKILL2 = SkillTable.getInstance().getSkillEntry(5494, 1);
	private long lastThrow = 0;

	public Sandstorm(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if(lastThrow + 5000 < System.currentTimeMillis())
			for(Playable target : World.getAroundPlayables(actor, AGGRO_RANGE, AGGRO_RANGE))
				if(target != null && !target.isAlikeDead() && !target.isInvul() && target.isVisible() && GeoEngine.canSeeTarget(actor, target, false))
				{
					actor.doCast(SKILL1, target, true);
					actor.doCast(SKILL2, target, true);
					lastThrow = System.currentTimeMillis();
					break;
				}

		return super.thinkActive();
	}

	@Override
	protected void thinkAttack()
	{}

	@Override
	protected void onIntentionAttack(Creature target)
	{}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{}

	@Override
	protected void onEvtAggression(Creature attacker, int aggro)
	{}

	@Override
	protected void onEvtClanAttacked(Creature attacked_member, Creature attacker, int damage)
	{}

	@Override
	protected boolean randomWalk()
	{
		NpcInstance actor = getActor();
		Location sloc = actor.getSpawnedLoc();

		Location pos = Location.findPointToStay(actor, sloc, 150, 300); 
		if(GeoEngine.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), pos.x, pos.y, pos.z, actor.getGeoIndex()))
		{
			actor.setRunning();
			addTaskMove(pos, false);
		}

		return true;
	}
}