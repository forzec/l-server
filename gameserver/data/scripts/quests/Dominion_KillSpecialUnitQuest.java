package quests;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage;

/**
 * @author VISTALL
 * @date 15:51/12.04.2011
 */
public abstract class Dominion_KillSpecialUnitQuest extends Quest
{
	private final ClassId[] _classIds;

	public Dominion_KillSpecialUnitQuest()
	{
		super(PARTY_ALL);

		_classIds = getTargetClassIds();
		DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
		for(ClassId c : _classIds)
			runnerEvent.addClassQuest(c, this);
	}

	protected abstract int getReward();

	protected abstract NpcString startNpcString();

	protected abstract NpcString progressNpcString();

	protected abstract NpcString doneNpcString();

	protected abstract int getRandomMin();

	protected abstract int getRandomMax();

	protected abstract ClassId[] getTargetClassIds();

	@Override
	public String onKill(Player killed, QuestState qs)
	{
		Player player = qs.getPlayer();
		if(player == null)
			return null;

		DominionSiegeEvent event1 = player.getEvent(DominionSiegeEvent.class);
		if(event1 == null)
			return null;
		DominionSiegeEvent event2 = killed.getEvent(DominionSiegeEvent.class);
		if(event2 == null || event2 == event1 || !event2.isInProgress())
			return null;

		if(!ArrayUtils.contains(_classIds, killed.getClassId()))
			return null;

		int max_kills = qs.getInt("max_kills");
		if(max_kills == 0)
		{
			qs.setState(STARTED);
			qs.setCond(1);

			max_kills = Rnd.get(getRandomMin(), getRandomMax());
			qs.set("max_kills", max_kills);
			qs.set("current_kills", 0);

			player.sendPacket(new ExShowScreenMessage(startNpcString(), 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, false, String.valueOf(max_kills)));
		}
		else
		{
			int current_kills = qs.getInt("current_kills") + 1;
			if(current_kills >= max_kills)
			{
				event1.addReward(player, DominionSiegeEvent.STATIC_BADGES, Math.round(getReward() * current_kills / 10f)); // TODO: DS: переделать награду, /10 должно быть в другом месте

				qs.setState(COMPLETED);

				final int level = player.getLevel();
				if (level >= 85)
					qs.addExpAndSp(587000, 59000);
				else if (level == 84)
					qs.addExpAndSp(582000, 59000);
				else if (level == 83)
					qs.addExpAndSp(576000, 58000);
				else if (level == 82)
					qs.addExpAndSp(570000, 58000);
				else if (level == 81)
					qs.addExpAndSp(565000, 57000);
				else if (level == 80)
					qs.addExpAndSp(559000, 57000);
				else if (level == 79)
					qs.addExpAndSp(555000, 56000);
				else if (level == 78)
					qs.addExpAndSp(551000, 56000);
				else if (level == 77)
					qs.addExpAndSp(548000, 55000);
				else if (level == 76)
					qs.addExpAndSp(545000, 55000);
				else if (level == 75)
					qs.addExpAndSp(543000, 54000);
				else if (level >= 70)
					qs.addExpAndSp(534000, 51000);
				else if (level >= 61)
					qs.addExpAndSp(505000, 45000);
				else if (level >= 55)
					qs.addExpAndSp(462000, 38000);
				else if (level >= 50)
					qs.addExpAndSp(413000, 32000);
				else if (level >= 45)
					qs.addExpAndSp(358000, 26000);
				else
					qs.addExpAndSp(301000, 20000);

				qs.exitCurrentQuest(false);

				qs.set(DominionSiegeEvent.DATE, String.valueOf(event1.getResidence().getSiegeDate().getTimeInMillis()));

				player.sendPacket(new ExShowScreenMessage(doneNpcString(), 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, false));
			}
			else
			{
				qs.set("current_kills", current_kills);
				player.sendPacket(new ExShowScreenMessage(progressNpcString(), 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, false, String.valueOf(max_kills), String.valueOf(current_kills)));
			}
		}

		return null;
	}

	@Override
	public boolean canAbortByPacket()
	{
		return false;
	}

	@Override
	public boolean isUnderLimit()
	{
		return true;
	}

	@Override
	public void onCreate(QuestState qs)
	{
		super.onCreate(qs);

		if(!qs.isCompleted())
			qs.addPlayerOnKillListener();
	}

	@Override
	public void onAbort(QuestState qs)
	{
		qs.removePlayerOnKillListener();
		super.onAbort(qs);
	}
}
