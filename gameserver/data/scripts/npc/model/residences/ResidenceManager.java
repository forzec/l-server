package npc.model.residences;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.entity.residence.ResidenceFunction;
import org.mmocore.gameserver.model.instances.MerchantInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.L2GameServerPacket;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.mmocore.gameserver.utils.TimeUtils;
import org.mmocore.gameserver.utils.WarehouseFunctions;

/**
 * some rework by VISTALL
 */
public abstract class ResidenceManager extends MerchantInstance
{
	protected static final int COND_FAIL	= 0;
	protected static final int COND_SIEGE	= 1;
	protected static final int COND_OWNER	= 2;

	protected String _siegeDialog;
	protected String _mainDialog;
	protected String _failDialog;

	protected int[] _doors;

	public ResidenceManager(int objectId, NpcTemplate template)
	{
		super(objectId, template);

		setDialogs();

		_doors = template.getAIParams().getIntegerArray("doors", ArrayUtils.EMPTY_INT_ARRAY);
	}

	protected void setDialogs()
	{
		_siegeDialog = getTemplate().getAIParams().getString("siege_dialog", "npcdefault.htm");
		_mainDialog = getTemplate().getAIParams().getString("main_dialog", "npcdefault.htm");
		_failDialog = getTemplate().getAIParams().getString("fail_dialog", "npcdefault.htm");
	}

	protected abstract Residence getResidence();

	protected abstract L2GameServerPacket decoPacket();

	protected abstract int getPrivUseFunctions();

	protected abstract int getPrivSetFunctions();

	protected abstract int getPrivDismiss();

	protected abstract int getPrivDoors();

	public void broadcastDecoInfo()
	{
		L2GameServerPacket decoPacket = decoPacket();
		if(decoPacket == null)
			return;
		for(Player player : World.getAroundObservers(this))
			player.sendPacket(decoPacket);
	}

