package ai;

import java.util.HashMap;
import java.util.Map;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.BossInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import bosses.AntharasManager;

/**
 * @author pchayka
 */

public class Antharas extends DefaultAI
{
	// debuffs
	final SkillEntry s_fear = getSkill(4108, 1), s_fear2 = getSkill(5092, 1), s_curse = getSkill(4109, 1), s_paralyze = getSkill(4111, 1);
	// damage skills
	final SkillEntry s_shock = getSkill(4106, 1), s_shock2 = getSkill(4107, 1), s_antharas_ordinary_attack = getSkill(4112, 1), s_antharas_ordinary_attack2 = getSkill(4113, 1), s_meteor = getSkill(5093, 1), s_breath = getSkill(4110, 1);
	// regen skills
	final SkillEntry s_regen1 = getSkill(4239, 1), s_regen2 = getSkill(4240, 1), s_regen3 = getSkill(4241, 1);

	// Vars
	private int _hpStage = 0;
	private static long _minionsSpawnDelay = 0;
	private static final long _timeUntilSleep = 15 * 60000L;
	private long _nonAggroTimer = 0;

	public Antharas(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		if(_nonAggroTimer == 0)
			_nonAggroTimer = System.currentTimeMillis();
		if(_nonAggroTimer + _timeUntilSleep < System.currentTimeMillis())
			AntharasManager.sleep();
		return super.thinkActive();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		if(_nonAggroTimer != 0)
			_nonAggroTimer = 0;
		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		_minionsSpawnDelay = System.currentTimeMillis() + 120000L;
	}

	@Override
	protected boolean createNewTask()
	{
		clearTasks();
		Creature target;
		if((target = prepareTarget()) == null)
			return false;

		NpcInstance actor = getActor();
		if(actor.isDead())
			return false;

		double distance = actor.getDistance(target);

		// Buffs and stats
		double chp = actor.getCurrentHpPercents();
		if(_hpStage == 0)
		{
			actor.altOnMagicUseTimer(actor, s_regen1);
			_hpStage = 1;
		}
		else if(chp < 75 && _hpStage == 1)
		{
			actor.altOnMagicUseTimer(actor, s_regen2);
			_hpStage = 2;
		}
		else if(chp < 50 && _hpStage == 2)
		{
			actor.altOnMagicUseTimer(actor, s_regen3);
			_hpStage = 3;
		}
		else if(chp < 30 && _hpStage == 3)
		{
			actor.altOnMagicUseTimer(actor, s_regen3);
			_hpStage = 4;
		}

		// Minions spawn
		if(_minionsSpawnDelay < System.currentTimeMillis() && Rnd.chance(5) && actor.getMinionList().getAliveMinions().size() < Config.ALT_ANTHARAS_MINIONS_NUMBER)
			actor.getMinionList().spawnMinion(Rnd.chance(50) ? 29190 : 29069, 1, false); // Antharas Minions

		// Basic Attack
		if(Rnd.chance(50))
			return chooseTaskAndTargets(Rnd.chance(50) ? s_antharas_ordinary_attack : s_antharas_ordinary_attack2, target, distance);

		// Stage based skill attacks
		Map<SkillEntry, Integer> d_skill = new HashMap<SkillEntry, Integer>();
		switch(_hpStage)
		{
			case 1:
				addDesiredSkill(d_skill, target, distance, s_curse);
				addDesiredSkill(d_skill, target, distance, s_paralyze);
				addDesiredSkill(d_skill, target, distance, s_meteor);
				break;
			case 2:
				addDesiredSkill(d_skill, target, distance, s_curse);
				addDesiredSkill(d_skill, target, distance, s_paralyze);
				addDesiredSkill(d_skill, target, distance, s_meteor);
				addDesiredSkill(d_skill, target, distance, s_fear2);
				break;
			case 3:
				addDesiredSkill(d_skill, target, distance, s_curse);
				addDesiredSkill(d_skill, target, distance, s_paralyze);
				addDesiredSkill(d_skill, target, distance, s_meteor);
				addDesiredSkill(d_skill, target, distance, s_fear2);
				addDesiredSkill(d_skill, target, distance, s_shock2);
				addDesiredSkill(d_skill, target, distance, s_breath);
				break;
			case 4:
				addDesiredSkill(d_skill, target, distance, s_curse);
				addDesiredSkill(d_skill, target, distance, s_paralyze);
				addDesiredSkill(d_skill, target, distance, s_meteor);
				addDesiredSkill(d_skill, target, distance, s_fear2);
				addDesiredSkill(d_skill, target, distance, s_shock2);
				addDesiredSkill(d_skill, target, distance, s_fear);
				addDesiredSkill(d_skill, target, distance, s_shock);
				addDesiredSkill(d_skill, target, distance, s_breath);
				break;
			default:
				break;
		}

		SkillEntry r_skill = selectTopSkill(d_skill);
		if(r_skill != null && !r_skill.getTemplate().isOffensive())
			target = actor;

		return chooseTaskAndTargets(r_skill, target, distance);
	}

	private SkillEntry getSkill(int id, int level)
	{
		return SkillTable.getInstance().getSkillEntry(id, level);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);
		AntharasManager.notifyDeath((BossInstance) getActor());
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
}