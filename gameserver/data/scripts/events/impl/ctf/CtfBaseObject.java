package events.impl.ctf;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.commons.geometry.Circle;
import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.Zone;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.objects.SpawnSimpleObject;
import org.mmocore.gameserver.model.items.attachment.FlagItemAttachment;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.ZoneTemplate;
import org.mmocore.gameserver.utils.Location;
import events.CaptureTeamFlagEvent;

/**
 * @author VISTALL
 * @date 23:00/03.04.2012
 */
public class CtfBaseObject extends SpawnSimpleObject
{
	private class OnZoneEnterLeaveListenerImpl implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature actor)
		{
			if(!actor.isPlayer() || actor.getTeam() == TeamType.NONE || _teamType != actor.getTeam())
				return;

			Player player = actor.getPlayer();

			CaptureTeamFlagEvent event = actor.getEvent(CaptureTeamFlagEvent.class);

			FlagItemAttachment flagItemAttachment = player.getActiveWeaponFlagAttachment();
			if(!(flagItemAttachment instanceof CtfFlagObject))
				return;

			event.setWinner(actor.getTeam());
		}

		@Override
		public void onZoneLeave(Zone zone, Creature actor)
		{
			//
		}
	}

	private Zone _zone = null;
	private TeamType _teamType;

	public CtfBaseObject(int npcId, Location loc, TeamType teamType)
	{
		super(npcId, loc);
		_teamType = teamType;
	}

	@Override
	public void spawnObject(Event event)
	{
		super.spawnObject(event);

		Circle c = new Circle(getLoc(), 250);
		c.setZmax(World.MAP_MAX_Z);
		c.setZmin(World.MAP_MIN_Z);

		StatsSet set = new StatsSet();
		set.set("name", StringUtils.EMPTY);
		set.set("type", Zone.ZoneType.dummy);
		set.set("territory", new Territory().add(c));

		_zone = new Zone(new ZoneTemplate(set));
		_zone.setReflection(event.getReflection());
		_zone.addListener(new OnZoneEnterLeaveListenerImpl());
		_zone.setActive(true);
	}

	@Override
	public void despawnObject(Event event)
	{
		super.despawnObject(event);

		_zone.setActive(false);
		_zone = null;
	}
}
