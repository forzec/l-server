package events;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.actions.StartStopAction;
import org.mmocore.gameserver.utils.TimeUtils;

/**
 * @author VISTALL
 * @date 17:31/07.04.2012
 */
public class FunEvent extends Event
{
	// times
	protected final Calendar _startPattern, _stopPattern;
	protected long _startTime;

	private final MultiValueSet<String> _parameters;

	private boolean _isInProgress;

	public FunEvent(MultiValueSet<String> set)
	{
		super(set);

		_startPattern = getCalendar(set.getString("start-pattern"));
		_stopPattern = getCalendar(set.getString("stop-pattern"));
		_parameters = set;
	}

	private static Calendar getCalendar(String d)
	{
		Calendar calendar = Calendar.getInstance();

		try
		{
			Date date = TimeUtils.SIMPLE_FORMAT.parse(d);
			calendar.setTimeInMillis(date.getTime());
		}
		catch(ParseException e)
		{
			throw new Error(e);
		}

		return calendar;
	}

	@Override
	public void startEvent()
	{
		_isInProgress = true;

		super.startEvent();
	}

	@Override
	public void stopEvent()
	{
		forceStopEvent();

		reCalcNextTime(false);
	}

	public void forceStopEvent()
	{
		super.stopEvent();

		_isInProgress = false;
	}

	@Override
	public void printInfo()
	{
		final long startSiegeMillis = startTimeMillis();

		if(startSiegeMillis == 0)
			info(getName() + " time - undefined");
		else
			info(getName() + " time - " + TimeUtils.toSimpleFormat(startSiegeMillis) + ", stop - " + TimeUtils.toSimpleFormat(_stopPattern.getTimeInMillis()));
	}

	@Override
	public void reCalcNextTime(boolean onInit)
	{
		_onTimeActions.clear();

		if(_stopPattern.getTimeInMillis() > System.currentTimeMillis())
		{
			_startTime = _startPattern.getTimeInMillis();
			long dff = _stopPattern.getTimeInMillis() - _startPattern.getTimeInMillis();

			addOnTimeAction(0, new StartStopAction(StartStopAction.EVENT, true));
			addOnTimeAction((int)(dff / 1000L), new StartStopAction(StartStopAction.EVENT, false));

			registerActions();
		}
	}

	@Override
	public EventType getType()
	{
		return EventType.FUN_EVENT;
	}

	@Override
	protected long startTimeMillis()
	{
		return _startTime;
	}

	@Override
	public boolean isInProgress()
	{
		return _isInProgress;
	}

	public MultiValueSet<String> getParameters()
	{
		return _parameters;
	}
}
