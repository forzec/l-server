package npc.model.events;

import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.base.SpecialEffectState;
import org.mmocore.gameserver.model.entity.events.impl.UndergroundColiseumBattleEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 16:27/15.04.2012
 */
public class UndergroundColiseumTowerInstance extends NpcInstance
{
	public UndergroundColiseumTowerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);

		setHasChatWindow(false);
		setUndying(SpecialEffectState.FALSE);
	}

	@Override
	public void onDeath(Creature killer)
	{
		super.onDeath(killer);

		UndergroundColiseumBattleEvent battleEvent = getEvent(UndergroundColiseumBattleEvent.class);
		if(battleEvent == null)
			return;

		battleEvent.cancelResurrects(getTeam());
	}

	@Override
	public boolean isAttackable(Creature attacker)
	{
		return attacker != null && attacker.getTeam() != TeamType.NONE && getTeam() != attacker.getTeam();
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return isAttackable(attacker);
	}
}
