package services;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.utils.Location;

/**
 * Используется для телепорта на Fantasy Isle и обратно.
 *
 * @author SYS
 * @date 01/07/2008
 */
public class TeleToFantasyIsle
{
	public static final Location[] POINTS = {
			new Location(-60695, -56896, -2032),
			new Location(-59716, -55920, -2032),
			new Location(-58752, -56896, -2032),
			new Location(-59716, -57864, -2032) };

	@Bypass("services.TeleToFantasyIsle:toFantasyIsle")
	public void toFantasyIsle(Player player, NpcInstance npc, String[] arg)
	{
		if(!NpcInstance.canBypassCheck(player, npc))
			return;

		player.setVar("backCoords", player.getLoc().toXYZString(), -1);
		player.teleToLocation(POINTS[Rnd.get(POINTS.length)]);
	}

	@Bypass("services.TeleToFantasyIsle:fromFantasyIsle")
	public void fromFantasyIsle(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null || npc == null)
			return;

		if(!NpcInstance.canBypassCheck(player, npc))
			return;

		String var = player.getVar("backCoords");
		if(var == null || var.equals(""))
		{
			teleOut(player, npc, arg);
			return;
		}
		player.teleToLocation(Location.parseLoc(var));
	}

	@Bypass("services.TeleToFantasyIsle:teleOut")
	public void teleOut(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null || npc == null)
			return;

		player.teleToLocation(-44316, -113136, -80); //Orc Village
		Functions.show(player.isLangRus() ? "Я не знаю, как Вы попали сюда, но я могу Вас отправить в ближайший город." : "I don't know from where you came here, but I can teleport you the nearest town.", player, npc);
	}
}