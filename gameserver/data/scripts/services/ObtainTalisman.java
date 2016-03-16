package services;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.napile.primitive.lists.IntList;
import org.napile.primitive.lists.impl.ArrayIntList;

/**
 * Используется для выдачи талисманов в крепостях и замках за Knight's Epaulette.
 * @author SYS
 */
public class ObtainTalisman
{
	private IntList _list = new ArrayIntList();

	public ObtainTalisman()
	{
		//9914-9965
		for(int i = 9914; i <= 9965; i++)
			if(i != 9923)
				_list.add(i);
		//10416-10424
		for(int i = 10416; i <= 10424; i++)
			_list.add(i);
		//10518-10519
		for(int i = 10518; i <= 10519; i++)
			_list.add(i);
		//10533-10543
		for(int i = 10533; i <= 10543; i++)
			_list.add(i);
	}

	@Bypass("services.ObtainTalisman:Obtain")
	public void Obtain(Player player, NpcInstance npc, String[] arg)
	{
		if(player == null || npc == null)
			return;

		if(!NpcInstance.canBypassCheck(player, npc))
			return;

		if(!player.isQuestContinuationPossible(false))
		{
			player.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
			return;
		}

		if(!ItemFunctions.deleteItem(player, 9912, 10))
		{
			Functions.show("scripts/services/ObtainTalisman-no.htm", player, npc);
			return;
		}

		ItemFunctions.addItem(player, _list.get(Rnd.get(_list.size())), 1);
		Functions.show("scripts/services/ObtainTalisman.htm", player, npc);
	}
}