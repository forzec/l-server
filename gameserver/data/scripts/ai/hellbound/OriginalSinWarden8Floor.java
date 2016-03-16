package ai.hellbound;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.SimpleSpawner;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.Location;

/**
 * Original Sin Warden 8-го этажа Tully Workshop
 * @автор VAVAN
 */
public class OriginalSinWarden8Floor extends Fighter
{
	private static final int[] servants = { 22432, 22433, 22434, 22435, 22436, 22437, 22438 };
	private static final int[] DarionsFaithfulServants = { 22408, 22409, 22410 };

	public OriginalSinWarden8Floor(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();

		if(Rnd.chance(15))
			try
		{
				Location loc = actor.getLoc();
				SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(DarionsFaithfulServants[Rnd.get(DarionsFaithfulServants.length-1)]));
				sp.setLoc(Location.findPointToStay(actor, 150, 350));
				sp.doSpawn(true);
				sp.stopRespawn();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		super.onEvtDead(killer);
	}

}