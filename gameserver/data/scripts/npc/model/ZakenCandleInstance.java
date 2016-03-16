package npc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Zone;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.l2.components.NpcString;
import org.mmocore.gameserver.network.l2.s2c.ExShowScreenMessage;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public final class ZakenCandleInstance extends NpcInstance
{
	private static final int OHS_Weapon = 15280; // spark
	private static final int THS_Weapon = 15281; // red
	private static final int BOW_Weapon = 15302; // blue
	private static final int Anchor = 32468;
	private boolean used = false;
	private static final Map<String, String[]> zoneSpawns = new HashMap<String, String[]>();
	private static final List<NpcString> phrases = new ArrayList<NpcString>();

	static
	{
		// Zone and spawns correspondence
		zoneSpawns.put("[zaken_day_83_room_lowest_left_bottom]", new String[]{"zaken_day_83_terr4_mob", "zaken_day_terr4_mob"});
		zoneSpawns.put("[zaken_day_83_room_lowest_left_top]", new String[]{"zaken_day_83_terr1_mob", "zaken_day_terr1_mob"});
		zoneSpawns.put("[zaken_day_83_room_lowest_right_top]", new String[]{"zaken_day_83_terr2_mob", "zaken_day_terr2_mob"});
		zoneSpawns.put("[zaken_day_83_room_lowest_right_bottom]", new String[]{"zaken_day_83_terr5_mob", "zaken_day_terr5_mob"});
		zoneSpawns.put("[zaken_day_83_room_lowest_center]", new String[]{"zaken_day_83_terr3_mob", "zaken_day_terr3_mob"});

		zoneSpawns.put("[zaken_day_83_room_mid_left_bottom]", new String[]{"zaken_day_83_terr9_mob", "zaken_day_terr9_mob"});
		zoneSpawns.put("[zaken_day_83_room_mid_left_top]", new String[]{"zaken_day_83_terr6_mob", "zaken_day_terr6_mob"});
		zoneSpawns.put("[zaken_day_83_room_mid_right_top]", new String[]{"zaken_day_83_terr7_mob", "zaken_day_terr7_mob"});
		zoneSpawns.put("[zaken_day_83_room_mid_right_bottom]", new String[]{"zaken_day_83_terr10_mob", "zaken_day_terr10_mob"});
		zoneSpawns.put("[zaken_day_83_room_mid_center]", new String[]{"zaken_day_83_terr8_mob", "zaken_day_terr8_mob"});

		zoneSpawns.put("[zaken_day_83_room_top_left_bottom]", new String[]{"zaken_day_83_terr14_mob", "zaken_day_terr14_mob"});
		zoneSpawns.put("[zaken_day_83_room_top_left_top]", new String[]{"zaken_day_83_terr11_mob", "zaken_day_terr11_mob"});
		zoneSpawns.put("[zaken_day_83_room_top_right_top]", new String[]{"zaken_day_83_terr12_mob", "zaken_day_terr12_mob"});
		zoneSpawns.put("[zaken_day_83_room_top_right_bottom]", new String[]{"zaken_day_83_terr15_mob", "zaken_day_terr15_mob"});
		zoneSpawns.put("[zaken_day_83_room_top_center]", new String[]{"zaken_day_83_terr13_mob", "zaken_day_terr13_mob"});

		phrases.add(NpcString.WHO_DARES_AWKAWEN_THE_MIGHTY_ZAKEN);
		phrases.add(NpcString.THE_CANDLES_CAN_LEAD_YOU_TO_ZAKEN);
		phrases.add(NpcString.YE_NOT_BE_FINDING_ME_BELOW_THE_DRINK);
		phrases.add(NpcString.YE_MUST_BE_THREE_SHEETS_TO_THE_WIND_IF_YER_LOOKIN_FOR_ME_THERE);
		phrases.add(NpcString.YE_NOT_BE_FINDING_ME_IN_THE_CROWS_NEST);
	}

	public ZakenCandleInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		_hasRandomAnimation = false;
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		Reflection r = getReflection();
		if(r.isDefault() || used)
			return;

		used = true;
		setRHandId(OHS_Weapon);
		broadcastCharInfo();
		ThreadPoolManager.getInstance().schedule(new CandleActivation(this, r), 5000L);
	}

	private class CandleActivation extends RunnableImpl
	{
		NpcInstance _candle;
		Reflection _r;

		public CandleActivation(NpcInstance candle, Reflection r)
		{
			_candle = candle;
			_r = r;
		}

		@Override
		public void runImpl() throws Exception
		{
			for(NpcInstance npc : getAroundNpc(1200, 100))
				if(npc.getNpcId() == Anchor)
				{
					setRHandId(BOW_Weapon);
					broadcastCharInfo();
					return;
				}
			for(Player p : _r.getPlayers())
				p.sendPacket(new ExShowScreenMessage(phrases.get(Rnd.get(phrases.size())), 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, true, 1, -1, false));
			Zone zone = _candle.getZone(Zone.ZoneType.dummy);
			if(zone != null && !_r.isDefault())
				_r.spawnByGroup(zoneSpawns.get(zone.getName())[_r.getInstancedZoneId() == 135 ? 0 : 1]);
			setRHandId(THS_Weapon);
			broadcastCharInfo();
		}
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
	}
}