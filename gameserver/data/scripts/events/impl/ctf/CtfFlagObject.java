package events.impl.ctf;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.dao.JdbcEntityState;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.GameObject;
import org.mmocore.gameserver.model.GameObjectsStorage;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.objects.SpawnableObject;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.model.items.ItemInstance.ItemLocation;
import org.mmocore.gameserver.model.items.attachment.FlagItemAttachment;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import events.CaptureTeamFlagEvent;

/**
 * @author VISTALL
 * @date 20:38/03.04.2012
 */
public class CtfFlagObject implements SpawnableObject, FlagItemAttachment
{
	private static final Logger _log = LoggerFactory.getLogger(CtfFlagObject.class);
	private ItemInstance _item;
	private Location _location;

	private Event _event;
	private TeamType _teamType;

	public CtfFlagObject(Location location, TeamType teamType)
	{
		_location = location;
		_teamType = teamType;
	}

	@Override
	public void spawnObject(Event event)
	{
		if(_item != null)
		{
			_log.info("CtfFlagObject: can't spawn twice: " + event);
			return;
		}
		_item = ItemFunctions.createItem(9819);
		_item.setAttachment(this);
		_item.dropMe(null, _location);
		_item.setReflection(event.getReflection());
		_item.setDropTime(0);

		_event = event;
	}

	@Override
	public void despawnObject(Event event)
	{
		if(_item == null)
			return;

		Player owner = GameObjectsStorage.getPlayer(_item.getOwnerId());
		if(owner != null)
		{
			owner.getInventory().destroyItem(_item);
			owner.sendDisarmMessage(_item);
		}

		_item.setAttachment(null);
		_item.setJdbcState(JdbcEntityState.UPDATED);
		_item.delete();

		_item.deleteMe();
		_item = null;

		_event = null;
	}

	@Override
	public void respawnObject(Event event)
	{

	}

	@Override
	public void refreshObject(Event event)
	{

	}

	@Override
	public void onLogout(Player player)
	{
		onDeath(player, null);
	}

	@Override
	public void onDeath(Player owner, Creature killer)
	{
		owner.getInventory().removeItem(_item);

		_item.setLocation(ItemLocation.VOID);
		_item.setJdbcState(JdbcEntityState.UPDATED);
		_item.update();

		owner.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_DROPPED_S1).addItemName(_item.getItemId()));

		_item.dropMe(null, _location);
		_item.setDropTime(0);
	}

	@Override
	public boolean canPickUp(Player player)
	{
		if(player.getActiveWeaponFlagAttachment() != null || player.isMounted())
			return false;
		CaptureTeamFlagEvent event = player.getEvent(CaptureTeamFlagEvent.class);
		if(event != _event)
			return false;
		if(player.getTeam() == TeamType.NONE || player.getTeam() == _teamType)
			return false;
		return true;
	}

	@Override
	public void pickUp(Player player)
	{
		player.getInventory().equipItem(_item);

		CaptureTeamFlagEvent event = player.getEvent(CaptureTeamFlagEvent.class);
		event.sendPacket(new SystemMessage(SystemMsg.C1_HAS_ACQUIRED_THE_FLAG).addName(player), _teamType.revert());
	}

	@Override
	public boolean canAttack(Player player)
	{
		player.sendPacket(SystemMsg.THAT_WEAPON_CANNOT_PERFORM_ANY_ATTACKS);
		return false;
	}

	@Override
	public boolean canCast(Player player, SkillEntry skill)
	{
		SkillEntry[] skills = player.getActiveWeaponItem().getAttachedSkills();
		if(!ArrayUtils.contains(skills, skill))
		{
			player.sendPacket(SystemMsg.THAT_WEAPON_CANNOT_USE_ANY_OTHER_SKILL_EXCEPT_THE_WEAPONS_SKILL);
			return false;
		}
		else
			return true;
	}

	@Override
	public void setItem(ItemInstance item)
	{
		// ignored
	}

	public Location getLocation()
	{
		GameObject owner = getOwner();
		if(owner != null)
			return owner.getLoc();
		else if(_item != null)
			return _item.getLoc();
		else
			return _location;
	}

	public GameObject getOwner()
	{
		return _item == null ? null : GameObjectsStorage.getPlayer(_item.getOwnerId());
	}

	public Event getEvent()
	{
		return _event;
	}

	public TeamType getTeamType()
	{
		return _teamType;
	}
}
