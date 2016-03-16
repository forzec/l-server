package services;

import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.instancemanager.QuestManager;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.base.Race;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SkillList;
import org.mmocore.gameserver.utils.ItemFunctions;

public class NoblessSell
{
	@Bypass("services.NoblessSell:get")
	public void get(Player player, NpcInstance npc, String[] arg)
	{
		if(!Config.SERVICES_NOBLESS_SELL_ENABLED)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
			return;
		}

		if(player.isNoble())
			return;

		if(player.getSubLevel() < 75)
		{
			player.sendMessage("You must make sub class level 75 first.");
			return;
		}

		if(ItemFunctions.deleteItem(player, Config.SERVICES_NOBLESS_SELL_ITEM, Config.SERVICES_NOBLESS_SELL_PRICE))
		{
			makeSubQuests(player, npc, arg);
			becomeNoble(player, npc, arg);
		}
		else if(Config.SERVICES_NOBLESS_SELL_ITEM == 57)
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
	}

	@Bypass("services.NoblessSell:makeSubQuests")
	public void makeSubQuests(Player player, NpcInstance npc, String[] arg)
	{
		if(!Config.SERVICES_NOBLESS_SELL_ENABLED)
		{
			player.sendPacket(new HtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
			return;
		}

		Quest q = QuestManager.getQuest(234);
		QuestState qs = player.getQuestState(234);
		if(qs != null)
			qs.exitCurrentQuest(true);
		q.newQuestState(player, Quest.COMPLETED);

		if(player.getRace() == Race.kamael)
		{
			q = QuestManager.getQuest(236);
			qs = player.getQuestState(236);
			if(qs != null)
				qs.exitCurrentQuest(true);
			q.newQuestState(player, Quest.COMPLETED);
		}
		else
		{
			q = QuestManager.getQuest(235);
			qs = player.getQuestState(235);
			if(qs != null)
				qs.exitCurrentQuest(true);
			q.newQuestState(player, Quest.COMPLETED);
		}
	}

	@Bypass("services.NoblessSell:becomeNoble")
	public void becomeNoble(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null || player.isNoble())
			return;

		Olympiad.addNoble(player);
		player.setNoble(true);
		player.updatePledgeClass();
		player.updateNobleSkills();
		player.sendPacket(new SkillList(player));
		player.broadcastUserInfo(true);
	}
}