 package handler.bbs;
 
 import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import static java.util.Collections.list;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 import java.util.StringTokenizer;
import javax.xml.bind.annotation.XmlElement;

 import org.apache.commons.lang3.tuple.Pair;
 import org.mmocore.gameserver.data.htm.HtmCache;
 import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
 import org.mmocore.gameserver.handler.bypass.BypassHolder;
 import org.mmocore.gameserver.model.Player;
 import org.mmocore.gameserver.network.l2.s2c.ShowBoard;
 import org.mmocore.gameserver.model.entity.residence.Castle;
 import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
 import org.mmocore.gameserver.network.l2.components.SystemMsg;
 import org.mmocore.gameserver.utils.Location;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import npc.model.ClassMasterInstance;
import org.apache.velocity.runtime.directive.Foreach;
import org.mmocore.commons.collections.LazyArrayList;
import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.dao.AccountBonusDAO;
import org.mmocore.gameserver.database.mysql;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.EffectList;
import org.mmocore.gameserver.model.Servitor;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.actor.instances.player.Bonus;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.BonusRequest;
import org.mmocore.gameserver.network.l2.s2c.ExBR_PremiumState;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillCanceled;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillLaunched;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillUse;
import org.mmocore.gameserver.network.l2.s2c.PledgeShowInfoUpdate;
import org.mmocore.gameserver.network.l2.s2c.PledgeStatusChanged;
import org.mmocore.gameserver.network.l2.s2c.SkillList;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.network.l2.s2c.UserInfo;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.skillclasses.EffectsFromSkills;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Language;
import org.mmocore.gameserver.utils.Log;
 

