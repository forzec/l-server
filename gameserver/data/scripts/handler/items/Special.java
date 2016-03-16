package handler.items;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.instancemanager.QuestManager;
import org.mmocore.gameserver.instancemanager.ReflectionManager;
import org.mmocore.gameserver.model.GameObject;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Zone.ZoneType;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.l2.components.CustomMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;

import bosses.AntharasManager;
import bosses.ValakasManager;

public class Special extends SimpleItemHandler
{
	private static final int[] ITEM_IDS = new int[]{8060, 8556, 13853, 13808, 13809, 14835, 15537, 21899, 21900, 21901, 21902, 21903, 21904, 17268};

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}

	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		int itemId = item.getItemId();

		switch(itemId)
		{
			//Key of Enigma
			case 8060:
				return use8060(player, ctrl);
			//Dewdrop of Destruction
			case 8556:
				return use8556(player, ctrl);
			//DestroyedDarknessFragmentPowder -> DestroyedLightFragmentPowder
			case 13853:
				return use13853(player, ctrl);
			//Holy Water for SSQ 2nd quest
			case 13808:
				return use13808(player, ctrl);
			//Court Mag Staff for SSQ 2nd quest
			case 13809:
				return use13809(player, ctrl);
			case 14835:
				return use14835(player, ctrl);
			//Strongbox of Promise
			case 15537:
				return use15537(player, ctrl);
			case 21899:
				return use21899(player, ctrl);
			case 21900:
				return use21900(player, ctrl);
			case 21901:
				return use21901(player, ctrl);
			case 21902:
				return use21902(player, ctrl);
			case 21903:
				return use21903(player, ctrl);
			case 21904:
				return use21904(player, ctrl);
			//Antharas Blood Crystal
			case 17268:
				return use17268(player, ctrl);
			default:
				return false;
		}
	}

	//Key of Enigma
	private boolean use8060(Player player, boolean ctrl)
	{
		if(ItemFunctions.deleteItem(player, 8058, 1))
		{
			ItemFunctions.addItem(player, 8059, 1);
			return true;
		}
		return false;
	}

	//Dewdrop of Destruction
	private boolean use8556(Player player, boolean ctrl)
	{
		int[] npcs = {29048, 29049};

		GameObject t = player.getTarget();
		if(t == null || !t.isNpc() || !ArrayUtils.contains(npcs, ((NpcInstance) t).getNpcId()))
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(8556));
			return false;
		}
		if(player.getDistance(t) > 200)
		{
			player.sendPacket(new SystemMessage(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE));
			return false;
		}

		if(useItem(player, 8556, 1))
		{
			((NpcInstance) t).doDie(player);
			return true;
		}

		return false;
	}

	//DestroyedDarknessFragmentPowder -> DestroyedLightFragmentPowde
	private boolean use13853(Player player, boolean ctrl)
	{
		if(!player.isInZone(ZoneType.mother_tree))
		{
			player.sendPacket(SystemMsg.THERE_WAS_NOTHING_FOUND_INSIDE);
			return false;
		}

		if(useItem(player, 13853, 1))
		{
			ItemFunctions.addItem(player, 13854, 1);
			return true;
		}

		return false;
	}

	//Holy Water for SSQ 2nd quest
	private boolean use13808(Player player, boolean ctrl)
	{
		int[] allowedDoors = {17240101, 17240105, 17240109};

		GameObject target = player.getTarget();
		if(player.getDistance(target) > 150)
			return false;
		if(target != null && target.isDoor())
		{
			int _door = ((DoorInstance) target).getDoorId();
			if(ArrayUtils.contains(allowedDoors, _door))
				player.getReflection().openDoor(_door);
			else
			{
				player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
				return false;
			}
		}
		else
		{
			player.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}
		return true;
	}

	//Court Mag Staff for SSQ 2nd quest
	private boolean use13809(Player player, boolean ctrl)
	{
		int[] allowedDoors = {17240103, 17240107};

		GameObject target = player.getTarget();
		if(target != null && target.isDoor())
		{
			int _door = ((DoorInstance) target).getDoorId();
			if(ArrayUtils.contains(allowedDoors, _door))
			{
				if(useItem(player, 13809, 1))
				{
					player.getReflection().openDoor(_door);
					return true;
				}
			}
			else
			{
				player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			}
		}
		else
		{
			player.sendPacket(SystemMsg.INVALID_TARGET);
		}

		return false;
	}

	private boolean use14835(Player player, boolean ctrl)
	{
		//TODO [G1ta0] добавить кучу других проверок на возможность телепорта
		if(player.isActionsDisabled() || player.isInOlympiadMode() || player.isInZone(ZoneType.no_escape))
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(14835));
			return false;
		}

		if(useItem(player, 14835, 1))
		{
			//Stakato nest entrance
			player.teleToLocation(89464, -44712, -2167, ReflectionManager.DEFAULT);
			return true;
		}

		return false;
	}

	//Strongbox of Promise
	private boolean use15537(Player player, boolean ctrl)
	{
		QuestState qs = player.getQuestState(464);
		if(player.getLevel() >= 82 && qs == null)
		{
			if(useItem(player, 15537, 1))
			{
				ItemFunctions.addItem(player, 15538, 1);
				Quest q = QuestManager.getQuest(464);
				QuestState st = q.newQuestState(player, Quest.CREATED);
				st.setState(Quest.STARTED);
				st.setCond(1);
				return true;
			}
		}
		else
		{
			player.sendMessage(new CustomMessage("Quest._464_Oath.QuestCannotBeTaken"));
		}

		return false;
	}

	//Totem
	private boolean use21899(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21899));
			return false;
		}
		Functions.spawn(Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()), 143);
		return true;
	}

	//Totem
	private boolean use21900(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21900));
			return false;
		}
		Functions.spawn(Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()), 144);
		return true;
	}

	//Totem
	private boolean use21901(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21901));
			return false;
		}
		Functions.spawn(Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()), 145);
		return true;
	}

	//Totem
	private boolean use21902(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21902));
			return false;
		}
		Functions.spawn(Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()), 146);
		return true;
	}

	// Refined Red Dragon Blood
	private boolean use21903(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21903));
			return false;
		}
		if(useItem(player, 21903, 1))
		{
			player.doCast(SkillTable.getInstance().getSkillEntry(22298, 1), player, false);
			return true;
		}
		return false;
	}

	// Refined Blue Dragon Blood
	private boolean use21904(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21904));
			return false;
		}
		if(useItem(player, 21904, 1))
		{
			player.doCast(SkillTable.getInstance().getSkillEntry(22299, 1), player, false);
			return true;
		}
		return false;
	}

	// Antharas Blood Crystal
	private boolean use17268(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(17268));
			return false;
		}
		if(useItem(player, 17268, 1))
		{
			player.doCast(SkillTable.getInstance().getSkillEntry(9179, 1), player, false);
			return true;
		}
		return false;
	}


	private static boolean useItem(Player player, int itemId, long count)
	{
		if(ItemFunctions.deleteItem(player, itemId, count))
		{
			player.sendPacket(new SystemMessage(SystemMsg.YOU_USE_S1).addItemName(itemId));
			return true;
		}
		return false;
	}
}
