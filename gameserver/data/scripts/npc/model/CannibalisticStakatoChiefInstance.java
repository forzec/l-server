package npc.model;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Party;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.RaidBossInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

public class CannibalisticStakatoChiefInstance extends RaidBossInstance
{
	private static final int ITEMS[] = { 14833, 14834 };

	public CannibalisticStakatoChiefInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);
		if(killer == null)
			return;
		Creature topdam = getAggroList().getTopDamager();
		if(topdam == null)
			topdam = killer;
		Player pc = topdam.getPlayer();
		if(pc == null)
			return;
		Party party = pc.getParty();
		int itemId;
		if(party != null)
		{
			for(Player partyMember : party.getPartyMembers())
				if(partyMember != null && pc.isInRange(partyMember, Config.ALT_PARTY_DISTRIBUTION_RANGE))
				{
					itemId = ITEMS[Rnd.get(ITEMS.length)];
					ItemFunctions.addItem(partyMember, itemId, 1);
				}
		}
		else
		{
			itemId = ITEMS[Rnd.get(ITEMS.length)];
			ItemFunctions.addItem(pc, itemId, 1);
		}
	}
}