package quests;

import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.network.l2.components.NpcString;

/**
 * @author VISTALL
 * @date 16:14/12.04.2011
 */
public class _735_MakeSpearsDull extends Dominion_KillSpecialUnitQuest
{
	public _735_MakeSpearsDull()
	{
		super();
	}

	@Override
	protected int getReward()
	{
		return 7;
	}

	@Override
	protected NpcString startNpcString()
	{
		return NpcString.DEFEAT_S1_WARRIORS_AND_ROGUES;
	}

	@Override
	protected NpcString progressNpcString()
	{
		return NpcString.YOU_HAVE_DEFEATED_S2_OF_S1_WARRIORS_AND_ROGUES;
	}

	@Override
	protected NpcString doneNpcString()
	{
		return NpcString.YOU_WEAKENED_THE_ENEMYS_ATTACK;
	}

	@Override
	protected int getRandomMin()
	{
		return 9;
	}

	@Override
	protected int getRandomMax()
	{
		return 18;
	}

	@Override
	protected ClassId[] getTargetClassIds()
	{
		return new ClassId[]
		{
				ClassId.gladiator,
				ClassId.warlord,
				ClassId.treasureHunter,
				ClassId.hawkeye,
				ClassId.swordSinger,
				ClassId.plainsWalker,
				ClassId.silverRanger,
				ClassId.bladedancer,
				ClassId.abyssWalker,
				ClassId.phantomRanger,
				ClassId.destroyer,
				ClassId.tyrant,
				ClassId.bountyHunter,
				ClassId.duelist,
				ClassId.dreadnought,
				ClassId.sagittarius,
				ClassId.adventurer,
				ClassId.swordMuse,
				ClassId.windRider,
				ClassId.moonlightSentinel,
				ClassId.spectralDancer,
				ClassId.ghostHunter,
				ClassId.ghostSentinel,
				ClassId.titan,
				ClassId.grandKhauatari,
				ClassId.fortuneSeeker,
				ClassId.berserker,
				ClassId.maleSoulbreaker,
				ClassId.femaleSoulbreaker,
				ClassId.arbalester,
				ClassId.doombringer,
				ClassId.maleSoulhound,
				ClassId.femaleSoulhound,
				ClassId.trickster,
				ClassId.inspector,
				ClassId.judicator
		};
	}
}
