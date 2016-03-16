package npc.model;

import java.util.ArrayList;
import java.util.List;

import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.s2c.MagicSkillUse;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import bosses.ValakasManager;

/**
 * @author pchayka
 */

public final class NevitHeraldInstance extends NpcInstance
{
	public NevitHeraldInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("request_blessing"))
		{
			if(player.getEffectList().containEffectFromSkills(23312))
			{
				showChatWindow(player, 1);
				return;
			}
			List<Creature> target = new ArrayList<Creature>();
			target.add(player);
			broadcastPacket(new MagicSkillUse(this, player, 23312, 1, 0, 0));
			callSkill(SkillTable.getInstance().getSkillEntry(23312, 1), target, true);
		}
		else
			super.onBypassFeedback(player, command);
	}
}