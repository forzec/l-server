package zones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Party;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Zone;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */
public class DragonValley implements OnInitScriptListener
{
	private static ZoneListener _zoneListener;
	private static Zone zone;
	private static final Map<ClassId, Double> weight = new HashMap<ClassId, Double>();
	private static List<Player> inzone = new CopyOnWriteArrayList<Player>();
	private static ScheduledFuture<?> buffTask;

	static
	{
		weight.put(ClassId.duelist, 0.2);
		weight.put(ClassId.dreadnought, 0.7);
		weight.put(ClassId.phoenixKnight, 0.5);
		weight.put(ClassId.hellKnight, 0.5);
		weight.put(ClassId.sagittarius, 0.3);
		weight.put(ClassId.adventurer, 0.4);
		weight.put(ClassId.archmage, 0.3);
		weight.put(ClassId.soultaker, 0.3);
		weight.put(ClassId.arcanaLord, 1.);
		weight.put(ClassId.cardinal, -0.6);
		weight.put(ClassId.hierophant, 0.);
		weight.put(ClassId.evaTemplar, 0.8);
		weight.put(ClassId.swordMuse, 0.5);
		weight.put(ClassId.windRider, 0.4);
		weight.put(ClassId.moonlightSentinel, 0.3);
		weight.put(ClassId.mysticMuse, 0.3);
		weight.put(ClassId.elementalMaster, 1.);
		weight.put(ClassId.evaSaint, -0.6);
		weight.put(ClassId.shillienTemplar, 0.8);
		weight.put(ClassId.spectralDancer, 0.5);
		weight.put(ClassId.ghostHunter, 0.4);
		weight.put(ClassId.ghostSentinel, 0.3);
		weight.put(ClassId.stormScreamer, 0.3);
		weight.put(ClassId.spectralMaster, 1.);
		weight.put(ClassId.shillienSaint, -0.6);
		weight.put(ClassId.titan, 0.3);
		weight.put(ClassId.dominator, 0.1);
		weight.put(ClassId.grandKhauatari, 0.2);
		weight.put(ClassId.doomcryer, 0.1);
		weight.put(ClassId.fortuneSeeker, 0.9);
		weight.put(ClassId.maestro, 0.7);
		weight.put(ClassId.doombringer, 0.2);
		weight.put(ClassId.trickster, 0.5);
		weight.put(ClassId.judicator, 0.1);
		weight.put(ClassId.maleSoulhound, 0.3);
		weight.put(ClassId.femaleSoulhound, 0.3);
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if(cha.isPlayer())
				inzone.add(cha.getPlayer());
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
			if(cha.isPlayer() && inzone.contains(cha.getPlayer()))
				inzone.remove(cha.getPlayer());
		}
	}

	private class BuffTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for(Player izp : inzone)
				if(getBuffLevel(izp) > 0)
					izp.altOnMagicUseTimer(izp, SkillTable.getInstance().getSkillEntry(6885, getBuffLevel(izp)));
		}
	}

	private int getBuffLevel(Player pc)
	{
		if(pc.getParty() == null)
			return 0;
		Party party = pc.getParty();
		// Small party check
		if(party.getMemberCount() < 5)	// toCheck
			return 0;
		// Newbie party or Not in zone member check
		for(Player p : party)
			if(p.getLevel() < 80 || !p.isInZone(zone))
				return 0;

		double points = 0;
		int count = party.getMemberCount();

		for(Player p : party)
			if(weight.containsKey(p.getClassId()))
				points += weight.get(p.getClassId());

		return (int) Math.max(0, Math.min(3, Math.round(points * getCoefficient(count))));  // Brutally custom
	}

	private double getCoefficient(int count)
	{
		double cf;
		switch(count)
		{
			case 4:
				cf = 0.7;
				break;
			case 5:
				cf = 0.75;
				break;
			case 6:
				cf = 0.8;
				break;
			case 7:
				cf = 0.85;
				break;
			case 8:
				cf = 0.9;
				break;
			case 9:
				cf = 0.95;
				break;
			default:
				cf = 1;
		}
		return cf;
	}

	@Override
	public void onInit()
	{
		_zoneListener = new ZoneListener();
		zone = ReflectionUtils.getZone("[dragon_valley]");
		zone.addListener(_zoneListener);
		buffTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new BuffTask(), 1000L, 60000L);
	}
}
