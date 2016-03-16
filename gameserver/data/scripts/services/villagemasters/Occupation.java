package services.villagemasters;

import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.base.Race;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.VillageMasterInstance;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.utils.ItemFunctions;

public class Occupation
{
	@Bypass("services.villagemasters.Occupation:onTalk30026")
	public void onTalk30026(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		//fighter
		if(classId == ClassId.fighter)
			htmltext = "bitz003h.htm";

		//warrior, knight, rogue
		else if(classId == ClassId.warrior || classId == ClassId.knight || classId == ClassId.rogue)
			htmltext = "bitz004.htm";
		//warlord, paladin, treasureHunter
		else if(classId == ClassId.warlord || classId == ClassId.paladin || classId == ClassId.treasureHunter)
			htmltext = "bitz005.htm";
		//gladiator, darkAvenger, hawkeye
		else if(classId == ClassId.gladiator || classId == ClassId.darkAvenger || classId == ClassId.hawkeye)
			htmltext = "bitz005.htm";
		else
			htmltext = "bitz002.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30026/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30031")
	public void onTalk30031(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.wizard || classId == ClassId.cleric)
			htmltext = "06.htm";
		else if(classId == ClassId.sorceror || classId == ClassId.necromancer || classId == ClassId.warlock || classId == ClassId.bishop || classId == ClassId.prophet)
			htmltext = "07.htm";
		else if(classId == ClassId.mage)
			htmltext = "01.htm";
		else
			// All other Races must be out
			htmltext = "08.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30031/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30037")
	public void onTalk30037(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.elvenMage)
			htmltext = "01.htm";
		else if(classId == ClassId.mage)
			htmltext = "08.htm";
		else if(classId == ClassId.wizard || classId == ClassId.cleric || classId == ClassId.elvenWizard || classId == ClassId.oracle)
			htmltext = "31.htm";
		else if(classId == ClassId.sorceror || classId == ClassId.necromancer || classId == ClassId.bishop || classId == ClassId.warlock || classId == ClassId.prophet)
			htmltext = "32.htm";
		else if(classId == ClassId.spellsinger || classId == ClassId.elder || classId == ClassId.elementalSummoner)
			htmltext = "32.htm";
		else
			htmltext = "33.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30037/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onChange30037")
	public void onChange30037(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		int MARK_OF_FAITH_ID = 1201;
		int ETERNITY_DIAMOND_ID = 1230;
		int LEAF_OF_ORACLE_ID = 1235;
		int BEAD_OF_SEASON_ID = 1292;
		int classid = Integer.parseInt(args[0]);

		int Level = pl.getLevel();
		String htmltext = "33.htm";

		if(classid == 26 && pl.getClassId() == ClassId.elvenMage)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, ETERNITY_DIAMOND_ID) == 0)
				htmltext = "15.htm";
			else if(Level <= 19 && ItemFunctions.getItemCount(pl, ETERNITY_DIAMOND_ID) > 0)
				htmltext = "16.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, ETERNITY_DIAMOND_ID) == 0)
				htmltext = "17.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, ETERNITY_DIAMOND_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, ETERNITY_DIAMOND_ID, 1);
				pl.setClassId(classid, false, true);
				htmltext = "18.htm";
			}
		}
		else if(classid == 29 && pl.getClassId() == ClassId.elvenMage)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, LEAF_OF_ORACLE_ID) == 0)
				htmltext = "19.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, LEAF_OF_ORACLE_ID) > 0)
				htmltext = "20.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, LEAF_OF_ORACLE_ID) == 0)
				htmltext = "21.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, LEAF_OF_ORACLE_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, LEAF_OF_ORACLE_ID, 1);
				pl.setClassId(classid, false, true);
				htmltext = "22.htm";
			}
		}
		else if(classid == 11 && pl.getClassId() == ClassId.mage)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, BEAD_OF_SEASON_ID) == 0)
				htmltext = "23.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, BEAD_OF_SEASON_ID) > 0)
				htmltext = "24.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, BEAD_OF_SEASON_ID) == 0)
				htmltext = "25.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, BEAD_OF_SEASON_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, BEAD_OF_SEASON_ID, 1);
				pl.setClassId(classid, false, true);
				htmltext = "26.htm";
			}
		}
		else if(classid == 15 && pl.getClassId() == ClassId.mage)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, MARK_OF_FAITH_ID) == 0)
				htmltext = "27.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, MARK_OF_FAITH_ID) > 0)
				htmltext = "28.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, MARK_OF_FAITH_ID) == 0)
				htmltext = "29.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, MARK_OF_FAITH_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, MARK_OF_FAITH_ID, 1);
				pl.setClassId(classid, false, true);
				htmltext = "30.htm";
			}
		}

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30037/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30066")
	public void onTalk30066(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.elvenFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.fighter)
			htmltext = "08.htm";
		else if(classId == ClassId.elvenKnight || classId == ClassId.elvenScout || classId == ClassId.warrior || classId == ClassId.knight || classId == ClassId.rogue)
			htmltext = "38.htm";
		else if(classId == ClassId.templeKnight || classId == ClassId.plainsWalker || classId == ClassId.swordSinger || classId == ClassId.silverRanger)
			htmltext = "39.htm";
		else if(classId == ClassId.warlord || classId == ClassId.paladin || classId == ClassId.treasureHunter)
			htmltext = "39.htm";
		else if(classId == ClassId.gladiator || classId == ClassId.darkAvenger || classId == ClassId.hawkeye)
			htmltext = "39.htm";
		else
			htmltext = "40.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30066/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onChange30066")
	public void onChange30066(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		int MEDALLION_OF_WARRIOR_ID = 1145;
		int SWORD_OF_RITUAL_ID = 1161;
		int BEZIQUES_RECOMMENDATION_ID = 1190;
		int ELVEN_KNIGHT_BROOCH_ID = 1204;
		int REORIA_RECOMMENDATION_ID = 1217;
		int newclass = Integer.parseInt(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(newclass == 19 && classId == ClassId.elvenFighter)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, ELVEN_KNIGHT_BROOCH_ID) == 0)
				htmltext = "18.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, ELVEN_KNIGHT_BROOCH_ID) > 0)
				htmltext = "19.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, ELVEN_KNIGHT_BROOCH_ID) == 0)
				htmltext = "20.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, ELVEN_KNIGHT_BROOCH_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, ELVEN_KNIGHT_BROOCH_ID, 1);
				pl.setClassId(newclass, false, true);
				htmltext = "21.htm";
			}
		}

		if(newclass == 22 && classId == ClassId.elvenFighter)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, REORIA_RECOMMENDATION_ID) == 0)
				htmltext = "22.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, REORIA_RECOMMENDATION_ID) > 0)
				htmltext = "23.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, REORIA_RECOMMENDATION_ID) == 0)
				htmltext = "24.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, REORIA_RECOMMENDATION_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, REORIA_RECOMMENDATION_ID, 1);
				pl.setClassId(newclass, false, true);
				htmltext = "25.htm";
			}
		}

		if(newclass == 1 && classId == ClassId.fighter)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, MEDALLION_OF_WARRIOR_ID) == 0)
				htmltext = "26.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, MEDALLION_OF_WARRIOR_ID) > 0)
				htmltext = "27.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, MEDALLION_OF_WARRIOR_ID) == 0)
				htmltext = "28.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, MEDALLION_OF_WARRIOR_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, MEDALLION_OF_WARRIOR_ID, 1);
				pl.setClassId(newclass, false, true);
				htmltext = "29.htm";
			}
		}

		if(newclass == 4 && classId == ClassId.fighter)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, SWORD_OF_RITUAL_ID) == 0)
				htmltext = "30.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, SWORD_OF_RITUAL_ID) > 0)
				htmltext = "31.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, SWORD_OF_RITUAL_ID) == 0)
				htmltext = "32.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, SWORD_OF_RITUAL_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, SWORD_OF_RITUAL_ID, 1);
				pl.setClassId(newclass, false, true);
				htmltext = "33.htm";
			}
		}

		if(newclass == 7 && classId == ClassId.fighter)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, BEZIQUES_RECOMMENDATION_ID) == 0)
				htmltext = "34.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, BEZIQUES_RECOMMENDATION_ID) > 0)
				htmltext = "35.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, BEZIQUES_RECOMMENDATION_ID) == 0)
				htmltext = "36.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, BEZIQUES_RECOMMENDATION_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, BEZIQUES_RECOMMENDATION_ID, 1);
				pl.setClassId(newclass, false, true);
				htmltext = "37.htm";
			}
		}

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30066/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30511")
	public void onTalk30511(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.scavenger)
			htmltext = "01.htm";
		else if(classId == ClassId.dwarvenFighter)
			htmltext = "09.htm";
		else if(classId == ClassId.bountyHunter || classId == ClassId.warsmith)
			htmltext = "10.htm";
		else
			htmltext = "11.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30511/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onChange30511")
	public void onChange30511(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		int MARK_OF_SEARCHER_ID = 2809;
		int MARK_OF_GUILDSMAN_ID = 3119;
		int MARK_OF_PROSPERITY_ID = 3238;
		int newclass = Integer.parseInt(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(newclass == 55 && classId == ClassId.scavenger)
			if(Level <= 39)
			{
				if(ItemFunctions.getItemCount(pl, MARK_OF_SEARCHER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_GUILDSMAN_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_PROSPERITY_ID) == 0)
					htmltext = "05.htm";
				else
					htmltext = "06.htm";
			}
			else if(ItemFunctions.getItemCount(pl, MARK_OF_SEARCHER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_GUILDSMAN_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_PROSPERITY_ID) == 0)
				htmltext = "07.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_SEARCHER_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_GUILDSMAN_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_PROSPERITY_ID, 1);
				pl.setClassId(newclass, false, true);
				htmltext = "08.htm";
			}

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30511/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30070")
	public void onTalk30070(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.elvenMage)
			htmltext = "01.htm";
		else if(classId == ClassId.wizard || classId == ClassId.cleric || classId == ClassId.elvenWizard || classId == ClassId.oracle)
			htmltext = "31.htm";
		else if(classId == ClassId.sorceror || classId == ClassId.necromancer || classId == ClassId.bishop || classId == ClassId.warlock || classId == ClassId.prophet || classId == ClassId.spellsinger || classId == ClassId.elder || classId == ClassId.elementalSummoner)
			htmltext = "32.htm";
		else if(classId == ClassId.mage)
			htmltext = "08.htm";
		else
			htmltext = "33.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30070/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onChange30070")
	public void onChange30070(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		int MARK_OF_FAITH_ID = 1201;
		int ETERNITY_DIAMOND_ID = 1230;
		int LEAF_OF_ORACLE_ID = 1235;
		int BEAD_OF_SEASON_ID = 1292;
		int event = Integer.parseInt(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 26 && classId == ClassId.elvenMage)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, ETERNITY_DIAMOND_ID) == 0)
				htmltext = "15.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, ETERNITY_DIAMOND_ID) > 0)
				htmltext = "16.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, ETERNITY_DIAMOND_ID) == 0)
				htmltext = "17.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, ETERNITY_DIAMOND_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, ETERNITY_DIAMOND_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "18.htm";
			}
		}
		else if(event == 29 && classId == ClassId.elvenMage)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, LEAF_OF_ORACLE_ID) == 0)
				htmltext = "19.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, LEAF_OF_ORACLE_ID) > 0)
				htmltext = "20.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, LEAF_OF_ORACLE_ID) == 0)
				htmltext = "21.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, LEAF_OF_ORACLE_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, LEAF_OF_ORACLE_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "22.htm";
			}
		}
		else if(event == 11 && classId == ClassId.mage)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, BEAD_OF_SEASON_ID) == 0)
				htmltext = "23.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, BEAD_OF_SEASON_ID) > 0)
				htmltext = "24.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, BEAD_OF_SEASON_ID) == 0)
				htmltext = "25.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, BEAD_OF_SEASON_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, BEAD_OF_SEASON_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "26.htm";
			}
		}
		else if(event == 15 && classId == ClassId.mage)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, MARK_OF_FAITH_ID) == 0)
				htmltext = "27.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, MARK_OF_FAITH_ID) > 0)
				htmltext = "28.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, MARK_OF_FAITH_ID) == 0)
				htmltext = "29.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, MARK_OF_FAITH_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, MARK_OF_FAITH_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "30.htm";
			}
		}

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30070/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30154")
	public void onTalk30154(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.elvenFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.elvenMage)
			htmltext = "02.htm";
		else if(classId == ClassId.elvenWizard || classId == ClassId.oracle || classId == ClassId.elvenKnight || classId == ClassId.elvenScout)
			htmltext = "12.htm";
		else if(pl.getRace() == Race.elf)
			htmltext = "13.htm";
		else
			htmltext = "11.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30154/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30358")
	public void onTalk30358(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.darkFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.darkMage)
			htmltext = "02.htm";
		else if(classId == ClassId.darkWizard || classId == ClassId.shillienOracle || classId == ClassId.palusKnight || classId == ClassId.assassin)
			htmltext = "12.htm";
		else if(pl.getRace() == Race.darkelf)
			htmltext = "13.htm";
		else
			htmltext = "11.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30358/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30498")
	public void onTalk30498(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.dwarvenFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.scavenger || classId == ClassId.artisan)
			htmltext = "09.htm";
		else if(pl.getRace() == Race.dwarf)
			htmltext = "10.htm";
		else
			htmltext = "11.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30498/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onChange30498")
	public void onChange30498(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		int RING_OF_RAVEN_ID = 1642;
		int event = Integer.parseInt(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 54 && classId == ClassId.dwarvenFighter)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, RING_OF_RAVEN_ID) == 0)
				htmltext = "05.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, RING_OF_RAVEN_ID) > 0)
				htmltext = "06.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, RING_OF_RAVEN_ID) == 0)
				htmltext = "07.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, RING_OF_RAVEN_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, RING_OF_RAVEN_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "08.htm";
			}
		}

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30498/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30499")
	public void onTalk30499(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.dwarvenFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.scavenger || classId == ClassId.artisan)
			htmltext = "09.htm";
		else if(pl.getRace() == Race.dwarf)
			htmltext = "10.htm";
		else
			htmltext = "11.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30499/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onChange30499")
	public void onChange30499(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		int PASS_FINAL_ID = 1635;
		int event = Integer.parseInt(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 56 && classId == ClassId.dwarvenFighter)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, PASS_FINAL_ID) == 0)
				htmltext = "05.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, PASS_FINAL_ID) > 0)
				htmltext = "06.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, PASS_FINAL_ID) == 0)
				htmltext = "07.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, PASS_FINAL_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, PASS_FINAL_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "08.htm";
			}
		}

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30499/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30525")
	public void onTalk30525(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.dwarvenFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.artisan)
			htmltext = "05.htm";
		else if(classId == ClassId.warsmith)
			htmltext = "06.htm";
		else
			htmltext = "07.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30525/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30520")
	public void onTalk30520(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.dwarvenFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.artisan || classId == ClassId.scavenger)
			htmltext = "05.htm";
		else if(classId == ClassId.warsmith || classId == ClassId.bountyHunter)
			htmltext = "06.htm";
		else
			htmltext = "07.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30520/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30512")
	public void onTalk30512(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.artisan)
			htmltext = "01.htm";
		else if(classId == ClassId.dwarvenFighter)
			htmltext = "09.htm";
		else if(classId == ClassId.warsmith || classId == ClassId.bountyHunter)
			htmltext = "10.htm";
		else
			htmltext = "11.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30512/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onChange30512")
	public void onChange30512(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		int MARK_OF_MAESTRO_ID = 2867;
		int MARK_OF_GUILDSMAN_ID = 3119;
		int MARK_OF_PROSPERITY_ID = 3238;
		int event = Integer.parseInt(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 57 && classId == ClassId.artisan)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_MAESTRO_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_GUILDSMAN_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_PROSPERITY_ID) == 0)
					htmltext = "05.htm";
				else
					htmltext = "06.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_MAESTRO_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_GUILDSMAN_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_PROSPERITY_ID) == 0)
				htmltext = "07.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_GUILDSMAN_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_MAESTRO_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_PROSPERITY_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "08.htm";
			}

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30512/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30565")
	public void onTalk30565(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.orcFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.orcRaider || classId == ClassId.orcMonk || classId == ClassId.orcShaman)
			htmltext = "09.htm";
		else if(classId == ClassId.orcMage)
			htmltext = "16.htm";
		else if(pl.getRace() == Race.orc)
			htmltext = "10.htm";
		else
			htmltext = "11.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30565/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30109")
	public void onTalk30109(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.elvenKnight)
			htmltext = "01.htm";
		else if(classId == ClassId.knight)
			htmltext = "08.htm";
		else if(classId == ClassId.rogue)
			htmltext = "15.htm";
		else if(classId == ClassId.elvenScout)
			htmltext = "22.htm";
		else if(classId == ClassId.warrior)
			htmltext = "29.htm";
		else if(classId == ClassId.elvenFighter || classId == ClassId.fighter)
			htmltext = "76.htm";
		else if(classId == ClassId.templeKnight || classId == ClassId.plainsWalker || classId == ClassId.swordSinger || classId == ClassId.silverRanger)
			htmltext = "77.htm";
		else if(classId == ClassId.warlord || classId == ClassId.paladin || classId == ClassId.treasureHunter)
			htmltext = "77.htm";
		else if(classId == ClassId.gladiator || classId == ClassId.darkAvenger || classId == ClassId.hawkeye)
			htmltext = "77.htm";
		else
			htmltext = "78.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30109/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onChange30109")
	public void onChange30109(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		int MARK_OF_CHALLENGER_ID = 2627;
		int MARK_OF_DUTY_ID = 2633;
		int MARK_OF_SEEKER_ID = 2673;
		int MARK_OF_TRUST_ID = 2734;
		int MARK_OF_DUELIST_ID = 2762;
		int MARK_OF_SEARCHER_ID = 2809;
		int MARK_OF_HEALER_ID = 2820;
		int MARK_OF_LIFE_ID = 3140;
		int MARK_OF_CHAMPION_ID = 3276;
		int MARK_OF_SAGITTARIUS_ID = 3293;
		int MARK_OF_WITCHCRAFT_ID = 3307;
		int event = Integer.parseInt(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 20 && classId == ClassId.elvenKnight)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_DUTY_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_LIFE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_HEALER_ID) == 0)
					htmltext = "36.htm";
				else
					htmltext = "37.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_DUTY_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_LIFE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_HEALER_ID) == 0)
				htmltext = "38.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_DUTY_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_LIFE_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_HEALER_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "39.htm";
			}

		else if(event == 21 && classId == ClassId.elvenKnight)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_CHALLENGER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_LIFE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_DUELIST_ID) == 0)
					htmltext = "40.htm";
				else
					htmltext = "41.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_CHALLENGER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_LIFE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_DUELIST_ID) == 0)
				htmltext = "42.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_CHALLENGER_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_LIFE_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_DUELIST_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "43.htm";
			}

		else if(event == 5 && classId == ClassId.knight)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_DUTY_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_HEALER_ID) == 0)
					htmltext = "44.htm";
				else
					htmltext = "45.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_DUTY_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_HEALER_ID) == 0)
				htmltext = "46.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_DUTY_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_TRUST_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_HEALER_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "47.htm";
			}

		else if(event == 6 && classId == ClassId.knight)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_DUTY_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_WITCHCRAFT_ID) == 0)
					htmltext = "48.htm";
				else
					htmltext = "49.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_DUTY_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_WITCHCRAFT_ID) == 0)
				htmltext = "50.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_DUTY_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_TRUST_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_WITCHCRAFT_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "51.htm";
			}

		else if(event == 8 && classId == ClassId.rogue)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_SEEKER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_SEARCHER_ID) == 0)
					htmltext = "52.htm";
				else
					htmltext = "53.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_SEEKER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_SEARCHER_ID) == 0)
				htmltext = "54.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_SEEKER_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_TRUST_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_SEARCHER_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "55.htm";
			}

		else if(event == 9 && classId == ClassId.rogue)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_SEEKER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_SAGITTARIUS_ID) == 0)
					htmltext = "56.htm";
				else
					htmltext = "57.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_SEEKER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_SAGITTARIUS_ID) == 0)
				htmltext = "58.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_SEEKER_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_TRUST_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_SAGITTARIUS_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "59.htm";
			}

		else if(event == 23 && classId == ClassId.elvenScout)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_SEEKER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_LIFE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_SEARCHER_ID) == 0)
					htmltext = "60.htm";
				else
					htmltext = "61.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_SEEKER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_LIFE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_SEARCHER_ID) == 0)
				htmltext = "62.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_SEEKER_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_LIFE_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_SEARCHER_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "63.htm";
			}

		else if(event == 24 && classId == ClassId.elvenScout)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_SEEKER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_LIFE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_SAGITTARIUS_ID) == 0)
					htmltext = "64.htm";
				else
					htmltext = "65.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_SEEKER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_LIFE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_SAGITTARIUS_ID) == 0)
				htmltext = "66.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_SEEKER_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_LIFE_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_SAGITTARIUS_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "67.htm";
			}

		else if(event == 2 && classId == ClassId.warrior)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_CHALLENGER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_DUELIST_ID) == 0)
					htmltext = "68.htm";
				else
					htmltext = "69.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_CHALLENGER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_DUELIST_ID) == 0)
				htmltext = "70.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_CHALLENGER_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_TRUST_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_DUELIST_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "71.htm";
			}

		else if(event == 3 && classId == ClassId.warrior)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_CHALLENGER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_CHAMPION_ID) == 0)
					htmltext = "72.htm";
				else
					htmltext = "73.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_CHALLENGER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_CHAMPION_ID) == 0)
				htmltext = "74.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_CHALLENGER_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_TRUST_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_CHAMPION_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "75.htm";
			}

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30109/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30115")
	public void onTalk30115(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.elvenWizard)
			htmltext = "01.htm";
		else if(classId == ClassId.wizard)
			htmltext = "08.htm";
		else if(classId == ClassId.sorceror || classId == ClassId.necromancer || classId == ClassId.warlock)
			htmltext = "39.htm";
		else if(classId == ClassId.spellsinger || classId == ClassId.elementalSummoner)
			htmltext = "39.htm";
		else if((pl.getRace() == Race.elf || pl.getRace() == Race.human) && classId.isMage())
			htmltext = "38.htm";
		else
			htmltext = "40.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30115/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onChange30115")
	public void onChange30115(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		int MARK_OF_SCHOLAR_ID = 2674;
		int MARK_OF_TRUST_ID = 2734;
		int MARK_OF_MAGUS_ID = 2840;
		int MARK_OF_LIFE_ID = 3140;
		int MARK_OF_WITCHCRFAT_ID = 3307;
		int MARK_OF_SUMMONER_ID = 3336;
		int event = Integer.parseInt(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 27 && classId == ClassId.elvenWizard)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_SCHOLAR_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_LIFE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_MAGUS_ID) == 0)
					htmltext = "18.htm";
				else
					htmltext = "19.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_SCHOLAR_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_LIFE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_MAGUS_ID) == 0)
				htmltext = "20.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_SCHOLAR_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_LIFE_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_MAGUS_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "21.htm";
			}

		else if(event == 28 && classId == ClassId.elvenWizard)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_SCHOLAR_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_LIFE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_SUMMONER_ID) == 0)
					htmltext = "22.htm";
				else
					htmltext = "23.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_SCHOLAR_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_LIFE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_SUMMONER_ID) == 0)
				htmltext = "24.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_SCHOLAR_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_LIFE_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_SUMMONER_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "25.htm";
			}

		else if(event == 12 && classId == ClassId.wizard)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_SCHOLAR_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_MAGUS_ID) == 0)
					htmltext = "26.htm";
				else
					htmltext = "27.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_SCHOLAR_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_MAGUS_ID) == 0)
				htmltext = "28.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_SCHOLAR_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_TRUST_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_MAGUS_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "29.htm";
			}

		else if(event == 13 && classId == ClassId.wizard)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_SCHOLAR_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_WITCHCRFAT_ID) == 0)
					htmltext = "30.htm";
				else
					htmltext = "31.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_SCHOLAR_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_WITCHCRFAT_ID) == 0)
				htmltext = "32.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_SCHOLAR_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_TRUST_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_WITCHCRFAT_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "33.htm";
			}

		else if(event == 14 && classId == ClassId.wizard)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_SCHOLAR_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_SUMMONER_ID) == 0)
					htmltext = "34.htm";
				else
					htmltext = "35.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_SCHOLAR_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_SUMMONER_ID) == 0)
				htmltext = "36.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_SCHOLAR_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_TRUST_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_SUMMONER_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "37.htm";
			}

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30115/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30120")
	public void onTalk30120(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.oracle)
			htmltext = "01.htm";
		else if(classId == ClassId.cleric)
			htmltext = "05.htm";
		else if(classId == ClassId.elder || classId == ClassId.bishop || classId == ClassId.prophet)
			htmltext = "25.htm";
		else if((pl.getRace() == Race.human || pl.getRace() == Race.elf) && classId.isMage())
			htmltext = "24.htm";
		else
			htmltext = "26.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30120/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onChange30120")
	public void onChange30120(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		int MARK_OF_PILGRIM_ID = 2721;
		int MARK_OF_TRUST_ID = 2734;
		int MARK_OF_HEALER_ID = 2820;
		int MARK_OF_REFORMER_ID = 2821;
		int MARK_OF_LIFE_ID = 3140;
		int event = Integer.parseInt(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 30 || classId == ClassId.oracle)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_PILGRIM_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_LIFE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_HEALER_ID) == 0)
					htmltext = "12.htm";
				else
					htmltext = "13.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_PILGRIM_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_LIFE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_HEALER_ID) == 0)
				htmltext = "14.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_PILGRIM_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_LIFE_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_HEALER_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "15.htm";
			}

		else if(event == 16 && classId == ClassId.cleric)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_PILGRIM_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_HEALER_ID) == 0)
					htmltext = "16.htm";
				else
					htmltext = "17.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_PILGRIM_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_HEALER_ID) == 0)
				htmltext = "18.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_PILGRIM_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_TRUST_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_HEALER_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "19.htm";
			}

		else if(event == 17 && classId == ClassId.cleric)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_PILGRIM_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_REFORMER_ID) == 0)
					htmltext = "20.htm";
				else
					htmltext = "21.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_PILGRIM_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_TRUST_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_REFORMER_ID) == 0)
				htmltext = "22.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_PILGRIM_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_TRUST_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_REFORMER_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "23.htm";
			}

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30120/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30500")
	public void onTalk30500(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.orcFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.orcMage)
			htmltext = "06.htm";
		else if(classId == ClassId.orcRaider || classId == ClassId.orcMonk || classId == ClassId.orcShaman)
			htmltext = "21.htm";
		else if(classId == ClassId.destroyer || classId == ClassId.tyrant || classId == ClassId.overlord || classId == ClassId.warcryer)
			htmltext = "22.htm";
		else
			htmltext = "23.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30500/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onChange30500")
	public void onChange30500(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		int MARK_OF_RAIDER_ID = 1592;
		int KHAVATARI_TOTEM_ID = 1615;
		int MASK_OF_MEDIUM_ID = 1631;
		int event = Integer.parseInt(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 45 && classId == ClassId.orcFighter)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, MARK_OF_RAIDER_ID) == 0)
				htmltext = "09.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, MARK_OF_RAIDER_ID) > 0)
				htmltext = "10.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, MARK_OF_RAIDER_ID) == 0)
				htmltext = "11.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, MARK_OF_RAIDER_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, MARK_OF_RAIDER_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "12.htm";
			}
		}

		else if(event == 47 && classId == ClassId.orcFighter)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, KHAVATARI_TOTEM_ID) == 0)
				htmltext = "13.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, KHAVATARI_TOTEM_ID) > 0)
				htmltext = "14.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, KHAVATARI_TOTEM_ID) == 0)
				htmltext = "15.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, KHAVATARI_TOTEM_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, KHAVATARI_TOTEM_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "16.htm";
			}
		}

		else if(event == 50 && classId == ClassId.orcMage)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, MASK_OF_MEDIUM_ID) == 0)
				htmltext = "17.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, MASK_OF_MEDIUM_ID) > 0)
				htmltext = "18.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, MASK_OF_MEDIUM_ID) == 0)
				htmltext = "19.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, MASK_OF_MEDIUM_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, MASK_OF_MEDIUM_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "20.htm";
			}
		}

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30500/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30290")
	public void onTalk30290(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.darkFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.darkMage)
			htmltext = "08.htm";
		else if(classId == ClassId.palusKnight || classId == ClassId.assassin || classId == ClassId.darkWizard || classId == ClassId.shillienOracle)
			htmltext = "31.htm";
		else if(pl.getRace() == Race.darkelf)
			htmltext = "32.htm";
		else
			htmltext = "33.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30290/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onChange30290")
	public void onChange30290(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		int GAZE_OF_ABYSS_ID = 1244;
		int IRON_HEART_ID = 1252;
		int JEWEL_OF_DARKNESS_ID = 1261;
		int ORB_OF_ABYSS_ID = 1270;
		int event = Integer.parseInt(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 32 && classId == ClassId.darkFighter)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, GAZE_OF_ABYSS_ID) == 0)
				htmltext = "15.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, GAZE_OF_ABYSS_ID) > 0)
				htmltext = "16.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, GAZE_OF_ABYSS_ID) == 0)
				htmltext = "17.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, GAZE_OF_ABYSS_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, GAZE_OF_ABYSS_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "18.htm";
			}
		}

		else if(event == 35 && classId == ClassId.darkFighter)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, IRON_HEART_ID) == 0)
				htmltext = "19.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, IRON_HEART_ID) > 0)
				htmltext = "20.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, IRON_HEART_ID) == 0)
				htmltext = "21.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, IRON_HEART_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, IRON_HEART_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "22.htm";
			}
		}

		else if(event == 39 && classId == ClassId.darkMage)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, JEWEL_OF_DARKNESS_ID) == 0)
				htmltext = "23.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, JEWEL_OF_DARKNESS_ID) > 0)
				htmltext = "24.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, JEWEL_OF_DARKNESS_ID) == 0)
				htmltext = "25.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, JEWEL_OF_DARKNESS_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, JEWEL_OF_DARKNESS_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "26.htm";
			}
		}

		else if(event == 42 && classId == ClassId.darkMage)
		{
			if(Level <= 19 && ItemFunctions.getItemCount(pl, ORB_OF_ABYSS_ID) == 0)
				htmltext = "27.htm";
			if(Level <= 19 && ItemFunctions.getItemCount(pl, ORB_OF_ABYSS_ID) > 0)
				htmltext = "28.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, ORB_OF_ABYSS_ID) == 0)
				htmltext = "29.htm";
			if(Level >= 20 && ItemFunctions.getItemCount(pl, ORB_OF_ABYSS_ID) > 0)
			{
				ItemFunctions.deleteItem(pl, ORB_OF_ABYSS_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "30.htm";
			}
		}

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30290/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30513")
	public void onTalk30513(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.orcMonk)
			htmltext = "01.htm";
		else if(classId == ClassId.orcRaider)
			htmltext = "05.htm";
		else if(classId == ClassId.orcShaman)
			htmltext = "09.htm";
		else if(classId == ClassId.destroyer || classId == ClassId.tyrant || classId == ClassId.overlord || classId == ClassId.warcryer)
			htmltext = "32.htm";
		else if(classId == ClassId.orcFighter || classId == ClassId.orcMage)
			htmltext = "33.htm";
		else
			htmltext = "34.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30513/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onChange30513")
	public void onChange30513(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		int MARK_OF_CHALLENGER_ID = 2627;
		int MARK_OF_PILGRIM_ID = 2721;
		int MARK_OF_DUELIST_ID = 2762;
		int MARK_OF_WARSPIRIT_ID = 2879;
		int MARK_OF_GLORY_ID = 3203;
		int MARK_OF_CHAMPION_ID = 3276;
		int MARK_OF_LORD_ID = 3390;
		int event = Integer.parseInt(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 48 && classId == ClassId.orcMonk)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_CHALLENGER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_GLORY_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_DUELIST_ID) == 0)
					htmltext = "16.htm";
				else
					htmltext = "17.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_CHALLENGER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_GLORY_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_DUELIST_ID) == 0)
				htmltext = "18.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_CHALLENGER_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_GLORY_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_DUELIST_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "19.htm";
			}

		else if(event == 46 && classId == ClassId.orcRaider)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_CHALLENGER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_GLORY_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_CHAMPION_ID) == 0)
					htmltext = "20.htm";
				else
					htmltext = "21.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_CHALLENGER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_GLORY_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_CHAMPION_ID) == 0)
				htmltext = "22.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_CHALLENGER_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_GLORY_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_CHAMPION_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "23.htm";
			}

		else if(event == 51 && classId == ClassId.orcShaman)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_PILGRIM_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_GLORY_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_LORD_ID) == 0)
					htmltext = "24.htm";
				else
					htmltext = "25.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_PILGRIM_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_GLORY_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_LORD_ID) == 0)
				htmltext = "26.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_PILGRIM_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_GLORY_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_LORD_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "27.htm";
			}

		else if(event == 52 && classId == ClassId.orcShaman)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_PILGRIM_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_GLORY_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_WARSPIRIT_ID) == 0)
					htmltext = "28.htm";
				else
					htmltext = "29.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_PILGRIM_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_GLORY_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_WARSPIRIT_ID) == 0)
				htmltext = "30.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_PILGRIM_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_GLORY_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_WARSPIRIT_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "31.htm";
			}

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30513/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk30474")
	public void onTalk30474(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(npc.getNpcId() == 30175)
		{
			if(classId == ClassId.shillienOracle)
				htmltext = "08.htm";
			else if(classId == ClassId.darkWizard)
				htmltext = "19.htm";
			else if(classId == ClassId.spellhowler || classId == ClassId.shillienElder || classId == ClassId.phantomSummoner)
				htmltext = "54.htm";
			else if(classId == ClassId.darkMage)
				htmltext = "55.htm";
			else
				htmltext = "56.htm";
		}
		else if(classId == ClassId.palusKnight)
			htmltext = "01.htm";
		else if(classId == ClassId.shillienOracle)
			htmltext = "08.htm";
		else if(classId == ClassId.assassin)
			htmltext = "12.htm";
		else if(classId == ClassId.darkWizard)
			htmltext = "19.htm";
		else if(classId == ClassId.shillienKnight || classId == ClassId.abyssWalker || classId == ClassId.bladedancer || classId == ClassId.phantomRanger)
			htmltext = "54.htm";
		else if(classId == ClassId.spellhowler || classId == ClassId.shillienElder || classId == ClassId.phantomSummoner)
			htmltext = "54.htm";
		else if(classId == ClassId.darkFighter || classId == ClassId.darkMage)
			htmltext = "55.htm";
		else
			htmltext = "56.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30474/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onChange30474")
	public void onChange30474(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		int MARK_OF_CHALLENGER_ID = 2627;
		int MARK_OF_DUTY_ID = 2633;
		int MARK_OF_SEEKER_ID = 2673;
		int MARK_OF_SCHOLAR_ID = 2674;
		int MARK_OF_PILGRIM_ID = 2721;
		int MARK_OF_DUELIST_ID = 2762;
		int MARK_OF_SEARCHER_ID = 2809;
		int MARK_OF_REFORMER_ID = 2821;
		int MARK_OF_MAGUS_ID = 2840;
		int MARK_OF_FATE_ID = 3172;
		int MARK_OF_SAGITTARIUS_ID = 3293;
		int MARK_OF_WITCHCRAFT_ID = 3307;
		int MARK_OF_SUMMONER_ID = 3336;
		int event = Integer.parseInt(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 33 && classId == ClassId.palusKnight)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_DUTY_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_FATE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_WITCHCRAFT_ID) == 0)
					htmltext = "26.htm";
				else
					htmltext = "27.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_DUTY_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_FATE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_WITCHCRAFT_ID) == 0)
				htmltext = "28.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_DUTY_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_FATE_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_WITCHCRAFT_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "29.htm";
			}

		else if(event == 34 && classId == ClassId.palusKnight)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_CHALLENGER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_FATE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_DUELIST_ID) == 0)
					htmltext = "30.htm";
				else
					htmltext = "31.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_CHALLENGER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_FATE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_DUELIST_ID) == 0)
				htmltext = "32.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_CHALLENGER_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_FATE_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_DUELIST_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "33.htm";
			}

		else if(event == 43 && classId == ClassId.shillienOracle)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_PILGRIM_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_FATE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_REFORMER_ID) == 0)
					htmltext = "34.htm";
				else
					htmltext = "35.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_PILGRIM_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_FATE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_REFORMER_ID) == 0)
				htmltext = "36.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_PILGRIM_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_FATE_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_REFORMER_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "37.htm";
			}

		else if(event == 36 && classId == ClassId.assassin)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_SEEKER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_FATE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_SEARCHER_ID) == 0)
					htmltext = "38.htm";
				else
					htmltext = "39.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_SEEKER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_FATE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_SEARCHER_ID) == 0)
				htmltext = "40.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_SEEKER_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_FATE_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_SEARCHER_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "41.htm";
			}

		else if(event == 37 && classId == ClassId.assassin)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_SEEKER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_FATE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_SAGITTARIUS_ID) == 0)
					htmltext = "42.htm";
				else
					htmltext = "43.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_SEEKER_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_FATE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_SAGITTARIUS_ID) == 0)
				htmltext = "44.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_SEEKER_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_FATE_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_SAGITTARIUS_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "45.htm";
			}

		else if(event == 40 && classId == ClassId.darkWizard)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_SCHOLAR_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_FATE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_MAGUS_ID) == 0)
					htmltext = "46.htm";
				else
					htmltext = "47.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_SCHOLAR_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_FATE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_MAGUS_ID) == 0)
				htmltext = "48.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_SCHOLAR_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_FATE_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_MAGUS_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "49.htm";
			}

		else if(event == 41 && classId == ClassId.darkWizard)
			if(Level <= 39)
				if(ItemFunctions.getItemCount(pl, MARK_OF_SCHOLAR_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_FATE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_SUMMONER_ID) == 0)
					htmltext = "50.htm";
				else
					htmltext = "51.htm";
			else if(ItemFunctions.getItemCount(pl, MARK_OF_SCHOLAR_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_FATE_ID) == 0 || ItemFunctions.getItemCount(pl, MARK_OF_SUMMONER_ID) == 0)
				htmltext = "52.htm";
			else
			{
				ItemFunctions.deleteItem(pl, MARK_OF_SCHOLAR_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_FATE_ID, 1);
				ItemFunctions.deleteItem(pl, MARK_OF_SUMMONER_ID, 1);
				pl.setClassId(event, false, true);
				htmltext = "53.htm";
			}

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/30474/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onChange32145")
	public void onChange32145(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		int SteelrazorEvaluation = 9772;
		int event = Integer.parseInt(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "04.htm";

		if(event == 126 && classId == ClassId.femaleSoldier)
			if(Level >= 20 && ItemFunctions.getItemCount(pl, SteelrazorEvaluation) > 0)
			{
				ItemFunctions.deleteItem(pl, SteelrazorEvaluation, 1);
				pl.setClassId(event, false, true);
				htmltext = "03.htm";
			}

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/32145/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32145")
	public void onTalk32145(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.femaleSoldier)
			htmltext = "01.htm";
		else
			htmltext = "02.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/32145/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onChange32146")
	public void onChange32146(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		int GwainsRecommendation = 9753;
		int event = Integer.parseInt(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "04.htm";

		if(event == 125 && classId == ClassId.maleSoldier)
			if(Level >= 20 && ItemFunctions.getItemCount(pl, GwainsRecommendation) > 0)
			{
				ItemFunctions.deleteItem(pl, GwainsRecommendation, 1);
				pl.setClassId(event, false, true);
				htmltext = "03.htm";
			}

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/32146/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32146")
	public void onTalk32146(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.maleSoldier)
			htmltext = "01.htm";
		else
			htmltext = "02.htm";

		npc.showChatWindow(pl, "villagemaster/32146/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32199")
	public void onTalk32199(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.warder)
			htmltext = "01.htm";
		else if(classId == ClassId.trooper)
			htmltext = "11.htm";
		else
			htmltext = "02.htm";

		npc.showChatWindow(pl, "villagemaster/32199/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32157")
	public void onTalk32157(Player pl, NpcInstance npc, String[] args)
	{
		String prefix = "head_blacksmith_mokabred";

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();
		Race race = pl.getRace();

		if(race != Race.dwarf)
			htmltext = "002.htm";
		else if(classId == ClassId.dwarvenFighter)
			htmltext = "003f.htm";
		else if(classId.getLevel() == 3)
			htmltext = "004.htm";
		else
			htmltext = "005.htm";

		npc.showChatWindow(pl, "villagemaster/32157/" + prefix + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32160")
	public void onTalk32160(Player pl, NpcInstance npc, String[] args)
	{
		String prefix = "grandmagister_devon";

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();
		Race race = pl.getRace();

		if(race != Race.darkelf)
			htmltext = "002.htm";
		else if(classId == ClassId.darkFighter)
			htmltext = "003f.htm";
		else if(classId == ClassId.darkMage)
			htmltext = "003m.htm";
		else if(classId.getLevel() == 3)
			htmltext = "004.htm";
		else
			htmltext = "005.htm";

		npc.showChatWindow(pl, "villagemaster/32160/" + prefix + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onChange32199")
	public void onChange32199(Player pl, NpcInstance npc, String[] args)
	{

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		int KamaelInquisitorMark = 9782;
		int SB_Certificate = 9806;
		int OrkurusRecommendation = 9760;
		int classid = Integer.parseInt(args[0]);

		int Level = pl.getLevel();
		String htmltext = "02.htm";

		if(classid == 130 && pl.getClassId() == ClassId.warder)
		{
			if(Level <= 39 && ItemFunctions.getItemCount(pl, KamaelInquisitorMark) == 0)
				htmltext = "03.htm";
			else if(Level <= 39 && ItemFunctions.getItemCount(pl, KamaelInquisitorMark) > 0)
				htmltext = "04.htm";
			if(Level >= 40 && ItemFunctions.getItemCount(pl, KamaelInquisitorMark) == 0)
				htmltext = "05.htm";
			if(Level >= 40 && ItemFunctions.getItemCount(pl, KamaelInquisitorMark) > 0)
			{
				ItemFunctions.deleteItem(pl, KamaelInquisitorMark, 1);
				pl.setClassId(classid, false, true);
				htmltext = "06.htm";
			}
		}
		else if(classid == 129 && pl.getClassId() == ClassId.warder)
		{
			if(Level <= 39 && ItemFunctions.getItemCount(pl, SB_Certificate) == 0)
				htmltext = "07.htm";
			else if(Level <= 39 && ItemFunctions.getItemCount(pl, SB_Certificate) > 0)
				htmltext = "08.htm";
			if(Level >= 40 && ItemFunctions.getItemCount(pl, SB_Certificate) == 0)
				htmltext = "09.htm";
			if(Level >= 40 && ItemFunctions.getItemCount(pl, SB_Certificate) > 0)
			{
				ItemFunctions.deleteItem(pl, SB_Certificate, 1);
				pl.setClassId(classid, false, true);
				htmltext = "10.htm";
			}
		}
		else if(classid == 127 && pl.getClassId() == ClassId.trooper)
		{
			if(Level <= 39 && ItemFunctions.getItemCount(pl, OrkurusRecommendation) == 0)
				htmltext = "12.htm";
			else if(Level <= 39 && ItemFunctions.getItemCount(pl, OrkurusRecommendation) > 0)
				htmltext = "13.htm";
			if(Level >= 40 && ItemFunctions.getItemCount(pl, OrkurusRecommendation) == 0)
				htmltext = "14.htm";
			if(Level >= 40 && ItemFunctions.getItemCount(pl, OrkurusRecommendation) > 0)
			{
				ItemFunctions.deleteItem(pl, OrkurusRecommendation, 1);
				pl.setClassId(classid, false, true);
				htmltext = "15.htm";
			}
		}
		else if(classid == 128 && pl.getClassId() == ClassId.trooper)
		{
			if(Level <= 39 && ItemFunctions.getItemCount(pl, SB_Certificate) == 0)
				htmltext = "16.htm";
			else if(Level <= 39 && ItemFunctions.getItemCount(pl, SB_Certificate) > 0)
				htmltext = "17.htm";
			if(Level >= 40 && ItemFunctions.getItemCount(pl, SB_Certificate) == 0)
				htmltext = "18.htm";
			if(Level >= 40 && ItemFunctions.getItemCount(pl, SB_Certificate) > 0)
			{
				ItemFunctions.deleteItem(pl, SB_Certificate, 1);
				pl.setClassId(classid, false, true);
				htmltext = "19.htm";
			}
		}
		npc.showChatWindow(pl, "villagemaster/32199/" + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32158")
	public void onTalk32158(Player pl, NpcInstance npc, String[] args)
	{
		String prefix = "warehouse_chief_fisser";

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();
		Race race = pl.getRace();

		if(race != Race.dwarf)
			htmltext = "002.htm";
		else if(classId == ClassId.dwarvenFighter)
			htmltext = "003f.htm";
		else if(classId.getLevel() == 3)
			htmltext = "004.htm";
		else
			htmltext = "005.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/32158/" + prefix + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32171")
	public void onTalk32171(Player pl, NpcInstance npc, String[] args)
	{
		String prefix = "warehouse_chief_hufran";

		if(!(npc instanceof VillageMasterInstance))
		{
			Functions.show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();
		Race race = pl.getRace();

		if(race != Race.dwarf)
			htmltext = "002.htm";
		else if(classId == ClassId.dwarvenFighter)
			htmltext = "003f.htm";
		else if(classId.getLevel() == 3)
			htmltext = "004.htm";
		else
			htmltext = "005.htm";

		((VillageMasterInstance) npc).showChatWindow(pl, "villagemaster/32171/" + prefix + htmltext);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32213")
	public void onTalk32213(Player pl, NpcInstance npc, String[] args)
	{
		onTalk32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onChange32213")
	public void onChange32213(Player pl, NpcInstance npc, String[] args)
	{
		onChange32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32214")
	public void onTalk32214(Player pl, NpcInstance npc, String[] args)
	{
		onTalk32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onChange32214")
	public void onChange32214(Player pl, NpcInstance npc, String[] args)
	{
		onChange32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32217")
	public void onTalk32217(Player pl, NpcInstance npc, String[] args)
	{
		onTalk32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onChange32217")
	public void onChange32217(Player pl, NpcInstance npc, String[] args)
	{
		onChange32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32218")
	public void onTalk32218(Player pl, NpcInstance npc, String[] args)
	{
		onTalk32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onChange32218")
	public void onChange32218(Player pl, NpcInstance npc, String[] args)
	{
		onChange32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32221")
	public void onTalk32221(Player pl, NpcInstance npc, String[] args)
	{
		onTalk32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onChange32221")
	public void onChange32221(Player pl, NpcInstance npc, String[] args)
	{
		onChange32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32222")
	public void onTalk32222(Player pl, NpcInstance npc, String[] args)
	{
		onTalk32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onChange32222")
	public void onChange32222(Player pl, NpcInstance npc, String[] args)
	{
		onChange32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32205")
	public void onTalk32205(Player pl, NpcInstance npc, String[] args)
	{
		onTalk32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onChange32205")
	public void onChange32205(Player pl, NpcInstance npc, String[] args)
	{
		onChange32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32206")
	public void onTalk32206(Player pl, NpcInstance npc, String[] args)
	{
		onTalk32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onChange32206")
	public void onChange32206(Player pl, NpcInstance npc, String[] args)
	{
		onChange32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32147")
	public void onTalk32147(Player pl, NpcInstance npc, String[] args)
	{
		onTalk30037(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32150")
	public void onTalk32150(Player pl, NpcInstance npc, String[] args)
	{
		onTalk30565(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32153")
	public void onTalk32153(Player pl, NpcInstance npc, String[] args)
	{
		onTalk30037(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32154")
	public void onTalk32154(Player pl, NpcInstance npc, String[] args)
	{
		onTalk30066(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32191")
	public void onTalk32191(Player pl, NpcInstance npc, String[] args)
	{
		onTalk32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32226")
	public void onTalk32226(Player pl, NpcInstance npc, String[] args)
	{
		onTalk32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32225")
	public void onTalk32225(Player pl, NpcInstance npc, String[] args)
	{
		onTalk32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32230")
	public void onTalk32230(Player pl, NpcInstance npc, String[] args)
	{
		onTalk32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32229")
	public void onTalk32229(Player pl, NpcInstance npc, String[] args)
	{
		onTalk32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32233")
	public void onTalk32233(Player pl, NpcInstance npc, String[] args)
	{
		onTalk32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32234")
	public void onTalk32234(Player pl, NpcInstance npc, String[] args)
	{
		onTalk32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32202")
	public void onTalk32202(Player pl, NpcInstance npc, String[] args)
	{
		onTalk32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32210")
	public void onTalk32210(Player pl, NpcInstance npc, String[] args)
	{
		onTalk32199(pl, npc, args);
	}

	@Bypass("services.villagemasters.Occupation:onTalk32209")
	public void onTalk32209(Player pl, NpcInstance npc, String[] args)
	{
		onTalk32199(pl, npc, args);
	}
}