package handler.admincommands;

import org.mmocore.gameserver.handler.admincommands.AdminCommandHandler;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;

/**
 * @author VISTALL
 * @date 22:57/08.04.2011
 */
public abstract class ScriptAdminCommand implements IAdminCommandHandler, OnInitScriptListener
{
	@Override
	public void onInit()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
	}
}
