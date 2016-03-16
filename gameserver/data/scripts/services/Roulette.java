package services;

import java.util.concurrent.atomic.AtomicLong;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.data.StringHolder;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.instancemanager.ServerVariables;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.Util;

public class Roulette implements OnInitScriptListener
{
	private static final String R = "red";
	private static final String B = "black";
	private static final String fst = "first";
	private static final String snd = "second";
	private static final String trd = "third";
	private static final String E = "even";
	private static final String O = "odd";
	private static final String L = "low";
	private static final String H = "high";
	private static final String Z = "zero";

	private static final String[][] Numbers = {
			// # Color Dozen Column Evenness Highness
			{ "0", Z, Z, Z, Z, Z },
			{ "1", R, fst, fst, O, L, },
			{ "2", B, fst, snd, E, L, },
			{ "3", R, fst, trd, O, L, },
			{ "4", B, fst, fst, E, L, },
			{ "5", R, fst, snd, O, L, },
			{ "6", B, fst, trd, E, L, },
			{ "7", R, fst, fst, O, L, },
			{ "8", B, fst, snd, E, L, },
			{ "9", R, fst, trd, O, L, },
			{ "10", B, fst, fst, E, L, },
			{ "11", B, fst, snd, O, L, },
			{ "12", R, fst, trd, E, L, },
			{ "13", B, snd, fst, O, L, },
			{ "14", R, snd, snd, E, L, },
			{ "15", B, snd, trd, O, L, },
			{ "16", R, snd, fst, E, L, },
			{ "17", B, snd, snd, O, L, },
			{ "18", R, snd, trd, E, L, },
			{ "19", R, snd, fst, O, H, },
			{ "20", B, snd, snd, E, H, },
			{ "21", R, snd, trd, O, H, },
			{ "22", B, snd, fst, E, H, },
			{ "23", R, snd, snd, O, H, },
			{ "24", B, snd, trd, E, H, },
			{ "25", R, trd, fst, O, H, },
			{ "26", B, trd, snd, E, H, },
			{ "27", R, trd, trd, O, H, },
			{ "28", B, trd, fst, E, H, },
			{ "29", B, trd, snd, O, H, },
			{ "30", R, trd, trd, E, H, },
			{ "31", B, trd, fst, O, H, },
			{ "32", R, trd, snd, E, H, },
			{ "33", B, trd, trd, O, H, },
			{ "34", R, trd, fst, E, H, },
			{ "35", B, trd, snd, O, H, },
			{ "36", R, trd, trd, E, H, }, };

	/*
	 * type это тип ставки, number это на что именно ставка
	 * type 1:
	 * Ставка на один номер (Straight Up), number соответствует номеру, выплата 35:1
	 * type 10:
	 * Column Bet, на столбец, number номер столбца, выплата 2:1, zero автоматический проигрыш
	 * type 11:
	 * Dozen Bet, на дюжину, number номер дюжины, выплата 2:1, zero автоматический проигрыш
	 * type 12:
	 * Red or Black, на цвет, number цвет (0=R,1=B), выплата 1:1, zero автоматический проигрыш
	 * type 13:
	 * Even or Odd, чет-нечет, number тип (0=even,1=odd), выплата 1:1, zero автоматический проигрыш
	 * type 14:
	 * Low or High, 0=1-18,1=19-36, выплата 1:1, zero автоматический проигрыш
	 */
	private static enum GameType
	{
		StraightUp,
		ColumnBet,
		DozenBet,
		RedOrBlack,
		EvenOrOdd,
		LowOrHigh;
	}

	private AtomicLong _rouletteSum = new AtomicLong(0L);

	public void addRoulette(long sum)
	{
		long rouletteSum = _rouletteSum.addAndGet(sum);
		ServerVariables.set("rouletteSum", rouletteSum);
	}

	public long getRouletteSum()
	{
		return _rouletteSum.get();
	}

	@Bypass("services.Roulette:dialog")
	public void dialog(Player player, NpcInstance npc, String[] arg)
	{
		HtmlMessage msg = new HtmlMessage(npc).setFile("scripts/services/roulette.htm");
		msg.replace("%min%", Util.formatAdena(Config.SERVICES_ROULETTE_MIN_BET));
		msg.replace("%max%", Util.formatAdena(Config.SERVICES_ROULETTE_MAX_BET));
		player.sendPacket(msg);
	}

