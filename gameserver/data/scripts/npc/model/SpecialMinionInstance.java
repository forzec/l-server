package npc.model;

import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author pchayka
 */
public final class SpecialMinionInstance extends MonsterInstance
{
	public SpecialMinionInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
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

	@Override
	public boolean canChampion()
	{
		return false;
	}

	@Override
	public void onRandomAnimation()
	{
		return;
	}

}