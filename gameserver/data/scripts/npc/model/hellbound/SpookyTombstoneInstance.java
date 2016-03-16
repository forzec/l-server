package npc.model.hellbound;

import org.mmocore.gameserver.model.Party;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 13:50/11.06.2012
 */
public class SpookyTombstoneInstance extends NpcInstance
{
	private static final int[] ITEMS ={10427, 10428, 10429, 10430, 10431};
	private static final Location LOC = new Location(26612, 248567, -2856);

	public SpookyTombstoneInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equals("insert"))
		{
			Party party = player.getParty();
			if(party != null)
			{
				boolean[] cond = new boolean[ITEMS.length];
				for(Player member : party)
				{
					if(getDistance(member) > 300)
						continue;

					for(int i = 0; i < ITEMS.length; i++)
						if(member.getInventory().getCountOf(ITEMS[i]) > 0)
							cond[i] = true;
				}

				if(allTrue(cond))
					party.Teleport(LOC);
				else
				{
					boolean oneTrue = false;
					for(boolean  a : cond)
						if(a)
						{
							oneTrue = true;
							break;
						}

					showChatWindow(player, oneTrue ? 2 : 3);
				}
			}
			else
				showChatWindow(player, 3);
		}
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if(val == 0)
		{
			boolean find = false;
			for(int i : ITEMS)
				if(player.getInventory().getCountOf(i) > 0)
				{
					find = true;
					break;
				}

			if(find)
				super.showChatWindow(player, 0, arg);
			else
				super.showChatWindow(player, 1, arg);
		}
		else
			super.showChatWindow(player, val, arg);
	}

	private static boolean allTrue(boolean[] ar)
	{
		for(boolean a : ar)
			if(!a)
				return false;
		return true;
	}
}
