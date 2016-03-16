package ai.freya;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;

/**
 * @author pchayka
 */

public class IceCaptainKnight extends Fighter
{
	private long _destroyTimer = 0;
	private boolean _destroyUsed = false;
	private boolean _isHard = false;
	Reflection r = _actor.getReflection();

	public IceCaptainKnight(NpcInstance actor)
	{
		super(actor);
		actor.setItemDropEnabled(false);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		_destroyTimer = System.currentTimeMillis();
		_isHard = r.getInstancedZoneId() == 144;
		for(Player p : r.getPlayers())
			this.notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5);
	}

	@Override
	protected void thinkAttack()
	{
		if(!_destroyUsed && _destroyTimer + 60 * 1000L < System.currentTimeMillis())
		{
			_destroyUsed = true;
			int mode = Rnd.get(3);
			if(!r.isDefault())
				for(Player p : r.getPlayers())
					p.sendPacket(new ExShowScreenMessage(NpcString.THE_SPACE_FEELS_LIKE_ITS_GRADUALLY_STARTING_TO_SHAKE, 5000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, true));
			switch(mode)
			{
				case 0:
					ChatUtils.shout(getActor(), NpcString.ARCHER);
					break;
				case 1:
					ChatUtils.shout(getActor(), NpcString.MY_KNIGHTS);
					break;
				case 2:
					ChatUtils.shout(getActor(), NpcString.I_CAN_TAKE_IT_NO_LONGER);
					break;
				case 3:
					ChatUtils.shout(getActor(), NpcString.ARCHER_);
					break;
			}
			int count = _isHard ? 7 : 5;
			for(int i = 0; i < count; i++)
				r.addSpawnWithoutRespawn(_isHard ? 18856 : 18855, Location.findAroundPosition(_actor, 350, 370), _actor.getGeoIndex());
		}

		super.thinkAttack();
	}

	@Override
	protected void teleportHome()
	{
	}
}