package events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.instancemanager.ReflectionManager;
import org.mmocore.gameserver.listener.CharListener;
import org.mmocore.gameserver.listener.actor.OnCurrentHpDamageListener;
import org.mmocore.gameserver.listener.actor.OnDeathFromUndyingListener;
import org.mmocore.gameserver.listener.actor.OnReviveListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerEnterListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerExitListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerSummonServitorListener;
import org.mmocore.gameserver.listener.actor.player.OnTeleportListener;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Servitor;
import org.mmocore.gameserver.model.base.SpecialEffectState;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.Hero;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.model.items.LockType;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.Die;
import org.mmocore.gameserver.network.l2.s2c.ExPVPMatchRecord.Member;
import org.mmocore.gameserver.network.l2.s2c.Revive;
import org.mmocore.gameserver.network.l2.s2c.SkillCoolTime;
import org.mmocore.gameserver.network.l2.s2c.SkillList;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.skills.EffectType;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.SkillEntryType;
import org.mmocore.gameserver.skills.TimeStamp;
import org.mmocore.gameserver.stats.Env;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.tables.SkillTreeTable;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.PlayerUtils;

public class CustomPlayerSnapshotObject
{
	private final AbstractCustomBattleEvent _owner;
	private final int _objectId;
	private HardReference<Player> _playerRef;
	private final TeamType _team;
	private final Member _matchRecord;
	private final String _clanName;

	private final boolean _olympiadMode;
	private final boolean _removeBuffs;
	private final boolean _disableHeroSkills;
	private final boolean _disableClanSkills;
	private final boolean _clearCooldown;
	private final int[] _forbiddenItems;
	private final boolean _maxEnchantedSkills;
	private final int _physWeaponEnchantLock;
	private final int _magicWeaponEnchantLock;
	private final int _armorEnchantLock;
	private final int _accEnchantLock;
	private final int _weaponAttLock;
	private final int _armorAttLock;

	private final CharListener _playerListeners = new PlayerListener();
	private final CharListener _petListeners = new PetListener();

	private volatile boolean _storedOnEnter = false;
	private volatile boolean _restoredOnExit = false;
	private volatile boolean _reEntered = false;
	private boolean _isExited = false;
	private boolean _isDead = false;
	private int _reflectionId = Integer.MIN_VALUE;
	private double _damage = 0;
	
	private int _classId;
	private List<Integer> _summonedServitors = new ArrayList<Integer>();
	private LockType _lockType;
	private int[] _lockedItems;
	private Location _returnLoc;
	private double _storedHp;
	private double _storedMp;
	private double _storedCp;
	private List<TimeStamp> _storedReuses = Collections.emptyList();
	private List<Effect> _storedEffects = Collections.emptyList();

	private Location _currentLoc = null;
	private double _currentHp = 0;
	private double _currentMp = 0;
	private double _currentCp = 0;
	private List<TimeStamp> _currentReuses = null;
	private List<Effect> _currentEffects = null;

	public CustomPlayerSnapshotObject(AbstractCustomBattleEvent owner, Player player, TeamType team, boolean olympiadMode, boolean removeBuffs, boolean disableHeroSkills, boolean disableClanSkills, boolean clearCooldown, int[] forbiddenItems, boolean maxEnchantedSkills, int physWeaponEnchantLock, int magicWeaponEnchantLock, int armorEnchantLock, int accEnchantLock, int weaponAttLock, int armorAttLock)
	{
		_owner = owner;
		_objectId = player.getObjectId();
		_playerRef = player.getRef();
		_team = team;
		_matchRecord = new Member(player.getName(), 0, 0);
		_clanName = player.getClan() != null ? player.getClan().getName() : "";
		_olympiadMode = olympiadMode;
		_removeBuffs = removeBuffs;
		_disableHeroSkills = disableHeroSkills;
		_disableClanSkills = disableClanSkills;
		_clearCooldown = clearCooldown;
		_forbiddenItems = forbiddenItems;
		_maxEnchantedSkills = maxEnchantedSkills;
		_physWeaponEnchantLock = physWeaponEnchantLock;
		_magicWeaponEnchantLock = magicWeaponEnchantLock;
		_armorEnchantLock = armorEnchantLock;
		_accEnchantLock = armorEnchantLock;
		_weaponAttLock = weaponAttLock;
		_armorAttLock = armorAttLock;
		player.addEvent(_owner);
		player.addListener(_playerListeners);
	}

