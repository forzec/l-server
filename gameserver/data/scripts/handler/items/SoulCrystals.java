package handler.items;

import gnu.trove.TIntHashSet;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.data.xml.holder.SoulCrystalHolder;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.s2c.ActionFail;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillUse;
import org.mmocore.gameserver.network.l2.s2c.SetupGauge;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.SoulCrystal;


public class SoulCrystals extends ScriptItemHandler
{
	private int[] _itemIds;

	public SoulCrystals()
	{
		TIntHashSet set = new TIntHashSet();
		for(SoulCrystal crystal : SoulCrystalHolder.getInstance().getCrystals())
		{
			set.add(crystal.getItemId());
			set.add(crystal.getNextItemId());
		}

		_itemIds = set.toArray();
	}
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;
		Player player = playable.getPlayer();

		if(player.getTarget() == null || !player.getTarget().isMonster())
		{
			player.sendPacket(SystemMsg.INVALID_TARGET, ActionFail.STATIC);
			return false;
		}

		if(player.isImmobilized() || player.isCastingNow())
		{
			player.sendActionFailed();
			return false;
		}

		MonsterInstance target = (MonsterInstance) player.getTarget();

		// u can use soul crystal only when target hp goes to <50%
		if(target.getCurrentHpPercents() >= 50)
		{
			player.sendPacket(SystemMsg.THE_SOUL_CRYSTAL_WAS_NOT_ABLE_TO_ABSORB_THE_SOUL, ActionFail.STATIC);
			return false;
		}

		// Soul Crystal Casting section
		int skillHitTime = SkillTable.getInstance().getSkillEntry(2096, 1).getTemplate().getHitTime();
		player.broadcastPacket(new MagicSkillUse(player, 2096, 1, skillHitTime, 0));
		player.sendPacket(new SetupGauge(player, SetupGauge.BLUE, skillHitTime));
		// End Soul Crystal Casting section

		// Continue execution later
		player._skillTask = ThreadPoolManager.getInstance().schedule(new CrystalFinalizer(player, target), skillHitTime);
		return true;
	}

	static class CrystalFinalizer extends RunnableImpl
	{
		private Player _activeChar;
		private MonsterInstance _target;

		CrystalFinalizer(Player activeChar, MonsterInstance target)
		{
			_activeChar = activeChar;
			_target = target;
		}

		@Override
		public void runImpl() throws Exception
		{
			_activeChar.sendActionFailed();
			_activeChar.clearCastVars();
			if(_activeChar.isDead() || _target.isDead())
				return;
			_target.addAbsorber(_activeChar);
		}
	}

	@Override
	public final int[] getItemIds()
	{
		return _itemIds;
	}
}