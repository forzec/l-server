package ai.isle_of_prayer;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.idfactory.IdFactory;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Party;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * AI моба-мага для Isle of Prayer.<br>
 * - Если атакован членом группы, состоящей более чем из 2х чаров, то спаунятся штрафные мобы Witch Warder ID: 18364, 18365, 18366 (случайным образом 2 штуки).
 * @author SYS
 */
public class IsleOfPrayerMystic extends Mystic
{
	private boolean _penaltyMobsNotSpawned = true;
	private static final int PENALTY_MOBS[] = { 18364, 18365, 18366 };
	private static final int YELLOW_CRYSTAL = 9593;
	private static final int GREEN_CRYSTAL = 9594;
	private static final int RED_CRYSTAL = 9596;

	public IsleOfPrayerMystic(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage)
	{
		NpcInstance actor = getActor();
		if(_penaltyMobsNotSpawned && attacker.getPlayer() != null && actor.getY() > 161800) // DS: жуткий хардкод, на оффе это параметр bSpawn=1 в спавне
		{
			Party party = attacker.getPlayer().getParty();
			if(party != null && party.getMemberCount() > 2)
			{
				_penaltyMobsNotSpawned = false;
				int count = party.getMemberCount() > 4 ? 4 : 2;
				for(int i = 0; i < count; i++)
					try
					{
						MonsterInstance npc = new MonsterInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(PENALTY_MOBS[Rnd.get(PENALTY_MOBS.length)]));
						npc.setSpawnedLoc(((MonsterInstance) actor).getRndMinionPosition());
						npc.setReflection(actor.getReflection());
						npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
						npc.spawnMe(npc.getSpawnedLoc());
						npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
			}
		}

		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_penaltyMobsNotSpawned = true;
		if (killer != null)
		{
			final Player player = killer.getPlayer();
			if (player != null)
			{
				final NpcInstance actor = getActor();
				switch (actor.getNpcId())
				{
					case 22261: // Seychelles
						if (Rnd.chance(12))
							actor.dropItem(player, GREEN_CRYSTAL, 1, false);
						break;
					case 22265: // Chrysocolla
						if (Rnd.chance(6))
							actor.dropItem(player, RED_CRYSTAL, 1, false);
						break;
					case 22260: // Kleopora
						if (Rnd.chance(23))
							actor.dropItem(player, YELLOW_CRYSTAL, 1, false);
						break;
					case 22262: // Naiad
						if (Rnd.chance(12))
							actor.dropItem(player, GREEN_CRYSTAL, 1, false);
						break;
					case 22264: // Castalia
						if (Rnd.chance(12))
							actor.dropItem(player, GREEN_CRYSTAL, 1, false);
						break;
					case 22266: // Pythia
						if (Rnd.chance(5))
							actor.dropItem(player, RED_CRYSTAL, 1, false);
						break;
					case 22257: // Island Guardian
						if (Rnd.chance(21))
							actor.dropItem(player, YELLOW_CRYSTAL, 1, false);
						break;
					case 22258: // White Sand Mirage
						if (Rnd.chance(22))
							actor.dropItem(player, YELLOW_CRYSTAL, 1, false);
						break;
				}
			}
		}
		super.onEvtDead(killer);
	}
}