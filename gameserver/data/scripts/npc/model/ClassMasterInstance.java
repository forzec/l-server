package npc.model;

import java.util.StringTokenizer;

import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.data.xml.holder.ItemHolder;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.MerchantInstance;
import org.mmocore.gameserver.network.l2.components.HtmlMessage;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.HtmlUtils;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Util;


public final class ClassMasterInstance extends MerchantInstance
{
	public ClassMasterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	private String makeMessage(Player player)
	{
		ClassId classId = player.getClassId();

		int jobLevel = classId.getLevel();
		int level = player.getLevel();

		StringBuilder html = new StringBuilder();
		if(Config.ALLOW_CLASS_MASTERS_LIST.isEmpty() || !Config.ALLOW_CLASS_MASTERS_LIST.contains(jobLevel))
			jobLevel = 4;
		if((level >= 20 && jobLevel == 1 || level >= 40 && jobLevel == 2 || level >= 76 && jobLevel == 3) && Config.ALLOW_CLASS_MASTERS_LIST.contains(jobLevel))
		{
			ItemTemplate item = ItemHolder.getInstance().getTemplate(Config.CLASS_MASTERS_PRICE_ITEM);
			if(Config.CLASS_MASTERS_PRICE_LIST[jobLevel] > 0)
				html.append("Price: ").append(Util.formatAdena(Config.CLASS_MASTERS_PRICE_LIST[jobLevel])).append(" ").append(item.getName()).append("<br1>");
			for(ClassId cid : ClassId.VALUES)
			{
				// Инспектор является наследником trooper и warder, но сменить его как профессию нельзя,
				// т.к. это сабкласс. Наследуется с целью получения скилов родителей.
				if(cid == ClassId.inspector)
					continue;
				if(cid.childOf(classId) && cid.getLevel() == classId.getLevel() + 1)
					html.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_change_class ").append(cid.getId()).append(" ").append(Config.CLASS_MASTERS_PRICE_LIST[jobLevel]).append("\">").append(HtmlUtils.htmlClassName(cid.getId())).append("</a><br>");
			}
			player.sendPacket(new HtmlMessage(this).setHtml(html.toString()));
		}
		else
			switch(jobLevel)
			{
				case 1:
					html.append("Come back here when you reached level 20 to change your class.");
					break;
				case 2:
					html.append("Come back here when you reached level 40 to change your class.");
					break;
				case 3:
					html.append("Come back here when you reached level 76 to change your class.");
					break;
				case 4:
					html.append("There is no class changes for you any more.");
					break;
			}
		return html.toString();
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		HtmlMessage msg = new HtmlMessage(this);
		msg.setFile("custom/31860.htm");
		msg.replace("%classmaster%", makeMessage(player));
		player.sendPacket(msg);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command);
		if(st.nextToken().equals("change_class"))
		{
			int val = Integer.parseInt(st.nextToken());
			long price = Long.parseLong(st.nextToken());
			if(ItemFunctions.deleteItem(player, Config.CLASS_MASTERS_PRICE_ITEM, price))
				changeClass(player, val);
			else if(Config.CLASS_MASTERS_PRICE_ITEM == 57)
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
		}
		else
			super.onBypassFeedback(player, command);
	}

	private void changeClass(Player player, int val)
	{
		if(player.getClassId().getLevel() == 3)
			player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_YOUR_THIRDCLASS_TRANSFER_QUEST); // для 3 профы
		else
			player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_A_CLASS_TRANSFER); // для 1 и 2 профы

		player.setClassId(val, false, false);
		player.broadcastCharInfo();
	}
}