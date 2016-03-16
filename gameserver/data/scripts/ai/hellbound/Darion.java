package ai.hellbound;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.GameObjectsStorage;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * RB Darion на крыше Tully Workshop
 *
 * @author pchayka
 */
public class Darion extends Fighter
{
	private static final int[] doors = {
			20250009,
			20250004,
			20250005,
			20250006,
			20250007
	};

	public Darion(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		NpcInstance actor = getActor();
		for(int i = 0; i < 5; i++)
			NpcUtils.spawnSingle(Rnd.get(25614, 25615), Location.findPointToStay(actor, 400, 900));

		//Doors
		for(int i = 0; i < doors.length; i++)
		{
			DoorInstance door = ReflectionUtils.getDoor(doors[i]);
			door.closeMe();
		}
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		//Doors
		for(int i = 0; i < doors.length; i++)
		{
			DoorInstance door = ReflectionUtils.getDoor(doors[i]);
			door.openMe();
		}

		for(NpcInstance npc : GameObjectsStorage.getAllByNpcId(25614, false))
			npc.deleteMe();

		for(NpcInstance npc : GameObjectsStorage.getAllByNpcId(25615, false))
			npc.deleteMe();

		super.onEvtDead(killer);
	}

}