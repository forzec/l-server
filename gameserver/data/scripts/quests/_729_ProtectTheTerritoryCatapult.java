package quests;

import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import org.mmocore.gameserver.model.quest.Quest;

/**
 * @author VISTALL
 * @date 2:15/09.06.2011
 */
public class _729_ProtectTheTerritoryCatapult extends Quest
{
	public _729_ProtectTheTerritoryCatapult()
	{
		super(PARTY_NONE);
		DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
		runnerEvent.addBreakQuest(this);
	}

	@Override
	public boolean isUnderLimit()
	{
		return true;
	}
}
