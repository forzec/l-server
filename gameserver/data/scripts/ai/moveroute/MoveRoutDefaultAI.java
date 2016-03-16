package ai.moveroute;

import java.util.List;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.data.xml.holder.MoveRouteHolder;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.MinionList;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.AggroList.AggroInfo;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.s2c.SocialAction;
import org.mmocore.gameserver.templates.moveroute.MoveNode;
import org.mmocore.gameserver.templates.moveroute.MoveRoute;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 22:39/24.10.2011
 */
public class MoveRoutDefaultAI extends DefaultAI
{
	protected MoveRoute _moveRoute;
	protected MoveNode _destination = null;
	protected int _currentNodeIndex = 0;
	protected boolean _incPoint = true;

	public MoveRoutDefaultAI(NpcInstance actor)
	{
		super(actor);

		String moveRoute = actor.getParameter("moveroute", null);

		_moveRoute = moveRoute == null ? null : MoveRouteHolder.getInstance().getRoute(moveRoute);
	}

	@Override
	protected boolean thinkActive()
	{
		if (_moveRoute == null)
			return super.thinkActive();

		NpcInstance actor = getActor();
		if(actor.isActionsDisabled())
			return true;

		if(_randomAnimationEnd > System.currentTimeMillis())
			return true;

		if(_def_think)
		{
			if(doTask())
				clearTasks();
			return true;
		}

		long now = System.currentTimeMillis();
		if(now - _checkAggroTimestamp > Config.AGGRO_CHECK_INTERVAL)
		{
			_checkAggroTimestamp = now;

			boolean aggressive = Rnd.chance(actor.getParameter("SelfAggressive", isAggressive() ? 100 : 0));
			if(!actor.getAggroList().isEmpty() || aggressive)
			{
				List<Creature> targets = World.getAroundCharacters(actor);
				while(!targets.isEmpty())
				{
					Creature target = getNearestTarget(targets);
					if(target == null)
						break;

					if(aggressive || actor.getAggroList().get(target) != null)
						if(checkAggression(target))
						{
							actor.getAggroList().addDamageHate(target, 0, 2);

							if(target.isServitor())
								actor.getAggroList().addDamageHate(target.getPlayer(), 0, 1);

							startRunningTask(AI_TASK_ATTACK_DELAY);
							setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);

							return true;
						}

					targets.remove(target);
				}
			}
		}

		if(actor.isMinion())
		{
			NpcInstance leader = actor.getLeader();
			if(leader != null)
			{
				double distance = actor.getDistance(leader.getX(), leader.getY());
				if(distance > 1000)
				{
					actor.teleToLocation(leader.getRndMinionPosition());
					return true;
				}
				else if(distance > 200)
				{
					addTaskMove(leader.getRndMinionPosition(), false);
					return true;
				}
			}
		}

		if (actor.isMoving)
			return true;

		int prevIndex = _currentNodeIndex;

		if(_incPoint)
			_currentNodeIndex ++;
		else
			_currentNodeIndex --;

		switch(_moveRoute.getType())
		{
			case LOOP:
				if(_currentNodeIndex >= _moveRoute.getNodes().size())
				{
					_incPoint = true;
					_currentNodeIndex = 0;					
				}
				break;
			case CIRCLE:
				if(_currentNodeIndex >= _moveRoute.getNodes().size())
				{
					_incPoint = false;
					// мы сейчас вне границы, масива, отнимаеш, что б ввойти в границу, и берем преведущею точку
					_currentNodeIndex = _moveRoute.getNodes().size() - 2;
				}
				else if(_currentNodeIndex < 0)
				{
					_incPoint = true;
					_currentNodeIndex = 1;
				}
				break;
			case ONCE:
				if(_currentNodeIndex >= _moveRoute.getNodes().size())
				{
					actor.decayOrDelete();
					return false;
				}
				break;
			case RANDOM:
				_currentNodeIndex = Rnd.get(_moveRoute.getNodes().size());
				break;
		}

		onEvtArrivedToNode(prevIndex, _currentNodeIndex);
		return false;
	}

	protected void onEvtArrivedToNode(int prev, int index)
	{
		NpcInstance actor = getActor();

		MoveNode node = _moveRoute.getNodes().get(prev);
		if(node.getSocialId() > 0)
			actor.broadcastPacketToOthers(new SocialAction(actor.getObjectId(), node.getSocialId()));

		if(node.getNpcString() != null)
			ChatUtils.chat(actor, node.getChatType(), node.getNpcString());

		if(node.getDelay() > 0)
			setIsInRandomAnimation(node.getDelay());

		if (_moveRoute.isRunning())
			getActor().setRunning();

		_destination = _moveRoute.getNodes().get(index);
		addTaskMove(_destination, false);
		if (actor.hasMinions())
		{
			MinionList minionList = actor.getMinionList();
			if(minionList.hasAliveMinions())
				for(NpcInstance minion : minionList.getAliveMinions())
					if (!minion.isInCombat() && !minion.isAfraid())
					{
						if (_moveRoute.isRunning())
							minion.setRunning();
						//((DefaultAI)minion.getAI()).addTaskMove(_destination, false);
						minion.followToCharacter(getActor(), 500, true);
					}
		}
	}

	@Override
	protected boolean isInAggroRange(Creature target)
	{
		if (_moveRoute == null)
			return super.isInAggroRange(target);

		final NpcInstance actor = getActor();
		final AggroInfo ai = actor.getAggroList().get(target);
		if (ai != null && ai.hate > 0)
		{
			final Location loc = _destination != null ? _destination : actor.getSpawnedLoc();
			if (!target.isInRangeZ(loc, MAX_PURSUE_RANGE))
				return false;
		}
		else if (!isAggressive() || !target.isInRangeZ(actor.getLoc(), actor.getAggroRange()))
			return false;

		return true;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		if (_moveRoute == null)
			return;

		_currentNodeIndex = 0;
		_incPoint = true;
		if (_moveRoute.isRunning())
			getActor().setRunning();

		onEvtArrivedToNode(_currentNodeIndex, _currentNodeIndex);
	}

	@Override
	protected void returnHome(boolean clearAggro, boolean teleport)
	{
		if (_moveRoute == null)
		{
			super.returnHome(clearAggro, teleport);
			return;
		}

		NpcInstance actor = getActor();

		clearTasks();
		actor.stopMove();

		if(clearAggro)
			actor.getAggroList().clear(true);

		setAttackTimeout(Long.MAX_VALUE);
		setAttackTarget(null);

		changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
		if (_moveRoute.isRunning())
			getActor().setRunning();

		_destination = _moveRoute.getNodes().get(_currentNodeIndex); // TODO: DS: поиск ближайшей точки ?
		addTaskMove(_destination, false);
		if (actor.hasMinions())
		{
			MinionList minionList = actor.getMinionList();
			if(minionList.hasAliveMinions())
				for(NpcInstance minion : minionList.getAliveMinions())
					if (!minion.isInCombat() && !minion.isAfraid())
					{
						if (_moveRoute.isRunning())
							minion.setRunning();
						minion.getAI().changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
						minion.followToCharacter(getActor(), 500, true);
					}
		}
	}

	@Override
	public boolean isGlobalAI()
	{
		return _moveRoute != null;
	}
}
