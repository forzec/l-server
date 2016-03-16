package ai.monasteryofsilence;

import org.mmocore.gameserver.ai.CharacterAI;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import events.MonasteryOfSilenceMiniGameEvent;

/**
 * @author VISTALL
 * @date 14:37/04.05.2012
 */
public class MinigameFurnace extends CharacterAI
{
	public MinigameFurnace(Creature actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSeeSpell(SkillEntry skill, Creature caster)
	{
		super.onEvtSeeSpell(skill, caster);

		if(skill.getId() == 9059)
		{
			NpcInstance actor = (NpcInstance)getActor();

			MonasteryOfSilenceMiniGameEvent event = getActor().getEvent(MonasteryOfSilenceMiniGameEvent.class);
			if(!event.isInProgress() || event.getPlayer() != caster)
				return;

			event.fireFurnace(actor);

			caster.setTarget(null);
		}
	}
}