	@Bypass("services.Roulette:play")
	public void play(Player player, NpcInstance npc, String[] param)
	{
		GameType type;
		long bet = 0;
		String betID = "";

		try
		{
			if(param.length != 3)
				throw new IllegalArgumentException();

			type = GameType.valueOf(param[0]);
			betID = param[1].trim();
			bet = Long.parseLong(param[2]);

			if(type == GameType.StraightUp && (betID.length() > 2 || Integer.parseInt(betID) < 0 || Integer.parseInt(betID) > 36))
				throw new IllegalArgumentException();
		}
		catch(IllegalArgumentException e)
		{
			player.sendPacket(new HtmlMessage(npc).setFile("scripts/services/roulette_err01.htm"));
			return;
		}

		if(bet < Config.SERVICES_ROULETTE_MIN_BET)
		{
			player.sendPacket(new HtmlMessage(npc).setFile("scripts/services/roulette_err02.htm").replace("%min%", Util.formatAdena(Config.SERVICES_ROULETTE_MIN_BET)));
			return;
		}

		if(bet > Config.SERVICES_ROULETTE_MAX_BET)
		{
			player.sendPacket(new HtmlMessage(npc).setFile("scripts/services/roulette_err03.htm").replace("%max%", Util.formatAdena(Config.SERVICES_ROULETTE_MAX_BET)));
			return;
		}

		if(player.getAdena() < bet)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			player.sendPacket(new HtmlMessage(npc).setFile("scripts/services/roulette_err04.htm"));
			return;
		}

		String[] roll = Numbers[Rnd.get(Numbers.length)];
		int result = check(betID, roll, type);

		HtmlMessage msg = new HtmlMessage(npc).setFile("scripts/services/roulette_result.htm");

		if(result == 0)
		{
			ItemFunctions.deleteItem(player, 57, bet);
			addRoulette(bet);
			msg.replace("%result%", HtmCache.getInstance().getHtml("scripts/services/roulette_result_fail.htm", player));
			Log.add("Character " + player + " loose " + bet + " adena, betID " + betID, "roulette");
		}
		else
		{
			long prize = bet * result;
			ItemFunctions.addItem(player, 57, prize);
			addRoulette(-1 * prize);
			msg.replace("%result%", HtmCache.getInstance().getHtml("scripts/services/roulette_result_success.htm", player));
			Log.add("Character " + player + " won " + prize+ " adena, betID " + betID + ", multiplier " + result, "roulette");
		}

		if(player.isGM())
			player.sendMessage("Roulette balance: " + Util.formatAdena(getRouletteSum()));

		msg.replace("%bettype%", StringHolder.getInstance().getString("Roulette." + type.toString(), player));
		msg.replace("%betnumber%", type == GameType.StraightUp ? betID : StringHolder.getInstance().getString("Roulette." + betID, player));
		msg.replace("%number%", roll[0]);
		msg.replace("%color%", StringHolder.getInstance().getString("Roulette." + roll[1], player));
		msg.replace("%evenness%", StringHolder.getInstance().getString("Roulette." + roll[4], player));
		msg.replace("%column%", StringHolder.getInstance().getString("Roulette." + roll[3], player));
		msg.replace("%dozen%", StringHolder.getInstance().getString("Roulette." + roll[2], player));
		msg.replace("%highness%", StringHolder.getInstance().getString("Roulette." + roll[5], player));
		msg.replace("%param%", param[0] + " " + param[1] + " " + param[2]);

		player.sendPacket(msg);
	}

	/**
	 * Возвращает множитель ставки или 0 при проигрыше
	 */
	private static final int check(String betID, String[] roll, GameType type)
	{
		switch(type)
		{
			case StraightUp:
				if(betID.equals(roll[0]))
					return 35;
				break;
			case ColumnBet:
				if(betID.equals(roll[3]))
					return 2;
				break;
			case DozenBet:
				if(betID.equals(roll[2]))
					return 2;
				break;
			case RedOrBlack:
				if(betID.equals(roll[1]))
					return 1;
				break;
			case EvenOrOdd:
				if(betID.equals(roll[4]))
					return 1;
				break;
			case LowOrHigh:
				if(betID.equals(roll[5]))
					return 1;
				break;
		}

		return 0;
	}

	@Override
	public void onInit()
	{
		_rouletteSum.set(ServerVariables.getLong("rouletteSum", 0));
	}
}