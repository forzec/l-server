package npc.model.residences.clanhall;

import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 13:29/20.06.2011
 */
public class AuctionedManagerInstance extends ManagerInstance
{
	public AuctionedManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	protected void setDialogs()
	{
		_mainDialog = getTemplate().getAIParams().getString("main_dialog", "residence2/clanhall/black001.htm");
		_failDialog = getTemplate().getAIParams().getString("fail_dialog", "residence2/clanhall/black002.htm");
	}

	@Override
	protected int getCond(Player player)
	{
		Residence residence = getResidence();
		Clan residenceOwner = residence.getOwner();
		if(residenceOwner != null && player.getClan() == residenceOwner)
			return COND_OWNER;
		else
			return COND_FAIL;
	}
}
