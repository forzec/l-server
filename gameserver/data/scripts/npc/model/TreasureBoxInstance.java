package npc.model;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.ChestInstance;
import org.mmocore.gameserver.network.l2.s2c.PlaySound;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * "Старый" вид сундуков, на ХФ используется в рифте
 */
public class TreasureBoxInstance extends ChestInstance
{
	private static final String BOX_NPC_ID = "boxNpcId";
	private static final int TREASURE_BOMB_ID = 4143;
	private static final int MIMIC_ATTACK_ID = 4144;

	private static final int[] SKILL_UNLOCK_MAX_CHANCE = { 98, 84, 99, 84, 88, 90, 89, 88, 86, 90, 87, 89, 89, 89, 89 };

	// шаблон настоящего сундука для мимика
	private final NpcTemplate _boxTemplate;

	private boolean _fake = true; // true если мимик
	private boolean _opened = false; // сундук успешно открыт

	public TreasureBoxInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		int boxId = getParameter(BOX_NPC_ID, 0);
		_boxTemplate = boxId > 0 ? NpcHolder.getInstance().getTemplate(boxId) : null;
	}

	public NpcTemplate getBoxTemplate()
	{
		return _boxTemplate;
	}

	@Override
	public void onSeeSpell(SkillEntry skill, Creature caster)
	{
		super.onSeeSpell(skill, caster);

		if (_opened || isDead() || caster.getCastingTarget() != this)
			return;

		final int chance = getChance(skill, this);
		if (chance >= 0 && caster.isPlayer() && ((Player)caster).isDebug())
			caster.sendMessage("Chest open chance: " + chance);

		if (_fake)
		{
			if (chance >= 0) // на попытку открыть мимик бьет скиллом
			{
				SkillEntry attack = SkillTable.getInstance().getSkillEntry(MIMIC_ATTACK_ID, Math.min(getLevel(), SkillTable.getInstance().getMaxLevel(MIMIC_ATTACK_ID)));
				if (attack != null)
					doCast(attack, caster, false);
			}
			getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, caster, 100);
		}
		else if (chance < 0) // если использовать что-то кроме открывания на настоящем сундуке он взрывается и пропадает
		{
			SkillEntry bomb = SkillTable.getInstance().getSkillEntry(TREASURE_BOMB_ID, getBombLevel(getLevel()));
			if(bomb != null)
				doCast(bomb, caster, false);
		}
		else if (Rnd.chance(chance)) // открыли
		{
			clearSweep();
			_opened = true;
			getAggroList().addDamageHate(caster, 10000, 0);
			doDie(caster);
		}
		else // не открыли
		{
			getAggroList().clear();
			doDie(null);
			caster.sendPacket(new PlaySound("ItemSound2.broken_key"));
		}
	}

	@Override
	public void calculateRewards(Creature lastAttacker)
	{
		if (!_fake && !_opened) // настоящий сундук, но не открыт - дропа нет
			return;

		super.calculateRewards(lastAttacker);
	}

	@Override
	public NpcTemplate getTemplate()
	{
		return (_opened && _boxTemplate != null) ? _boxTemplate : super.getTemplate();
	}

	@Override
	public int getDisplayId()
	{
		return _boxTemplate != null ? _boxTemplate.npcId : super.getTemplate().npcId;
	}

	@Override
	public void onSpawn()
	{
		_fake = _boxTemplate != null ? Rnd.nextBoolean() : false; // если boxId не задан то сундук всегда открывается
		_opened = false;

		super.onSpawn();
	}

	@Override
	public void onReduceCurrentHp(final double damage, final Creature attacker, SkillEntry skill, final boolean awake, final boolean standUp, boolean directHp)
	{
		if (!_fake)
		{
			SkillEntry bomb = SkillTable.getInstance().getSkillEntry(TREASURE_BOMB_ID, getBombLevel(getLevel()));
			if(bomb != null)
				doCast(bomb, attacker, false);

			doDie(null); // TODO: DS: выяснить почему не наносит повреждения и не заканчивает каст, это убрать
		}

		super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}

	private static final int getChance(SkillEntry skill, Creature chest)
	{
		int chance = 0;
		int maxChance = 0;
		switch (skill.getId())
		{
			case 27: // Unlock skill
			case 3155: // Item Skill: Unlock (custom) TODO
				maxChance = SKILL_UNLOCK_MAX_CHANCE[skill.getLevel() - 1];
				chance = maxChance - (chest.getLevel() - skill.getLevel() * 4 - 16) * 6;
				break;
			case 2065: // Box Key
				maxChance = 60;
				chance = (int)Math.round(maxChance - (chest.getLevel() - (skill.getLevel() - 1) * 10) * 1.5);
				break;
			case 2229: // Treasure Chest Key
				maxChance = 100;
				switch (skill.getLevel())
				{
					case 1:
						chance = chest.getLevel() - 19;
						if (chance <= 0)
							chance = maxChance;
						else
							chance = (int)Math.round(0.02 * chance * chance - 2.64 * chance + 76.95);
						break;
					case 2:
						chance = chest.getLevel() - 29;
						if (chance <= 0)
							chance = maxChance;
						else
							chance = (int)Math.round(0.03 * chance * chance - 2.79 * chance + 75.68);
						break;
					case 3:
						chance = chest.getLevel() - 39;
						if (chance <= 0)
							chance = maxChance;
						else
							chance = (int)Math.round(0.03 * chance * chance - 2.69 * chance + 73.34);
						break;
					case 4:
						chance = chest.getLevel() - 49;
						if (chance <= 0)
							chance = maxChance;
						else
							chance = (int)Math.round(0.03 * chance * chance - 2.84 * chance + 80.34);
						break;
					case 5:
						chance = chest.getLevel() - 59;
						if (chance <= 0)
							chance = maxChance;
						else
							chance = (int)Math.round(0.05 * chance * chance - 3.56 * chance + 90.65);
						break;
					case 6:
						chance = chest.getLevel() - 69;
						if (chance <= 0)
							chance = maxChance;
						else
							chance = (int)Math.round(0.09 * chance * chance - 3.73 * chance + 85.72);
						break;
					case 7:
						chance = chest.getLevel() - 79;
						if (chance <= 0)
							chance = maxChance;
						else
							chance = (int)Math.round(0.43 * chance * chance - 6.71 * chance + 95.93);
						break;
					case 8:
						chance = 100;
				}
				break;
			default:
				return -1;
		}

		if (chance > maxChance)
			chance = maxChance;
		else if (chance < 0)
			chance = 0;

		return chance;
	}

	private static final int getBombLevel(int chestLevel)
	{
		if(chestLevel >= 78)
			return 10;
		if(chestLevel >= 72)
			return 9;
		if(chestLevel >= 66)
			return 8;
		if(chestLevel >= 60)
			return 7;
		if(chestLevel >= 54)
			return 6;
		if(chestLevel >= 48)
			return 5;
		if(chestLevel >= 42)
			return 4;
		if(chestLevel >= 36)
			return 3;
		if(chestLevel >= 30)
			return 2;
		return 1;
	}
}