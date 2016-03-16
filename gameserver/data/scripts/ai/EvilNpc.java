package ai;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

public class EvilNpc extends DefaultAI
{
	private long _lastAction;
	private static final String[] _txt = {
			"отстань!",
			"уймись!",
			"я тебе отомщу, потом будешь прощения просить!",
			"у тебя будут неприятности!",
			"я на тебя пожалуюсь, тебя арестуют!" };

	public EvilNpc(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();
		if(attacker == null || attacker.getPlayer() == null)
			return;

		// Ругаемся и кастуем скилл не чаще, чем раз в 3 секунды
		if(System.currentTimeMillis() - _lastAction > 3000)
		{
			int chance = Rnd.get(0, 100);
			if(chance < 2)
				attacker.getPlayer().setKarma(attacker.getPlayer().getKarma() + 5, true);
			else if(chance < 4)
				actor.doCast(SkillTable.getInstance().getSkillEntry(4578, 1), attacker, true); // Petrification
			else
				actor.doCast(SkillTable.getInstance().getSkillEntry(4185, 7), attacker, true); // Sleep

			Functions.npcSay(actor, attacker.getName() + ", " + _txt[Rnd.get(_txt.length)]);
			_lastAction = System.currentTimeMillis();
		}
	}
}