package ai.custom;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.scripts.Functions;

public class MutantChest extends Fighter
{

	public MutantChest(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		if(Rnd.chance(30))
			Functions.npcSay(actor, "Враги! Всюду враги! Все сюда, враги здесь!");

		actor.deleteMe();
	}
}