package ai.isle_of_prayer;

import instances.CrystalCaverns;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.ItemFunctions;

public class EmeraldDoorController extends DefaultAI
{
	private boolean openedDoor = false;
	private Player opener = null;

	public EmeraldDoorController(NpcInstance actor)
	{
		super(actor);
		actor.setHasChatWindow(false);
	}

	@Override
	protected boolean thinkActive()
	{
		DoorInstance door = getClosestDoor();
		boolean active = false;
		if(getActor().getReflection().getInstancedZoneId() == 10)
			active = ((CrystalCaverns) getActor().getReflection()).areDoorsActivated();
		if(door != null && active)
		{
			for(Creature c : getActor().getAroundCharacters(250, 150))
				if(!openedDoor && c.isPlayer() && ItemFunctions.deleteItem(c.getPlayer(), 9694, 1)) // Secret Key
				{
					openedDoor = true;
					door.openMe();
					opener = c.getPlayer();
				}

			boolean found = false;
			if(opener != null)
				for(Creature c : getActor().getAroundCharacters(250, 150))
					if(openedDoor && c.isPlayer() && c.getPlayer() == opener)
						found = true;

			if(!found)
				door.closeMe();
		}
		return super.thinkActive();
	}

	private DoorInstance getClosestDoor()
	{
		NpcInstance actor = getActor();
		for(Creature c : actor.getAroundCharacters(200, 200))
			if(c.isDoor())
				return (DoorInstance) c;
		return null;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}