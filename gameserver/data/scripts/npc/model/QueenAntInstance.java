package npc.model;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Zone;
import org.mmocore.gameserver.model.base.SpecialEffectState;
import org.mmocore.gameserver.model.instances.BossInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage;
import org.mmocore.gameserver.network.l2.s2c.PlaySound;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.ReflectionUtils;


public class QueenAntInstance extends BossInstance
{
	private static final String ZONE = "[queen_ant_epic]";

	private volatile NpcInstance _minionLarva = null;

	public QueenAntInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	public NpcInstance getMinionLarva()
	{
		return _minionLarva;
	}

	@Override
	protected int getKilledInterval(NpcInstance minion)
	{
		return minion.getNpcId() == 29003 ? 10000 : 280000 + Rnd.get(40000);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);

		broadcastPacketToOthers(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, 0, getLoc()));
	}

	@Override
	protected void onDecay()
	{
		super.onDecay();

		if (_minionLarva != null)
			_minionLarva.decayOrDelete();
		_minionLarva = null;
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		final Zone zone = ReflectionUtils.getZone(ZONE);
		if (zone != null)
		{
			final int x, y, z;
			if (Rnd.get(100) < 33)
			{
				x = -19480 - 100;
				y = 187344 - 100;
				z = -5600;
			}
			else if (Rnd.get(100) < 50)
			{
				x = -17928 - 100;
				y = 180912 - 100;
				z = -5520;
			}
			else
			{
				x = -23808 - 100;
				y = 182368 - 100;
				z = -5600;
			}

			for (Playable p : zone.getInsidePlayables())
				p.teleToLocation(x + Rnd.get(200), y + Rnd.get(200), z);
		}

		if(_minionLarva == null)
		{
			_minionLarva = NpcUtils.spawnSingle(29002, new Location(-21600, 179482, -5846, Rnd.get(0, 0xFFFF)));
			if (_minionLarva != null)
			{
				_minionLarva.setUndying(SpecialEffectState.TRUE);
				_minionLarva.startImmobilized();
			}
		}

		broadcastPacketToOthers(new PlaySound(PlaySound.Type.MUSIC, "BS01_A", 1, 0, getLoc()));
	}

	@Override
	protected int getMinChannelSizeForLock()
	{
		return 36;
	}

	@Override
	protected void onChannelLock(String leaderName)
	{
		broadcastPacket(new ExShowScreenMessage(NpcString.QUEEN_ANT_S1_COMMAND_CHANNEL_HAS_LOOTING_RIGHTS, 4000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, false, leaderName));		
	}

	@Override
	protected void onChannelUnlock()
	{
		broadcastPacket(new ExShowScreenMessage(NpcString.QUEEN_ANT_LOOTING_RULES_ARE_NO_LONGER_ACTIVE, 4000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, false));
	}
}