	public final int getObjectId()
	{
		return _objectId;
	}

	public final Player getPlayer()
	{
		return _playerRef.get();
	}

	public final boolean isDead()
	{
		if (_isDead)
			return true;
		final Player player = _playerRef.get();
		if (player == null || player.isTeleporting()) // во время телепорта игроки считаются мертвыми
			return true;

		return false;
	}

	public final boolean isExited()
	{
		return _isExited;
	}

	public final Member getMatchRecord()
	{
		return _matchRecord;
	}

	public final int getClassId()
	{
		return _classId;
	}

	public final String getClanName()
	{
		return _clanName;
	}

	public final int getKilled()
	{
		return _matchRecord.deaths;
	}

	public final int getKills()
	{
		return _matchRecord.kills;
	}

	public final double getDamage()
	{
		return _damage;
	}

	public final AbstractCustomBattleEvent getOwner()
	{
		return _owner;
	}

	/**
	 * Вызывается при входе в игру во время загрузки, ставит листенеры для полного восстановления состояния
	 */
	public final boolean updatePlayer(Player player)
	{
		if (player.getObjectId() != _objectId)
			return false;
		if (!_storedOnEnter)
		{
			if (_currentLoc != null)
				return false;

			_playerRef = player.getRef();
			player.addEvent(_owner);
			player.addListener(_playerListeners);
			return true;
		}

		_playerRef = player.getRef();

		if (!_restoredOnExit)
			return false;

		if (!_owner.canReEnter(player))
			return false;

		if (_reEntered)
			return false;
		_reEntered = true;

		player.addEvent(_owner);
		player.addListener(_playerListeners);

		if (_olympiadMode)
		{
			player.setIsInOlympiadMode(true);
			player.setOlympiadSide(_team.ordinalWithoutNone());
			player.setOlympiadCompStarted(_owner.isBattleStarted());
		}

		player._stablePoint = _returnLoc;
		player.setTeam(_team);
		player.setUndying(SpecialEffectState.TRUE);
		player.setLoc(_currentLoc);
		player.setReflection(_reflectionId);
		return true;
	}

	public final void teleport(Location loc, Reflection ref)
	{
		_currentLoc = loc; // признак того что был порт на арену
		_reflectionId = ref.getId();

		final Player player = _playerRef.get();
		if (player == null)
		{
			_isDead = true;
			_isExited = true;
			return;
		}

		_isExited = false;
		_isDead = player.isDead();
		player.teleToLocation(loc, ref);
	}

	public final void onStart()
	{
		if (!_storedOnEnter || _isExited)
			return;

		final Player player = _playerRef.get();
		if (player != null)
		{
			if (_olympiadMode && player.isInOlympiadMode())
				player.setOlympiadCompStarted(true);

			PlayerUtils.updateAttackableFlags(player);
		}
	}

