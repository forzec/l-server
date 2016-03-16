package handler.items;

import gnu.trove.TIntHashSet;

import java.util.List;

import org.mmocore.gameserver.data.xml.holder.SkillAcquireHolder;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.SkillLearn;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillUse;
import org.mmocore.gameserver.network.l2.s2c.SkillList;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author VISTALL
 */
public class Spellbooks extends ScriptItemHandler
{
	private int[] _itemIds = null;

	public Spellbooks()
	{
		TIntHashSet list = new TIntHashSet();
		List<SkillLearn> l = SkillAcquireHolder.getInstance().getAllNormalSkillTreeWithForgottenScrolls();
		for(SkillLearn learn : l)
			list.add(learn.getItemId());

		_itemIds = list.toArray();
	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(!playable.isPlayer())
			return false;

		Player player = (Player) playable;

		if(item.getCount() < 1)
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return false;
		}

		List<SkillLearn> list = SkillAcquireHolder.getInstance().getSkillLearnListByItemId(player, item.getItemId());
		if(list.isEmpty())
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return false;
		}

		// проверяем ли есть нужные скилы
		boolean alreadyHas = true;
		for(SkillLearn learn : list)
		{
			if(player.getSkillLevel(learn.getId()) != learn.getLevel())
			{
				alreadyHas = false;
				break;
			}
		}
		if(alreadyHas)
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return false;
		}

		// проверка по уровне
		boolean wrongLvl = false;
		for(SkillLearn learn : list)
		{
			if(player.getLevel() < learn.getMinLevel())
				wrongLvl = true;
		}

		if(wrongLvl)
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return false;
		}

		if(!player.consumeItem(item.getItemId(), 1L))
			return false;

		for(SkillLearn skillLearn :  list)
		{
			SkillEntry skill = SkillTable.getInstance().getSkillEntry(skillLearn.getId(), skillLearn.getLevel());
			if(skill == null)
				continue;

			player.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skill.getId(),  skill.getLevel()));

			player.addSkill(skill, true);
		}

		player.updateStats();
		player.sendPacket(new SkillList(player));
		// Анимация изучения книги над головой чара (на самом деле, для каждой книги своя анимация, но они одинаковые)
		player.broadcastPacket(new MagicSkillUse(player, player, 2790, 1, 1, 0));
		return true;
	}

	@Override
	public int[] getItemIds()
	{
		return _itemIds;
	}
}