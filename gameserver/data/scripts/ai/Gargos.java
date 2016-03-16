package ai;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * AI Gargos<br>
 * Юзает огненый скил, пишет в чат фразу "Вперед!"<br>
 * @author n0nam3
 */
public class Gargos extends Fighter
{
	private long _lastFire;

	public Gargos(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		return super.thinkActive() || thinkFire();
	}

	protected boolean thinkFire()
	{
		if(System.currentTimeMillis() - _lastFire > 60000L)
		{
			NpcInstance actor = getActor();
			//Functions.npcSayCustomMessage(actor, "scripts.ai.Gargos.fire");
			actor.doCast(SkillTable.getInstance().getSkillEntry(5705, 1), actor, false);
			_lastFire = System.currentTimeMillis();
			return true;
		}

		return false;
	}
}