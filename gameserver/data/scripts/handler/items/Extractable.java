package handler.items;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.utils.ItemFunctions;

//TODO [G1ta0] вынести в датапак
public class Extractable extends SimpleItemHandler
{
	private static final int[] ITEM_IDS = new int[]{
			10408,
			10473,
			20069,
			20070,
			20071,
			20072,
			20073,
			20074,
			20210,
			20211,
			20215,
			20216,
			20217,
			20218,
			20219,
			20220,
			20227,
			20228,
			20229,
			20233,
			20234,
			20235,
			20239,
			20240,
			20241,
			20242,
			20243,
			20244,
			20251,
			20254,
			20278,
			20279,
			20041,
			20042,
			20043,
			20044,
			20035,
			20036,
			20037,
			20038,
			20039,
			20040,
			20060,
			20061,
			22000,
			22001,
			22002,
			22003,
			22004,
			22005,
			20326,
			20327,
			20329,
			20330,
			20059,
			20494,
			20493,
			20395,
			13281,
			13282,
			13283,
			13284,
			13285,
			13286,
			13287,
			13288,
			13289,
			13290,
			14267,
			14268,
			14269,
			13280,
			22087,
			22088,
			14616,
			20575,
			20804,
			20807,
			20805,
			20808,
			20806,
			20809,
			20842,
			20843,
			20844,
			20845,
			20846,
			20847,
			20848,
			20849,
			20850,
			20851,
			20852,
			20853,
			20854,
			20855,
			20856,
			20857,
			20858,
			20859,
			20860,
			20861,
			20862,
			20863,
			20864,
			20811,
			20812,
			20813,
			20814,
			20815,
			20816,
			20817,
			20810,
			20865,
			20748,
			20749,
			20750,
			20751,
			20752,
			20195,
			20196,
			20197,
			20198,
			13777,
			13778,
			13779,
			13780,
			13781,
			13782,
			13783,
			13784,
			13785,
			13786,
			14849,
			14834,
			14833,
			13988,
			13989,
			13003,
			13004,
			13005,
			13006,
			13007,
			13990,
			13991,
			13992,
			14850,
			14713,
			14714,
			14715,
			14716,
			14717,
			14718,
			17138,
			15482,
			15483,
			13270,
			13271,
			13272,
			14231,
			14232,
			21747,
			21748,
			21749,
			21169,
			21753,
			21752,

			17073,
			17070,
			22203,
			22202};

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}

	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		int itemId = item.getItemId();

		if(!canBeExtracted(player, item))
			return false;

		if(!useItem(player, item, 1))
			return false;

		switch(itemId)
		{
			case 10408:
				use10408(player, ctrl);
				break;
			case 10473:
				use10473(player, ctrl);
				break;
			case 20069:
				use20069(player, ctrl);
				break;
			case 20070:
				use20070(player, ctrl);
				break;
			case 20071:
				use20071(player, ctrl);
				break;
			case 20072:
				use20072(player, ctrl);
				break;
			case 20073:
				use20073(player, ctrl);
				break;
			case 20074:
				use20074(player, ctrl);
				break;
			case 20210:
				use20210(player, ctrl);
				break;
			case 20211:
				use20211(player, ctrl);
				break;
			case 20215:
				use20215(player, ctrl);
				break;
			case 20216:
				use20216(player, ctrl);
				break;
			case 20217:
				use20217(player, ctrl);
				break;
			case 20218:
				use20218(player, ctrl);
				break;
			case 20219:
				use20219(player, ctrl);
				break;
			case 20220:
				use20220(player, ctrl);
				break;
			case 20227:
				use20227(player, ctrl);
				break;
			case 20228:
				use20228(player, ctrl);
				break;
			case 20229:
				use20229(player, ctrl);
				break;
			case 20233:
				use20233(player, ctrl);
				break;
			case 20234:
				use20234(player, ctrl);
				break;
			case 20235:
				use20235(player, ctrl);
				break;
			case 20239:
				use20239(player, ctrl);
				break;
			case 20240:
				use20240(player, ctrl);
				break;
			case 20241:
				use20241(player, ctrl);
				break;
			case 20242:
				use20242(player, ctrl);
				break;
			case 20243:
				use20243(player, ctrl);
				break;
			case 20244:
				use20244(player, ctrl);
				break;
			case 20251:
				use20251(player, ctrl);
				break;
			case 20254:
				use20254(player, ctrl);
				break;
			case 20278:
				use20278(player, ctrl);
				break;
			case 20279:
				use20279(player, ctrl);
				break;
			case 20041:
				use20041(player, ctrl);
				break;
			case 20042:
				use20042(player, ctrl);
				break;
			case 20043:
				use20043(player, ctrl);
				break;
			case 20044:
				use20044(player, ctrl);
				break;
			case 20035:
				use20035(player, ctrl);
				break;
			case 20036:
				use20036(player, ctrl);
				break;
			case 20037:
				use20037(player, ctrl);
				break;
			case 20038:
				use20038(player, ctrl);
				break;
			case 20039:
				use20039(player, ctrl);
				break;
			case 20040:
				use20040(player, ctrl);
				break;
			case 20060:
				use20060(player, ctrl);
				break;
			case 20061:
				use20061(player, ctrl);
				break;
			case 22000:
				use22000(player, ctrl);
				break;
			case 22001:
				use22001(player, ctrl);
				break;
			case 22002:
				use22002(player, ctrl);
				break;
			case 22003:
				use22003(player, ctrl);
				break;
			case 22004:
				use22004(player, ctrl);
				break;
			case 22005:
				use22005(player, ctrl);
				break;
			case 20326:
				use20326(player, ctrl);
				break;
			case 20327:
				use20327(player, ctrl);
				break;
			case 20329:
				use20329(player, ctrl);
				break;
			case 20330:
				use20330(player, ctrl);
				break;
			case 20059:
				use20059(player, ctrl);
				break;
			case 20494:
				use20494(player, ctrl);
				break;
			case 20493:
				use20493(player, ctrl);
				break;
			case 20395:
				use20395(player, ctrl);
				break;
			case 13281:
				use13281(player, ctrl);
				break;
			case 13282:
				use13282(player, ctrl);
				break;
			case 13283:
				use13283(player, ctrl);
				break;
			case 13284:
				use13284(player, ctrl);
				break;
			case 13285:
				use13285(player, ctrl);
				break;
			case 13286:
				use13286(player, ctrl);
				break;
			case 13287:
				use13287(player, ctrl);
				break;
			case 13288:
				use13288(player, ctrl);
				break;
			case 13289:
				use13289(player, ctrl);
				break;
			case 13290:
				use13290(player, ctrl);
				break;
			case 14267:
				use14267(player, ctrl);
				break;
			case 14268:
				use14268(player, ctrl);
				break;
			case 14269:
				use14269(player, ctrl);
				break;
			case 13280:
				use13280(player, ctrl);
				break;
			case 22087:
				use22087(player, ctrl);
				break;
			case 22088:
				use22088(player, ctrl);
				break;
			case 14616:
				use14616(player, ctrl);
				break;
			case 20575:
				use20575(player, ctrl);
				break;
			case 20804:
				use20804(player, ctrl);
				break;
			case 20807:
				use20807(player, ctrl);
				break;
			case 20805:
				use20805(player, ctrl);
				break;
			case 20808:
				use20808(player, ctrl);
				break;
			case 20806:
				use20806(player, ctrl);
				break;
			case 20809:
				use20809(player, ctrl);
				break;
			case 20842:
				use20842(player, ctrl);
				break;
			case 20843:
				use20843(player, ctrl);
				break;
			case 20844:
				use20844(player, ctrl);
				break;
			case 20845:
				use20845(player, ctrl);
				break;
			case 20846:
				use20846(player, ctrl);
				break;
			case 20847:
				use20847(player, ctrl);
				break;
			case 20848:
				use20848(player, ctrl);
				break;
			case 20849:
				use20849(player, ctrl);
				break;
			case 20850:
				use20850(player, ctrl);
				break;
			case 20851:
				use20851(player, ctrl);
				break;
			case 20852:
				use20852(player, ctrl);
				break;
			case 20853:
				use20853(player, ctrl);
				break;
			case 20854:
				use20854(player, ctrl);
				break;
			case 20855:
				use20855(player, ctrl);
				break;
			case 20856:
				use20856(player, ctrl);
				break;
			case 20857:
				use20857(player, ctrl);
				break;
			case 20858:
				use20858(player, ctrl);
				break;
			case 20859:
				use20859(player, ctrl);
				break;
			case 20860:
				use20860(player, ctrl);
				break;
			case 20861:
				use20861(player, ctrl);
				break;
			case 20862:
				use20862(player, ctrl);
				break;
			case 20863:
				use20863(player, ctrl);
				break;
			case 20864:
				use20864(player, ctrl);
				break;
			case 20811:
				use20811(player, ctrl);
				break;
			case 20812:
				use20812(player, ctrl);
				break;
			case 20813:
				use20813(player, ctrl);
				break;
			case 20814:
				use20814(player, ctrl);
				break;
			case 20815:
				use20815(player, ctrl);
				break;
			case 20816:
				use20816(player, ctrl);
				break;
			case 20817:
				use20817(player, ctrl);
				break;
			case 20810:
				use20810(player, ctrl);
				break;
			case 20865:
				use20865(player, ctrl);
				break;
			case 20748:
				use20748(player, ctrl);
				break;
			case 20749:
				use20749(player, ctrl);
				break;
			case 20750:
				use20750(player, ctrl);
				break;
			case 20751:
				use20751(player, ctrl);
				break;
			case 20752:
				use20752(player, ctrl);
				break;
			case 20195:
				use20195(player, ctrl);
				break;
			case 20196:
				use20196(player, ctrl);
				break;
			case 20197:
				use20197(player, ctrl);
				break;
			case 20198:
				use20198(player, ctrl);
				break;
			case 13777:
				use13777(player, ctrl);
				break;
			case 13778:
				use13778(player, ctrl);
				break;
			case 13779:
				use13779(player, ctrl);
				break;
			case 13780:
				use13780(player, ctrl);
				break;
			case 13781:
				use13781(player, ctrl);
				break;
			case 13782:
				use13782(player, ctrl);
				break;
			case 13783:
				use13783(player, ctrl);
				break;
			case 13784:
				use13784(player, ctrl);
				break;
			case 13785:
				use13785(player, ctrl);
				break;
			case 13786:
				use13786(player, ctrl);
				break;
			case 14849:
				use14849(player, ctrl);
				break;
			case 14834:
				use14834(player, ctrl);
				break;
			case 14833:
				use14833(player, ctrl);
				break;
			case 13988:
				use13988(player, ctrl);
				break;
			case 13989:
				use13989(player, ctrl);
				break;
			case 13003:
				use13003(player, ctrl);
				break;
			case 13004:
				use13004(player, ctrl);
				break;
			case 13005:
				use13005(player, ctrl);
				break;
			case 13006:
				use13006(player, ctrl);
				break;
			case 13007:
				use13007(player, ctrl);
				break;
			case 13990:
				use13990(player, ctrl);
				break;
			case 13991:
				use13991(player, ctrl);
				break;
			case 13992:
				use13992(player, ctrl);
				break;
			case 14850:
				use14850(player, ctrl);
				break;
			case 14713:
				use14713(player, ctrl);
				break;
			case 14714:
				use14714(player, ctrl);
				break;
			case 14715:
				use14715(player, ctrl);
				break;
			case 14716:
				use14716(player, ctrl);
				break;
			case 14717:
				use14717(player, ctrl);
				break;
			case 14718:
				use14718(player, ctrl);
				break;
			case 17138:
				use17138(player, ctrl);
				break;
			case 15482:
				use15482(player, ctrl);
				break;
			case 15483:
				use15483(player, ctrl);
				break;
			case 13270:
				use13270(player, ctrl);
				break;
			case 13271:
				use13271(player, ctrl);
				break;
			case 13272:
				use13272(player, ctrl);
				break;
			case 14231:
				use14231(player, ctrl);
				break;
			case 14232:
				use14232(player, ctrl);
				break;
			case 21747:
				use21747(player, ctrl);
				break;
			case 21748:
				use21748(player, ctrl);
				break;
			case 21749:
				use21749(player, ctrl);
				break;
			case 21169:
				use21169(player, ctrl);
				break;
			case 21753:
				use21753(player, ctrl);
				break;
			case 21752:
				use21752(player, ctrl);
				break;
			case 17073:
				use17073(player, ctrl);
				break;
			case 17070:
				use17070(player, ctrl);
				break;
			case 22203:
				use22203(player, ctrl);
				break;
			case 22202:
				use22202(player, ctrl);
				break;
			default:
				return false;
		}

		return true;
	}

	// Old Agathion
	private void use10408(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 6471, 20);
		ItemFunctions.addItem(player, 5094, 40);
		ItemFunctions.addItem(player, 9814, 3);
		ItemFunctions.addItem(player, 9816, 4);
		ItemFunctions.addItem(player, 9817, 4);
		ItemFunctions.addItem(player, 9815, 2);
		ItemFunctions.addItem(player, 57, 6000000);
	}

	// Magic Armor Set
	private void use10473(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 10470, 2); // Shadow Item - Red Crescent
		ItemFunctions.addItem(player, 10471, 2); // Shadow Item - Ring of Devotion
		ItemFunctions.addItem(player, 10472, 1); // Shadow Item - Necklace of Devotion
	}

	// Baby Panda Agathion Pack
	private void use20069(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20063, 1);
	}

	// Bamboo Panda Agathion Pack
	private void use20070(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20064, 1);
	}

	// Sexy Panda Agathion Pack
	private void use20071(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20065, 1);
	}

	// Agathion of Baby Panda 15 Day Pack
	private void use20072(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20066, 1);
	}

	// Bamboo Panda Agathion 15 Day Pack
	private void use20073(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20067, 1);
	}

	// Agathion of Sexy Panda 15 Day Pack
	private void use20074(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20068, 1);
	}

	// Charming Valentine Gift Set
	private void use20210(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20212, 1);
	}

	// Naughty Valentine Gift Set
	private void use20211(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20213, 1);
	}

	// White Maneki Neko Agathion Pack
	private void use20215(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20221, 1);
	}

	// Black Maneki Neko Agathion Pack
	private void use20216(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20222, 1);
	}

	// Brown Maneki Neko Agathion Pack
	private void use20217(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20223, 1);
	}

	// White Maneki Neko Agathion 7-Day Pack
	private void use20218(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20224, 1);
	}

	// Black Maneki Neko Agathion 7-Day Pack
	private void use20219(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20225, 1);
	}

	// Brown Maneki Neko Agathion 7-Day Pack
	private void use20220(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20226, 1);
	}

	// One-Eyed Bat Drove Agathion Pack
	private void use20227(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20230, 1);
	}

	// One-Eyed Bat Drove Agathion 7-Day Pack
	private void use20228(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20231, 1);
	}

	// One-Eyed Bat Drove Agathion 7-Day Pack
	private void use20229(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20232, 1);
	}

	// Pegasus Agathion Pack
	private void use20233(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20236, 1);
	}

	// Pegasus Agathion 7-Day Pack
	private void use20234(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20237, 1);
	}

	// Pegasus Agathion 7-Day Pack
	private void use20235(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20238, 1);
	}

	// Yellow-Robed Tojigong Pack
	private void use20239(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20245, 1);
	}

	// Blue-Robed Tojigong Pack
	private void use20240(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20246, 1);
	}

	// Green-Robed Tojigong Pack
	private void use20241(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20247, 1);
	}

	// Yellow-Robed Tojigong 7-Day Pack
	private void use20242(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20248, 1);
	}

	// Blue-Robed Tojigong 7-Day Pack
	private void use20243(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20249, 1);
	}

	// Green-Robed Tojigong 7-Day Pack
	private void use20244(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20250, 1);
	}

	// Bugbear Agathion Pack
	private void use20251(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20252, 1);
	}

	// Agathion of Love Pack (Event)
	private void use20254(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20253, 1);
	}

	// Gold Afro Hair Pack
	private void use20278(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20275, 1);
	}

	// Pink Afro Hair Pack
	private void use20279(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20276, 1);
	}

	// Plaipitak Agathion Pack
	private void use20041(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20012, 1);
	}

	// Plaipitak Agathion 30-Day Pack
	private void use20042(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20013, 1);
	}

	// Plaipitak Agathion 30-Day Pack
	private void use20043(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20014, 1);
	}

	// Plaipitak Agathion 30-Day Pack
	private void use20044(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20015, 1);
	}

	// Majo Agathion Pack
	private void use20035(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20006, 1);
	}

	// Gold Crown Majo Agathion Pack
	private void use20036(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20007, 1);
	}

	// Black Crown Majo Agathion Pack
	private void use20037(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20008, 1);
	}

	// Majo Agathion 30-Day Pack
	private void use20038(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20009, 1);
	}

	// Gold Crown Majo Agathion 30-Day Pack
	private void use20039(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20010, 1);
	}

	// Black Crown Majo Agathion 30-Day Pack
	private void use20040(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20011, 1);
	}

	// Kat the Cat Hat Pack
	private void use20060(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20031, 1);
	}

	// Skull Hat Pack
	private void use20061(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20032, 1);
	}

	// ****** Start Item Mall ******
	// Small fortuna box
	private void use22000(Player player, boolean ctrl)
	{
		int[][] list = new int[][]{{22006, 3}, {22007, 2}, {22008, 1}, {22014, 1}, {22022, 3}, {22023, 3}, {22024, 1}, {8743, 1}, {8744, 1}, {8745, 1}, {8753, 1}, {8754, 1}, {8755, 1}, {22025, 5}};
		double[] chances = new double[]{20.55555, 14.01515, 6.16666, 0.86999, 3.19444, 6.38888, 5.75, 10, 8.33333, 6.94444, 2, 1.6666, 1.38888, 12.77777};
		extractRandomOneItem(player, list, chances);
	}

	// Middle fortuna box
	private void use22001(Player player, boolean ctrl)
	{
		int[][] list = new int[][]{{22007, 3}, {22008, 2}, {22009, 1}, {22014, 1}, {22015, 1}, {22022, 5}, {22023, 5}, {22024, 2}, {8746, 1}, {8747, 1}, {8748, 1}, {8756, 1}, {8757, 1}, {8758, 1}, {22025, 10}};
		double[] chances = new double[]{27.27272, 9, 5, 0.93959, 0.32467, 3.75, 7.5, 5.625, 9.11458, 7.875, 6.5625, 1.82291, 1.575, 1.3125, 12.5};
		extractRandomOneItem(player, list, chances);
	}

	// Large fortuna box
	private void use22002(Player player, boolean ctrl)
	{
		int[][] list = new int[][]{{22008, 2}, {22009, 1}, {22014, 1}, {22015, 1}, {22018, 1}, {22019, 1}, {22022, 10}, {22023, 10}, {22024, 5}, {8749, 1}, {8750, 1}, {8751, 1}, {8759, 1}, {8760, 1}, {8761, 1}, {22025, 20}};
		double[] chances = new double[]{27, 15, 0.78299, 0.27056, 0.00775, 0.0027, 3.75, 7.5, 4.5, 9.75, 8.125, 6.77083, 1.95, 1.625, 1.35416, 12.5};
		extractRandomOneItem(player, list, chances);
	}

	// Small fortuna cube
	private void use22003(Player player, boolean ctrl)
	{
		int[][] list = new int[][]{{22010, 3}, {22011, 2}, {22012, 1}, {22016, 1}, {22022, 3}, {22023, 3}, {22024, 1}, {8743, 1}, {8744, 1}, {8745, 1}, {8753, 1}, {8754, 1}, {8755, 1}, {22025, 5}};
		double[] chances = new double[]{20.22222, 13.78787, 6.06666, 0.69599, 3.47222, 6.94444, 6.25, 9.5, 7.91666, 6.59722, 1.9, 1.58333, 1.31944, 13.88888};
		extractRandomOneItem(player, list, chances);
	}

	// Middle fortuna cube
	private void use22004(Player player, boolean ctrl)
	{
		int[][] list = new int[][]{{22011, 3}, {22012, 2}, {22013, 1}, {22016, 1}, {22017, 1}, {22022, 5}, {22023, 5}, {22024, 2}, {8746, 1}, {8747, 1}, {8748, 1}, {8756, 1}, {8757, 1}, {8758, 1}, {22025, 10}};
		double[] chances = new double[]{26.51515, 8.75, 4.86111, 0.91349, 0.31565, 3.75, 7.5, 5.625, 9.54861, 8.25, 6.875, 1.90972, 1.65, 1.375, 12.5};
		extractRandomOneItem(player, list, chances);
	}

	// Large fortuna cube
	private void use22005(Player player, boolean ctrl)
	{
		int[][] list = new int[][]{{22012, 2}, {22013, 1}, {22016, 1}, {22017, 1}, {22020, 1}, {22021, 1}, {22022, 10}, {22023, 10}, {22024, 5}, {8749, 1}, {8750, 1}, {8751, 1}, {8759, 1}, {8760, 1}, {8761, 1}, {22025, 20}};
		double[] chances = new double[]{26.25, 14.58333, 0.69599, 0.24049, 0.00638, 0.0022, 3.95833, 7.91666, 4.75, 9.58333, 7.98611, 6.65509, 1.91666, 1.59722, 1.33101, 13.19444};
		extractRandomOneItem(player, list, chances);
	}

	// Beast Soulshot Pack
	private void use20326(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20332, 5000);
	}

	// Beast Spiritshot Pack
	private void use20327(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20333, 5000);
	}

	// Beast Soulshot Large Pack
	private void use20329(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20332, 10000);
	}

	// Beast Spiritshot Large Pack
	private void use20330(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20333, 10000);
	}

	// Light Purple Maned Horse Bracelet 30-Day Pack
	private void use20059(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20030, 1);
	}

	// Steam Beatle Mounting Bracelet 7 Day Pack
	private void use20494(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20449, 1);
	}

	// Light Purple Maned Horse Mounting Bracelet 7 Day Pack
	private void use20493(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20448, 1);
	}

	// Steam Beatle Mounting Bracelet Pack
	private void use20395(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20396, 1);
	}

	// Pumpkin Transformation Stick 7 Day Pack
	private void use13281(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 13253, 1);
	}

	// Kat the Cat Hat 7-Day Pack
	private void use13282(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 13239, 1);
	}

	// Feline Queen Hat 7-Day Pack
	private void use13283(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 13240, 1);
	}

	// Monster Eye Hat 7-Day Pack
	private void use13284(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 13241, 1);
	}

	// Brown Bear Hat 7-Day Pack
	private void use13285(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 13242, 1);
	}

	// Fungus Hat 7-Day Pack
	private void use13286(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 13243, 1);
	}

	// Skull Hat 7-Day Pack
	private void use13287(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 13244, 1);
	}

	// Ornithomimus Hat 7-Day Pack
	private void use13288(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 13245, 1);
	}

	// Feline King Hat 7-Day Pack
	private void use13289(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 13246, 1);
	}

	// Kai the Cat Hat 7-Day Pack
	private void use13290(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 13247, 1);
	}

	// Sudden Agathion 7 Day Pack
	private void use14267(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 14093, 1);
	}

	// Shiny Agathion 7 Day Pack
	private void use14268(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 14094, 1);
	}

	// Sobbing Agathion 7 Day Pack
	private void use14269(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 14095, 1);
	}

	// Agathion of Love 7-Day Pack
	private void use13280(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20201, 1);
	}

	// A Scroll Bundle of Fighter
	private void use22087(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 22039, 1);
		ItemFunctions.addItem(player, 22040, 1);
		ItemFunctions.addItem(player, 22041, 1);
		ItemFunctions.addItem(player, 22042, 1);
		ItemFunctions.addItem(player, 22043, 1);
		ItemFunctions.addItem(player, 22044, 1);
		ItemFunctions.addItem(player, 22047, 1);
		ItemFunctions.addItem(player, 22048, 1);
	}

	// A Scroll Bundle of Mage
	private void use22088(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 22045, 1);
		ItemFunctions.addItem(player, 22046, 1);
		ItemFunctions.addItem(player, 22048, 1);
		ItemFunctions.addItem(player, 22049, 1);
		ItemFunctions.addItem(player, 22050, 1);
		ItemFunctions.addItem(player, 22051, 1);
		ItemFunctions.addItem(player, 22052, 1);
		ItemFunctions.addItem(player, 22053, 1);
	}

	// ****** End Item Mall ******

	// Gift from Santa Claus
	private void use14616(Player player, boolean ctrl)
	{
		// Santa Claus' Weapon Exchange Ticket - 12 Hour Expiration Period
		ItemFunctions.addItem(player, 20107, 1);

		// Christmas Red Sock
		ItemFunctions.addItem(player, 14612, 1);

		// Special Christmas Tree
		if(Rnd.chance(30))
			ItemFunctions.addItem(player, 5561, 1);

		// Christmas Tree
		if(Rnd.chance(50))
			ItemFunctions.addItem(player, 5560, 1);

		// Agathion Seal Bracelet - Rudolph (постоянный предмет)
		if(ItemFunctions.getItemCount(player, 10606) == 0 && Rnd.chance(5))
			ItemFunctions.addItem(player, 10606, 1);

		// Agathion Seal Bracelet: Rudolph - 30 дней со скилом на виталити
		if(ItemFunctions.getItemCount(player, 20094) == 0 && Rnd.chance(3))
			ItemFunctions.addItem(player, 20094, 1);

		// Chest of Experience (Event)
		if(Rnd.chance(30))
			ItemFunctions.addItem(player, 20575, 1);
	}

	// Chest of Experience (Event)
	private void use20575(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20335, 1); // Rune of Experience: 30% - 5 hour limited time
		ItemFunctions.addItem(player, 20341, 1); // Rune of SP 30% - 5 Hour Expiration Period
	}

	// Nepal Snow Agathion Pack
	private void use20804(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20782, 1);
	}

	// Nepal Snow Agathion 7-Day Pack - Snow's Haste
	private void use20807(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20785, 1);
	}

	// Round Ball Snow Agathion Pack
	private void use20805(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20783, 1);
	}

	// Round Ball Snow Agathion 7-Day Pack - Snow's Acumen
	private void use20808(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20786, 1);
	}

	// Ladder Snow Agathion Pack
	private void use20806(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20784, 1);
	}

	// Ladder Snow Agathion 7-Day Pack - Snow's Wind Walk
	private void use20809(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20787, 1);
	}

	// Iken Agathion Pack
	private void use20842(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20818, 1);
	}

	// Iken Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	private void use20843(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20819, 1);
	}

	// Lana Agathion Pack
	private void use20844(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20820, 1);
	}

	// Lana Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	private void use20845(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20821, 1);
	}

	// Gnocian Agathion Pack
	private void use20846(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20822, 1);
	}

	// Gnocian Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	private void use20847(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20823, 1);
	}

	// Orodriel Agathion Pack
	private void use20848(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20824, 1);
	}

	// Orodriel Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	private void use20849(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20825, 1);
	}

	// Lakinos Agathion Pack
	private void use20850(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20826, 1);
	}

	// Lakinos Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	private void use20851(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20827, 1);
	}

	// Mortia Agathion Pack
	private void use20852(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20828, 1);
	}

	// Mortia Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	private void use20853(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20829, 1);
	}

	// Hayance Agathion Pack
	private void use20854(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20830, 1);
	}

	// Hayance Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	private void use20855(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20831, 1);
	}

	// Meruril Agathion Pack
	private void use20856(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20832, 1);
	}

	// Meruril Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	private void use20857(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20833, 1);
	}

	// Taman ze Lapatui Agathion Pack
	private void use20858(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20834, 1);
	}

	// Taman ze Lapatui Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	private void use20859(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20835, 1);
	}

	// Kaurin Agathion Pack
	private void use20860(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20836, 1);
	}

	// Kaurin Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	private void use20861(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20837, 1);
	}

	// Ahertbein Agathion Pack
	private void use20862(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20838, 1);
	}

	// Ahertbein Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	private void use20863(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20839, 1);
	}

	// Naonin Agathion Pack
	private void use20864(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20840, 1);
	}

	// Rocket Gun Hat Pack Continuous Fireworks
	private void use20811(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20789, 1);
	}

	// Yellow Paper Hat 7-Day Pack Bless the Body
	private void use20812(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20790, 1);
	}

	// Pink Paper Mask Set 7-Day Pack Bless the Soul
	private void use20813(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20791, 1);
	}

	// Flavorful Cheese Hat Pack
	private void use20814(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20792, 1);
	}

	// Sweet Cheese Hat Pack
	private void use20815(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20793, 1);
	}

	// Flavorful Cheese Hat 7-Day Pack Scent of Flavorful Cheese
	private void use20816(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20794, 1);
	}

	// Sweet Cheese Hat 7-Day Pack Scent of Sweet Cheese
	private void use20817(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20795, 1);
	}

	// Flame Box Pack
	private void use20810(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20725, 1);
	}

	// Naonin Agathion 7-Day Pack Prominent Outsider Adventurer's Ability
	private void use20865(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20841, 1);
	}

	// Shiny Mask of Giant Hercules 7 day Pack
	private void use20748(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20743, 1);
	}

	// Shiny Mask of Silent Scream 7 day Pack
	private void use20749(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20744, 1);
	}

	// Shiny Spirit of Wrath Mask 7 day Pack
	private void use20750(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20745, 1);
	}

	// Shiny Undecaying Corpse Mask 7 Day Pack
	private void use20751(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20746, 1);
	}

	// Shiny Planet X235 Alien Mask 7 day Pack
	private void use20752(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 20747, 1);
	}

	// Simple Valentine Cake
	private void use20195(Player player, boolean ctrl)
	{
		// Velvety Valentine Cake
		if(Rnd.chance(20))
			ItemFunctions.addItem(player, 20196, 1);
		else
		{
			// Dragon Bomber Transformation Scroll
			if(Rnd.chance(5))
				ItemFunctions.addItem(player, 20371, 1);

			// Unicorn Transformation Scroll
			if(Rnd.chance(5))
				ItemFunctions.addItem(player, 20367, 1);

			// Quick Healing Potion
			if(Rnd.chance(10))
				ItemFunctions.addItem(player, 1540, 1);

			// Greater Healing Potion
			if(Rnd.chance(15))
				ItemFunctions.addItem(player, 1539, 1);
		}
	}

	// Velvety Valentine Cake
	private void use20196(Player player, boolean ctrl)
	{
		// Delectable Valentine Cake
		if(Rnd.chance(15))
			ItemFunctions.addItem(player, 20197, 1);
		else
		{
			// Scroll: Enchant Armor (C)
			if(Rnd.chance(10))
				ItemFunctions.addItem(player, 952, 1);

			// Scroll: Enchant Armor (B)
			if(Rnd.chance(5))
				ItemFunctions.addItem(player, 948, 1);

			// Blessed Scroll of Escape
			if(Rnd.chance(10))
				ItemFunctions.addItem(player, 1538, 1);

			// Blessed Scroll of Resurrection
			if(Rnd.chance(5))
				ItemFunctions.addItem(player, 3936, 1);

			// Agathion of Love - 3 Day Expiration Period
			if(Rnd.chance(10))
				ItemFunctions.addItem(player, 20200, 1);
		}
	}

	// Delectable Valentine Cake
	private void use20197(Player player, boolean ctrl)
	{
		// Decadent Valentine Cake
		if(Rnd.chance(10))
			ItemFunctions.addItem(player, 20198, 1);
		else
		{
			// Scroll: Enchant Weapon (C)
			if(Rnd.chance(10))
				ItemFunctions.addItem(player, 951, 1);

			// Scroll: Enchant Weapon (B)
			if(Rnd.chance(5))
				ItemFunctions.addItem(player, 947, 1);

			// Agathion of Love - 7 Day Expiration Period
			if(Rnd.chance(5))
				ItemFunctions.addItem(player, 20201, 1);
		}
	}

	// Decadent Valentine Cake
	private void use20198(Player player, boolean ctrl)
	{
		// Scroll: Enchant Weapon (S)
		if(Rnd.chance(5))
			ItemFunctions.addItem(player, 959, 1);

		// Scroll: Enchant Weapon (A)
		if(Rnd.chance(10))
			ItemFunctions.addItem(player, 729, 1);

		// Agathion of Love - 15 Day Expiration Period
		if(Rnd.chance(10))
			ItemFunctions.addItem(player, 20202, 1);

		// Agathion of Love - 30 Day Expiration Period
		if(Rnd.chance(5))
			ItemFunctions.addItem(player, 20203, 1);
	}

	private static final int[] SOI_books = {14209, // Forgotten Scroll - Hide
			14212, // Forgotten Scroll - Enlightenment - Wizard
			14213, // Forgotten Scroll - Enlightenment - Healer
			10554, // Forgotten Scroll - Anti-Magic Armor
			14208, // Forgotten Scroll - Final Secret
			10577 // Forgotten Scroll - Excessive Loyalty
	};

	// Jewel Ornamented Duel Supplies
	private void use13777(Player player, boolean ctrl)
	{
		int rnd = Rnd.get(100);
		if(rnd <= 65)
		{
			ItemFunctions.addItem(player, 9630, 3); // 3 Orichalcum
			ItemFunctions.addItem(player, 9629, 3); // 3 Adamantine
			ItemFunctions.addItem(player, 9628, 4); // 4 Leonard
			ItemFunctions.addItem(player, 8639, 6); // 6 Elixir of CP (S-Grade)
			ItemFunctions.addItem(player, 8627, 6); // 6 Elixir of Life (S-Grade)
			ItemFunctions.addItem(player, 8633, 6); // 6 Elixir of Mental Strength (S-Grade)
		}
		else if(rnd <= 95)
			ItemFunctions.addItem(player, SOI_books[Rnd.get(SOI_books.length)], 1);
		else
			ItemFunctions.addItem(player, 14027, 1); // Collection Agathion Summon Bracelet
	}

	// Mother-of-Pearl Ornamented Duel Supplies
	private void use13778(Player player, boolean ctrl)
	{
		int rnd = Rnd.get(100);
		if(rnd <= 65)
		{
			ItemFunctions.addItem(player, 9630, 2); // 3 Orichalcum
			ItemFunctions.addItem(player, 9629, 2); // 3 Adamantine
			ItemFunctions.addItem(player, 9628, 3); // 4 Leonard
			ItemFunctions.addItem(player, 8639, 5); // 5 Elixir of CP (S-Grade)
			ItemFunctions.addItem(player, 8627, 5); // 5 Elixir of Life (S-Grade)
			ItemFunctions.addItem(player, 8633, 5); // 5 Elixir of Mental Strength (S-Grade)
		}
		else if(rnd <= 95)
			ItemFunctions.addItem(player, SOI_books[Rnd.get(SOI_books.length)], 1);
		else
			ItemFunctions.addItem(player, 14027, 1); // Collection Agathion Summon Bracelet
	}

	// Gold-Ornamented Duel Supplies
	private void use13779(Player player, boolean ctrl)
	{
		int rnd = Rnd.get(100);
		if(rnd <= 65)
		{
			ItemFunctions.addItem(player, 9630, 1); // 1 Orichalcum
			ItemFunctions.addItem(player, 9629, 1); // 1 Adamantine
			ItemFunctions.addItem(player, 9628, 2); // 2 Leonard
			ItemFunctions.addItem(player, 8639, 4); // 4 Elixir of CP (S-Grade)
			ItemFunctions.addItem(player, 8627, 4); // 4 Elixir of Life (S-Grade)
			ItemFunctions.addItem(player, 8633, 4); // 4 Elixir of Mental Strength (S-Grade)
		}
		else if(rnd <= 95)
			ItemFunctions.addItem(player, SOI_books[Rnd.get(SOI_books.length)], 1);
		else
			ItemFunctions.addItem(player, 14027, 1); // Collection Agathion Summon Bracelet
	}

	// Silver-Ornamented Duel Supplies
	private void use13780(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 8639, 4); // 4 Elixir of CP (S-Grade)
		ItemFunctions.addItem(player, 8627, 4); // 4 Elixir of Life (S-Grade)
		ItemFunctions.addItem(player, 8633, 4); // 4 Elixir of Mental Strength (S-Grade)
	}

	// Bronze-Ornamented Duel Supplies
	private void use13781(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 8639, 4); // 4 Elixir of CP (S-Grade)
		ItemFunctions.addItem(player, 8627, 4); // 4 Elixir of Life (S-Grade)
		ItemFunctions.addItem(player, 8633, 4); // 4 Elixir of Mental Strength (S-Grade)
	}

	// Non-Ornamented Duel Supplies
	private void use13782(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 8639, 3); // 3 Elixir of CP (S-Grade)
		ItemFunctions.addItem(player, 8627, 3); // 3 Elixir of Life (S-Grade)
		ItemFunctions.addItem(player, 8633, 3); // 3 Elixir of Mental Strength (S-Grade)
	}

	// Weak-Looking Duel Supplies
	private void use13783(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 8639, 3); // 3 Elixir of CP (S-Grade)
		ItemFunctions.addItem(player, 8627, 3); // 3 Elixir of Life (S-Grade)
		ItemFunctions.addItem(player, 8633, 3); // 3 Elixir of Mental Strength (S-Grade)
	}

	// Sad-Looking Duel Supplies
	private void use13784(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 8639, 3); // 3 Elixir of CP (S-Grade)
		ItemFunctions.addItem(player, 8627, 3); // 3 Elixir of Life (S-Grade)
		ItemFunctions.addItem(player, 8633, 3); // 3 Elixir of Mental Strength (S-Grade)
	}

	// Poor-Looking Duel Supplies
	private void use13785(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 8639, 2); // 2 Elixir of CP (S-Grade)
		ItemFunctions.addItem(player, 8627, 2); // 2 Elixir of Life (S-Grade)
		ItemFunctions.addItem(player, 8633, 2); // 2 Elixir of Mental Strength (S-Grade)
	}

	// Worthless Duel Supplies
	private void use13786(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 8639, 1); // 1 Elixir of CP (S-Grade)
		ItemFunctions.addItem(player, 8627, 1); // 1 Elixir of Life (S-Grade)
		ItemFunctions.addItem(player, 8633, 1); // 1 Elixir of Mental Strength (S-Grade)
	}

	// Kahman's Supply Box
	private void use14849(Player player, boolean ctrl)
	{
		int[] list = new int[]{9625, 9626}; // codex_of_giant_forgetting, codex_of_giant_training
		int[] chances = new int[]{100, 80};
		int[] counts = new int[]{1, 1};
		extract_item_r(list, counts, chances, player);
	}

	// Big Stakato Cocoon
	private void use14834(Player player, boolean ctrl)
	{
		int[][] items = new int[][]{{9575, 1}, // rare_80_s
				{10485, 1}, // rare_82_s
				{10577, 1}, // sb_excessive_loyalty
				{14209, 1}, // sb_hide1
				{14208, 1}, // sb_final_secret1
				{14212, 1}, // sb_enlightenment_wizard1
				{960, 1}, // scrl_of_ench_am_s
				{9625, 1}, // codex_of_giant_forgetting
				{9626, 1}, // codex_of_giant_training
				{959, 1}, // scrl_of_ench_wp_s
				{10373, 1}, // rp_icarus_sowsword_i
				{10374, 1}, // rp_icarus_disperser_i
				{10375, 1}, // rp_icarus_spirits_i
				{10376, 1}, // rp_icarus_heavy_arms_i
				{10377, 1}, // rp_icarus_trident_i
				{10378, 1}, // rp_icarus_chopper_i
				{10379, 1}, // rp_icarus_knuckle_i
				{10380, 1}, // rp_icarus_wand_i
				{10381, 1}}; // rp_icarus_accipiter_i
		double[] chances = new double[]{2.77, 2.31, 3.2, 3.2, 3.2, 3.2, 6.4, 3.2, 2.13, 0.64, 1.54, 1.54, 1.54, 1.54, 1.54, 1.54, 1.54, 1.54, 1.54};
		extractRandomOneItem(player, items, chances);
	}

	// Small Stakato Cocoon
	private void use14833(Player player, boolean ctrl)
	{
		int[][] items = new int[][]{{9575, 1}, // rare_80_s
				{10485, 1}, // rare_82_s
				{10577, 1}, // sb_excessive_loyalty
				{14209, 1}, // sb_hide1
				{14208, 1}, // sb_final_secret1
				{14212, 1}, // sb_enlightenment_wizard1
				{960, 1}, // scrl_of_ench_am_s
				{9625, 1}, // codex_of_giant_forgetting
				{9626, 1}, // codex_of_giant_training
				{959, 1}, // scrl_of_ench_wp_s
				{10373, 1}, // rp_icarus_sowsword_i
				{10374, 1}, // rp_icarus_disperser_i
				{10375, 1}, // rp_icarus_spirits_i
				{10376, 1}, // rp_icarus_heavy_arms_i
				{10377, 1}, // rp_icarus_trident_i
				{10378, 1}, // rp_icarus_chopper_i
				{10379, 1}, // rp_icarus_knuckle_i
				{10380, 1}, // rp_icarus_wand_i
				{10381, 1}}; // rp_icarus_accipiter_i
		double[] chances = new double[]{2.36, 1.96, 2.72, 2.72, 2.72, 2.72, 5.44, 2.72, 1.81, 0.54, 1.31, 1.31, 1.31, 1.31, 1.31, 1.31, 1.31, 1.31, 1.31};
		extractRandomOneItem(player, items, chances);
	}

	private void use13988(Player player, boolean ctrl)
	{
		int[] list = new int[]{9442, 9443, 9444, 9445, 9446, 9447, 9448, 9450, 10252, 10253, 10215, 10216, 10217, 10218, 10219, 10220, 10221, 10222, 10223};
		int[] chances = new int[]{64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 40, 40, 40, 40, 40, 40, 40, 40, 40};
		int[] counts = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		extract_item_r(list, counts, chances, player);
	}

	private void use13989(Player player, boolean ctrl)
	{
		int[] list = new int[]{9514, 9515, 9516, 9517, 9518, 9519, 9520, 9521, 9522, 9523, 9524, 9525, 9526, 9527, 9528};
		int[] chances = new int[]{50, 63, 70, 75, 75, 50, 63, 70, 75, 75, 50, 63, 70, 75, 75};
		int[] counts = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		extract_item_r(list, counts, chances, player);
	}

	// Pathfinder's Reward - D-Grade
	private void use13003(Player player, boolean ctrl)
	{
		if(Rnd.chance(3.2))
			ItemFunctions.addItem(player, 947, 1); // Scroll: Enchant Weapon B
	}

	// Pathfinder's Reward - C-Grade
	private void use13004(Player player, boolean ctrl)
	{
		if(Rnd.chance(1.6111))
			ItemFunctions.addItem(player, 729, 1); // Scroll: Enchant Weapon A
	}

	// Pathfinder's Reward - B-Grade
	private void use13005(Player player, boolean ctrl)
	{
		if(Rnd.chance(1.14))
			ItemFunctions.addItem(player, 959, 1); // Scroll: Enchant Weapon S
	}

	// Pathfinder's Reward - A-Grade
	private void use13006(Player player, boolean ctrl)
	{
		int[][] items = new int[][]{{9546, 1}, {9548, 1}, {9550, 1}, {959, 1}, {9442, 1}, {9443, 1}, {9444, 1}, {9445, 1}, {9446, 1}, {9447, 1}, {9448, 1}, {9449, 1}, {9450, 1}, {10252, 1}, {10253, 1}, {15645, 1}, {15646, 1}, {15647, 1}};
		double[] chances = new double[]{19.8, 19.8, 19.8, 1.98, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 1, 1, 1};
		extractRandomOneItem(player, items, chances);
	}

	// Pathfinder's Reward - S-Grade
	private void use13007(Player player, boolean ctrl)
	{
		int[][] items = new int[][]{{9546, 1}, {9548, 1}, {9550, 1}, {959, 1}, {10215, 1}, {10216, 1}, {10217, 1}, {10218, 1}, {10219, 1}, {10220, 1}, {10221, 1}, {10222, 1}, {10223, 1}};
		double[] chances = new double[]{26.4, 26.4, 26.4, 3.84, 0.13, 0.13, 0.13, 0.13, 0.13, 0.13, 0.13, 0.13, 0.13};
		extractRandomOneItem(player, items, chances);
	}

	//Pathfinder's Reward - AU Karm
	private void use13270(Player player, boolean ctrl)
	{
		if(Rnd.chance(30))
			ItemFunctions.addItem(player, 13236, 1);
	}

	//Pathfinder's Reward - AR Karm
	private void use13271(Player player, boolean ctrl)
	{
		if(Rnd.chance(30))
			ItemFunctions.addItem(player, 13237, 1);
	}

	//Pathfinder's Reward - AE Karm
	private void use13272(Player player, boolean ctrl)
	{
		if(Rnd.chance(30))
			ItemFunctions.addItem(player, 13238, 1);
	}

	private void use13990(Player player, boolean ctrl)
	{
		int[] list = new int[]{6364, 6365, 6366, 6367, 6368, 6369, 6370, 6371, 6372, 6534, 6579, 7575};
		int[] chances = new int[]{83, 83, 83, 83, 83, 83, 83, 83, 83, 83, 83, 83};
		int[] counts = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		extract_item_r(list, counts, chances, player);
	}

	private void use13991(Player player, boolean ctrl)
	{
		int[] list = new int[]{6674, 6675, 6676, 6677, 6679, 6680, 6681, 6682, 6683, 6684, 6685, 6686, 6687};
		int[] chances = new int[]{70, 80, 95, 95, 90, 55, 95, 95, 90, 55, 95, 95, 90};
		int[] counts = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		extract_item_r(list, counts, chances, player);
	}

	private void use13992(Player player, boolean ctrl)
	{
		int[] list = new int[]{6724, 6725, 6726};
		int[] chances = new int[]{25, 32, 42};
		int[] counts = new int[]{1, 1, 1};
		extract_item_r(list, counts, chances, player);
	}

	//Droph's Support Items
	private void use14850(Player player, boolean ctrl)
	{
		int rndAA = Rnd.get(80000, 100000);
		ItemFunctions.addItem(player, 5575, rndAA); // Ancient Adena
	}

	//Greater Elixir Gift Box (No-Grade)
	private void use14713(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 14682, 50); // Greater Elixir of Life (No-Grade)
		ItemFunctions.addItem(player, 14688, 50); // Greater Elixir of Mental Strength (No-Grade)
		ItemFunctions.addItem(player, 14694, 50); // Greater Elixir of CP (No Grade)
	}

	//Greater Elixir Gift Box (D-Grade)
	private void use14714(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 14683, 50); // Greater Elixir of Life (D-Grade)
		ItemFunctions.addItem(player, 14689, 50); // Greater Elixir of Mental Strength (D-Grade)
		ItemFunctions.addItem(player, 14695, 50); // Greater Elixir of CP (D Grade)
	}

	//Greater Elixir Gift Box (C-Grade)
	private void use14715(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 14684, 50); // Greater Elixir of Life (C-Grade)
		ItemFunctions.addItem(player, 14690, 50); // Greater Elixir of Mental Strength (C-Grade)
		ItemFunctions.addItem(player, 14696, 50); // Greater Elixir of CP (C Grade)
	}

	//Greater Elixir Gift Box (B-Grade)
	private void use14716(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 14685, 50); // Greater Elixir of Life (B-Grade)
		ItemFunctions.addItem(player, 14691, 50); // Greater Elixir of Mental Strength (B-Grade)
		ItemFunctions.addItem(player, 14697, 50); // Greater Elixir of CP (B Grade)
	}

	//Greater Elixir Gift Box (A-Grade)
	private void use14717(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 14686, 50); // Greater Elixir of Life (A-Grade)
		ItemFunctions.addItem(player, 14692, 50); // Greater Elixir of Mental Strength (A-Grade)
		ItemFunctions.addItem(player, 14698, 50); // Greater Elixir of CP (A Grade)
	}

	//Greater Elixir Gift Box (S-Grade)
	private void use14718(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 14687, 50); // Greater Elixir of Life (S-Grade)
		ItemFunctions.addItem(player, 14693, 50); // Greater Elixir of Mental Strength (S-Grade)
		ItemFunctions.addItem(player, 14699, 50); // Greater Elixir of CP (S Grade)
	}

	// Freya's Gift
	private void use17138(Player player, boolean ctrl)
	{
		int[][] items = new int[][]{{16026, 1}, {9627, 1}, {17139, 1}, {17140, 1}, {14052, 1}, {6622, 1}, {2134, 2}, {14701, 1}};
		double[] chances = new double[]{0.0001, 0.1417, 1.4172, 1.4172, 2.8345, 18.424, 21.2585, 54.5068};
		extractRandomOneItem(player, items, chances);
	}

	// Beginner Adventurer's Treasure Sack
	private void use21747(Player player, boolean ctrl)
	{
		int group = Rnd.get(7);
		int[] items = new int[0];
		if(group < 4) //Low D-Grade rewards
			items = new int[]{312, 167, 220, 258, 178, 221, 123, 156, 291, 166, 274};
		else if(group >= 4) //Mid D-Grade rewards
			items = new int[]{261, 224, 318, 93, 129, 294, 88, 90, 158, 172, 279, 169};

		ItemFunctions.addItem(player, items[Rnd.get(items.length)], 1);
	}

	// Experienced Adventurer's Treasure Sack
	private void use21748(Player player, boolean ctrl)
	{
		int group = Rnd.get(10);
		int[] items = new int[0];
		if(group < 4) //Low C-Grade rewards
			items = new int[]{160, 298, 72, 193, 192, 281, 7887, 226, 2524, 191, 71, 263};
		else if(group >= 4 && group < 7) //Low B-Grade rewards
			items = new int[]{78, 2571, 300, 284, 142, 267, 229, 148, 243, 92, 7892, 91};
		else if(group >= 7 && group < 9) //Low A-Grade rewards
			items = new int[]{98, 5233, 80, 235, 269, 288, 7884, 2504, 150, 7899, 212};
		else if(group == 9) //Low S-Grade rewards
			items = new int[]{6365, 6371, 6364, 6366, 6580, 7575, 6579, 6372, 6370, 6369, 6367};

		ItemFunctions.addItem(player, items[Rnd.get(items.length)], 1);
	}

	// Great Adventurer's Treasure Sack
	private void use21749(Player player, boolean ctrl)
	{
		int group = Rnd.get(9);
		int[] items = new int[0];
		if(group < 5) //Top S-Grade rewards
			items = new int[]{9447, 9384, 9449, 9380, 9448, 9443, 9450, 10253, 9445, 9442, 9446, 10004, 10252, 9376, 9444};
		else if(group >= 5 && group < 8) //S80-Grade rewards
			items = new int[]{10226, 10217, 10224, 10215, 10225, 10223, 10220, 10415, 10216, 10221, 10219, 10218, 10222};
		else if(group == 8) //Low S84-Grade rewards
			items = new int[]{13467, 13462, 13464, 13461, 13465, 13468, 13463, 13470, 13460, 52, 13466, 13459, 13457, 13469, 13458};

		ItemFunctions.addItem(player, items[Rnd.get(items.length)], 1);
	}

	// Golden Spice Crate
	private void use15482(Player player, boolean ctrl)
	{
		if(Rnd.chance(10))
		{
			ItemFunctions.addItem(player, 15474, 40);
			if(Rnd.chance(50))
				ItemFunctions.addItem(player, 15476, 5);
			else
				ItemFunctions.addItem(player, 15478, 5);
		}
		else
			ItemFunctions.addItem(player, 15474, 50);
	}

	// Crystal Spice Crate
	private void use15483(Player player, boolean ctrl)
	{
		if(Rnd.chance(10))
		{
			ItemFunctions.addItem(player, 15475, 40);
			if(Rnd.chance(50))
				ItemFunctions.addItem(player, 15477, 5);
			else
				ItemFunctions.addItem(player, 15479, 5);
		}
		else
			ItemFunctions.addItem(player, 15475, 50);
	}

	// Gold Maned Lion Mounting Bracelet 7 Day Pack
	private void use14231(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 14053, 1);
	}

	// Steam Beatle Mounting Bracelet 7 Day Pack
	private void use14232(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 14054, 1);
	}

	// Birthday Present Pack
	private void use21169(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, 21170, 3);
		ItemFunctions.addItem(player, 21595, 1);
		ItemFunctions.addItem(player, 13488, 1);
	}

	// Pablo's Box
	private void use21753(Player player, boolean ctrl)
	{
		int category = Rnd.get(7);
		switch(category)
		{
			case 0:
				ItemFunctions.addItem(player, 21122, 1);
				break;
			case 1:
				ItemFunctions.addItem(player, 21118, 1);
				break;
			case 2:
				ItemFunctions.addItem(player, 21116, 1);
				break;
			case 3:
				ItemFunctions.addItem(player, 21114, 1);
				break;
			case 4:
				ItemFunctions.addItem(player, 21112, 1);
				break;
			case 5:
				ItemFunctions.addItem(player, 21120, 1);
				break;
			case 6:
				ItemFunctions.addItem(player, 21126, 1);
				break;
		}
	}

	// Rune Jewelry Box - Talisman
	private void use21752(Player player, boolean ctrl)
	{
		final List<Integer> talismans = new ArrayList<Integer>();

		//9914-9965
		for(int i = 9914; i <= 9965; i++)
			if(i != 9923)
				talismans.add(i);
		//10416-10424
		for(int i = 10416; i <= 10424; i++)
			talismans.add(i);
		//10518-10519
		for(int i = 10518; i <= 10519; i++)
			talismans.add(i);
		//10533-10543
		for(int i = 10533; i <= 10543; i++)
			talismans.add(i);

		ItemFunctions.addItem(player, talismans.get(Rnd.get(talismans.size())), 1);
	}

	// Moirai Armor Box
	private void use17073(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, Rnd.get(15694, 15710), 1);
	}

	// Icarus Weapon Box
	private void use17070(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, Rnd.get(10215, 10226), 1);
	}

	// Vorpal Armor Box
	private void use22203(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, Rnd.get(15743, 15759), 1);
	}

	// S85 weapon box
	private void use22202(Player player, boolean ctrl)
	{
		ItemFunctions.addItem(player, Rnd.get(15558, 15571), 1);
	}

	private static void extract_item_r(int[] list, int[] counts, int[] chances, Player player)
	{
		int sum = 0;

		for(int i = 0; i < list.length; i++)
			sum += chances[i];

		int[] table = new int[sum];
		int k = 0;

		for(int i = 0; i < list.length; i++)
			for(int j = 0; j < chances[i]; j++)
			{
				table[k] = i;
				k++;
			}

		int i = table[Rnd.get(table.length)];
		int item = list[i];
		int count = counts[i];

		ItemFunctions.addItem(player, item, count);
	}

	private static boolean canBeExtracted(Player player, ItemInstance item)
	{
		if(player.getWeightPenalty() >= 3 || player.getUsedInventoryPercents() >= 90)
		{
			player.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL, new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return false;
		}
		return true;
	}

	private static boolean extractRandomOneItem(Player player, int[][] items, double[] chances)
	{
		if(items.length != chances.length)
			return false;

		double extractChance = 0;
		for(double c : chances)
			extractChance += c;

		if(Rnd.chance(extractChance))
		{
			int[] successfulItems = new int[0];
			while(successfulItems.length == 0)
				for(int i = 0; i < items.length; i++)
					if(Rnd.chance(chances[i]))
						successfulItems = ArrayUtils.add(successfulItems, i);
			int[] item = items[successfulItems[Rnd.get(successfulItems.length)]];
			if(item.length < 2)
				return false;

			ItemFunctions.addItem(player, item[0], item[1]);
		}
		return true;
	}
}