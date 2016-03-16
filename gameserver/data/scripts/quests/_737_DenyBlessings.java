package quests;

import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.network.l2.components.NpcString;

/**
 * @author VISTALL
 * @date 16:19/12.04.2011
 */
public class _737_DenyBlessings extends Dominion_KillSpecialUnitQuest
{
	public _737_DenyBlessings()
	{
		super();
	}

	@Override
	protected int getReward()
	{
		return 8;
	}

	@Override
	protected NpcString startNpcString()
	{
		return NpcString.DEFEAT_S1_HEALERS_AND_BUFFERS;
	}

	@Override
	protected NpcString progressNpcString()
	{
		return NpcString.YOU_HAVE_DEFEATED_S2_OF_S1_HEALERS_AND_BUFFERS;
	}

	@Override
	protected NpcString doneNpcString()
	{
		return NpcString.YOU_HAVE_WEAKENED_THE_ENEMYS_SUPPORT;
	}

	@Override
	protected int getRandomMin()
	{
		return 5;
	}

	@Override
	protected int getRandomMax()
	{
		return 10;
	}

	@Override
	protected ClassId[] getTargetClassIds()
	{
		return new ClassId[]
		{
				ClassId.bishop,
				ClassId.prophet,
				ClassId.elder,
				ClassId.shillienElder,
				ClassId.warcryer,
				ClassId.cardinal,
				ClassId.hierophant,
				ClassId.evaSaint,
				ClassId.shillienSaint,
				ClassId.doomcryer
		};
	}
}
