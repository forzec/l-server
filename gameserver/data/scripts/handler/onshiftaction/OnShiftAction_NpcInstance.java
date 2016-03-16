package handler.onshiftaction;

import handler.onshiftaction.commons.RewardListInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.text.StrBuilder;
import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.AggroList;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestEventType;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.utils.HtmlUtils;
import org.mmocore.gameserver.utils.PositionUtils;
import org.mmocore.gameserver.utils.Util;

/**
 * @author VISTALL
 * @date 2:43/19.08.2011
 */
public class OnShiftAction_NpcInstance extends ScriptOnShiftActionHandler<NpcInstance>
{
	@Override
	public Class<NpcInstance> getClazz()
	{
		return NpcInstance.class;
	}

	@Override
	public boolean call(NpcInstance npc, Player player)
	{
		return showMain(player, npc, Config.ALT_FULL_NPC_STATS_PAGE);
	}

	@Bypass("actions.OnActionShift:showShort")
	public void showShort(Player player, NpcInstance npc, String[] par)
	{
		showMain(player, npc, false);
	}

	private boolean showMain(Player player, NpcInstance npc, boolean full)
	{
		if (npc == null)
			return false;

		if(!Config.ALLOW_NPC_SHIFTCLICK && !player.isGM())
		{
			if(Config.ALT_GAME_SHOW_DROPLIST)
			{
				if(npc.noShiftClick() || npc.isDead())
					return false;
				droplist(player, npc, null);
			}
			return false;
		}

		// Для мертвых мобов не показываем табличку, иначе спойлеры плачут
		if((npc.noShiftClick() || npc.isDead()) && !player.isGM())
			return false;

		HtmlMessage msg = new HtmlMessage(0);
		msg.setFile("scripts/actions/player.L2NpcInstance.onActionShift." + (full ? "full.htm" : "htm"));

		if(Config.ALT_FULL_NPC_STATS_PAGE)
		{
			msg.replace("%class%", String.valueOf(npc.getClass().getSimpleName()));
			msg.replace("%id%", String.valueOf(npc.getNpcId()));
			msg.replace("%respawn%", String.valueOf(npc.getSpawn() != null ? Util.formatTime(npc.getSpawn().getRespawnDelay()) : "0"));
			msg.replace("%walkSpeed%", String.valueOf(npc.getWalkSpeed()));
			msg.replace("%evs%", String.valueOf(npc.getEvasionRate(null)));
			msg.replace("%acc%", String.valueOf(npc.getAccuracy()));
			msg.replace("%crt%", String.valueOf(npc.getCriticalHit(null, null)));
			msg.replace("%aspd%", String.valueOf(npc.getPAtkSpd(true)));
			msg.replace("%cspd%", String.valueOf(npc.getMAtkSpd()));
			msg.replace("%currentMP%", String.valueOf(npc.getCurrentMp()));
			msg.replace("%currentHP%", String.valueOf(npc.getCurrentHp()));
			msg.replace("%loc%", "");
			msg.replace("%dist%", String.valueOf((int) npc.getDistance3D(player)));
			msg.replace("%killed%", String.valueOf(0));//TODO [G1ta0] убрать
			msg.replace("%spReward%", String.valueOf(npc.getSpReward()));
			msg.replace("%xyz%", npc.getLoc().x + " " + npc.getLoc().y + " " + npc.getLoc().z);
			msg.replace("%ai_type%", npc.getAI().getClass().getSimpleName());
			msg.replace("%direction%", PositionUtils.getDirectionTo(npc, player).toString().toLowerCase());

			StrBuilder b = new StrBuilder("");
			for(Event e : npc.getEvents())
				b.append(e.toString()).append(";");
			msg.replace("%event%", b.toString());
		}

		msg.replace("%name%", nameNpc(npc));
		msg.replace("%id%", String.valueOf(npc.getNpcId()));
		msg.replace("%level%", String.valueOf(npc.getLevel()));
		msg.replace("%respawn%", String.valueOf(npc.getSpawn() != null ? Util.formatTime(npc.getSpawn().getRespawnDelay()) : "0"));
		msg.replace("%factionId%", String.valueOf(npc.getFaction()));
		msg.replace("%aggro%", String.valueOf(npc.getAggroRange()));
		msg.replace("%maxHp%", String.valueOf(npc.getMaxHp()));
		msg.replace("%maxMp%", String.valueOf(npc.getMaxMp()));
		msg.replace("%pDef%", String.valueOf(npc.getPDef(null)));
		msg.replace("%mDef%", String.valueOf(npc.getMDef(null, null)));
		msg.replace("%pAtk%", String.valueOf(npc.getPAtk(null)));
		msg.replace("%mAtk%", String.valueOf(npc.getMAtk(null, null)));
		msg.replace("%expReward%", String.valueOf(npc.getExpReward()));
		msg.replace("%spReward%", String.valueOf(npc.getSpReward()));
		msg.replace("%runSpeed%", String.valueOf(npc.getRunSpeed()));

		// Дополнительная инфа для ГМов
		if(player.isGM())
			msg.replace("%AI%", String.valueOf(npc.getAI()) + ",<br1>active: " + npc.getAI().isActive() + ",<br1>intention: " + npc.getAI().getIntention());
		else
			msg.replace("%AI%", "");

		player.sendPacket(msg);
		return true;
	}

