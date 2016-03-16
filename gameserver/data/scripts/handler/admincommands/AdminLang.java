package handler.admincommands;

import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.utils.Language;

/**
 * @author VISTALL
 * @date 17:07/07.03.2012
 */
public class AdminLang extends ScriptAdminCommand
{
	static enum Command
	{
		admin_lang,
		admin_l
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Command command = (Command)comm;
		switch(command)
		{
			case admin_lang:
				activeChar.sendMessage("Lang: " + activeChar.getLanguage());
				break;
			case admin_l:
				if(activeChar.getLanguage() == Language.ENGLISH)
					activeChar.getNetConnection().setLanguage(Language.RUSSIAN);
				else
					activeChar.getNetConnection().setLanguage(Language.ENGLISH);
				activeChar.sendMessage("Lang: " + activeChar.getLanguage());
				break;
		}
		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Command.values();
	}
}
