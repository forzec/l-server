package handler.bbs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.actor.instances.player.Friend;
import org.mmocore.gameserver.network.l2.s2c.ShowBoard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DonateTest extends ScriptBbsHandler {
		private static final Logger _log = LoggerFactory.getLogger(DonateTest.class);

	@Override
	public String[] getBypassCommands()
	{
		return new String[] { 
			"_donatetest" 
		};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		String html = "";
		if(cmd.equals("donatetest"))
		{
			html = HtmCache.getInstance().getHtml("scripts/services/community/bbs_donate.htm", player);
		}
		ShowBoard.separateAndSend(html, player);
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
		
	}
}