	public final void teleportBack(long delay)
	{
		if (_isExited)
			return;

		final Player player = _playerRef.get();
		if (player == null)
			return;

		player._stablePoint = null;
		player.startFrozen();
		final Servitor pet = player.getServitor();
		if (pet != null)
			pet.startFrozen();

		PlayerUtils.updateAttackableFlags(player);

		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				player.stopFrozen();
				if (pet != null)
					pet.stopFrozen();
				player.teleToLocation(_returnLoc, ReflectionManager.DEFAULT);
			}
		}, delay);
	}

	public final void heal()
	{
		if (_isDead || _isExited)
			return;
		
		final Player player = _playerRef.get();
		if (player == null)
			return;

		player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
		player.setCurrentCp(player.getMaxCp());
		player.broadcastUserInfo(true);
	}

	private final void onPlayerReEnter(Player player)
	{
		if (!_storedOnEnter || !_restoredOnExit)
			return;
		if (!_reEntered)
			return;

		boolean cheater = player.getActiveClassId() != _classId;

		restoreEffects(player, _currentEffects, cheater);

		player.setCurrentHpMp(_currentHp, _currentMp);
		player.setCurrentCp(_currentCp);

		boolean updateSkills = false;

		if (_disableHeroSkills && player.isHero())
		{
			Hero.removeSkills(player);
			updateSkills = true;
		}

		if (_disableClanSkills && player.getClan() != null)
		{
			player.disableSkillsByEntryType(SkillEntryType.CLAN);
			updateSkills = true;
		}

		if (_maxEnchantedSkills)
			SkillTreeTable.lockMaxEnchant(player, false);

		restoreReuses(player, _currentReuses);

		if (updateSkills || _maxEnchantedSkills)
			player.sendPacket(new SkillList(player));

		player.sendPacket(new SkillCoolTime(player));

		for (ItemInstance item : player.getInventory().getItems())
			item.lockEnchantAndAttribute(_physWeaponEnchantLock, _magicWeaponEnchantLock, _armorEnchantLock, _accEnchantLock, _weaponAttLock, _armorAttLock);

		lockItems(player, false);

		_restoredOnExit = false;
		_isExited = false;

		player.setTeam(_team);
		player.setUndying(SpecialEffectState.TRUE);

		if (_olympiadMode)
		{
			player.setIsInOlympiadMode(true);
			player.setOlympiadSide(_team.ordinal());
			player.setOlympiadCompStarted(_owner.isInProgress());
		}

		_isDead = player.isDead();
		if (_isDead)
			player.broadcastPacket(new Die(player));

		PlayerUtils.updateAttackableFlags(player);
	}

	private final boolean storeStateOnEnter(Player player)
	{
		_classId = player.getActiveClassId();
		_returnLoc = player._stablePoint == null ? player.getReflection().getReturnLoc() == null ? player.getLoc() : player.getReflection().getReturnLoc() : player._stablePoint;
		player._stablePoint = _returnLoc;
		_storedCp = player.getCurrentCp();
		_storedHp = player.getCurrentHp();
		_storedMp = player.getCurrentMp();

		_storedEffects = storeEffects(player);

		if (_removeBuffs)
			clearEffects(player);

		boolean updateSkills = _maxEnchantedSkills;
		boolean updateReuses = false;

		if (_disableHeroSkills && player.isHero())
		{
			Hero.removeSkills(player);
			updateSkills = true;
			updateReuses = true;
		}

		if (_disableClanSkills && player.getClan() != null)
		{
			player.disableSkillsByEntryType(SkillEntryType.CLAN);
			updateSkills = true;
			updateReuses = true;
		}

		if (_maxEnchantedSkills)
			SkillTreeTable.lockMaxEnchant(player, false);

		if (_clearCooldown)
		{
			_storedReuses = storeReuses(player);
			if (!_storedReuses.isEmpty())
				updateReuses = true;
		}

		if (updateSkills)
			player.sendPacket(new SkillList(player));
		if (updateReuses)
			player.sendPacket(new SkillCoolTime(player));

		lockItems(player, true);

		for (ItemInstance item : player.getInventory().getItems())
			item.lockEnchantAndAttribute(_physWeaponEnchantLock, _magicWeaponEnchantLock, _armorEnchantLock, _accEnchantLock, _weaponAttLock, _armorAttLock);

		return true;
	}

	private final boolean restoreStateOnExit(boolean storeCurrentState)
	{
		if (_restoredOnExit || !_storedOnEnter)
			return false;
		_restoredOnExit = true;

		final Player player = _playerRef.get();
		if (player == null)
			return false;

		try
		{
			if (!_reEntered && storeCurrentState)
			{
				_currentLoc = player.getLoc();
				_currentHp = player.getCurrentHp();
				_currentMp = player.getCurrentMp();
				_currentCp = player.getCurrentCp();
				_currentEffects = storeEffects(player);
				_currentReuses = storeReuses(player);
			}
			else
				_reEntered = true; // принудительно отключаем повторный вход

			final boolean cheater = player.getActiveClassId() != _classId;

			restoreEffects(player, _storedEffects, cheater);

			if (player.isDead())
			{
				player.setCurrentHpMp(_storedHp, _storedMp, true);
				player.broadcastPacket(new Revive(player));
			}
			else
				player.setCurrentHpMp(_storedHp, _storedMp, false);
			player.setCurrentCp(_storedCp);

			final Servitor pet = player.getServitor();
			if (pet != null && pet.isPet())
				if (pet.isDead())
					pet.setCurrentHp(1, true);

			boolean updateSkills = _maxEnchantedSkills;
			boolean updateReuses = false;

			if (_maxEnchantedSkills)
				SkillTreeTable.unlockMaxEnchant(player, false);

			if (!cheater)
			{
				if (_disableHeroSkills && player.isHero() && player.getActiveClassId() == player.getBaseClassId())
				{
					Hero.addSkills(player);
					updateSkills = true;
					updateReuses = true;
				}

				if (_disableClanSkills && player.getClan() != null && _clanName.equals(player.getClan().getName()) && player.getClan().getReputationScore() >= 0)
				{
					player.enableSkillsByEntryType(SkillEntryType.CLAN);
					updateSkills = true;
					updateReuses = true;
				}

				if (_clearCooldown && restoreReuses(player, _storedReuses))
					updateReuses = true;
			}
			else
			{
				setAllReuses(player);
				updateReuses = true;				
			}

			if (updateSkills)
				player.sendPacket(new SkillList(player));
			if (updateReuses)
				player.sendPacket(new SkillCoolTime(player));

			for (ItemInstance item : player.getInventory().getItems())
				item.unlockEnchantAndAttribute();

			unlockItems(player);
		}
		finally
		{
			_isDead = true;
			player.removeListener(_playerListeners);
			player.removeEvent(_owner);
			player.setTeam(TeamType.NONE);
			player.setUndying(SpecialEffectState.FALSE);			

			if (_olympiadMode)
			{
				player.setIsInOlympiadMode(false);
				player.setOlympiadSide(-1);
				player.setOlympiadCompStarted(false);
			}

			final Servitor pet = player.getServitor();
			if (pet != null)
			{
				if (pet.isPet())
				{
					pet.removeListener(_petListeners);
					pet.setUndying(SpecialEffectState.FALSE);				
				}
				pet.setTeam(TeamType.NONE);
			}
		}

		return true;
	}


	private static final List<Effect> storeEffects(Player player)
	{
		final List<Effect> effectList = player.getEffectList().getAllEffects();
		if (effectList.isEmpty())
			return Collections.emptyList();

		final List<Effect >effects = new ArrayList<Effect>(effectList.size());
		for(Effect e : effectList)
			if (e != null)
			{
				if (e.getSkill().getTemplate().isToggle())
					continue;
				Effect effect = e.getTemplate().getEffect(new Env(e.getEffector(), e.getEffected(), e.getSkill()));
				effect.setCount(e.getCount());
				effect.setPeriod(e.getCount() == 1 ? e.getPeriod() - e.getTime() : e.getPeriod());

				effects.add(effect);
			}

		return effects;
	}

	private static final void clearEffects(Creature actor)
	{
		for (Effect e : actor.getEffectList().getAllEffects())
		{
			if (e == null)
				continue;
			if (e.getEffectType() == EffectType.Cubic && actor.getSkillLevel(e.getSkill().getId()) > 0)
				continue;
			if (e.getSkill().getTemplate().isToggle())
				continue;
			actor.sendPacket(new SystemMessage(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(e.getSkill()));
			e.exit();
		}

		if (!actor.isPlayer())
			return;

		final Servitor servitor = ((Player)actor).getServitor();
		if (servitor == null)
			return;

		for (Effect e : servitor.getEffectList().getAllEffects())
			if (e != null && !e.getSkill().getTemplate().isToggle())
				e.exit();
	}

	private static final void restoreEffects(Player player, List<Effect> effects, boolean clearOnly)
	{
		for (Effect e : player.getEffectList().getAllEffects())
			if (e != null && !e.getSkill().getTemplate().isToggle())
				e.exit();

		if (clearOnly || effects == null || effects.isEmpty())
			return;

		int size = effects.size();
		for (Effect e : effects)
		{
			player.getEffectList().addEffect(e);
			e.fixStartTime(size--);
		}
	}

	private static final List<TimeStamp> storeReuses(Player player)
	{
		final Collection<TimeStamp> reuseList = player.getSkillReuses();
		if (reuseList.isEmpty())
			return Collections.emptyList();

		final List<TimeStamp> reuses = new ArrayList<TimeStamp>(reuseList.size());
		for (TimeStamp t : reuseList)
			if (t != null && t.hasNotPassed())
			{
				reuses.add(t);
				SkillEntry skill = player.getKnownSkill(t.getId());
				if (skill == null || skill.getLevel() != t.getLevel())
					continue;
				player.enableSkill(skill);
			}

		return reuses;
	}

	private static final boolean restoreReuses(Player player, List<TimeStamp> reuses)
	{
		boolean result = false;
		final Collection<TimeStamp> reuseList = player.getSkillReuses();
		if (!reuseList.isEmpty())
			for (TimeStamp t : reuseList)
				if (t != null && t.hasNotPassed())
				{
					SkillEntry skill = player.getKnownSkill(t.getId());
					if (skill == null || skill.getLevel() != t.getLevel())
						continue;
					player.enableSkill(skill);
					result = true;
				}		

		if (reuses != null && !reuses.isEmpty())
			for (TimeStamp t : reuses)
				if (t.hasNotPassed())
				{
					SkillEntry skill = SkillTable.getInstance().getSkillEntry(t.getId(), t.getLevel());
					player.disableSkill(skill, t);
					result = true;
				}

		return result;
	}

	private static final void setAllReuses(Player player)
	{
		// someone is cheating, disabling all skills
		for (SkillEntry skill : player.getAllSkills())
			if (skill != null && skill.getTemplate().getReuseDelay() > 0)
				player.disableSkill(skill, skill.getTemplate().getReuseDelay());
	}

	private final void lockItems(Player player, boolean save)
	{
		if (_forbiddenItems == null)
			return;

		if (save)
		{
			_lockedItems = player.getInventory().getLockItems();
			_lockType = player.getInventory().getLockType();
		}

		for (ItemInstance item : player.getInventory().getPaperdollItems())
			if (item != null && ArrayUtils.contains(_forbiddenItems, item.getItemId()))
				player.getInventory().unEquipItem(item);

		player.getInventory().lockItems(LockType.INCLUDE, _forbiddenItems);
	}

	private final void unlockItems(Player player)
	{
		if (_forbiddenItems == null)
			return;

		if (_lockType == LockType.NONE)
			player.getInventory().unlock();
		else
		{
			for (ItemInstance item : player.getInventory().getPaperdollItems())
				if (item != null && ArrayUtils.contains(_lockedItems, item.getItemId()))
					player.getInventory().unEquipItem(item);

			player.getInventory().lockItems(_lockType, _lockedItems);
		}
	}

	private class PlayerListener implements OnPlayerEnterListener, OnPlayerExitListener, OnTeleportListener, OnCurrentHpDamageListener, OnDeathFromUndyingListener, OnReviveListener, OnPlayerSummonServitorListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			onPlayerReEnter(player);
		}

		@Override
		public void onPlayerExit(Player player)
		{
			if (_isExited)
				return;

			_isExited = true;
			restoreStateOnExit(true);
		}

		@Override
		public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
		{
			if (reflection.getId() != _reflectionId)
			{
				if (player.getReflectionId() != _reflectionId || _isExited)
					return;

				_isExited = true;
				player._stablePoint = null;
				restoreStateOnExit(false);
			}
			else
			{
				if (player.getReflectionId() == _reflectionId || _storedOnEnter || _restoredOnExit)
					return;

				_storedOnEnter = true;
				storeStateOnEnter(player);
				player.setTeam(_team);
				player.setUndying(SpecialEffectState.TRUE);

				if (_olympiadMode)
				{
					player.setIsInOlympiadMode(true);
					player.setOlympiadSide(_team.ordinal());
					player.setOlympiadCompStarted(_owner.isBattleStarted());
				}

				for (ItemInstance item : player.getInventory().getItems())
					item.lockEnchantAndAttribute(_physWeaponEnchantLock, _magicWeaponEnchantLock, _armorEnchantLock, _accEnchantLock, _weaponAttLock, _armorAttLock);

				if (_maxEnchantedSkills)
					SkillTreeTable.lockMaxEnchant(player, true);

				final Servitor servitor = player.getServitor();
				if (servitor != null)
				{
					if (servitor.isPet())
					{
						if (!_owner.allowPets())
						{
							servitor.unSummon(false, false);
							return;
						}
						servitor.addListener(_petListeners);
						servitor.setUndying(SpecialEffectState.TRUE);							
					}
					servitor.setTeam(_team);
					_summonedServitors.add(servitor.getId());
				}
			}
		}

		@Override
		public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, SkillEntry skill, boolean crit)
		{
			if (!_storedOnEnter || _restoredOnExit || _isDead || _isExited)
				return;

			if (!_owner.isInProgress())
				return;

			// считаем дамаг от простых ударов и атакующих скиллов
			if (actor == attacker || (skill != null && !skill.getTemplate().isOffensive()))
				return;

			_damage += Math.min(actor.getCurrentHp(), damage);
		}

		@Override
		public void onDeathFromUndying(Creature actor, Creature killer)
		{
			if (!_storedOnEnter || _restoredOnExit)
				return;

			_isDead = true;
			actor.doDie(null); // no karma or exp penalty
			final Player pk = killer.getPlayer();
			if (pk == null)
				return;

			TeamType pkTeam = pk.getTeam();
			if (pkTeam != TeamType.NONE && pkTeam != _team && _owner.equals(pk.getEvent(_owner.getClass())))
			{
				_matchRecord.deaths++;
				List<CustomPlayerSnapshotObject> team = _owner.getObjects(pkTeam);
				for (CustomPlayerSnapshotObject pkEvent : team)
					if (pkEvent.getPlayer() == pk)
						pkEvent._matchRecord.kills++;

				_owner.onDie(actor.getPlayer());
			}
			actor.broadcastCharInfo(); // отображение команды после смерти
		}

		@Override
		public void onRevive(Creature actor)
		{
			if (!_storedOnEnter || _restoredOnExit)
				return;

			if (actor.getReflectionId() == _reflectionId)
			{
				_isDead = false;
				actor.setUndying(SpecialEffectState.TRUE);
				actor.setTeam(_team);
				actor.broadcastCharInfo();				
			}
		}

		@Override
		public void onSummonServitor(Player player, Servitor servitor)
		{
			if (!_storedOnEnter || _restoredOnExit)
				return;

			if (player.getReflectionId() == _reflectionId)
			{
				if (servitor.isPet())
				{
					if (!_owner.allowPets())
					{
						servitor.unSummon(false, false);
						return;
					}
					servitor.addListener(_petListeners);
					servitor.setUndying(SpecialEffectState.TRUE);
					if (servitor.getCurrentHp() == 1.0) // restoreStateOnExit() оживляет мертвого пета, давая ему 1hp
						servitor.doDie(null);
				}
				if (!_olympiadMode && !_summonedServitors.contains(servitor.getId()))
				{
					_summonedServitors.add(servitor.getId());
					if (_removeBuffs)
						clearEffects(servitor);
				}
			}
		}
	}

	private class PetListener implements OnDeathFromUndyingListener, OnReviveListener
	{
		@Override
		public void onDeathFromUndying(Creature actor, Creature killer)
		{
			if (!_storedOnEnter || _restoredOnExit)
				return;

			actor.doDie(null); // no karma or exp penalty
			actor.broadcastCharInfo(); // отображение команды после смерти
		}

		@Override
		public void onRevive(Creature actor)
		{
			if (!_storedOnEnter || _restoredOnExit)
				return;

			if (actor.getReflectionId() == _reflectionId)
			{
				actor.setUndying(SpecialEffectState.TRUE);
				actor.setTeam(_team);
				actor.broadcastCharInfo();				
			}
		}
	}
}