public class CommunityBoard extends ScriptBbsHandler
{      
	private static final Logger _log = LoggerFactory.getLogger(CommunityBoard.class);
        int[] warrior = {10354,12403,10362,10622,13883,13521,15421,12594,13541,13531,12843,15191,
                14991,15011,15021,15041,70641,14611,13641,14161,46993,2741,2711,2751,3101,9151,
                2691,2671,2681,2641,3041,3491,3641,13231};
        int[] mage = {10354,10786,10853,10622,13893,13521,15421,12594,13973,13032,13541,13531,13571,
            12843,14991,15001,15011,15031,15041,14611,14161,8301,47033,2731,2761,3651,2671,2681,2641,
            3041,3491,3631,13231};
        static int[] allbuffs = {14611,13641,10403,10683,12042,10354,11913,12403,10362,10456,10486,10786,10323,10853,10622,
            13883,13893,13933,11823,11893,15483,10333,13923,13521,13561,15421,12594,10873,13973,13032,13541,
            13531,13551,10862,12684,10593,12423,10773,13571,12843,15191,14991,15001,15011,15021,15031,15041,15491,
            70641,14131,14161,14141,8281,8291,8301,8251,8261,8271,10432,47023,47033,46993,47003,2741,2771,2721,2731,
            2761,2711,2751,3091,3071,3101,3651,5301,9151,2691,2671,2681,2701,2651,2641,3041,3061,2661,3081,3491,3631,
            3641,5291,13231};
	@Override
	public String[] getBypassCommands()
	{
		return new String[] { 
		"_bbshome", 
		"_bbslink", 
		"_bbsmultisell", 
		"_bbspage", 
		"_bbssetlocation",  
		"_bbsshop", 
		"_bbsbuff", 
		"_bbsteleport",
                "_bbshtmlshow",
                "_bbsclass",
                "_bbssetclass",
                "_bbsinfo",
                "_bbsservice",
                "_bbscolor",
                "_bbsnoblesse",
                "_cluseskill_",
                "_bbssetlvlclan",
                "_clsetname_",
                "_clbuffshow_",
                "_savebuff_",
                "_usebuff_",
                "_delbtn_",
                "_bbsbonus",
                "_bbslung"
		};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		String html = "";                              
		if("bbshome".equals(cmd))
		{
                        html = HtmCache.getInstance().getHtml("scripts/services/community/new/index.htm", player);
		}
		else if("bbsshop".equals(cmd))
		{       
                        if(player.isInOlympiadMode())
                        {
                                player.sendMessage("В данных условиях использовать 'Магазин' запрещено.");
                                onBypassCommand(player, "_bbshome");
                                return;
                        }  
                        html = HtmCache.getInstance().getHtml("scripts/services/community/new/shop.htm", player);
		}
		else if("bbsbuff".equals(cmd))
		{                    
                        html = HtmCache.getInstance().getHtml("scripts/services/community/new/buff.htm", player);
                        html = html.replace("%user_buff%" , GetGroups(player.getObjectId()));
		}
		else if("bbsteleport".equals(cmd))
		{                       
                        html = HtmCache.getInstance().getHtml("scripts/services/community/new/teleport.htm", player);
		}
                else if("bbsinfo".equals(cmd))
                {
                        html = HtmCache.getInstance().getHtml("scripts/services/community/new/info_server.htm", player);
                }
                else if("bbsservice".equals(cmd))
                {
                        html = HtmCache.getInstance().getHtml("scripts/services/community/new/service.htm", player);
                }
		else if("bbslink".equals(cmd))
                        html = HtmCache.getInstance().getHtml("scripts/services/community/bbs_homepage.htm", player);
		else if(bypass.startsWith("_bbspage"))
		{
			//Example: "bypass _bbspage:index".
			String[] b = bypass.split(":");
			String page = b[1];
			html = HtmCache.getInstance().getHtml("scripts/services/community/pages/" + page + ".htm", player);
			if (html == null)
				return;
		}
		else if(bypass.startsWith("_bbsmultisell"))
		{                                               
                    //Example: "_bbsmultisell:10000;_bbspage:index" or "_bbsmultisell:10000;_bbshome" or "_bbsmultisell:10000"...
                    StringTokenizer st2 = new StringTokenizer(bypass, ";");
                    String[] mBypass = st2.nextToken().split(":");
                    String pBypass = st2.hasMoreTokens() ? st2.nextToken() : null;
                    if(pBypass != null)
                            onBypassCommand(player, pBypass);

                    int listId = Integer.parseInt(mBypass[1]);
                    MultiSellHolder.getInstance().SeparateAndSend(listId, player, -1, 0);
                    return;
		}
		else if(bypass.startsWith("_bbssetlocation"))
		{                  
                    try
                    {                    
                        if(player.isDead() || player.isAlikeDead() || player.isCastingNow() || player.isInCombat() || player.isAttackingNow() || player.isInOlympiadMode() || player.isFlying() || player.getKarma() > 0 || player.isInDuel() || player.isTerritoryFlagEquipped() || player.isDamageBlocked() || player.isInvisible())
                        {
                                player.sendMessage("В данных условиях использовать 'Телепорт' запрещено.");
                                onBypassCommand(player, "_bbshome");
                                return;
                        }                                                 
                        
                        if(player.getMountType() == 2)
                        {
                            player.sendMessage("Телепортация верхом на виверне невозможна.");
                            return;
                        }
                        
                        StringTokenizer st2 = new StringTokenizer(bypass, ";");
                        String[] sBypass = st2.nextToken().trim().split(":");
                        String pBypass = st2.hasMoreTokens() ? st2.nextToken() : null;
			if(pBypass != null)
				onBypassCommand(player, pBypass);
                        if (sBypass.length < 2)
                                return;
                        String[] args = sBypass[1].trim().split(" ");
                        if (args.length < 4)
                                return;
                        int x = Integer.parseInt(args[0]);
                        int y = Integer.parseInt(args[1]);
                        int z = Integer.parseInt(args[2]);
                        int castleId = args.length > 4 ? Integer.parseInt(args[3]) : 0;
                        if(player.getReflection().isDefault())
                        {
                                Castle castle = castleId > 0 ? ResidenceHolder.getInstance().getResidence(Castle.class, castleId) : null;
                                if(castle != null && castle.getSiegeEvent().isInProgress())
                                {
                                        //player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
                                        //return;
                                }
                        }
                        Location pos = Location.findPointToStay(x, y, z, 30, 80, player.getGeoIndex());
                        player.teleToLocation(pos);
                        return;	
                    }
                    catch(Exception e)
                    {
                            //player.sendMessage(e.getMessage());
                            _log.error("Exception: " + e, e);
                            return;
                    }
		}
		else if(bypass.startsWith("_bbshtmlshow"))
		{
                    //Example: "_bbshtmlshow:new/town;" or "_bbshtmlshow:town;" or "_bbshtmlshow:new/towns/dion;"
                    StringTokenizer st2 = new StringTokenizer(bypass, ";");
                    String command = st2.nextToken().substring(13);                  
                    html = HtmCache.getInstance().getHtml("scripts/services/community/" + command + ".htm", player);
		}
                else if(bypass.startsWith("_bbssetclass"))
		{
                    //Example: "_bbssetclass:0;_bbshome"
                    if(player.isInCombat())
                    {
                        player.sendMessage("В данных условиях использовать 'Карьера' запрещено.");
                        onBypassCommand(player, "_bbshome");
                        return;                       
                    }
                    StringTokenizer st2 = new StringTokenizer(bypass, ";");
                    String command = st2.nextToken().substring(13);  
                    String pBypass = st2.hasMoreTokens() ? st2.nextToken() : null;
			if(pBypass != null)
				onBypassCommand(player, pBypass);   
                    int id = Integer.parseInt(command);
                    player.setClassId(id, true, true);                    
                    return;
                }
                else if(bypass.startsWith("_bbsclass"))
                {                                 
                    ClassId classId = player.getClassId();
                    //fighter
                    if(classId == ClassId.fighter && player.getLevel() >= 20)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Human_Fighter;");
                        return;
                    }
                    else if(classId == ClassId.warrior && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Warrior;");
                        return;
                    }
                    else if(classId == ClassId.knight && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Knight;");
                        return;
                    }
                    else if(classId == ClassId.rogue && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Rogue;");
                        return;
                    }
                    else if(classId == ClassId.gladiator && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Duelist;");
                        return;
                    }
                    else if(classId == ClassId.warlord && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/DreadNought;");
                        return;
                    }
                    else if(classId == ClassId.paladin && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/DreadNought;");
                        return;
                    }
                    else if(classId == ClassId.darkAvenger && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Hell_Knight;");
                        return;
                    }
                    else if(classId == ClassId.treasureHunter && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Adventurer;");
                        return;
                    }
                    else if(classId == ClassId.hawkeye && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Sagittarios;");
                        return;
                    }
                    //Mage
                    else if(classId == ClassId.mage && player.getLevel() >= 20)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Human_Mage;");
                        return;
                    }
                    else if(classId == ClassId.wizard && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Wizard;");
                        return;
                    }
                    else if(classId == ClassId.cleric && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Cleric;");
                        return;
                    }
                    else if(classId == ClassId.sorceror && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Archmage;");
                        return;
                    }
                    else if(classId == ClassId.necromancer && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Soultaker;");
                        return;
                    }
                    else if(classId == ClassId.warlock && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Arcana_Lord;");
                        return;
                    }
                    else if(classId == ClassId.bishop && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Cardinal;");
                        return;
                    }
                    else if(classId == ClassId.prophet && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Hierophant;");
                        return;
                    }                    
                    //Elf Fighter
                    else if(classId == ClassId.elvenFighter && player.getLevel() >= 20)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Elf_Fighter;");
                        return;
                    }
                    else if(classId == ClassId.elvenKnight && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Elf_Knight;");
                        return;
                    }
                    else if(classId == ClassId.elvenScout && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Scout;");
                        return;
                    }
                    else if(classId == ClassId.templeKnight && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Eva_Templar;");
                        return;
                    }
                    else if(classId == ClassId.swordSinger && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Sword_Muse;");
                        return;
                    }
                    else if(classId == ClassId.plainsWalker && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Wind_Rider;");
                        return;
                    }
                    else if(classId == ClassId.silverRanger && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Moonlight_Sentinel;");
                        return;
                    }
                    //Elf Mustic
                    else if(classId == ClassId.elvenMage && player.getLevel() >= 20)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Elf_Mage;");
                        return;
                    }
                    else if(classId == ClassId.elvenWizard && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Elf_Wizard;");
                        return;
                    }
                    else if(classId == ClassId.oracle && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Oracle;");
                        return;
                    }
                    else if(classId == ClassId.spellsinger && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Mustic_Muse;");
                        return;
                    }
                    else if(classId == ClassId.elementalSummoner && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Elemental_Master;");
                        return;
                    }
                    else if(classId == ClassId.elder && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Eva_Saint;");
                        return;
                    }
                    //De Elf Fighter
                    else if(classId == ClassId.darkFighter && player.getLevel() >= 20)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/De_Fighter;");
                        return;
                    }
                    else if(classId == ClassId.palusKnight && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Palus_Knight;");
                        return;
                    }
                    else if(classId == ClassId.assassin && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Assassin;");
                        return;
                    }
                    else if(classId == ClassId.shillienKnight && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Shillien_Templar;");
                        return;
                    }
                    else if(classId == ClassId.bladedancer && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Spectral_Dancer;");
                        return;
                    }
                    else if(classId == ClassId.abyssWalker && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Ghost_Hunter;");
                        return;
                    }
                    else if(classId == ClassId.phantomRanger && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Ghost_Sentinel;");
                        return;
                    }
                    //De Elf Mage
                    else if(classId == ClassId.darkMage && player.getLevel() >= 20)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/De_Mage;");
                        return;
                    }
                    else if(classId == ClassId.darkWizard && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/De_Wizard;");
                        return;
                    }
                    else if(classId == ClassId.shillienOracle && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Shillien_Oracle;");
                        return;
                    }
                    else if(classId == ClassId.spellhowler && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Storm_Screamer;");
                        return;
                    }
                    else if(classId == ClassId.phantomSummoner && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Spectral_Master;");
                        return;
                    }
                    else if(classId == ClassId.shillienElder && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Shillen_Saint;");
                        return;
                    }
                    //Orc Fighter
                    else if(classId == ClassId.orcFighter && player.getLevel() >= 20)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Orc_Fighter;");
                        return;
                    }
                    else if(classId == ClassId.orcRaider && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Raider;");
                        return;
                    }
                    else if(classId == ClassId.orcMonk && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Monk;");
                        return;
                    }
                    else if(classId == ClassId.destroyer && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Titan;");
                        return;
                    }
                    else if(classId == ClassId.tyrant && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Grand_Khauatari;");
                        return;
                    }
                    //Orc Mage
                    else if(classId == ClassId.orcMage && player.getLevel() >= 20)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Orc_Mage;");
                        return;
                    }
                    else if(classId == ClassId.orcShaman && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Shaman;");
                        return;
                    }
                    else if(classId == ClassId.overlord && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Dominator;");
                        return;
                    }
                    else if(classId == ClassId.warcryer && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Doomcryer;");
                        return;
                    }
                     //Dwarf Fighter
                    else if(classId == ClassId.dwarvenFighter && player.getLevel() >= 20)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Dwarf_Fighter;");
                        return;
                    }
                    else if(classId == ClassId.scavenger && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Bounty_Hunter;");
                        return;
                    }
                    else if(classId == ClassId.artisan && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Warsmith;");
                        return;
                    }
                    else if(classId == ClassId.bountyHunter && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Fortune_Seeker;");
                        return;
                    }
                    else if(classId == ClassId.warsmith && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Maestro;");
                        return;
                    }
                    //Male Soldier
                    else if(classId == ClassId.maleSoldier && player.getLevel() >= 20)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Male_Soldier;");
                        return;
                    }
                    else if(classId == ClassId.trooper && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Trooper;");
                        return;
                    }
                    else if(classId == ClassId.berserker && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Doombringer;");
                        return;
                    }
                    else if(classId == ClassId.maleSoulbreaker && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Male_Soulhound;");
                        return;
                    }   
                    //Female Soldier
                    else if(classId == ClassId.femaleSoldier && player.getLevel() >= 20)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Female_Soldier;");
                        return;
                    }
                    else if(classId == ClassId.warder && player.getLevel() >= 40)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Warder;");
                        return;
                    }
                    else if(classId == ClassId.femaleSoulbreaker && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Female_Soulhound;");
                        return;
                    }
                    else if(classId == ClassId.arbalester && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Trickster;");
                        return;
                    }  
                    else if(classId == ClassId.inspector && player.getLevel() >= 76)
                    {
                        onBypassCommand(player,"_bbshtmlshow:new/classes/Judicator;");
                        return;
                    }  
                    //else
                    else if(player.getLevel() < 20)
                    {
                        player.sendMessage("Вернитесь, когда вы достигните 20 уровня , чтобы изменить свой класс.");
                        onBypassCommand(player, "_bbshome");
                        return;
                    }
                    else if(player.getLevel() < 40)
                    {
                        player.sendMessage("Вернитесь, когда вы достигните 40 уровня , чтобы изменить свой класс.");
                        onBypassCommand(player, "_bbshome");
                        return;
                    }
                    else if(player.getLevel() < 76)
                    {
                        player.sendMessage("Вернитесь, когда вы достигните 76 уровня , чтобы изменить свой класс.");
                        onBypassCommand(player, "_bbshome");
                        return;
                    }
                    else
                    {
                        player.sendMessage("Вы достигли максимального класса.");
                        onBypassCommand(player, "_bbshome");
                        return;
                    }                   
                }                          
                else if(bypass.startsWith("_bbscolor"))
                {  
                    //Example: "_bbscolorname:20 20 199 0;_bbsservice"     
                    //0 - name 1 - title                                                       
                    StringTokenizer st2 = new StringTokenizer(bypass, ";");
                    String[] sBypass = st2.nextToken().trim().split(":");
                    String pBypass = st2.hasMoreTokens() ? st2.nextToken() : null;
                    if(pBypass != null)
                            onBypassCommand(player, pBypass);
                    if (sBypass.length < 2)
                            return;
                    String[] args = sBypass[1].trim().split(" ");
                    if (args.length < 4)
                            return;
                    int r = Integer.parseInt(args[0]);
                    int g = Integer.parseInt(args[1]);
                    int b = Integer.parseInt(args[2]); 
                    if("0".equals(args[3]))
                    {
                        int nameColor = (r & 0xFF) + ((g & 0xFF) << 8) + ((b & 0xFF) << 16);
                        int defaultColorName = (255 & 0xFF) + ((255 & 0xFF) << 8) + ((255 & 0xFF) << 16);
                        int price = 5;
                        if(nameColor == player.getNameColor())
                        {
                            player.sendMessage("У вас уже используется этот цвет.");
                            onBypassCommand(player, "_bbsservice");
                            return;
                        }
                        if(nameColor == defaultColorName)
                            price = 1;
                        if(!player.getInventory().destroyItemByItemId(4037, price))
                        {
                            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);;
                            onBypassCommand(player, "_bbsservice");
                            return;
                        }
                        player.sendPacket(SystemMessage.removeItems(4037,price));
                        player.setNameColor(r,g,b);
                    }                      
                    if("1".equals(args[3]))
                    {
                        int titleColor = (r & 0xFF) + ((g & 0xFF) << 8) + ((b & 0xFF) << 16);
                        int defaultColorTitle = (119 & 0xFF) + ((255 & 0xFF) << 8) + ((255 & 0xFF) << 16);
                        int price = 5;
                        if(titleColor == player.getTitleColor())
                        {
                            player.sendMessage("У вас уже используется этот цвет.");
                            onBypassCommand(player, "_bbsservice");
                            return;
                        }
                        if(titleColor == defaultColorTitle)
                            price = 1;
                        if(!player.getInventory().destroyItemByItemId(4037, price))
                        {
                            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                            onBypassCommand(player, "_bbsservice");
                            return;
                        }
                        player.sendPacket(SystemMessage.removeItems(4037,price));
                        player.setTitleColor(r,g,b);  
                    }
                    player.broadcastUserInfo(true);                   
                    return;                   
                }
                else if(bypass.startsWith("_bbsnoblesse"))
                {
                    if(player.isNoble())
                    {
                        player.sendMessage("Вы уже имеете статус дворянина.");
                        onBypassCommand(player, "_bbsservice");
                        return;
                    }
                    if(!player.getInventory().destroyItemByItemId(4037, 5))
                    {
                    	player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                        onBypassCommand(player, "_bbsservice");
                        return;
                    }
                    player.sendPacket(SystemMessage.removeItems(4037, 5));
                    Olympiad.addNoble(player);
                    player.setNoble(true);
                    player.updateNobleSkills();                                   
                    player.sendMessage("Поздравляем вы получили статус дворянина.");
                    onBypassCommand(player, "_bbsservice");
                    return;
                }               
                else if(bypass.startsWith("_bbssetlvlclan"))
                {
                    if(player.getClanId() == 0)
                    {
                        player.sendMessage("Вы не состоите в клане.");
                        onBypassCommand(player, "_bbsservice");
                        return;
                    }
                    if(!player.isClanLeader())
                    {
                        player.sendMessage("Вы не являетесь клан лидером.");
                        onBypassCommand(player, "_bbsservice");
                        return;
                    }
                    if(player.getClan().getLevel() == 11)
                    {
                        player.sendMessage("Ваш клан уже имеет 11 уровень.");
                        onBypassCommand(player, "_bbsservice");
                        return;
                    }
                    if(!player.getInventory().destroyItemByItemId(4037, 5))
                    {
                        player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                        onBypassCommand(player, "_bbsservice");
                        return;
                    }
                    player.sendPacket(SystemMessage.removeItems(4037, 5));
                    player.getClan().setLevel(11);
                    player.getClan().updateClanInDB(); 
                    PledgeShowInfoUpdate pu = new PledgeShowInfoUpdate(player.getClan());
		    PledgeStatusChanged ps = new PledgeStatusChanged(player.getClan());
                    for(Player member : player.getClan().getOnlineMembers(0))
                    {
                            member.updatePledgeClass();
                            member.sendPacket(SystemMsg.YOUR_CLANS_LEVEL_HAS_INCREASED, pu, ps);
                            member.broadcastUserInfo(true);
                    }
                    onBypassCommand(player, "_bbsservice");
                    return;
                }
                else if(bypass.startsWith("_bbsbonus"))
                {               
                    //Example: "_bbsbonus:1;_bbsservice;"
                    StringTokenizer st2 = new StringTokenizer(bypass, ";");
                    String[] sBypass = st2.nextToken().trim().split(":");                   
                    if(sBypass.length < 2)
                        return;
                    int time = player.getNetConnection().getBonusExpire() - (int)(System.currentTimeMillis() / 1000L); 
                    int i = Integer.parseInt(sBypass[1]);
                    Date date = new Date();
                    long tim = date.getTime() + (long)((Config.SERVICES_RATE_BONUS_DAYS[i] * 24 * 60 * 60)*1000);                 
                    SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                    if(time > 0)
                    {                      
                        tim = date.getTime() + (long)(time*1000);
                        player.sendMessage("У вас уже активирован премиум аккаунт");
                        player.sendMessage("До " + format1.format(tim));                        
                        onBypassCommand(player, "_bbsservice");
                        return;
                    }                   
                    if(!ItemFunctions.deleteItem(player, Config.SERVICES_RATE_BONUS_ITEM[i], Config.SERVICES_RATE_BONUS_PRICE[i]))
                    {
                        if(Config.SERVICES_RATE_BONUS_ITEM[i] == ItemTemplate.ITEM_ID_ADENA)
                                player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                        else
                                player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                        onBypassCommand(player, "_bbsservice");
                        return;
                    }
                    
                    double bonus = Config.SERVICES_RATE_BONUS_VALUE[i];
                    int bonusExpire = (int) (System.currentTimeMillis() / 1000L) + Config.SERVICES_RATE_BONUS_DAYS[i] * 24 * 60 * 60;
                    time = bonusExpire - (int) (System.currentTimeMillis() / 1000L);
                    switch(Config.SERVICES_RATE_TYPE)
                    {
                        case Bonus.BONUS_GLOBAL_ON_AUTHSERVER:
                                AuthServerCommunication.getInstance().sendPacket(new BonusRequest(player.getAccountName(), bonus, bonusExpire));
                                break;
                        case Bonus.BONUS_GLOBAL_ON_GAMESERVER:
                                AccountBonusDAO.getInstance().insert(player.getAccountName(), bonus, bonusExpire);
                                break;
                    }

                    player.getNetConnection().setBonus(bonus);
                    player.getNetConnection().setBonusExpire(bonusExpire);

                    player.stopBonusTask();
                    player.startBonusTask();     
                    
                    player.updatePremiumItems();
                    
                    if(player.getParty() != null)
                        player.getParty().recalculatePartyData();

                    player.sendPacket(new ExBR_PremiumState(player, true));
                    player.sendMessage("Премиум аккаунт активирован до " + format1.format(tim));                   
                    onBypassCommand(player, "_bbsservice");
                    return;                                             
                }
                                                     
		ShowBoard.separateAndSend(html, player);
	}

        @Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{              
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
                if("usebuff".equals(cmd))               
                {
                    if(player.isDead() || player.isAlikeDead() || player.isCastingNow() || player.isInCombat() || player.isAttackingNow() || player.isInOlympiadMode() || player.isFlying() || player.isDamageBlocked())
                    {
                        player.sendMessage("В данных условиях использовать 'Баффер' запрещено.");
                        onBypassCommand(player, "_bbshome");
                        return;
                    }  
                    List<Integer> buffs = GetBuffs(Integer.parseInt(arg1));                   
                    for(Integer buff : buffs)
                    {
                        SkillEntry skillentry = SkillTable.getInstance().getSkillEntry(((int)buff / 10), ((int)buff % 10));
                        Skill skill = skillentry.getTemplate();
                        if("Player".equals(arg3))                        
                            player.callSkill(skillentry, skill.getTargets(player, player, true,true), false); 
                        else if("Pet".equals(arg3))
                            player.getServitor().callSkill(skillentry, skill.getTargets(player.getServitor(), player.getServitor(), true,true), false);
                    }   
                    onBypassCommand(player, "_bbsbuff");
                    return;
                }  
                else if("clbuffshow".equals(cmd))
                {
                    if("Player".equals(arg3))
                    {
                        onBypassCommand(player, "_bbshtmlshow:new/all_buffs;");
                        return;
                    }
                    if("Pet".equals(arg3))
                    {
                        onBypassCommand(player, "_bbshtmlshow:new/all_buffs_pet;");
                        return;
                    }
                }
                else if("cluseskill".equals(cmd))
                {                     
                    if(player.isDead() || player.isAlikeDead() || player.isCastingNow() || player.isInCombat() || player.isAttackingNow() || player.isInOlympiadMode() || player.isFlying() || player.isDamageBlocked())
                    {
                            player.sendMessage("В данных условиях использовать 'Баффер' запрещено.");
                            onBypassCommand(player, "_bbshome");
                            return;
                    }                                      
                    if("warrior".equals(arg2))
                    {                       
                        for(int i=0; i<warrior.length; i++)
                        {
                            int id = warrior[i]/10;
                            int lvl = warrior[i]%10;
                            SkillEntry skillentry = SkillTable.getInstance().getSkillEntry(id, lvl);
                            Skill skill = skillentry.getTemplate();  
                            if("Player".equals(arg3))
                                player.callSkill(skillentry, skill.getTargets(player, player, true,true), true);                                                                                                                                                                                         
                            else if("Pet".equals(arg3))
                            {
                                if(player.getServitor() == null)
                                {                
                                    player.sendMessage("У вас нет слуги.");
                                    onBypassCommand(player, "_bbsbuff");
                                    return;
                                }     
                                player.getServitor().callSkill(skillentry, skill.getTargets(player.getServitor(), player.getServitor(), true,true), false);                                                                                                                            
                            }                                               
                        }
                        onBypassCommand(player, "_bbsbuff");
                        return;
                    }
                    if("mage".equals(arg2))
                    {                       
                        for(int i=0; i<mage.length; i++)
                        {
                            int id = mage[i]/10;
                            int lvl = mage[i]%10;
                            SkillEntry skillentry = SkillTable.getInstance().getSkillEntry(id, lvl);
                            Skill skill = skillentry.getTemplate();
                            if("Player".equals(arg3))
                                player.callSkill(skillentry, skill.getTargets(player, player, true, true), true); 
                            else if("Pet".equals(arg3))
                            {
                                if(player.getServitor() == null)
                                {                
                                    player.sendMessage("У вас нет слуги.");
                                    onBypassCommand(player, "_bbsbuff");
                                    return;
                                }    
                                player.getServitor().callSkill(skillentry, skill.getTargets(player.getServitor(), player.getServitor(), true, true), true); 
                            }
                        }
                        onBypassCommand(player, "_bbsbuff");
                        return;
                    }
                    if("heal".equals(arg2))
                    {
                        if("Player".equals(arg3))
                        {
                            if(!player.isCurrentCpFull())
                                player.setCurrentCp(player.getMaxCp());
                            if(!player.isCurrentHpFull())
                                player.setCurrentHp(player.getMaxHp(),false);
                            if(!player.isCurrentMpFull())
                                player.setCurrentMp(player.getMaxMp());
                        }
                        if("Pet".equals(arg3))
                        {
                            if(player.getServitor() == null)
                            {                
                                player.sendMessage("У вас нет слуги.");
                                onBypassCommand(player, "__bbsbuff");
                                return;
                            }    
                            if(!player.getServitor().isCurrentCpFull())
                                player.getServitor().setCurrentCp(player.getServitor().getMaxCp());
                            if(!player.getServitor().isCurrentHpFull())
                                player.getServitor().setCurrentHp(player.getServitor().getMaxHp(),false);
                            if(!player.getServitor().isCurrentMpFull())
                                player.getServitor().setCurrentMp(player.getServitor().getMaxMp());                            
                        }
                        onBypassCommand(player, "_bbsbuff");
                        return;
                    }
                    if("cancel".equals(arg2))
                    {
                        if("Player".equals(arg3))
                            player.getEffectList().stopAllEffects();                                              
                        if("Pet".equals(arg3))
                        {
                            if(player.getServitor() == null)
                            {                
                                player.sendMessage("У вас нет слуги.");
                                onBypassCommand(player, "_bbsbuff");
                                return;
                            }   
                            player.getServitor().getEffectList().stopAllEffects();
                        }                        
                        onBypassCommand(player, "_bbsbuff");
                        return;
                    }
                    else if("solobuff".equals(arg2))
                    {                                                                       
                        int arg = Integer.parseInt(arg1);
                        int id = arg/10;
                        int lvl = arg%10;
                        SkillEntry skillentry = SkillTable.getInstance().getSkillEntry(id, lvl);
                        Skill skill = skillentry.getTemplate();
                        if(arg3.contains("Player"))
                            player.callSkill(skillentry, skill.getTargets(player, player, true, true), true);
                        else if(arg3.contains("Pet"))                       
                        {
                            if(player.getServitor() == null)
                            {                
                                player.sendMessage("У вас нет слуги.");
                                onBypassCommand(player, "_bbshtmlshow:new/buff;");
                                return;
                            }  
                            player.getServitor().callSkill(skillentry, skill.getTargets(player.getServitor(), player.getServitor(), true, true), true); 
                        }
                        if("Player_st1".equals(arg5))
                            onBypassCommand(player, "_bbshtmlshow:new/all_buffs;");
                        if("Player_st2".equals(arg5))
                            onBypassCommand(player, "_bbshtmlshow:new/all_buffs_1;");
                        if("Player_st3".equals(arg5))
                            onBypassCommand(player, "_bbshtmlshow:new/all_buffs_2;");
                        if("Pet_st1".equals(arg5))
                            onBypassCommand(player, "_bbshtmlshow:new/all_buffs_pet;");
                        if("Pet_st2".equals(arg5))
                            onBypassCommand(player, "_bbshtmlshow:new/all_buffs_1_pet;");
                        if("Pet_st3".equals(arg5))
                            onBypassCommand(player, "_bbshtmlshow:new/all_buffs_2_pet;");
                        return;                                              
                    }
                }                   
                else if("savebuff".equals(cmd))
                {
                    if(!dumbCheck(arg3))
                    {
                            player.sendMessage("Неверный формат имени.");
                            onBypassCommand(player, "_bbsbuff");
                            return;
                    } 
                    if(IsMaxBuff(player.getObjectId()))
                        AddBuff(player.getObjectId(), GetBuffIdS(player), arg3);                           
                    else
                        player.sendMessage("Максимальное количество наборов.");                    
                    onBypassCommand(player, "_bbsbuff");
                    return;                   
                }
                else if("delbtn".equals(cmd))
                {                   
                    DeleteGroup(arg1);
                    onBypassCommand(player, "_bbsbuff");
                    return;
                }
                else if("clsetname".equals(cmd))
                {     
                    if(arg3.contains("[Admin]") || arg3.contains("[ADMIN]") || arg3.contains("[admin]")
                            || arg3.contains("[Adm]") || arg3.contains("[ADM]") || arg3.contains("[adm]")
                            || arg3.contains("[GM]") || arg3.contains("[gm]") || arg3.contains("[Gm]"))
                    {
                        player.sendMessage("Неверный формат имени.");
                        onBypassCommand(player, "_bbsservice");
                        return;
                    }
                    if(mysql.simple_get_int("count(*)", "characters", "`char_name` like '" + arg3 + "'") > 0 || arg3.equals(player.getName()))
                    {
                            player.sendMessage("Такое имя уже существует.");
                            onBypassCommand(player, "_bbsservice");
                            return;
                    }  
                    if(!dumbCheck(arg3))
                    {
                            player.sendMessage("Неверный формат имени.");
                            onBypassCommand(player, "_bbsservice");
                            return;
                    }                   
                    if(!player.getInventory().destroyItemByItemId(4037, 5))
                    {
                            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                            onBypassCommand(player, "_bbsservice");
                            return;
                    }                                   
                    player.sendPacket(SystemMessage.removeItems(4037, 5));
                    player.reName(arg3);                    
                    player.sendMessage("Имя было изменено на " + arg3 + ".");
                    player.broadcastUserInfo(true);  
                    onBypassCommand(player, "_bbsservice");
                    return;                          
                } 
        }	
        
        private static boolean IsMaxBuff (int playerId)
        {
            return mysql.simple_get_int("count(*)", "buff_groups", "`user_id` = " + String.valueOf(playerId)) < 5;
        }
        
        private static boolean AddBuff (int playerId, List<Integer> buffIdS, String buttonName)
        {
            try
            {
                int lastId = mysql.addEx("INSERT INTO buff_groups SET user_id = " + String.valueOf(playerId) + ", droup_name = \"" + buttonName + "\"");                 
                for (Integer id : buffIdS) {                   
                    mysql.addEx("INSERT INTO buff_groups_buffs SET group_id = " + String.valueOf(lastId) + ", buff_id = " + String.valueOf(id));                  
                }                             
                return true;              
            }
            catch(Exception ex)
            {               
                return false;
            }                      
        }
        
        private static String GetGroups (int playerId)
        {           
            List<Object> groups = mysql.get_array("SELECT * FROM buff_groups WHERE user_id = " + String.valueOf(playerId));
            String html = "";
            if(groups.size() > 0)
            {
                for (Object group : groups) {
                    Map<String, Object> tmp = (HashMap<String, Object>)group;
                    html += "<tr><td align=center><button value=\"" + tmp.get("droup_name").toString() + "\" action=\"Write _usebuff_ " + tmp.get("id").toString() + " 0 cbox cbox cbox\" width=150 height=36 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><a action=\"Write _delbtn_ " + tmp.get("id").toString() + " 0 cbox cbox cbox\">Удалить</a></td></tr>";
                }
            }
            return html;
        }
        
        private static void DeleteGroup (String id)
        {
            mysql.set("DELETE buff_groups FROM buff_groups WHERE id = " + id);
            mysql.set("DELETE buff_groups_buffs FROM buff_groups_buffs WHERE group_id = " + id);
        }
        
        private static List<Integer> GetBuffs (int groupId)
        {           
            List<Object> buffs = mysql.get_array("SELECT * FROM buff_groups_buffs WHERE group_id = " + String.valueOf(groupId));
            List<Integer> buffIds = new ArrayList<Integer>();

            for (Object buff : buffs) {
                Map<String, Object> tmp = (HashMap<String, Object>)buff;
                buffIds.add((Integer)tmp.get("buff_id"));
            }           
            return buffIds;
        }
        
        private static List<Integer> GetBuffIdS (Player player)
        {         
            List<Integer> buffIds = new ArrayList<Integer>();
            List<Effect> effects = player.getEffectList().getAllEffects();
            if(effects.size() > 0)
            {
                for (Effect effect : effects)
                {
                    for (int j = 0; j < allbuffs.length; j++) 
                    {
                        if (effect.getSkill().getId() == (allbuffs[j] / 10) && effect.getSkill().getLevel() == (allbuffs[j] % 10)) {
                            buffIds.add(allbuffs[j]);                       
                        }
                    }
                }
            }
            return buffIds;        
        }
        
        public static boolean dumbCheck(String userNameString)
        {           
            char[] symbols = userNameString.toCharArray();               
            String validationString = "QWERTYUIOPASDFGHJKLZXCVBNMabcdefghijklmnopqrstuvwxyz0123456789_+-[]{}=()*^.'<>\"";  
            for(char c : symbols)
            {  
                if(validationString.indexOf(c)==-1) 
                    return false;  
            }  
            return true;  
        }        
}