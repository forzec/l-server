package ai.moveroute;

import org.mmocore.gameserver.model.instances.NpcInstance;

/**
 * @author VISTALL
 * @date 14:23/25.10.2011
 */
public class Fighter extends MoveRoutDefaultAI
{
	public Fighter(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		return super.thinkActive() || defaultThinkBuff(2);
	}

	@Override
	protected boolean createNewTask()
	{
		return defaultFightTask();
	}

	@Override
	public int getRatePHYS()
	{
		return 30;
	}

	@Override
	public int getRateDOT()
	{
		return 20;
	}

	@Override
	public int getRateDEBUFF()
	{
		return 20;
	}

	@Override
	public int getRateDAM()
	{
		return 15;
	}

	@Override
	public int getRateSTUN()
	{
		return 30;
	}

	@Override
	public int getRateBUFF()
	{
		return 10;
	}

	@Override
	public int getRateHEAL()
	{
		return 20;
	}
}
