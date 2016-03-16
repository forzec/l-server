package npc.model.residences.clanhall;

import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.network.l2.s2c.AgitDecoInfo;
import org.mmocore.gameserver.network.l2.s2c.L2GameServerPacket;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import npc.model.residences.ResidenceManager;

public class ManagerInstance extends ResidenceManager
{
	public ManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	protected Residence getResidence()
	{
		return getClanHall();
	}

	@Override
	public L2GameServerPacket decoPacket()
	{
		ClanHall clanHall = getClanHall();
		if(clanHall != null)
			return new AgitDecoInfo(clanHall);
		else
			return null;
	}

	@Override
	protected int getPrivUseFunctions()
	{
		return Clan.CP_CH_USE_FUNCTIONS;
	}

	@Override
	protected int getPrivSetFunctions()
	{
		return Clan.CP_CH_SET_FUNCTIONS;
	}

	@Override
	protected int getPrivDismiss()
	{
		return Clan.CP_CH_DISMISS;
	}

	@Override
	protected int getPrivDoors()
	{
		return Clan.CP_CH_ENTRY_EXIT;
	}
}