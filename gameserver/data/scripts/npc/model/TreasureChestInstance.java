package npc.model;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.instances.ChestInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.npc.NpcTemplate;


public class TreasureChestInstance extends ChestInstance
{
	private static final int TREASURE_BOMB_ID = 4143;

	public TreasureChestInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	public void tryOpen(Player opener, Skill skill)
	{
		double chance = calcChance(opener, skill);
		if(Rnd.chance(chance))
		{
			getAggroList().addDamageHate(opener, 10000, 0);
			doDie(opener);
		}
		else
			fakeOpen(opener);
	}

	public double calcChance(Player opener, Skill skill)
	{
		double chance;
		int npcLvl = getLevel();
		if(!isCommonTreasureChest())
		{
			chance = skill.getActivateRate();
			double levelmod = (double) skill.getMagicLevel() - npcLvl;
			chance += levelmod * skill.getLevelModifier();
		}
		else
		{
			chance = 25;
			int openerLvl = skill.getId() == 22271 ? opener.getLevel() : skill.getMagicLevel();
			int lvlDiff = Math.abs(openerLvl - npcLvl);
			if((openerLvl <= 77 && lvlDiff >= 6) || (openerLvl >= 78 && lvlDiff >= 5))
				chance = 0;
		}
		return chance;
	}

	private void fakeOpen(Creature opener)
	{
		SkillEntry bomb = SkillTable.getInstance().getSkillEntry(TREASURE_BOMB_ID, getBombLvl());
		if(bomb != null)
			doCast(bomb, opener, false);
		onDecay();
	}

	private int getBombLvl()
	{
		int npcLvl = getLevel();
		int lvl = 1;
		if(npcLvl >= 78)
			lvl = 10;
		else if(npcLvl >= 72)
			lvl = 9;
		else if(npcLvl >= 66)
			lvl = 8;
		else if(npcLvl >= 60)
			lvl = 7;
		else if(npcLvl >= 54)
			lvl = 6;
		else if(npcLvl >= 48)
			lvl = 5;
		else if(npcLvl >= 42)
			lvl = 4;
		else if(npcLvl >= 36)
			lvl = 3;
		else if(npcLvl >= 30)
			lvl = 2;
		return lvl;
	}

	private boolean isCommonTreasureChest()
	{
		int npcId = getNpcId();
		return npcId >= 18265 && npcId <= 18286;
	}


	@Override
	public void onReduceCurrentHp(final double damage, final Creature attacker, SkillEntry skill, final boolean awake, final boolean standUp, boolean directHp)
	{
		if(!isCommonTreasureChest())
			fakeOpen(attacker);
	}
}