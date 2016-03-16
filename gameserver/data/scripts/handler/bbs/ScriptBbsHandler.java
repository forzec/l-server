package handler.bbs;

import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.handler.bbs.BbsHandlerHolder;
import org.mmocore.gameserver.handler.bbs.IBbsHandler;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;

/**
 * @author VISTALL
 * @date 2:17/19.08.2011
 */
public abstract class ScriptBbsHandler implements OnInitScriptListener, IBbsHandler
{
	@Override
	public void onInit()
	{
		if(Config.COMMUNITYBOARD_ENABLED)
			BbsHandlerHolder.getInstance().registerHandler(this);
	}
}
