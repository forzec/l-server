package ai.dragonvalley;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * @author pchayka
 */
public class BleedingFly extends DragonRaid
{
	private int _spawnCount = 0;

	public BleedingFly(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void thinkAttack()
	{
		if(_spawnCount == 0 && _actor.getCurrentHpPercents() < 50)
		{
			_spawnCount++;
			spawnMinions();
		}
		else if(_spawnCount == 1 && _actor.getCurrentHpPercents() < 25)
		{
			_spawnCount++;
			spawnMinions();
			_actor.doCast(SkillTable.getInstance().getSkillEntry(6915, 3), _actor, false);
		}
		super.thinkAttack();
	}

	private void spawnMinions()
	{
		int count = 3 + Rnd.get(1, 3);
		_actor.doCast(SkillTable.getInstance().getSkillEntry(6832, 1), _actor, false);
		for(int i = 0; i < count; i++)
		{
			NpcInstance minion = NpcUtils.spawnSingle(25734, Location.findPointToStay(_actor, 250));
			minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getAttackTarget(), 5000);
		}
	}

	// Should be Mystic type
	@Override
	public int getRateDEBUFF()
	{
		return 5;
	}

	@Override
	public int getRateDAM()
	{
		return 80;
	}

}