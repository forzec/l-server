package handler.usercommands;

import org.mmocore.gameserver.handler.usercommands.IUserCommandHandler;
import org.mmocore.gameserver.handler.usercommands.UserCommandHandler;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;

/**
 * @author VISTALL
 * @date 16:53/24.06.2011
 */
public abstract class ScriptUserCommand implements IUserCommandHandler, OnInitScriptListener
{
	@Override
	public void onInit()
	{
		UserCommandHandler.getInstance().registerUserCommandHandler(this);
	}
}