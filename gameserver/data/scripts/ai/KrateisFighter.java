package ai;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.entity.events.impl.KrateisCubeEvent;
import org.mmocore.gameserver.model.entity.events.objects.KrateisCubePlayerObject;
import org.mmocore.gameserver.model.instances.NpcInstance;

/**
 * @author VISTALL
 * @date  11:31/18.11.2010
 */
public class KrateisFighter extends Fighter
{
	public KrateisFighter(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);

		Player player = killer.getPlayer();
		if(player == null)
			return;

		KrateisCubeEvent cubeEvent = getActor().getEvent(KrateisCubeEvent.class);
		if(cubeEvent == null)
			return;

		KrateisCubePlayerObject particlePlayer = cubeEvent.getParticlePlayer(player);

		particlePlayer.setPoints(particlePlayer.getPoints() + 3);
		cubeEvent.updatePoints(particlePlayer);
	}
}
