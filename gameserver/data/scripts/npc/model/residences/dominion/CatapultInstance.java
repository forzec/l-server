package npc.model.residences.dominion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.model.AggroList;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestEventType;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 6:11/07.06.2011
 */
public class CatapultInstance extends SiegeToggleNpcInstance
{
	public CatapultInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onDeathImpl(Creature lastAttacker)
	{
		DominionSiegeEvent siegeEvent = getEvent(DominionSiegeEvent.class);
		if(siegeEvent == null)
			return;

		getAI().notifyEvent(CtrlEvent.EVT_DEAD, lastAttacker);

		Player killer = lastAttacker.getPlayer();
		if(killer == null)
			return;

		Map<Playable, AggroList.HateInfo> aggroMap = getAggroList().getPlayableMap();

		Quest[] quests = getTemplate().getEventQuests(QuestEventType.MOB_KILLED_WITH_QUEST);
		if(quests != null && quests.length > 0)
		{
			List<Player> players = null; // массив с игроками, которые могут быть заинтересованы в квестах
			if(isRaid() && Config.ALT_NO_LASTHIT) // Для альта на ластхит берем всех игроков вокруг
			{
				players = new ArrayList<Player>(20);
				for(Playable pl : aggroMap.keySet())
					if(!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
						if (!players.contains(pl.getPlayer())) // не добавляем дважды если есть пет
							players.add(pl.getPlayer());
			}
			if(killer.getParty() != null) // если пати то собираем всех кто подходит
			{
				if (players == null)
					players = new ArrayList<Player>(killer.getParty().getMemberCount());
				for(Player pl : killer.getParty().getPartyMembers())
					if(!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
						if (!players.contains(pl.getPlayer())) // не добавляем дважды
							players.add(pl);
			}

			for(Quest quest : quests)
			{
				Player toReward = killer;
				if(quest.getParty() != Quest.PARTY_NONE && players != null)
					if(isRaid() || quest.getParty() == Quest.PARTY_ALL) // если цель рейд или квест для всей пати награждаем всех участников
					{
						for(Player pl : players)
						{
							QuestState qs = pl.getQuestState(quest);
							if(qs != null && !qs.isCompleted())
								quest.notifyKill(this, qs);
						}
						toReward = null;
					}
					else
					{ // иначе выбираем одного
						List<Player> interested = new ArrayList<Player>(players.size());
						for(Player pl : players)
						{
							QuestState qs = pl.getQuestState(quest);
							if(qs != null && !qs.isCompleted()) // из тех, у кого взят квест
								interested.add(pl);
						}

						if(interested.isEmpty())
							continue;

						toReward = interested.get(Rnd.get(interested.size()));
						if(toReward == null)
							toReward = killer;
					}

				if(toReward != null)
				{
					QuestState qs = toReward.getQuestState(quest);
					if(qs != null && !qs.isCompleted())
						quest.notifyKill(this, qs);
				}
				else if (quest.getParty() == Quest.PARTY_ANY && killer.getParty() != null) // этот тип квеста вызывается если он есть хотя бы у кого-то в пати
					for(Player pl : killer.getParty().getPartyMembers())
						if(!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
						{
							QuestState qs = pl.getQuestState(quest);
							if(qs != null && !qs.isCompleted())
							{
								quest.notifyKill(this, qs);
								break;
							}
						}
			}
		}
	}
}
