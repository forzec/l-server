package npc.model;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public final class GvGBossInstance extends MonsterInstance
{
	public GvGBossInstance(int objectId, NpcTemplate template)
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
	public boolean canChampion()
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