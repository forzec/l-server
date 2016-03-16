package npc.model;

import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * Данный инстанс используется мобами, которых нельзя убить, но нужно, чтобы на них действовали дебафы в onEvtSeeSpell
 * @author n0nam3
 */
public final class MobInvulInstance extends MonsterInstance
{
	public MobInvulInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{}

	@Override
	public void showChatWindow(Player player, String filename, Object... replace)
	{}

	@Override
	public void reduceCurrentHp(double i, Creature attacker, SkillEntry skill, int poleHitCount, boolean crit,  boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage)
	{}

	@Override
	public boolean isInvul()
	{
		return false;
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}
}