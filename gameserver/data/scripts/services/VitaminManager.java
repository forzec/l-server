package services;

import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.ItemFunctions;

public class VitaminManager
{
	private static final int PetCoupon = 13273;
	private static final int SpecialPetCoupon = 14065;

	private static final int WeaselNeck = 13017;
	private static final int PrincNeck = 13018;
	private static final int BeastNeck = 13019;
	private static final int FoxNeck = 13020;

	private static final int KnightNeck = 13548;
	private static final int SpiritNeck = 13549;
	private static final int OwlNeck = 13550;
	private static final int TurtleNeck = 13551;

	@Bypass("services.VitaminManager:giveWeasel")
	public void giveWeasel(Player player, NpcInstance npc, String[] arg)
	{
		String htmltext;
		if(ItemFunctions.deleteItem(player, PetCoupon, 1))
		{
			ItemFunctions.addItem(player, WeaselNeck, 1);
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
			htmltext = npc.getNpcId() + "-no.htm";

		npc.showChatWindow(player, "default/" + htmltext);
	}

	@Bypass("services.VitaminManager:givePrinc")
	public void givePrinc(Player player, NpcInstance npc, String[] arg)
	{
		String htmltext;
		if(ItemFunctions.deleteItem(player, PetCoupon, 1))
		{
			ItemFunctions.addItem(player, PrincNeck, 1);
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
			htmltext = npc.getNpcId() + "-no.htm";

		npc.showChatWindow(player, "default/" + htmltext);
	}

	@Bypass("services.VitaminManager:giveBeast")
	public void giveBeast(Player player, NpcInstance npc, String[] arg)
	{
		String htmltext;
		if(ItemFunctions.deleteItem(player, PetCoupon, 1))
		{
			ItemFunctions.addItem(player, BeastNeck, 1);
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
			htmltext = npc.getNpcId() + "-no.htm";

		npc.showChatWindow(player, "default/" + htmltext);
	}

	@Bypass("services.VitaminManager:giveFox")
	public void giveFox(Player player, NpcInstance npc, String[] arg)
	{
		String htmltext;
		if(ItemFunctions.deleteItem(player, PetCoupon, 1))
		{
			ItemFunctions.addItem(player, FoxNeck, 1);
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
			htmltext = npc.getNpcId() + "-no.htm";

		npc.showChatWindow(player, "default/" + htmltext);
	}

	@Bypass("services.VitaminManager:giveKnight")
	public void giveKnight(Player player, NpcInstance npc, String[] arg)
	{
		String htmltext;
		if(ItemFunctions.deleteItem(player, SpecialPetCoupon, 1))
		{
			ItemFunctions.addItem(player, KnightNeck, 1);
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
			htmltext = npc.getNpcId() + "-no.htm";

		npc.showChatWindow(player, "default/" + htmltext);
	}

	@Bypass("services.VitaminManager:giveSpirit")
	public void giveSpirit(Player player, NpcInstance npc, String[] arg)
	{
		String htmltext;
		if(ItemFunctions.deleteItem(player, SpecialPetCoupon, 1))
		{
			ItemFunctions.addItem(player, SpiritNeck, 1);
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
			htmltext = npc.getNpcId() + "-no.htm";

		npc.showChatWindow(player, "default/" + htmltext);
	}

	@Bypass("services.VitaminManager:giveOwl")
	public void giveOwl(Player player, NpcInstance npc, String[] arg)
	{
		String htmltext;
		if(ItemFunctions.deleteItem(player, SpecialPetCoupon, 1))
		{
			ItemFunctions.addItem(player, OwlNeck, 1);
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
			htmltext = npc.getNpcId() + "-no.htm";

		npc.showChatWindow(player, "default/" + htmltext);
	}

	@Bypass("services.VitaminManager:giveTurtle")
	public void giveTurtle(Player player, NpcInstance npc, String[] arg)
	{
		String htmltext;
		if(ItemFunctions.deleteItem(player, SpecialPetCoupon, 1))
		{
			ItemFunctions.addItem(player, TurtleNeck, 1);
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
			htmltext = npc.getNpcId() + "-no.htm";

		npc.showChatWindow(player, "default/" + htmltext);
	}
}