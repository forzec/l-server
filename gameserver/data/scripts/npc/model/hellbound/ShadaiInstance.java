package npc.model.hellbound;

import org.mmocore.gameserver.GameTimeController;
import org.mmocore.gameserver.listener.game.OnDayNightChangeListener;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;

public class ShadaiInstance extends NpcInstance implements OnDayNightChangeListener
{
	private static final Location DAY_LOC = new Location(16882, 238952, 9776);
	private static final Location NIGHT_LOC = new Location(9032, 253063, -1928);

	public ShadaiInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();

		if (!GameTimeController.getInstance().isNowNight())
			teleToLocation(DAY_LOC);

		GameTimeController.getInstance().addListener(this);
	}

	@Override
	public void onDespawn()
	{
		super.onDespawn();

		GameTimeController.getInstance().removeListener(this);
	}

	@Override
	public void onDay()
	{
		teleToLocation(DAY_LOC);
	}

	@Override
	public void onNight()
	{
		teleToLocation(NIGHT_LOC);
	}
}