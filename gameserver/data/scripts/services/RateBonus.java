package services;

import java.util.Date;

import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.dao.AccountBonusDAO;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.data.xml.holder.ItemHolder;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.actor.instances.player.Bonus;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.BonusRequest;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.ExBR_PremiumState;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;

public class RateBonus
{
	@Bypass("services.RateBonus:list")
	public void list(Player player, NpcInstance npc, String[] arg)
	{
		if(Config.SERVICES_RATE_TYPE == Bonus.NO_BONUS)
		{
			player.sendPacket(new HtmlMessage(5).setFile("npcdefault.htm"));
			return;
		}

		String html;
		if(player.getNetConnection().getBonus() >= 1.)
		{
			int endtime = player.getNetConnection().getBonusExpire();
			if(endtime >= System.currentTimeMillis() / 1000L)
				html = HtmCache.getInstance().getHtml("scripts/services/RateBonusAlready.htm", player).replaceFirst("endtime", new Date(endtime * 1000L).toString());
			else
			{
				html = HtmCache.getInstance().getHtml("scripts/services/RateBonus.htm", player);

				String add = "";
				for(int i = 0; i < Config.SERVICES_RATE_BONUS_DAYS.length; i++)
					add += "<a action=\"bypass -h htmbypass_services.RateBonus:get " + i + "\">" //
							+ (int) (Config.SERVICES_RATE_BONUS_VALUE[i] * 100 - 100) + //
							"% for " + Config.SERVICES_RATE_BONUS_DAYS[i] + //
							" days - " + Config.SERVICES_RATE_BONUS_PRICE[i] + //
							" " + ItemHolder.getInstance().getTemplate(Config.SERVICES_RATE_BONUS_ITEM[i]).getName() + "</a><br>";

				html = html.replaceFirst("%toreplace%", add);
			}
		}
		else
			html = HtmCache.getInstance().getHtml("scripts/services/RateBonusNo.htm", player);

		Functions.show(html, player, npc);
	}

	@Bypass("services.RateBonus:get")
	public void get(Player player, NpcInstance npc, String[] arg)
	{
		if (arg == null || arg.length < 1)
			return;

		if(Config.SERVICES_RATE_TYPE == Bonus.NO_BONUS)
		{
			player.sendPacket(new HtmlMessage(5).setFile("npcdefault.htm"));
			return;
		}

		int i = Integer.parseInt(arg[0]);

		if(!ItemFunctions.deleteItem(player, Config.SERVICES_RATE_BONUS_ITEM[i], Config.SERVICES_RATE_BONUS_PRICE[i]))
		{
			if(Config.SERVICES_RATE_BONUS_ITEM[i] == ItemTemplate.ITEM_ID_ADENA)
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return;
		}

		if(Config.SERVICES_RATE_TYPE == Bonus.BONUS_GLOBAL_ON_AUTHSERVER && AuthServerCommunication.getInstance().isShutdown())
		{
			list(player, npc, arg);
			return;
		}

		Log.add(player.getName() + "|" + player.getObjectId() + "|rate bonus|" + Config.SERVICES_RATE_BONUS_VALUE[i] + "|" + Config.SERVICES_RATE_BONUS_DAYS[i] + "|", "services");

		double bonus = Config.SERVICES_RATE_BONUS_VALUE[i];
		int bonusExpire = (int) (System.currentTimeMillis() / 1000L) + Config.SERVICES_RATE_BONUS_DAYS[i] * 24 * 60 * 60;

		switch(Config.SERVICES_RATE_TYPE)
		{
			case Bonus.BONUS_GLOBAL_ON_AUTHSERVER:
				AuthServerCommunication.getInstance().sendPacket(new BonusRequest(player.getAccountName(), bonus, bonusExpire));
				break;
			case Bonus.BONUS_GLOBAL_ON_GAMESERVER:
				AccountBonusDAO.getInstance().insert(player.getAccountName(), bonus, bonusExpire);
				break;
		}

		player.getNetConnection().setBonus(bonus);
		player.getNetConnection().setBonusExpire(bonusExpire);

		player.stopBonusTask();
		player.startBonusTask();

		player.updatePremiumItems();

		if(player.getParty() != null)
			player.getParty().recalculatePartyData();

		player.sendPacket(new ExBR_PremiumState(player, true));

		Functions.show(HtmCache.getInstance().getHtml("scripts/services/RateBonusGet.htm", player), player, npc);
	}
}