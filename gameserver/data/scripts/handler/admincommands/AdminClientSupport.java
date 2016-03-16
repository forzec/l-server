package handler.admincommands;

import org.mmocore.gameserver.data.xml.holder.ItemHolder;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.GameObject;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.L2GameServerPacket;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date 23:46/17.05.2011
 */
public class AdminClientSupport extends ScriptAdminCommand
{
	private static final Logger _log = LoggerFactory.getLogger(AdminClientSupport.class);

	public enum Commands
	{
		admin_setskill,
		admin_summon,
		admin_systemmsg
	}

	@Override
	public boolean useAdminCommand(Enum comm, final String[] wordList, String fullString, Player player)
	{
		Commands c = (Commands)comm;
		GameObject target = player.getTarget();
		switch(c)
		{
			case admin_systemmsg:
				if(wordList.length != 2)
					return false;
				if(!player.getPlayerAccess().CanEditChar)
					return false;
				player.sendPacket(new L2GameServerPacket()
				{
					@Override
					protected void writeImpl()
					{
						writeC(0x62);
						writeD(Integer.parseInt(wordList[1]));
						writeD(0);
					}
				});
				break;
			case admin_setskill:
				if(wordList.length != 3)
					return false;

				if(!player.getPlayerAccess().CanEditChar)
					return false;
				if(target == null || !target.isPlayer())
					return false;
				try
				{
					SkillEntry skill = SkillTable.getInstance().getSkillEntry(Integer.parseInt(wordList[1]), Integer.parseInt(wordList[2]));
					if(skill == null)
					{
						player.sendMessage("not find skill: " + wordList[1] + ":" + wordList[2]);
						return false;
					}
					target.getPlayer().addSkill(skill, true);
					target.getPlayer().sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skill.getId(),  skill.getLevel()));
				}
				catch(NumberFormatException e)
				{
					_log.error("AdminClientSupport:useAdminCommand(Enum,String[],String,L2Player): " + e, e);
					return false;
				}
				break;
			case admin_summon:
				if(wordList.length != 3)
					return false;

				try
				{
					int id = Integer.parseInt(wordList[1]);
					long count = Long.parseLong(wordList[2]);

					if(id >= 1000000)
					{
						if(!player.getPlayerAccess().CanEditNPC)
							return false;

						if(target == null)
							target = player;

						NpcTemplate template = NpcHolder.getInstance().getTemplate(id - 1000000);

						for(int i = 0; i < count; i++)
						{
							NpcInstance npc = template.getNewInstance();
							npc.setSpawnedLoc(target.getLoc());
							npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);

							npc.spawnMe(npc.getSpawnedLoc());
						}
					}
					else
					{
						if(!player.getPlayerAccess().UseGMShop)
							return false;
						if(target == null)
							target = player;

						if(!target.isPlayer())
							return false;

						ItemTemplate template = ItemHolder.getInstance().getTemplate(id);
						if(template == null)
							return false;

						if(template.isStackable())
						{
							ItemInstance item = ItemFunctions.createItem(id);
							item.setCount(count);

							target.getPlayer().getInventory().addItem(item);
							target.getPlayer().sendPacket(SystemMessage.obtainItems(id, count, 0));
						}
						else
						{
							for(int i = 0; i < count; i++)
							{
								ItemInstance item = ItemFunctions.createItem(id);

								target.getPlayer().getInventory().addItem(item);
								target.getPlayer().sendPacket(SystemMessage.obtainItems(item));
							}
						}
					}
				}
				catch(NumberFormatException e)
				{
					_log.info("AdminClientSupport:useAdminCommand(Enum,String[],String,L2Player): " + e, e);
					return false;
				}

				break;
		}
		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
