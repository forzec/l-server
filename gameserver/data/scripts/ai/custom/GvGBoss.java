package ai.custom;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * AI Ekimus
 * @author pchayka
 */
public class GvGBoss extends Fighter
{
	boolean phrase1 = false;
	boolean phrase2 = false;
	boolean phrase3 = false;

	public GvGBoss(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();

		if(actor.getCurrentHpPercents() < 50 && phrase1 == false)
		{
			phrase1 = true;
			Functions.npcSay(actor, "Вам не удастся похитить сокровища Геральда!");
		}
		else if(actor.getCurrentHpPercents() < 30 && phrase2 == false)
		{
			phrase2 = true;
			Functions.npcSay(actor, "Я тебе череп проломлю!");
		}
		else if(actor.getCurrentHpPercents() < 5 && phrase3 == false)
		{
			phrase3 = true;
			Functions.npcSay(actor, "Вы все погибнете в страшных муках! Уничтожу!");
		}

		super.onEvtAttacked(attacker, skill, damage);
	}
}