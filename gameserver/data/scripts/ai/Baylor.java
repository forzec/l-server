package ai;

import java.util.HashMap;
import java.util.Map;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.instancemanager.HellboundManager;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Party;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillUse;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.skills.SkillEntry;
import org.napile.primitive.maps.IntObjectMap;

/**
 * @author Diamond
 */
public class Baylor extends DefaultAI
{
	final SkillEntry Berserk; // Increases P. Atk. and P. Def.
	final SkillEntry Invincible; // Неуязвимость при 30% hp
	final SkillEntry Imprison; // Помещает одиночную цель в тюрьму, рейндж 600
	final SkillEntry GroundStrike; // Массовая атака, 2500 каст
	final SkillEntry JumpAttack; // Массовая атака, 2500 каст
	final SkillEntry StrongPunch; // Откидывает одиночную цель кулаком, и оглушает, рейндж 600
	final SkillEntry Stun1; // Массовое оглушение, 5000 каст
	final SkillEntry Stun2; // Массовое оглушение, 3000 каст
	final SkillEntry Stun3; // Массовое оглушение, 2000 каст
	//final L2Skill Stun4; // Не работает?

	final int PresentationBalor2 = 5402; // Прыжок, удар по земле
	final int PresentationBalor3 = 5403; // Электрическая аура
	final int PresentationBalor4 = 5404; // Электрическая аура, в конце сияние

	final int PresentationBalor10 = 5410; // Не работает?
	final int PresentationBalor11 = 5411; // Не работает?
	final int PresentationBalor12 = 5412; // Массовый удар

	private static final int Water_Dragon_Claw = 2360;

	private boolean _isUsedInvincible = false;

	private int _claw_count = 0;
	private long _last_claw_time = 0;

	private class SpawnSocial extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			if(actor != null)
				actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, PresentationBalor2, 1, 4000, 0));
		}
	}

	public Baylor(NpcInstance actor)
	{
		super(actor);

		IntObjectMap<SkillEntry> skills = getActor().getTemplate().getSkills();

		Berserk = skills.get(5224);
		Invincible = skills.get(5225);
		Imprison = skills.get(5226);
		GroundStrike = skills.get(5227);
		JumpAttack = skills.get(5228);
		StrongPunch = skills.get(5229);
		Stun1 = skills.get(5230);
		Stun2 = skills.get(5231);
		Stun3 = skills.get(5232);
		//Stun4 = skills.get(5401);
	}

	@Override
	protected void onEvtSpawn()
	{
		ThreadPoolManager.getInstance().schedule(new SpawnSocial(), 20000);
		super.onEvtSpawn();
	}

	@Override
	protected void onEvtSeeSpell(SkillEntry skill, Creature caster)
	{
		super.onEvtSeeSpell(skill, caster);

		NpcInstance actor = getActor();
		if(actor.isDead() || skill == null || caster == null)
			return;

		if(System.currentTimeMillis() - _last_claw_time > 5000)
			_claw_count = 0;

		if(skill.getId() == Water_Dragon_Claw)
		{
			_claw_count++;
			_last_claw_time = System.currentTimeMillis();
		}

		Player player = caster.getPlayer();
		if(player == null)
			return;

		int count = 1;
		Party party = player.getParty();
		if(party != null)
			count = party.getMemberCount();

		// Снимаем неуязвимость
		if(_claw_count >= count)
		{
			_claw_count = 0;
			actor.getEffectList().stopEffect(Invincible);
			Functions.npcSay(actor, "Да как вы посмели! Я непобедим!!!");
		}
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
		double actor_hp_precent = actor.getCurrentHpPercents();

		if(actor_hp_precent < 30 && !_isUsedInvincible)
		{
			_isUsedInvincible = true;
			addTaskBuff(actor, Invincible);
			Functions.npcSay(actor, "Ахаха! Теперь вы все умрете.");
			return true;
		}

		int rnd_per = Rnd.get(100);
		if(rnd_per < 7 && actor.getEffectList().getEffectsBySkill(Berserk) == null)
		{
			addTaskBuff(actor, Berserk);
			Functions.npcSay(actor, "Beleth, дай мне силу!");
			return true;
		}

		if(rnd_per < 15 || rnd_per < 33 && actor.getEffectList().getEffectsBySkill(Berserk) != null)
			return chooseTaskAndTargets(StrongPunch, target, distance);

		if(!actor.isAMuted() && rnd_per < 50)
			return chooseTaskAndTargets(null, target, distance);

		Map<SkillEntry, Integer> skills = new HashMap<SkillEntry, Integer>();

		addDesiredSkill(skills, target, distance, GroundStrike);
		addDesiredSkill(skills, target, distance, JumpAttack);
		addDesiredSkill(skills, target, distance, StrongPunch);
		addDesiredSkill(skills, target, distance, Stun1);
		addDesiredSkill(skills, target, distance, Stun2);
		addDesiredSkill(skills, target, distance, Stun3);

		SkillEntry skill = selectTopSkill(skills);
		if(skill != null && !skill.getTemplate().isOffensive())
			target = actor;

		return chooseTaskAndTargets(skill, target, distance);
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		return false;
	}

	//Hellbound opening hook
	@Override
	protected void onEvtDead(Creature killer)
	{
		if(HellboundManager.getConfidence() < 1)
			HellboundManager.setConfidence(1);
		getActor().getReflection().setReenterTime(System.currentTimeMillis());
		super.onEvtDead(killer);
	}
}