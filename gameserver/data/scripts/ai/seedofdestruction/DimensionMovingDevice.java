package ai.seedofdestruction;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * AI Dimension Moving Device в Seed of Destruction:
 *
 * Из ловушки спаунятся мобы с задержкой в 3 сек через 5 сек после спауна в следующей последовательности:
 * Dragon Steed Troop Commander
 * White Dragon Leader
 * Dragon Steed Troop Healer (not off-like)
 * Dragon Steed Troop Magic Leader
 * Dragon Steed Troop Javelin Thrower
 *
 * @author pchayka
 */
public class DimensionMovingDevice extends DefaultAI
{
	private static final int INITIAL_WAVE_DELAY = 5000; // пауза перед первой волной мобов
	private static final int DELAY_BETWEEN_MOBS = 1000; // задержка после каждого моба
	private static final int NEXT_WAVE_DELAY = 120 * 1000; // 2 мин между волнами мобов

	private long _spawnTime = 0;
	private int _spawnCount = 0;
	private int _nextMobIdx = 0;

	private static final int[] MOBS = { 22538, // Dragon Steed Troop Commander
			22540, // White Dragon Leader
			22547, // Dragon Steed Troop Healer
			22542, // Dragon Steed Troop Magic Leader
			22548 // Dragon Steed Troop Javelin Thrower
	};

	public DimensionMovingDevice(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		_spawnTime = System.currentTimeMillis() + INITIAL_WAVE_DELAY;
		_spawnCount = 0;
		_nextMobIdx = 0;
	}

	@Override
	protected boolean thinkActive()
	{
		if (_spawnCount < 100 && System.currentTimeMillis() > _spawnTime)
		{
			_spawnCount++;
			getActor().getReflection().addSpawnWithoutRespawn(MOBS[_nextMobIdx++], getActor().getLoc(), 0);
			if (_nextMobIdx >= MOBS.length)
			{
				_nextMobIdx = 0;
				_spawnTime = System.currentTimeMillis() + NEXT_WAVE_DELAY;
			}
			else
				_spawnTime = System.currentTimeMillis() + DELAY_BETWEEN_MOBS;
		}
		return true;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{}
}