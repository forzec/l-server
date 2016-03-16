package handler.items;

import gnu.trove.TIntHashSet;

import org.mmocore.gameserver.data.xml.holder.ItemHolder;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.item.ItemTemplate;

/**
 * @author VISTALL
 * @date 7:34/17.03.2011
 */
public class ItemSkills extends ScriptItemHandler
{
	private int[] _itemIds;

	public ItemSkills()
	{
		TIntHashSet set = new TIntHashSet();
		for(ItemTemplate template : ItemHolder.getInstance().getAllTemplates())
		{
			if(template == null)
				continue;

			for(SkillEntry skill : template.getAttachedSkills())
				if(skill.getTemplate().isHandler())
					set.add(template.getItemId());
		}
		_itemIds = set.toArray();
	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		Creature caster;
		if(playable.isPlayer())
			caster = playable;
		else if(playable.isPet())
			caster = item.getTemplate().isHerb() ? playable.getPlayer() : playable;
		else
			return false;

		SkillEntry[] skills = item.getTemplate().getAttachedSkills();

		for(int i = 0; i < skills.length; i++)
		{
			SkillEntry skill = skills[i];
			Creature aimingTarget = skill.getTemplate().getAimingTarget(caster, caster.getTarget());
			if(skill.checkCondition(caster, aimingTarget, ctrl, false, true))
				caster.getAI().Cast(skill, aimingTarget, ctrl, false);
			else if(i == 0)  //FIXME [VISTALL] всегда первый скил идет вместо конда?
				return false;
		}
		return true;
	}

	@Override
	public int[] getItemIds()
	{
		return _itemIds;
	}
}
