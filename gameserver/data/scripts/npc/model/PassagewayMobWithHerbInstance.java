package npc.model;

import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * Моб при смерти дропает херб "Fiery Demon Blood"
 * @author SYS
 */
public final class PassagewayMobWithHerbInstance extends MonsterInstance
{
	public PassagewayMobWithHerbInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	public static final int FieryDemonBloodHerb = 9849;

	@Override
	public void calculateRewards(Creature lastAttacker)
	{
		if(lastAttacker == null)
			return;

		super.calculateRewards(lastAttacker);

		if(lastAttacker.isPlayable())
			dropItem(lastAttacker.getPlayer(), FieryDemonBloodHerb, 1, false);
	}
}