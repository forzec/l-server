package npc.model;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.instances.BossInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage;
import org.mmocore.gameserver.network.l2.s2c.PlaySound;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;

public class OrfenInstance extends BossInstance
{
	public static final Location nest = new Location(43728, 17220, -4342);

	public static final Location[] locs = new Location[] {
		new Location(55024, 17368, -5412),
		new Location(53504, 21248, -5496),
		new Location(53248, 24576, -5272) };

	public OrfenInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void setTeleported(boolean flag)
	{
		super.setTeleported(flag);
		Location loc = flag ? nest : locs[Rnd.get(locs.length)];
		setSpawnedLoc(loc);
		getAggroList().clear(true);
		getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
		teleToLocation(loc);
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();
		setTeleported(false);
		broadcastPacketToOthers(new PlaySound(PlaySound.Type.MUSIC, "BS01_A", 1, 0, getLoc()));
	}

	@Override
	protected void onDeath(Creature killer)
	{
		broadcastPacketToOthers(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, 0, getLoc()));
		super.onDeath(killer);
	}

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, SkillEntry skill, int poleHitCount, boolean crit, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage)
	{
		super.reduceCurrentHp(damage, attacker, skill, poleHitCount, crit, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
		if(!isTeleported() && getCurrentHpPercents() <= 50)
			setTeleported(true);
	}

	@Override
	protected int getMinChannelSizeForLock()
	{
		return 36;
	}

	@Override
	protected void onChannelLock(String leaderName)
	{
		broadcastPacket(new ExShowScreenMessage(NpcString.ORPHEN_S1_COMMAND_CHANNEL_HAS_LOOTING_RIGHTS, 4000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, false, leaderName));		
	}

	@Override
	protected void onChannelUnlock()
	{
		broadcastPacket(new ExShowScreenMessage(NpcString.ORPHEN_LOOTING_RULES_ARE_NO_LONGER_ACTIVE, 4000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, false));
	}
}