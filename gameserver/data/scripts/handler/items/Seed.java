package handler.items;

import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.instancemanager.MapRegionManager;
import org.mmocore.gameserver.model.GameObject;
import org.mmocore.gameserver.model.Manor;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.ChestInstance;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.RaidBossInstance;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.mapregion.DomainArea;

public class Seed extends ScriptItemHandler
{
	private static int[] _itemIds = {};

	public Seed()
	{
		_itemIds = new int[Manor.getInstance().getAllSeeds().size()];
		int id = 0;
		for(Integer s : Manor.getInstance().getAllSeeds().keySet())
			_itemIds[id++] = s.shortValue();
	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;
		Player player = (Player) playable;

		// Цель не выбрана
		GameObject obj = player.getTarget();
		if(obj == null)
		{
			player.sendActionFailed();
			return false;
		}

		// Цель не моб, РБ или миньон
		if(!obj.isMonster() || obj instanceof RaidBossInstance || obj.isMinion() && ((MonsterInstance) obj).getLeader() instanceof RaidBossInstance || obj instanceof ChestInstance || ((MonsterInstance) obj).getChampion() > 0 && !item.isAltSeed())
		{
			player.sendPacket(SystemMsg.THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING);
			return false;
		}

		MonsterInstance target = (MonsterInstance) obj;

		// Моб мертв
		if(target.isDead())
		{
			player.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}

		// Уже посеяно
		if(target.isSeeded())
		{
			player.sendPacket(SystemMsg.THE_SEED_HAS_BEEN_SOWN);
			return false;
		}

		int seedId = item.getItemId();
		if(seedId == 0 || player.getInventory().getItemByItemId(item.getItemId()) == null)
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return false;
		}

		DomainArea domain = MapRegionManager.getInstance().getRegionData(DomainArea.class, player);
		int castleId = domain == null ? 0 : domain.getId();
		// Несовпадение зоны
		if(Manor.getInstance().getCastleIdForSeed(seedId) != castleId)
		{
			player.sendPacket(SystemMsg.THIS_SEED_MAY_NOT_BE_SOWN_HERE);
			return false;
		}

		// use Sowing skill, id 2097
		SkillEntry skill = SkillTable.getInstance().getSkillEntry(2097, 1);
		if(skill == null)
		{
			player.sendActionFailed();
			return false;
		}

		if(skill.checkCondition(player, target, false, false, true))
		{
			player.setUseSeed(seedId);
			player.getAI().Cast(skill, target);
		}
		return true;
	}

	@Override
	public final int[] getItemIds()
	{
		return _itemIds;
	}
}