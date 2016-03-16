package npc.model.residences.fortress.siege;

import java.util.List;

import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.entity.events.impl.FortressSiegeEvent;
import org.mmocore.gameserver.model.entity.events.objects.DoorObject;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;

import ai.residences.fortress.siege.MercenaryCaption;
import npc.model.residences.SiegeAttackerInstance;

/**
 * @author VISTALL
 * @date 8:41/19.04.2011
 */
public class MercenaryCaptionInstance extends SiegeAttackerInstance
{
	private class DoorDeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature door, Creature killer)
		{
			if(isDead())
				return;

			FortressSiegeEvent event = door.getEvent(FortressSiegeEvent.class);
			if(event == null)
				return;

			ChatUtils.shout(MercenaryCaptionInstance.this, NpcString.WE_HAVE_BROKEN_THROUGH_THE_GATE_DESTROY_THE_ENCAMPMENT_AND_MOVE_TO_THE_COMMAND_POST);

			List<DoorObject> objects = event.getObjects(FortressSiegeEvent.ENTER_DOORS);
			for(DoorObject d : objects)
				d.open(event);

			((MercenaryCaption)getAI()).startMove(true);
		}
	}

	private DoorDeathListener _doorDeathListener = new DoorDeathListener();

	public MercenaryCaptionInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();

		Fortress f = getFortress();
		FortressSiegeEvent event = f.getSiegeEvent();
		List<DoorObject> objects = event.getObjects(FortressSiegeEvent.ENTER_DOORS);
		for(DoorObject d : objects)
			d.getDoor().addListener(_doorDeathListener);
	}

	@Override
	public void onDeath(Creature killer)
	{
		super.onDeath(killer);

		ChatUtils.shout(this, NpcString.THE_GODS_HAVE_FORSAKEN_US__RETREAT);
	}

	@Override
	public void onDecay()
	{
		super.onDecay();

		Fortress f = getFortress();
		FortressSiegeEvent event = f.getSiegeEvent();
		List<DoorObject> objects = event.getObjects(FortressSiegeEvent.ENTER_DOORS);
		for(DoorObject d : objects)
			d.getDoor().removeListener(_doorDeathListener);
	}
}
