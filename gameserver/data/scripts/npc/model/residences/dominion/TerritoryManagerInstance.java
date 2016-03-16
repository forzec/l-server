package npc.model.residences.dominion;

import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.instancemanager.QuestManager;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.base.Race;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.entity.residence.Dominion;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SkillList;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.HtmlUtils;
import org.mmocore.gameserver.utils.ItemFunctions;

public class TerritoryManagerInstance extends NpcInstance
{
	public TerritoryManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this) || !checkForTW(player))
			return;

		final Dominion dominion = getDominion();
		final DominionSiegeEvent siegeEvent = dominion.getSiegeEvent();

		int npcId = getNpcId();
		int badgeId = 13676 + dominion.getId();

		if(command.equalsIgnoreCase("buyspecial"))
		{
			if(ItemFunctions.getItemCount(player, badgeId) < 1)
				showChatWindow(player, 1);
			else
				MultiSellHolder.getInstance().SeparateAndSend(npcId, player, getObjectId(), 0);
		}
		else if(command.equalsIgnoreCase("buyNobless"))
		{
			if(player.isNoble())
			{
				//
				return; //TODO [VISTALL] неизвестно
			}
			if(player.consumeItem(badgeId, 100L))
			{
				Quest q = QuestManager.getQuest(234);
				QuestState qs = player.getQuestState(q);
				if(qs != null)
					qs.exitCurrentQuest(true);
				q.newQuestState(player, Quest.COMPLETED);

				if(player.getRace() == Race.kamael)
				{
					q = QuestManager.getQuest(236);
					qs = player.getQuestState(q);
					if(qs != null)
						qs.exitCurrentQuest(true);
					q.newQuestState(player, Quest.COMPLETED);
				}
				else
				{
					q = QuestManager.getQuest(235);
					qs = player.getQuestState(q);
					if(qs != null)
						qs.exitCurrentQuest(true);
					q.newQuestState(player, Quest.COMPLETED);
				}

				Olympiad.addNoble(player);
				player.setNoble(true);
				player.updatePledgeClass();
				player.updateNobleSkills();
				player.sendPacket(new SkillList(player));
				player.broadcastUserInfo(true);
			}
			else
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
		}
		else if(command.equalsIgnoreCase("calculate"))
		{
			if(!player.isQuestContinuationPossible(true))
				return;
			int[] rewards = siegeEvent.calculateReward(player);
			if(rewards == null || rewards[0] == 0)
			{
				showChatWindow(player, 4);
				return;
			}

			HtmlMessage html = new HtmlMessage(this, getHtmlPath(npcId, 5, player));
			html.replace("%territory%", HtmlUtils.htmlResidenceName(dominion.getId()));
			html.replace("%badges%", String.valueOf(rewards[0]));
			html.replace("%adena%", String.valueOf(rewards[1]));
			html.replace("%fame%", String.valueOf(rewards[2]));
			player.sendPacket(html);
		}
		else if(command.equalsIgnoreCase("recivelater"))
			showChatWindow(player, getHtmlPath(npcId, 6, player));
		else if(command.equalsIgnoreCase("recive"))
		{
			int[] rewards = siegeEvent.calculateReward(player);
			if(rewards == null || rewards[0] == 0)
			{
				showChatWindow(player, 4);
				return;
			}

			ItemFunctions.addItem(player, badgeId, rewards[0]);
			ItemFunctions.addItem(player, ItemTemplate.ITEM_ID_ADENA, rewards[1]);
			if(rewards[2] > 0)
				player.setFame(player.getFame() + rewards[2], "CalcBadges:" + dominion.getId());

			siegeEvent.clearReward(player.getObjectId());
			showChatWindow(player, 7);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		if(player.getLevel() < 40 || player.getClassId().getLevel() <= 2)
			val = 8;
		return val == 0 ? "residence2/dominion/TerritoryManager.htm" : "residence2/dominion/TerritoryManager-" + val + ".htm";
	}

	@Override
	public void showChatWindow(Player player, int val, Object... replace)
	{
		if (!checkForTW(player))
			return;

		super.showChatWindow(player, val, replace);
	}

	private boolean checkForTW(Player player)
	{
		final Dominion dominion = getDominion();
		final DominionSiegeEvent siegeEvent = dominion.getSiegeEvent();
		if (siegeEvent == null || !siegeEvent.isInProgress())
			return true;

		final DominionSiegeEvent playerEvent = player.getEvent(DominionSiegeEvent.class);
		if (playerEvent != null && playerEvent != siegeEvent)
			showChatWindow(player, "residence2/dominion/gludio_feud_manager_q0717_02.htm");
		else
			showChatWindow(player, "residence2/dominion/gludio_feud_manager_q0717_01.htm", "%territory%", HtmlUtils.htmlResidenceName(dominion.getId()));
		return false;
	}
}