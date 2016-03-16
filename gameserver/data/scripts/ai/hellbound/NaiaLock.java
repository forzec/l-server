package ai.hellbound;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.SimpleSpawner;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;

/**
 * @author pchayka
 */
public class NaiaLock extends Fighter
{
	private static boolean _attacked = false;
	private static boolean _entranceactive = false;

	public NaiaLock(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		_entranceactive = true;
		Functions.npcShout(actor, "The lock has been removed from the Controller device");
		super.onEvtDead(killer);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		NpcInstance actor = getActor();
		_entranceactive = false;
		Functions.npcShout(actor, "The lock has been put on the Controller device");
	}

	@Override
	public boolean checkAggression(Creature target)
	{
		return false;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();

		if(!_attacked)
		{
			for(int i = 0; i < 4; i++)
				try
			{
					SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(18493));
					sp.setLoc(Location.findPointToStay(actor, 150, 250));
					sp.setReflection(actor.getReflection());
					sp.doSpawn(true);
					sp.stopRespawn();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			_attacked = true;
		}
	}

	public static boolean isEntranceActive()
	{
		return _entranceactive;
	}
}