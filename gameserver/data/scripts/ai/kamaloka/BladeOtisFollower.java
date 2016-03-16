package ai.kamaloka;

import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;

/**
 * Минион босса 43й камалоки
 *
 * @author pchayka
 */
public class BladeOtisFollower extends Mystic
{
	private NpcInstance actor = getActor();
	private long _skillTimer = 0L;
	private final static long _skillInterval = 20000L;
	private Reflection r = actor.getReflection();
	final SkillEntry s_heal_boss_a_2a_6 = SkillTable.getInstance().getSkillEntry(4209, 6);  // s_heal_boss_a_2a_6

	public BladeOtisFollower(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		if(_skillTimer == 0)
			_skillTimer = System.currentTimeMillis();
		if(!r.isDefault() && _skillTimer + _skillInterval < System.currentTimeMillis())
		{
			NpcInstance boss = null;
			for(NpcInstance npc : r.getNpcs())
				if(npc.getNpcId() == 18562)
					boss = npc;

			if(boss != null)
			{
				actor.doCast(s_heal_boss_a_2a_6, boss, true);
				_skillTimer = System.currentTimeMillis();
				setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				addTaskMove(Location.findAroundPosition(actor, 400), false);
				ChatUtils.say(actor, NpcString.THERES_NOT_MUCH_I_CAN_DO_BUT_I_WANT_TO_HELP_YOU);
			}
		}
		return super.thinkActive();
	}

	@Override
	protected void thinkAttack()
	{
		// do not attack
		setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}
}