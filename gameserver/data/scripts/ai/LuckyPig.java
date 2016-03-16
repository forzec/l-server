package ai;

import java.util.List;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.GameObject;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.network.l2.s2c.GetItem;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.ChatUtils;
import npc.model.LuckyPigInstance;

/**
 * @author VISTALL
 * @date 14:53/25.04.2012
 */
public class LuckyPig extends DefaultAI
{
	private int _targetItemObjectId;
	private long _lastSay = 0;

	private int _displayId;

	public LuckyPig(NpcInstance actor)
	{
		super(actor);

		AI_TASK_ATTACK_DELAY = AI_TASK_ACTIVE_DELAY = AI_TASK_DELAY_CURRENT = 500L;
	}

	@Override
	protected void onEvtThink()
	{
		LuckyPigInstance actor = getActor();
		if(_lastSay < System.currentTimeMillis())
		{
			ChatUtils.say(actor, NpcString.LUCKPY_I_WANNA_EAT_ADENA);
			_lastSay = System.currentTimeMillis() + 10000L;
		}

		if(actor.isMoving)
			return;

		List<GameObject> item = World.getAroundObjects(actor, 500, 100);
		for(GameObject gameObject : item)
			if(gameObject.isItem() && ((ItemInstance)gameObject).getItemId() == ItemTemplate.ITEM_ID_ADENA && ((ItemInstance) gameObject).getCount() < 99)
			{
				if(actor.getPickCount() == 10)
				{
					actor.meFull();
					return;
				}

				_targetItemObjectId = gameObject.getObjectId();

				actor.moveToLocation(gameObject.getLoc(), 10, true);
				return;
			}
			else if(gameObject.isPlayer())
				actor.moveToLocation(gameObject.getLoc(), 100, true);

		randomWalk();
	}

	@Override
	protected void onEvtArrived()
	{
		LuckyPigInstance actor = getActor();
		if(_targetItemObjectId > 0)
		{
			GameObject gameObject = World.getAroundObjectById(actor, _targetItemObjectId);
			if(gameObject == null || !gameObject.isItem())
			{
				_targetItemObjectId = 0;
				return;
			}

			ItemInstance item = (ItemInstance)gameObject;

			long count = item.getCount();
			pick(item);
			if(count < 50)
			{
				actor.altUseSkill(SkillTable.getInstance().getSkillEntry(5758, 1), actor);  //s_display_jackpot_firework
				ChatUtils.say(actor, NpcString.YUMMY);

				actor.incPickCount();

				if(actor.getPickCount() >= 2 && actor.getPickCount() < 5)
				{
					if(_displayId == 0)
					{
						actor.altUseSkill(SkillTable.getInstance().getSkillEntry(23325, 1), actor); //s_g_display_luckpi_a
						_displayId ++;
					}

				}
				else if(actor.getPickCount() >= 5)
				{
					if(_displayId == 1)
					{
						actor.altUseSkill(SkillTable.getInstance().getSkillEntry(23325, 1), actor); //s_g_display_luckpi_a
						_displayId ++;
					}
				}
			}
			else if(count < 99)
			{
				actor.altUseSkill(SkillTable.getInstance().getSkillEntry(6037, 1), actor);  //s_dispaly_soul_unleash1
				ChatUtils.say(actor, NpcString.GRRRR);

				actor.decPickCount();

				if(actor.getPickCount() >= 2 && actor.getPickCount() < 5)
				{
					if(_displayId == 2)
					{
						actor.altUseSkill(SkillTable.getInstance().getSkillEntry(23326, 1), actor); //s_g_display_luckpi_b
						_displayId --;
					}

				}
				else if(actor.getPickCount() >= 5)
					_displayId ++;
				else if(_displayId == 1)
					actor.altUseSkill(SkillTable.getInstance().getSkillEntry(23326, 1), actor); //s_g_display_luckpi_b
			}
		}
	}

	private void pick(ItemInstance item)
	{
		LuckyPigInstance actor = getActor();

		actor.broadcastPacket(new GetItem(item, actor.getObjectId()));

		item.deleteMe();
		item.delete();

		_targetItemObjectId = 0;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{}

	@Override
	protected void onEvtAggression(Creature attacker, int aggro)
	{}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	public LuckyPigInstance getActor()
	{
		return (LuckyPigInstance) super.getActor();
	}
}
