package npc.model.residences.dominion;

import java.util.StringTokenizer;

import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.entity.residence.Dominion;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.s2c.ExShowDominionRegistry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

public class MercenaryCaptainInstance extends NpcInstance
{
	private static final int[][] PRODUCTS = {{4422,1,50},{4423,1,50},{4424,1,50},{14819,1,80}};

	public MercenaryCaptainInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		Dominion dominion = getDominion();
		int badgeId = 13676 + dominion.getId();

		if(command.equalsIgnoreCase("territory_register"))
			player.sendPacket(new ExShowDominionRegistry(player, dominion));
		else if(command.startsWith("certificate_multisell"))
		{
			StringTokenizer tokenizer = new StringTokenizer(command);
			tokenizer.nextToken();
			int certification = Integer.parseInt(tokenizer.nextToken());
			int multisell = Integer.parseInt(tokenizer.nextToken());

			if(player.getInventory().getCountOf(certification) > 0)
				MultiSellHolder.getInstance().SeparateAndSend(multisell, player, getObjectId(), getCastle().getTaxRate());
			else
				showChatWindow(player, 25);
		}
		else if(command.startsWith("BuyTW"))
		{
			int idx = Integer.parseInt(command.substring(6));
			if (idx < 0 || idx >= PRODUCTS.length)
				return;

			if(ItemFunctions.deleteItem(player, badgeId, PRODUCTS[idx][2]))
			{
				ItemFunctions.addItem(player, PRODUCTS[idx][0], PRODUCTS[idx][1]);
				showChatWindow(player, 7);
			}
			else
				showChatWindow(player, 6);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		if(player.getLevel() < 40 || player.getClassId().getLevel() <= 2)
			val = 26;
		else
		{
			Castle castle = getCastle();
			Dominion dominion = getDominion();

			if(castle.getOwner() != null && player.getClan() == castle.getOwner() || dominion.getLordObjectId() == player.getObjectId())
			{
				if(castle.getSiegeEvent().isInProgress() || dominion.getSiegeEvent().isInProgress())
					val = 21;
				else
					val = 7;
			}
			else if(castle.getSiegeEvent().isInProgress() || dominion.getSiegeEvent().isInProgress())
				val = 22;
		}

		if(val == 0)
			val = 1;
		return val > 9 ? "residence2/dominion/gludio_merc_captain0" + val + ".htm" : "residence2/dominion/gludio_merc_captain00" + val + ".htm";
	}
}