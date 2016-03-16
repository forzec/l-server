package ai;

import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillUse;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Location;

public class Lematan extends Fighter
{
	private int _teleportedPhase = 0;
	private boolean _spawned = false;
	private long _skillReuseTimer = 0;

	public Lematan(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void thinkAttack()
	{
		if(_teleportedPhase == 0 && _actor.getCurrentHpPercents() <= 50)
		{
			_teleportedPhase = 1;
			_actor.startDamageBlocked();
			getActor().getAggroList().clear();
			addTaskMove(new Location(86116, -209117, -3774), false);
			ThreadPoolManager.getInstance().schedule(new Runnable()
			{
				@Override
				public void run()
				{
					if(_teleportedPhase == 1)
					{
						_actor.broadcastPacket(new MagicSkillUse(_actor, 4671, 1, 500, 0));
						_actor.teleToLocation(new Location(85000, -208699, -3336));
						getActor().setSpawnedLoc(new Location(85000, -208699, -3336));
						_teleportedPhase = 2;
						_actor.stopDamageBlocked();
					}
				}
			}, 8000L);
		}
		else if(_teleportedPhase == 2)
		{
			if(_skillReuseTimer + 15000 < System.currentTimeMillis())
			{
				_skillReuseTimer = System.currentTimeMillis();
				for(NpcInstance n : _actor.getAroundNpc(1000, 200))
					if(n.getNpcId() == 18634)
					{
						n.doCast(SkillTable.getInstance().getSkillEntry(5712, 1), _actor, false); // animation
						n.altOnMagicUseTimer(_actor, SkillTable.getInstance().getSkillEntry(5713, 1));
					}
			}
		}
		super.thinkAttack();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		if(_teleportedPhase == 1)
			return;
		else if(!_spawned && _teleportedPhase == 2)
		{
			_spawned = true;
			_actor.getReflection().spawnByGroup("lematan_privates");
		}
		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_actor.getReflection().despawnByGroup("lematan_privates");
		_actor.getReflection().addSpawnWithoutRespawn(32511, new Location(84983, -208736, -3328, 49915), 0);
		super.onEvtDead(killer);
	}


}