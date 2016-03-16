package npc.model;

import java.util.StringTokenizer;

import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.base.Race;
import org.mmocore.gameserver.model.entity.Hero;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.s2c.ExHeroList;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author VISTALL
 * @date 17:17/01.09.2011
 */
public class OlympiadObeliskInstance extends NpcInstance
{
	private static final int[] ITEMS =
	{
		6611,
		6612,
		6613,
		6614,
		6615,
		6616,
		6617,
		6618,
		6619,
		6620,
		6621,
		9388,
		9389,
		9390
	};

	public OlympiadObeliskInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(checkForDominionWard(player))
			return;

		if(!Config.ENABLE_OLYMPIAD)
			return;

		StringTokenizer token = new StringTokenizer(command, " ");
		String actualCommand = token.nextToken();
		if(actualCommand.equals("becameHero"))
		{
			if(Hero.getInstance().isInactiveHero(player.getObjectId()))
			{
				Hero.getInstance().activateHero(player);
				showChatWindow(player, "olympiad/monument_give_hero.htm");
			}
			else
				showChatWindow(player, "olympiad/monument_dont_hero.htm");
		}
		else if(actualCommand.equals("heroList"))
		{
			player.sendPacket(new ExHeroList());
		}
		else if(actualCommand.equals("getCirclet"))
		{
			if(player.isHero())
			{
				if(ItemFunctions.getItemCount(player, 6842) != 0)
					showChatWindow(player, "olympiad/monument_circlet_have.htm");
				else
					ItemFunctions.addItem(player, 6842, 1); //Wings of Destiny Circlet
			}
			else
				showChatWindow(player, "olympiad/monument_circlet_no_hero.htm");
		}
		else if(actualCommand.equals("getItem"))
		{
			if(!player.isHero())
			{
				showChatWindow(player, "olympiad/monument_weapon_no_hero.htm");
			}
			else
			{
				for(int heroItem : ITEMS)
					if(ItemFunctions.getItemCount(player, heroItem) != 0)
					{
						showChatWindow(player, "olympiad/monument_weapon_have.htm");
						return;
					}

				int val = Integer.parseInt(token.nextToken());

				if (val < 11)
				{
					if (player.getRace() == Race.kamael)
						return;
				}
				else if (player.getRace() != Race.kamael)
					return;

				ItemFunctions.addItem(player, ITEMS[val], 1);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if(checkForDominionWard(player))
			return;

		String fileName = "olympiad/monument";
		if(player.isNoble())
			fileName += "_n";
		if(val > 0)
			fileName += "-" + val;
		fileName += ".htm";
		player.sendPacket(new HtmlMessage(this, fileName));
	}
}
