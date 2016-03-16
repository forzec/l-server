package npc.model;

import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.ReflectionBossInstance;
import org.mmocore.gameserver.templates.InstantZone;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

public class KamalokaBossInstance extends ReflectionBossInstance
{
	private static final int TELE_DEVICE_ID = 4314;

	public KamalokaBossInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		Reflection r = getReflection();
		r.setReenterTime(System.currentTimeMillis());
		super.onDeath(killer);
		InstantZone iz = r.getInstancedZone();
		if(iz != null)
		{
			String tele_device_loc = iz.getAddParams().getString("tele_device_loc", null);
			if(tele_device_loc != null)
				NpcUtils.spawnSingle(TELE_DEVICE_ID, Location.parseLoc(tele_device_loc), r);
		}
	}
}