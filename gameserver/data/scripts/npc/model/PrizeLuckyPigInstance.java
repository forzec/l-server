package npc.model;

import java.util.concurrent.Future;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.GameObjectTasks;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * @author VISTALL
 * @date 23:34/28.04.2012
 */
public class PrizeLuckyPigInstance extends MonsterInstance
{
	private static final int ItemName_52_A = 14678;
	private static final int ItemName_52_B = 8755;
	private static final int ItemName_70_A = 14679;
	private static final int ItemName_70_B_1 = 5577;
	private static final int ItemName_70_B_2 = 5578;
	private static final int ItemName_70_B_3 = 5579;
	private static final int ItemName_80_A = 14680;
	private static final int ItemName_80_B_1 = 9552;
	private static final int ItemName_80_B_2 = 9553;
	private static final int ItemName_80_B_3 = 9554;
	private static final int ItemName_80_B_4 = 9555;
	private static final int ItemName_80_B_5 = 9556;
	private static final int ItemName_80_B_6 = 9557;

	private int _pickCount;
	private int _pigLevel;

	private int _temp;

	private Future<?> _task;

	public PrizeLuckyPigInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void calculateRewards(Creature lastAttacker)
	{
		if(!(lastAttacker instanceof Playable))
			return;

		_task.cancel(false);

		switch(_pigLevel)
		{
			case 52:
				if(_pickCount >= 5)
					dropItem(lastAttacker.getPlayer(), ItemName_52_A, 1, false);
				else if(_pickCount >= 2 && _pickCount < 5)
					dropItem(lastAttacker.getPlayer(), ItemName_52_B, 2, false);
				else
					dropItem(lastAttacker.getPlayer(), ItemName_52_B, 1, false);
				break;
			case 70:
				if(_pickCount >= 5)
					dropItem(lastAttacker.getPlayer(), ItemName_70_A, 1, false);
				else if(_pickCount >= 2 && _pickCount < 5)
				{
					if(_temp == 2)
						dropItem(lastAttacker.getPlayer(), ItemName_70_B_1, 2, false);
					else if(_temp == 1)
						dropItem(lastAttacker.getPlayer(), ItemName_70_B_2, 2, false);
					else
						dropItem(lastAttacker.getPlayer(), ItemName_70_B_3, 2, false);
				}
				else
				{
					if(_temp == 2)
						dropItem(lastAttacker.getPlayer(), ItemName_70_B_1, 1, false);
					else if(_temp == 1)
						dropItem(lastAttacker.getPlayer(), ItemName_70_B_2, 1, false);
					else
						dropItem(lastAttacker.getPlayer(), ItemName_70_B_3, 1, false);
				}
				break;
			case 80:
				if(_pickCount >= 5)
					dropItem(lastAttacker.getPlayer(), ItemName_80_A, 1, false);
				else if(_pickCount >= 2 && _pickCount < 5)
				{
					if(_temp == 5)
						dropItem(lastAttacker.getPlayer(), ItemName_80_B_1, 2, false);
					else if(_temp == 4)
						dropItem(lastAttacker.getPlayer(), ItemName_80_B_2, 2, false);
					else if(_temp == 3)
						dropItem(lastAttacker.getPlayer(), ItemName_80_B_3, 2, false);
					else if(_temp == 2)
						dropItem(lastAttacker.getPlayer(), ItemName_80_B_4, 2, false);
					else if(_temp == 1)
						dropItem(lastAttacker.getPlayer(), ItemName_80_B_5, 2, false);
					else
						dropItem(lastAttacker.getPlayer(), ItemName_80_B_6, 2, false);
				}
				else
				{
					if(_temp == 5)
						dropItem(lastAttacker.getPlayer(), ItemName_80_B_1, 1, false);
					else if(_temp == 4)
						dropItem(lastAttacker.getPlayer(), ItemName_80_B_2, 1, false);
					else if(_temp == 3)
						dropItem(lastAttacker.getPlayer(), ItemName_80_B_3, 1, false);
					else if(_temp == 2)
						dropItem(lastAttacker.getPlayer(), ItemName_80_B_4, 1, false);
					else if(_temp == 1)
						dropItem(lastAttacker.getPlayer(), ItemName_80_B_5, 1, false);
					else
						dropItem(lastAttacker.getPlayer(), ItemName_80_B_6, 1, false);
				}
				break;
		}
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();

		_temp = Rnd.get(0, 5);

		if(_temp == 0)
			ChatUtils.say(this, NpcString.OH_MY_WINGS_DISAPPEARED_ARE_YOU_GONNA_HIT_ME_IF_YOU_HIT_ME_ILL_THROW_UP_EVERYTHING_THAT_I_ATE);
		else
			ChatUtils.say(this, NpcString.OH_MY_WINGS_ACK_ARE_YOU_GONNA_HIT_ME_SCARY_SCARY_IF_YOU_HIT_ME_SOMETHING_BAD_IS_GOING_HAPPEN);

		_task = ThreadPoolManager.getInstance().schedule(new GameObjectTasks.DeleteTask(this), 600000L);
	}

	public void setPickCount(int pickCount)
	{
		_pickCount = pickCount;
	}

	public void setPigLevel(int pigLevel)
	{
		_pigLevel = pigLevel;
	}
}
