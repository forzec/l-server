package npc.model;

import org.mmocore.gameserver.instancemanager.ReflectionManager;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import instances.RimPailaka;

public class SeducedInvestigatorInstance extends MonsterInstance
{
	public SeducedInvestigatorInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setHasChatWindow(true);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if(((RimPailaka) getReflection()).getStage() < 4)
			player.sendPacket(new HtmlMessage(this, "common/seducedinvestigator.htm"));
		else
			player.sendPacket(new HtmlMessage(this, "common/seducedinvestigator_done.htm"));
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("leave"))
		{
			Reflection r = player.getReflection();
			if(r.getReturnLoc() != null)
				player.teleToLocation(r.getReturnLoc(), ReflectionManager.DEFAULT);
			else
				player.setReflection(ReflectionManager.DEFAULT);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		Player player = attacker.getPlayer();
		return player != null && !player.isPlayable();
	}

	@Override
	public boolean isMovementDisabled()
	{
		return true;
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
}