	@Bypass("actions.OnActionShift:droplist")
	public void droplist(Player player, NpcInstance npc, String[] par)
	{
		if(player == null || npc == null)
			return;

		if(Config.ALT_GAME_SHOW_DROPLIST)
			RewardListInfo.showInfo(player, npc);
	}

	@Bypass("actions.OnActionShift:stats")
	public void stats(Player player, NpcInstance npc, String[] par)
	{
		if(npc == null)
			return;

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setFile("scripts/actions/player.L2NpcInstance.stats.htm");

		msg.replace("%name%", nameNpc(npc));
		msg.replace("%level%", String.valueOf(npc.getLevel()));
		msg.replace("%factionId%", String.valueOf(npc.getFaction()));
		msg.replace("%aggro%", String.valueOf(npc.getAggroRange()));
		msg.replace("%race%", getNpcRaceById(npc.getTemplate().getRace()));
		msg.replace("%maxHp%", String.valueOf(npc.getMaxHp()));
		msg.replace("%maxMp%", String.valueOf(npc.getMaxMp()));
		msg.replace("%pDef%", String.valueOf(npc.getPDef(null)));
		msg.replace("%mDef%", String.valueOf(npc.getMDef(null, null)));
		msg.replace("%pAtk%", String.valueOf(npc.getPAtk(null)));
		msg.replace("%mAtk%", String.valueOf(npc.getMAtk(null, null)));
		msg.replace("%accuracy%", String.valueOf(npc.getAccuracy()));
		msg.replace("%evasionRate%", String.valueOf(npc.getEvasionRate(null)));
		msg.replace("%criticalHit%", String.valueOf(npc.getCriticalHit(null, null)));
		msg.replace("%runSpeed%", String.valueOf(npc.getRunSpeed()));
		msg.replace("%walkSpeed%", String.valueOf(npc.getWalkSpeed()));
		msg.replace("%pAtkSpd%", String.valueOf(npc.getPAtkSpd(true)));
		msg.replace("%mAtkSpd%", String.valueOf(npc.getMAtkSpd()));

		player.sendPacket(msg);
	}

	@Bypass("actions.OnActionShift:quests")
	public void quests(Player player, NpcInstance npc, String[] par)
	{
		if(player == null || npc == null)
			return;

		StrBuilder dialog = new StrBuilder("<html><body><center><font color=\"LEVEL\">");
		dialog.append(nameNpc(npc)).append("<br></font></center><br>");

		Map<QuestEventType, Quest[]> list = npc.getTemplate().getQuestEvents();
		for(Map.Entry<QuestEventType, Quest[]> entry : list.entrySet())
		{
			for(Quest q : entry.getValue())
				dialog.append(entry.getKey()).append(" ").append(q.getClass().getSimpleName()).append("<br1>");
		}

		dialog.append("</body></html>");

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setHtml(dialog.toString());
		player.sendPacket(msg);
	}