	protected int getCond(Player player)
	{
		Residence residence = getResidence();
		Clan residenceOwner = residence.getOwner();
		if(residenceOwner != null && player.getClan() == residenceOwner)
		{
			if(residence.getSiegeEvent().isInProgress())
				return COND_SIEGE;
			else
				return COND_OWNER;
		}
		else
			return COND_FAIL;
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		String filename = null;
		int cond = getCond(player);
		switch(cond)
		{
			case COND_OWNER:
				filename = _mainDialog;
				break;
			case COND_SIEGE:
				filename = _siegeDialog;
				break;
			case COND_FAIL:
				filename = _failDialog;
				break;
		}
		player.sendPacket(new HtmlMessage(this, filename));
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();
		String val = "";
		if(st.countTokens() >= 1)
			val = st.nextToken();

		int cond = getCond(player);
		switch(cond)
		{
			case COND_SIEGE:
				showChatWindow(player, _siegeDialog);
				return;
			case COND_FAIL:
				showChatWindow(player, _failDialog);
				return;
		}

		if(actualCommand.equalsIgnoreCase("banish"))
		{
			HtmlMessage html = new HtmlMessage(this);
			html.setFile("residence/Banish.htm");
			sendHtmlMessage(player, html);
		}
		else if(actualCommand.equalsIgnoreCase("banish_foreigner"))
		{
			if(!isHaveRigths(player, getPrivDismiss()))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			getResidence().banishForeigner();
		}
		else if(actualCommand.equalsIgnoreCase("Buy"))
		{
			if(val.equals(""))
				return;
			showShopWindow(player, Integer.valueOf(val), true);
		}
		else if(actualCommand.equalsIgnoreCase("manage_vault"))
		{
			if(val.equalsIgnoreCase("deposit"))
				WarehouseFunctions.showDepositWindowClan(player);
			else if(val.equalsIgnoreCase("withdraw"))
			{
				int value = Integer.valueOf(st.nextToken());
				if(value == 99)
				{
					HtmlMessage html = new HtmlMessage(this);
					html.setFile("residence/clan.htm");
					player.sendPacket(html);
				}
				else
					WarehouseFunctions.showWithdrawWindowClan(player, value);
			}
			else
			{
				HtmlMessage html = new HtmlMessage(this);
				html.setFile("residence/vault.htm");
				sendHtmlMessage(player, html);
			}
		}
		else if(actualCommand.equalsIgnoreCase("door"))
		{
			showChatWindow(player, "residence/door.htm");
		}
		else if(actualCommand.equalsIgnoreCase("openDoors"))
		{
			if(isHaveRigths(player, getPrivDoors()))
			{
				for(int i : _doors)
					ReflectionUtils.getDoor(i).openMe();

				showChatWindow(player, "residence/door.htm");
			}
			else
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
		}
		else if(actualCommand.equalsIgnoreCase("closeDoors"))
		{
			if(isHaveRigths(player, getPrivDoors()))
			{
				for(int i : _doors)
					ReflectionUtils.getDoor(i).closeMe();

				showChatWindow(player, "residence/door.htm");
			}
			else
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
		}
		else if(actualCommand.equalsIgnoreCase("functions"))
		{
			if(!isHaveRigths(player, getPrivUseFunctions()))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			if(val.equalsIgnoreCase("tele"))
			{
				if(!getResidence().isFunctionActive(ResidenceFunction.TELEPORT))
				{
					HtmlMessage html = new HtmlMessage(this);
					html.setFile("residence/teleportNotActive.htm");
					sendHtmlMessage(player, html);
					return;
				}
				showTeleportList(player, getResidence().getFunction(ResidenceFunction.TELEPORT).getTeleports(), 0);
			}
			else if(val.equalsIgnoreCase("item_creation"))
			{
				if(!getResidence().isFunctionActive(ResidenceFunction.ITEM_CREATE))
				{
					HtmlMessage html = new HtmlMessage(this);
					html.setFile("residence/itemNotActive.htm");
					sendHtmlMessage(player, html);
					return;
				}
				HtmlMessage html = new HtmlMessage(this);
				html.setFile("residence/item.htm");
				String template = "<button value=\"Buy Item\" action=\"bypass -h npc_" + getObjectId() + "_Buy %id%\" width=90 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\">";
				html.replace("%itemList%", template);
				html.replace("%id%", String.valueOf(getResidence().getFunction(ResidenceFunction.ITEM_CREATE).getBuylist()[1]));
				sendHtmlMessage(player, html);
			}
			else if(val.equalsIgnoreCase("support"))
			{
				if(!getResidence().isFunctionActive(ResidenceFunction.SUPPORT))
				{
					HtmlMessage html = new HtmlMessage(this);
					html.setFile("residence/supportNotActive.htm");
					sendHtmlMessage(player, html);
					return;
				}
				HtmlMessage html = new HtmlMessage(this);
				html.setFile("residence/support.htm");
				Object[][] allBuffs = getResidence().getFunction(ResidenceFunction.SUPPORT).getBuffs();
				StringBuilder support_list = new StringBuilder(allBuffs.length * 50);
				int i = 0;
				for(Object[] buff : allBuffs)
				{
					SkillEntry s = (SkillEntry) buff[0];
					support_list.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_support ");
					support_list.append(String.valueOf(s.getId()));
					support_list.append(" ");
					support_list.append(String.valueOf(s.getLevel()));
					support_list.append("\">");
					support_list.append(s.getName());
					support_list.append(" Lv.");
					support_list.append(String.valueOf(s.getDisplayLevel()));
					support_list.append("</a><br1>");
					if(++i % 5 == 0)
						support_list.append("<br>");
				}
				html.replace("%magicList%", support_list.toString());
				html.replace("%mp%", String.valueOf(Math.round(getCurrentMp())));
				html.replace("%all%", Config.ALT_CH_ALL_BUFFS ? "<a action=\"bypass -h npc_" + getObjectId() + "_support all\">Give all</a><br1><a action=\"bypass -h npc_" + getObjectId() + "_support allW\">Give warrior</a><br1><a action=\"bypass -h npc_" + getObjectId() + "_support allM\">Give mystic</a><br>" : "");
				sendHtmlMessage(player, html);
			}
			else if(val.equalsIgnoreCase("back"))
				showChatWindow(player, 0);
			else
			{
				HtmlMessage html = new HtmlMessage(this);
				html.setFile("residence/functions.htm");
				if(getResidence().isFunctionActive(ResidenceFunction.RESTORE_EXP))
					html.replace("%xp_regen%", String.valueOf(getResidence().getFunction(ResidenceFunction.RESTORE_EXP).getLevel()) + "%");
				else
					html.replace("%xp_regen%", "0%");
				if(getResidence().isFunctionActive(ResidenceFunction.RESTORE_HP))
					html.replace("%hp_regen%", String.valueOf(getResidence().getFunction(ResidenceFunction.RESTORE_HP).getLevel()) + "%");
				else
					html.replace("%hp_regen%", "0%");
				if(getResidence().isFunctionActive(ResidenceFunction.RESTORE_MP))
					html.replace("%mp_regen%", String.valueOf(getResidence().getFunction(ResidenceFunction.RESTORE_MP).getLevel()) + "%");
				else
					html.replace("%mp_regen%", "0%");
				sendHtmlMessage(player, html);
			}
		}
		else if(actualCommand.equalsIgnoreCase("manage"))
		{
			if(!isHaveRigths(player, getPrivSetFunctions()))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}

			if(val.equalsIgnoreCase("recovery"))
			{
				if(st.countTokens() >= 1)
				{
					val = st.nextToken();
					boolean success = true;
					if(val.equalsIgnoreCase("hp"))
						success = getResidence().updateFunctions(ResidenceFunction.RESTORE_HP, Integer.valueOf(st.nextToken()));
					else if(val.equalsIgnoreCase("mp"))
						success = getResidence().updateFunctions(ResidenceFunction.RESTORE_MP, Integer.valueOf(st.nextToken()));
					else if(val.equalsIgnoreCase("exp"))
						success = getResidence().updateFunctions(ResidenceFunction.RESTORE_EXP, Integer.valueOf(st.nextToken()));
					if(!success)
						player.sendPacket(SystemMsg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
					else
						broadcastDecoInfo();
				}
				showManageRecovery(player);
			}
			else if(val.equalsIgnoreCase("other"))
			{
				if(st.countTokens() >= 1)
				{
					val = st.nextToken();
					boolean success = true;
					if(val.equalsIgnoreCase("item"))
						success = getResidence().updateFunctions(ResidenceFunction.ITEM_CREATE, Integer.valueOf(st.nextToken()));
					else if(val.equalsIgnoreCase("tele"))
						success = getResidence().updateFunctions(ResidenceFunction.TELEPORT, Integer.valueOf(st.nextToken()));
					else if(val.equalsIgnoreCase("support"))
						success = getResidence().updateFunctions(ResidenceFunction.SUPPORT, Integer.valueOf(st.nextToken()));
					if(!success)
						player.sendPacket(SystemMsg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
					else
						broadcastDecoInfo();
				}
				showManageOther(player);
			}
			else if(val.equalsIgnoreCase("deco"))
			{
				if(st.countTokens() >= 1)
				{
					val = st.nextToken();
					boolean success = true;
					if(val.equalsIgnoreCase("platform"))
						success = getResidence().updateFunctions(ResidenceFunction.PLATFORM, Integer.valueOf(st.nextToken()));
					else if(val.equalsIgnoreCase("curtain"))
						success = getResidence().updateFunctions(ResidenceFunction.CURTAIN, Integer.valueOf(st.nextToken()));
					if(!success)
						player.sendPacket(SystemMsg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
					else
						broadcastDecoInfo();
				}
				showManageDeco(player);
			}
			else if(val.equalsIgnoreCase("back"))
				showChatWindow(player, 0);
			else
			{
				HtmlMessage html = new HtmlMessage(this);
				html.setFile("residence/manage.htm");
				sendHtmlMessage(player, html);
			}
		}
		else if(actualCommand.equalsIgnoreCase("support"))
		{
			if(!isHaveRigths(player, getPrivUseFunctions()))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}

			setTarget(player);
			if(val.equals(""))
				return;

			if(!getResidence().isFunctionActive(ResidenceFunction.SUPPORT))
				return;

			if(val.startsWith("all"))
				for(Object[] buff : getResidence().getFunction(ResidenceFunction.SUPPORT).getBuffs())
				{
					if(val.equals("allM") && buff[1] == ResidenceFunction.W || val.equals("allW") && buff[1] == ResidenceFunction.M)
						continue;
					SkillEntry s = (SkillEntry) buff[0];
					if(!useSkill(s.getId(), s.getLevel(), player))
						break;
				}
			else
			{
				int skill_id = Integer.parseInt(val);
				int skill_lvl = 0;
				if(st.countTokens() >= 1)
					skill_lvl = Integer.parseInt(st.nextToken());
				useSkill(skill_id, skill_lvl, player);
			}

			onBypassFeedback(player, "functions support");
		}
		else
			super.onBypassFeedback(player, command);
	}

	private boolean useSkill(int id, int level, Player player)
	{
		SkillEntry skill = SkillTable.getInstance().getSkillEntry(id, level);
		if(skill == null)
			throw new IllegalArgumentException("Invalid skill " + id);

		setCurrentMp(getMaxMp());
		/*if(skill.getTemplate().getMpConsume() > getCurrentMp())
		{
			HtmlMessage html = new HtmlMessage(this);
			html.setFile("residence/NeedCoolTime.htm");
			html.replace("%mp%", String.valueOf(Math.round(getCurrentMp())));
			sendHtmlMessage(player, html);
			return false;
		} */
		altUseSkill(skill, player);
		return true;
	}

	private void sendHtmlMessage(Player player, HtmlMessage html)
	{
		player.sendPacket(html);
	}

	private void replace(HtmlMessage html, int type, String replace1, String replace2)
	{
		boolean proc = type == ResidenceFunction.RESTORE_HP || type == ResidenceFunction.RESTORE_MP || type == ResidenceFunction.RESTORE_EXP;
		if(getResidence().isFunctionActive(type))
		{
			html.replace("%" + replace1 + "%", String.valueOf(getResidence().getFunction(type).getLevel()) + (proc ? "%" : ""));
			html.replace("%" + replace1 + "Price%", String.valueOf(getResidence().getFunction(type).getLease()));
			html.replace("%" + replace1 + "Date%", TimeUtils.toSimpleFormat(getResidence().getFunction(type).getEndTimeInMillis()));
		}
		else
		{
			html.replace("%" + replace1 + "%", "0");
			html.replace("%" + replace1 + "Price%", "0");
			html.replace("%" + replace1 + "Date%", "0");
		}
		if(getResidence().getFunction(type) != null && getResidence().getFunction(type).getLevels().size() > 0)
		{
			String out = "[<a action=\"bypass -h npc_%objectId%_manage " + replace2 + " " + replace1 + " 0\">Stop</a>]";
			for(int level : getResidence().getFunction(type).getLevels())
				out += "[<a action=\"bypass -h npc_%objectId%_manage " + replace2 + " " + replace1 + " " + level + "\">" + level + (proc ? "%" : "") + "</a>]";
			html.replace("%" + replace1 + "Manage%", out);
		}
		else
			html.replace("%" + replace1 + "Manage%", "Not Available");
	}

	private void showManageRecovery(Player player)
	{
		HtmlMessage html = new HtmlMessage(this);
		html.setFile("residence/edit_recovery.htm");

		replace(html, ResidenceFunction.RESTORE_EXP, "exp", "recovery");
		replace(html, ResidenceFunction.RESTORE_HP, "hp", "recovery");
		replace(html, ResidenceFunction.RESTORE_MP, "mp", "recovery");

		sendHtmlMessage(player, html);
	}

	private void showManageOther(Player player)
	{
		HtmlMessage html = new HtmlMessage(this);
		html.setFile("residence/edit_other.htm");

		replace(html, ResidenceFunction.TELEPORT, "tele", "other");
		replace(html, ResidenceFunction.SUPPORT, "support", "other");
		replace(html, ResidenceFunction.ITEM_CREATE, "item", "other");

		sendHtmlMessage(player, html);
	}

	private void showManageDeco(Player player)
	{
		HtmlMessage html = new HtmlMessage(this);
		html.setFile("residence/edit_deco.htm");

		replace(html, ResidenceFunction.CURTAIN, "curtain", "deco");
		replace(html, ResidenceFunction.PLATFORM, "platform", "deco");

		sendHtmlMessage(player, html);
	}

	protected boolean isHaveRigths(Player player, int rigthsToCheck)
	{
		return player.getClan() != null && (player.getClanPrivileges() & rigthsToCheck) == rigthsToCheck;
	}

	@Override
	public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper)
	{
		List<L2GameServerPacket> list = super.addPacketList(forPlayer, dropper);
		L2GameServerPacket p = decoPacket();
		if(p != null)
			list.add(p);
		return list;
	}
}