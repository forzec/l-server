package ai.freya;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author pchayka
 */

public class Glacier extends Fighter
{
	private long _buffTimer = 0;

	public Glacier(NpcInstance actor)
	{
		super(actor);
		actor.block();
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		getActor().setNpcState(1);
		ThreadPoolManager.getInstance().schedule(new Freeze(), 800);
		ThreadPoolManager.getInstance().schedule(new Despawn(), 120000L);
		setIntention(CtrlIntention.AI_INTENTION_ATTACK);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		for(Creature cha : _actor.getAroundCharacters(350, 100))
			if(cha.isPlayer())
				cha.altOnMagicUseTimer(cha, SkillTable.getInstance().getSkillEntry(6301, 1));
		super.onEvtDead(killer);
	}

	@Override
	protected void thinkAttack()
	{
		// Cold Air buff
		if(_buffTimer + 5000L < System.currentTimeMillis())
		{
			_buffTimer = System.currentTimeMillis();
			for(Creature cha : _actor.getAroundCharacters(200, 100))
				if(cha.isPlayer() && Rnd.chance(50))
					cha.altOnMagicUseTimer(cha, SkillTable.getInstance().getSkillEntry(6302, 1));
		}
		super.thinkAttack();
	}

	private class Freeze extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			getActor().setNpcState(2);
		}
	}

	private class Despawn extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			getActor().deleteMe();
		}
	}
}