	@Bypass("actions.OnActionShift:skills")
	public void skills(Player player, NpcInstance npc, String[] par)
	{
		if(player == null || npc == null)
			return;

		StrBuilder dialog = new StrBuilder("<html><body><center><font color=\"LEVEL\">");
		dialog.append(nameNpc(npc)).append("<br></font></center>");

		Collection<SkillEntry> list = npc.getAllSkills();
		if(list != null && !list.isEmpty())
		{
			dialog.append("<br><font color=\"LEVEL\">Active:</font><br>");
			for(SkillEntry s : list)
				if(s.getTemplate().isActive())
					dialog.append(s.getName()).append(" <font color=\"LEVEL\">Id: ").append(s.getId()).append(" Level: ").append(s.getLevel()).append("</font><br1>");
			dialog.append("<br><font color=\"LEVEL\">Passive:</font><br>");
			for(SkillEntry s : list)
				if(!s.getTemplate().isActive())
					dialog.append(s.getName()).append(" <font color=\"LEVEL\">Id: ").append(s.getId()).append(" Level: ").append(s.getLevel()).append("</font><br1>");
		}

		dialog.append("</body></html>");

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setHtml(dialog.toString());
		player.sendPacket(msg);
	}

	@Bypass("actions.OnActionShift:effects")
	public void effects(Player player, NpcInstance npc, String[] par)
	{
		if(player == null || npc == null)
			return;

		StrBuilder dialog = new StrBuilder("<html><body><center><font color=\"LEVEL\">");
		dialog.append(nameNpc(npc)).append("<br></font></center><br>");

		List<Effect> list = npc.getEffectList().getAllEffects();
		if(!list.isEmpty())
			for(Effect e : list)
				dialog.append(e.getSkill().getName()).append("<br1>");

		dialog.append("<br><center><button value=\"");
		dialog.append(player.isLangRus() ? "Обновить" : "Refresh");
		dialog.append("\" action=\"bypass -h htmbypass_actions.OnActionShift:effects\" width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" /></center></body></html>");

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setHtml(dialog.toString());
		player.sendPacket(msg);
	}

	@Bypass("actions.OnActionShift:resists")
	public void resists(Player player, NpcInstance npc, String[] par)
	{
		if(player == null || npc == null)
			return;

		StrBuilder dialog = new StrBuilder("<html><body><center><font color=\"LEVEL\">");
		dialog.append(nameNpc(npc)).append("<br></font></center><table width=\"80%\">");

		boolean hasResist;

		hasResist = addResist(dialog, "Fire", npc.calcStat(Stats.DEFENCE_FIRE, 0, null, null));
		hasResist |= addResist(dialog, "Wind", npc.calcStat(Stats.DEFENCE_WIND, 0, null, null));
		hasResist |= addResist(dialog, "Water", npc.calcStat(Stats.DEFENCE_WATER, 0, null, null));
		hasResist |= addResist(dialog, "Earth", npc.calcStat(Stats.DEFENCE_EARTH, 0, null, null));
		hasResist |= addResist(dialog, "Light", npc.calcStat(Stats.DEFENCE_HOLY, 0, null, null));
		hasResist |= addResist(dialog, "Darkness", npc.calcStat(Stats.DEFENCE_UNHOLY, 0, null, null));
		hasResist |= addResist(dialog, "Bleed", npc.calcStat(Stats.BLEED_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Poison", npc.calcStat(Stats.POISON_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Stun", npc.calcStat(Stats.STUN_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Root", npc.calcStat(Stats.ROOT_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Sleep", npc.calcStat(Stats.SLEEP_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Paralyze", npc.calcStat(Stats.PARALYZE_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Mental", npc.calcStat(Stats.MENTAL_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Debuff", npc.calcStat(Stats.DEBUFF_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Cancel", npc.calcStat(Stats.CANCEL_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Sword", 100 - npc.calcStat(Stats.SWORD_WPN_VULNERABILITY, null, null));
		hasResist |= addResist(dialog, "Dual Sword", 100 - npc.calcStat(Stats.DUAL_WPN_VULNERABILITY, null, null));
		hasResist |= addResist(dialog, "Blunt", 100 - npc.calcStat(Stats.BLUNT_WPN_VULNERABILITY, null, null));
		hasResist |= addResist(dialog, "Dagger", 100 - npc.calcStat(Stats.DAGGER_WPN_VULNERABILITY, null, null));
		hasResist |= addResist(dialog, "Bow", 100 - npc.calcStat(Stats.BOW_WPN_VULNERABILITY, null, null));
		hasResist |= addResist(dialog, "Crossbow", 100 - npc.calcStat(Stats.CROSSBOW_WPN_VULNERABILITY, null, null));
		hasResist |= addResist(dialog, "Polearm", 100 - npc.calcStat(Stats.POLE_WPN_VULNERABILITY, null, null));
		hasResist |= addResist(dialog, "Fist", 100 - npc.calcStat(Stats.FIST_WPN_VULNERABILITY, null, null));

		if(!hasResist)
			dialog.append("</table>No resists</body></html>");
		else
			dialog.append("</table></body></html>");

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setHtml(dialog.toString());
		player.sendPacket(msg);
	}

	@Bypass("actions.OnActionShift:aggro")
	public void aggro(Player player, NpcInstance npc, String[] par)
	{
		if(player == null || npc == null)
			return;

		StrBuilder dialog = new StrBuilder("<html><body><table width=\"80%\"><tr><td>Attacker</td><td>Damage</td><td>Hate</td></tr>");

		Set<AggroList.HateInfo> set = new TreeSet<AggroList.HateInfo>(AggroList.HateComparator.getInstance());
		set.addAll(npc.getAggroList().getCharMap().values());
		for(AggroList.HateInfo aggroInfo : set)
			dialog.append("<tr><td>").append(aggroInfo.attacker.getName()).append("</td><td>").append(aggroInfo.damage).append("</td><td>").append(aggroInfo.hate).append("</td></tr>");

		dialog.append("</table><br><center><button value=\"");
		dialog.append(player.isLangRus() ? "Обновить" : "Refresh");
		dialog.append("\" action=\"bypass -h htmbypass_actions.OnActionShift:aggro\" width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" /></center></body></html>");

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setHtml(dialog.toString());
		player.sendPacket(msg);
	}

	private static boolean addResist(StrBuilder dialog, String name, double val)
	{
		if (val == 0)
			return false;

		dialog.append("<tr><td>").append(name).append("</td><td>");
		if (val == Double.POSITIVE_INFINITY)
			dialog.append("MAX");
		else if (val == Double.NEGATIVE_INFINITY)
			dialog.append("MIN");
		else
		{
			dialog.append(String.valueOf((int)val));
			dialog.append("</td></tr>");
			return true;
		}

		dialog.append("</td></tr>");
		return true;
	}

	private static String getNpcRaceById(int raceId)
	{
		switch(raceId)
		{
			case 1:
				return "Undead";
			case 2:
				return "Magic Creatures";
			case 3:
				return "Beasts";
			case 4:
				return "Animals";
			case 5:
				return "Plants";
			case 6:
				return "Humanoids";
			case 7:
				return "Spirits";
			case 8:
				return "Angels";
			case 9:
				return "Demons";
			case 10:
				return "Dragons";
			case 11:
				return "Giants";
			case 12:
				return "Bugs";
			case 13:
				return "Fairies";
			case 14:
				return "Humans";
			case 15:
				return "Elves";
			case 16:
				return "Dark Elves";
			case 17:
				return "Orcs";
			case 18:
				return "Dwarves";
			case 19:
				return "Others";
			case 20:
				return "Non-living Beings";
			case 21:
				return "Siege Weapons";
			case 22:
				return "Defending Army";
			case 23:
				return "Mercenaries";
			case 24:
				return "Unknown Creature";
			case 25:
				return "Kamael";
			default:
				return "Not defined";
		}
	}

	private static String nameNpc(NpcInstance npc)
	{
		if(npc.getNameNpcString() == NpcString.NONE)
			return HtmlUtils.htmlNpcName(npc.getNpcId());
		else
			return HtmlUtils.htmlNpcString(npc.getNameNpcString().getId(), npc.getName());
	}
}
