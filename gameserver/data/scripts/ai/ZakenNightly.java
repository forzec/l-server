package ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.geodata.GeoEngine;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Location;

/**
 * Ночной Закен.
 * Регулярно призывает аддов.
 * Телепортирует несколько игроков случайно по комнатам.
 *
 * @author pchayka
 */
public class ZakenNightly extends Fighter
{
	private static final int[] zaken_adds = {
			29023,
			29024,
			29026,
			29027
	};

	private static final Location[] _locations = new Location[]{
			new Location(55272, 219112, -3496),
			new Location(56296, 218072, -3496),
			new Location(54232, 218072, -3496),
			new Location(54248, 220136, -3496),
			new Location(56296, 220136, -3496),
			new Location(55272, 219112, -3224),
			new Location(56296, 218072, -3224),
			new Location(54232, 218072, -3224),
			new Location(54248, 220136, -3224),
			new Location(56296, 220136, -3224),
			new Location(55272, 219112, -2952),
			new Location(56296, 218072, -2952),
			new Location(54232, 218072, -2952),
			new Location(54248, 220136, -2952),
			new Location(56296, 220136, -2952)
	};

	private NpcInstance actor = getActor();
	private static final long _spawnTimeout = 15000L;
	private static final int _addcount = 2;
	private long _spawnTimer = 0L;
	private Reflection r = actor.getReflection();
	private List<NpcInstance> _spawnedAdds = new ArrayList<NpcInstance>();

	private long _scatterTimer = 0L;
	private static final long _scatterTimeout = 90000L;
	private Player _scatterTarget = null;

	private long _escapeTimer = 0L;
	private static final long _escapeTimeout = 240000L;

	private static final long _generalSkillUseDelay = 30000L;


	public ZakenNightly(NpcInstance actor)
	{
		super(actor);
		MAX_PURSUE_RANGE = Integer.MAX_VALUE / 2;
	}

	private void selectScatterTarget()
	{
		_scatterTarget = null;
		List<Creature> hateList = actor.getAggroList().getHateList(900);
		Collections.shuffle(hateList);
		for(Creature c : hateList)
			if(c.isPlayer() && !c.isDead() && GeoEngine.canSeeTarget(actor, c, false))
				_scatterTarget = c.getPlayer();
	}

	@Override
	protected void onEvtFinishCasting(SkillEntry skillEntry)
	{
		if(_scatterTarget != null && skillEntry.getId() == 4216)
		{
			_scatterTarget.teleToLocation(_locations[Rnd.get(_locations.length)]);
			actor.getAggroList().remove(_scatterTarget, true);
			_scatterTarget = null;
		}
		else if(skillEntry.getId() == 4222)
		{
			actor.teleToLocation(_locations[Rnd.get(_locations.length)]);
			actor.getAggroList().clear(true);
			for(int i = 0; i < 10; i++)
				r.addSpawnWithoutRespawn(zaken_adds[Rnd.get(zaken_adds.length)], actor.getLoc(), 500);
		}
		super.onEvtFinishCasting(skillEntry);
	}

	@Override
	protected void thinkAttack()
	{
		if(_spawnTimer == 0)
			_spawnTimer = System.currentTimeMillis() + _generalSkillUseDelay;
		if(_scatterTimer == 0)
			_scatterTimer = System.currentTimeMillis() + 3 * _generalSkillUseDelay;
		if(_escapeTimer == 0)
			_escapeTimer = System.currentTimeMillis() + 5 * _generalSkillUseDelay;

		if(_spawnTimer + _spawnTimeout < System.currentTimeMillis())
		{
			int x = 0;
			for(NpcInstance n : _spawnedAdds)
				if(!n.isDead())
					x++;

			_spawnTimer = System.currentTimeMillis();
			if(x < 30)
				for(int i = 0; i < _addcount; i++)
				{
					NpcInstance newadd = r.addSpawnWithoutRespawn(zaken_adds[Rnd.get(zaken_adds.length)], actor.getLoc(), 350);
					newadd.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, actor.getAI().getAttackTarget(), 5000);
					_spawnedAdds.add(newadd);
				}
		}
		if(_scatterTimer + _scatterTimeout < System.currentTimeMillis())
		{
			selectScatterTarget();
			if(_scatterTarget != null)
				actor.doCast(SkillTable.getInstance().getSkillEntry(4216, 1), _scatterTarget, false);
			_scatterTimer = System.currentTimeMillis();
		}
		if(_escapeTimer + _escapeTimeout < System.currentTimeMillis())
		{
			actor.doCast(SkillTable.getInstance().getSkillEntry(4222, 1), actor, false);
			_escapeTimer = System.currentTimeMillis();
		}
		super.thinkAttack();
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		Reflection r = actor.getReflection();
		r.setReenterTime(System.currentTimeMillis());
		super.onEvtDead(killer);
	}

	@Override
	protected void teleportHome()
	{
	}

	@Override
	protected void returnHome()
	{
	}
}