package quests;

import java.util.List;

import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.dao.CharacterQuestDAO;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.scripts.Functions;
import org.mmocore.gameserver.utils.Location;


public class _503_PursuitClanAmbition extends Quest
{
	// Items

	// first part
	private final int G_Let_Martien = 3866;
	private final int Th_Wyrm_Eggs = 3842;
	private final int Drake_Eggs = 3841;
	private final int Bl_Wyrm_Eggs = 3840;
	private final int Mi_Drake_Eggs = 3839;
	private final int Brooch = 3843;
	private final int Bl_Anvil_Coin = 3871;

	// second Part
	private final int G_Let_Balthazar = 3867;
	private final int Recipe_Spiteful_Soul_Energy = 14854;
	private final int Spiteful_Soul_Energy = 14855;
	private final int Spiteful_Soul_Vengeance = 14856;

	// third part
	private final int G_Let_Rodemai = 3868;
	private final int Imp_Keys = 3847;
	private final int Scepter_Judgement = 3869;

	// the final item
	private final int Proof_Aspiration = 3870;

	private final int[] EggList = new int[]{
			Mi_Drake_Eggs,
			Bl_Wyrm_Eggs,
			Drake_Eggs,
			Th_Wyrm_Eggs
	};

	// NPCs
	private final int Gustaf = 30760;
	private final int Martien = 30645;
	private final int Athrea = 30758;
	private final int Kalis = 30759;
	private final int Fritz = 30761;
	private final int Lutz = 30762;
	private final int Kurtz = 30763;
	private final int Kusto = 30512;
	private final int Balthazar = 30764;
	private final int Rodemai = 30868;
	private final int Coffer = 30765;
	private final int Cleo = 30766;

	// MOBS
	private final int ThunderWyrm1 = 20282;
	private final int ThunderWyrm2 = 20243;
	private final int Drake1 = 20137;
	private final int Drake2 = 20285;
	private final int BlitzWyrm = 27178;
	private final int SpitefulSoulLeader = 20974;
	private final int GraveGuard = 20668;
	private final int GraveKeymaster = 27179;
	private final int ImperialGravekeeper = 27181;

	public _503_PursuitClanAmbition()
	{
		super(PARTY_ALL);

		addStartNpc(Gustaf);

		addTalkId(Martien);
		addTalkId(Athrea);
		addTalkId(Kalis);
		addTalkId(Fritz);
		addTalkId(Lutz);
		addTalkId(Kurtz);
		addTalkId(Kusto);
		addTalkId(Balthazar);
		addTalkId(Rodemai);
		addTalkId(Coffer);
		addTalkId(Cleo);

		addKillId(ThunderWyrm1, ThunderWyrm2, Drake1, Drake2, BlitzWyrm, SpitefulSoulLeader, GraveGuard, GraveKeymaster, ImperialGravekeeper);

		addAttackId(ImperialGravekeeper);

		for(int i = 3839; i <= 3848; i++)
			addQuestItem(i);

		for(int i = 3866; i <= 3869; i++)
			addQuestItem(i);

		addQuestItem(Recipe_Spiteful_Soul_Energy, Spiteful_Soul_Energy, Spiteful_Soul_Vengeance);
	}

	public Player getLeader(QuestState st)
	{
		Player player = st.getPlayer();
		if(player == null)
			return null;
		Clan clan = player.getClan();
		if(clan == null)
			return null;
		return clan.getLeader().getPlayer();
	}

	// set's leaders quest cond, if he is offline will read out of database :)
	// for now, if the leader is not logged in, this assumes that the variable
	// has already been inserted once (initialized, in some sense).
	public void setLeaderVar(QuestState st, String var, String value)
	{
		Clan clan = st.getPlayer().getClan();
		if(clan == null)
			return;
		Player leader = clan.getLeader().getPlayer();
		if(leader != null)
		{
			if (QuestState.VAR_COND.equalsIgnoreCase(var))
				leader.getQuestState(this).setCond(Integer.parseInt(value));
			else
				leader.getQuestState(this).set(var, value);
		}
		else // для прямой записи в базу cond не корректируем, при загрузке сконвертится само.
		{
			int leaderId = st.getPlayer().getClan().getLeaderId();

			CharacterQuestDAO.getInstance().Q503_setLeaderVar(getId(), leaderId, var, value);
		}
	}

	public boolean checkEggs(QuestState st)
	{
		int count = 0;
		for(int item : EggList)
			if(st.getQuestItemsCount(item) > 9)
				count += 1;
		return count > 3;
	}

	public void giveItem(int item, long maxcount, QuestState st)
	{
		Player player = st.getPlayer();
		if(player == null)
			return;
		Player leader = getLeader(st);
		if(leader == null)
			return;
		if(player.getDistance(leader) > Config.ALT_PARTY_DISTRIBUTION_RANGE)
			return;
		QuestState qs = leader.getQuestState(this);
		if(qs == null)
			return;
		long count = qs.getQuestItemsCount(item);
		if(count < maxcount)
		{
			qs.giveItems(item, 1);
			if(count == maxcount - 1)
				qs.playSound(SOUND_MIDDLE);
			else
				qs.playSound(SOUND_ITEMGET);
		}
	}

	public String exit503(boolean completed, QuestState st)
	{
		if(completed)
		{
			st.giveItems(Proof_Aspiration, 1);
			st.addExpAndSp(0, 250000);
			st.unset("cond");
			st.unset("Fritz");
			st.unset("Lutz");
			st.unset("Kurtz");
			st.unset("ImpGraveKeeper");
			st.exitCurrentQuest(false);
		}
		else
			st.exitCurrentQuest(true);
		st.takeItems(Scepter_Judgement, -1);
		try
		{
			List<Player> members = st.getPlayer().getClan().getOnlineMembers(0);
			for(Player player : members)
			{
				if(player == null)
					continue;
				QuestState qs = player.getQuestState(this);
				if(qs != null)
					qs.exitCurrentQuest(true);
			}
			CharacterQuestDAO.getInstance().Q503_offlineMemberExit(st);
		}
		catch(Exception e)
		{
			return "You dont have any members in your Clan, so you can't finish the Pursuit of Aspiration";
		}
		return "Congratulations, you have finished the Pursuit of Clan Ambition";
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		// Events Gustaf
		if(event.equalsIgnoreCase("30760-08.htm"))
		{
			st.giveItems(G_Let_Martien, 1);
			st.setCond(1);
			st.set("Fritz", "1");
			st.set("Lutz", "1");
			st.set("Kurtz", "1");
			st.set("ImpGraveKeeper", "1");
			st.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("30760-12.htm"))
		{
			st.giveItems(G_Let_Balthazar, 1);
			st.setCond(4);
		}
		else if(event.equalsIgnoreCase("30760-16.htm"))
		{
			st.giveItems(G_Let_Rodemai, 1);
			st.setCond(7);
		}
		else if(event.equalsIgnoreCase("30760-20.htm"))
			exit503(true, st);
		else if(event.equalsIgnoreCase("30760-22.htm"))
			st.setCond(13);
		else if(event.equalsIgnoreCase("30760-23.htm"))
			exit503(true, st);
			// Events Martien
		else if(event.equalsIgnoreCase("30645-03.htm"))
		{
			st.takeItems(G_Let_Martien, -1);
			st.setCond(2);
			CharacterQuestDAO.getInstance().Q503_suscribe_members(st);
			List<Player> members = st.getPlayer().getClan().getOnlineMembers(st.getPlayer().getObjectId());
			for(Player player : members)
				newQuestState(player, STARTED);
		}
		// Events Kurtz
		else if(event.equalsIgnoreCase("30763-03.htm"))
		{
			if(st.getInt("Kurtz") == 1)
			{
				htmltext = "30763-02.htm";
				st.giveItems(Mi_Drake_Eggs, 6);
				st.giveItems(Brooch, 1);
				st.set("Kurtz", "2");
			}
		}
		// Events Lutz
		else if(event.equalsIgnoreCase("30762-03.htm"))
		{
			int lutz = st.getInt("Lutz");
			if(lutz == 1)
			{
				htmltext = "30762-02.htm";
				st.giveItems(Mi_Drake_Eggs, 4);
				st.giveItems(Bl_Wyrm_Eggs, 3);
				st.set("Lutz", "2");
			}
			st.addSpawn(BlitzWyrm, npc.getLoc().x, npc.getLoc().y, npc.getLoc().z, Location.getRandomHeading(), 300, 120000);
			st.addSpawn(BlitzWyrm, npc.getLoc().x, npc.getLoc().y, npc.getLoc().z, Location.getRandomHeading(), 300, 120000);
		}
		// Events Fritz
		else if(event.equalsIgnoreCase("30761-03.htm"))
		{
			int fritz = st.getInt("Fritz");
			if(fritz == 1)
			{
				htmltext = "30761-02.htm";
				st.giveItems(Bl_Wyrm_Eggs, 3);
				st.set("Fritz", "2");
			}
			st.addSpawn(BlitzWyrm, npc.getLoc().x, npc.getLoc().y, npc.getLoc().z, Location.getRandomHeading(), 300, 120000);
			st.addSpawn(BlitzWyrm, npc.getLoc().x, npc.getLoc().y, npc.getLoc().z, Location.getRandomHeading(), 300, 120000);
		}
		// Events Kusto
		else if(event.equalsIgnoreCase("30512-03.htm"))
		{
			st.takeItems(Brooch, -1);
			st.giveItems(Bl_Anvil_Coin, 1);
			st.set("Kurtz", "3");
		}
		// Events Balthazar
		else if(event.equalsIgnoreCase("30764-03.htm"))
		{
			st.takeItems(G_Let_Balthazar, -1);
			st.setCond(5);
			st.set("Kurtz", "3");
		}
		else if(event.equalsIgnoreCase("30764-05.htm"))
		{
			st.takeItems(G_Let_Balthazar, -1);
			st.setCond(5);
		}
		else if(event.equalsIgnoreCase("30764-06.htm"))
		{
			st.takeItems(Bl_Anvil_Coin, -1);
			st.set("Kurtz", "4");
			st.giveItems(Recipe_Spiteful_Soul_Energy, 1);
		}
		// Events Rodemai
		else if(event.equalsIgnoreCase("30868-04.htm"))
		{
			st.takeItems(G_Let_Rodemai, -1);
			st.setCond(8);
		}
		else if(event.equalsIgnoreCase("30868-06a.htm"))
			st.setCond(10);
		else if(event.equalsIgnoreCase("30868-10.htm"))
			st.setCond(12);
			// Events Cleo
		else if(event.equalsIgnoreCase("30766-04.htm"))
		{
			st.setCond(9);

			NpcInstance n = st.findTemplate(Cleo);
			if(n != null)
				Functions.npcSay(n, "Blood and Honour");

			n = st.findTemplate(Kalis);
			if(n != null)
				Functions.npcSay(n, "Ambition and Power");

			n = st.findTemplate(Athrea);
			if(n != null)
				Functions.npcSay(n, "War and Death");
		}
		else if(event.equalsIgnoreCase("30766-08.htm"))
		{
			st.takeItems(Scepter_Judgement, -1);
			exit503(false, st);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int id = st.getState();

		String htmltext = "noquest";
		boolean isLeader = st.getPlayer().isClanLeader();
		if(id == CREATED && npcId == Gustaf)
		{
			if(st.getPlayer().getClan() != null) // has Clan
			{
				if(isLeader) // check if player is clan leader
				{
					int clanLevel = st.getPlayer().getClan().getLevel();
					if(st.getQuestItemsCount(Proof_Aspiration) > 0) // if he has the proof
					// already, tell him
					// what to do now
					{
						htmltext = "30760-03.htm";
						st.exitCurrentQuest(true);
					}
					else if(clanLevel > 3) // if clanLevel > 3 you can take this quest,
						// because repeatable
						htmltext = "30760-04.htm";
					else
					// if clanLevel < 4 you cant take it
					{
						htmltext = "30760-02.htm";
						st.exitCurrentQuest(true);
					}
				}
				else
				// player isnt a leader
				{
					htmltext = "30760-04t.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
			// no Clan
			{
				htmltext = "30760-01.htm";
				st.exitCurrentQuest(true);
			}
			return htmltext;
		}
		else if(st.getPlayer().getClan() != null && st.getPlayer().getClan().getLevel() == 5) // player has level 5 clan already
			return "completed";
		else
			// ######## Leader Area ######
			if(isLeader)
			{
				if(st.getCond() == 0)
					st.setCond(1);
				if(st.get("Kurtz") == null)
					st.set("Kurtz", "1");
				if(st.get("Lutz") == null)
					st.set("Lutz", "1");
				if(st.get("Fritz") == null)
					st.set("Fritz", "1");
				int cond = st.getCond();
				int kurtz = st.getInt("Kurtz");
				int lutz = st.getInt("Lutz");
				int fritz = st.getInt("Fritz");

				if(npcId == Gustaf)
				{
					if(cond == 1)
						htmltext = "30760-09.htm";
					else if(cond == 2)
						htmltext = "30760-10.htm";
					else if(cond == 3)
						htmltext = "30760-11.htm";
					else if(cond == 4)
						htmltext = "30760-13.htm";
					else if(cond == 5)
						htmltext = "30760-14.htm";
					else if(cond == 6)
						htmltext = "30760-15.htm";
					else if(cond == 7)
						htmltext = "30760-17.htm";
					else if(cond == 12)
						htmltext = "30760-19.htm";
					else if(cond == 13)
						htmltext = "30760-24.htm";
					else
						htmltext = "30760-18.htm";
				}
				else if(npcId == Martien)
				{
					if(cond == 1)
						htmltext = "30645-02.htm";
					else if(cond == 2)
						if(checkEggs(st) && kurtz > 1 && lutz > 1 && fritz > 1)
						{
							htmltext = "30645-05.htm";
							st.setCond(3);
							for(int item : EggList)
								st.takeItems(item, -1);
						}
						else
							htmltext = "30645-04.htm";
					else if(cond == 3)
						htmltext = "30645-07.htm";
					else
						htmltext = "30645-08.htm";
				}
				else if(npcId == Lutz && cond == 2)
					htmltext = "30762-01.htm";
				else if(npcId == Kurtz && cond == 2)
					htmltext = "30763-01.htm";
				else if(npcId == Fritz && cond == 2)
					htmltext = "30761-01.htm";
				else if(npcId == Kusto)
				{
					if(kurtz == 1)
						htmltext = "30512-01.htm";
					else if(kurtz == 2)
						htmltext = "30512-02.htm";
					else
						htmltext = "30512-04.htm";
				}
				else if(npcId == Balthazar)
				{
					if(cond == 4)
						if(kurtz > 2)
							htmltext = "30764-04.htm";
						else
							htmltext = "30764-02.htm";
					else if(cond == 5)
						if(st.getQuestItemsCount(Spiteful_Soul_Energy) > 9)
						{
							htmltext = "30764-08.htm";
							st.takeItems(Spiteful_Soul_Energy, -1);
							st.takeItems(Brooch, -1);
							st.setCond(6);
						}
						else
							htmltext = "30764-07.htm";
					else if(cond == 6)
						htmltext = "30764-09.htm";
				}
				else if(npcId == Rodemai)
				{
					if(cond == 7)
						htmltext = "30868-02.htm";
					else if(cond == 8)
						htmltext = "30868-05.htm";
					else if(cond == 9)
						htmltext = "30868-06.htm";
					else if(cond == 10)
						htmltext = "30868-08.htm";
					else if(cond == 11)
						htmltext = "30868-09.htm";
					else if(cond == 12)
						htmltext = "30868-11.htm";
				}
				else if(npcId == Cleo)
				{
					if(cond == 8)
						htmltext = "30766-02.htm";
					else if(cond == 9)
						htmltext = "30766-05.htm";
					else if(cond == 10)
						htmltext = "30766-06.htm";
					else if(cond == 11 || cond == 12 || cond == 13)
						htmltext = "30766-07.htm";
				}
				else if(npcId == Coffer)
				{
					if(st.getCond() == 10)
						if(st.getQuestItemsCount(Imp_Keys) < 6)
							htmltext = "30765-03a.htm";
						else if(st.getInt("ImpGraveKeeper") == 3)
						{
							htmltext = "30765-02.htm";
							st.setCond(11);
							st.takeItems(Imp_Keys, 6);
							st.giveItems(Scepter_Judgement, 1);
						}
						else
							htmltext = "<html><head><body>(You and your Clan didn't kill the Imperial Gravekeeper by your own, do it try again.)</body></html>";
					else
						htmltext = "<html><head><body>(You already have the Scepter of Judgement.)</body></html>";
				}
				else if(npcId == Kalis)
					htmltext = "30759-01.htm";
				else if(npcId == Athrea)
					htmltext = "30758-01.htm";
				return htmltext;
			}
			// ######## Member Area ######
			else
			{
				int cond = CharacterQuestDAO.getInstance().Q503_getLeaderVar(st, QuestState.VAR_COND, getLeader(st));
				if(npcId == Martien && (cond == 1 || cond == 2 || cond == 3))
					htmltext = "30645-01.htm";
				else if(npcId == Rodemai)
				{
					if(cond == 9 || cond == 10)
						htmltext = "30868-07.htm";
					else if(cond == 7)
						htmltext = "30868-01.htm";
				}
				else if(npcId == Balthazar && cond == 4)
					htmltext = "30764-01.htm";
				else if(npcId == Cleo && cond == 8)
					htmltext = "30766-01.htm";
				else if(npcId == Kusto && cond > 2 && cond < 6)
					htmltext = "30512-01a.htm";
				else if(npcId == Coffer && cond == 10)
					htmltext = "30765-01.htm";
				else if(npcId == Gustaf)
					if(cond == 3)
						htmltext = "30760-11t.htm";
					else if(cond == 4)
						htmltext = "30760-15t.htm";
					else if(cond == 12)
						htmltext = "30760-19t.htm";
					else if(cond == 13)
						htmltext = "30766-24t.htm";
				return htmltext;
			}
	}

	@Override
	public String onAttack(NpcInstance npc, QuestState st)
	{
		if(npc.getMaxHp() / 2 > npc.getCurrentHp())
			if(Rnd.chance(4))
			{
				int ImpGraveKepperStat = CharacterQuestDAO.getInstance().Q503_getLeaderVar(st, "ImpGraveKeeper", getLeader(st));
				if(ImpGraveKepperStat == 1)
				{
					for(int i = 1; i <= 4; i++)
						st.addSpawn(27180, 120000);
					setLeaderVar(st, "ImpGraveKeeper", "2");
				}
				else
				{
					List<Player> players = World.getAroundPlayers(npc, 900, 200);
					if(players.size() > 0)
					{
						Player player = players.get(Rnd.get(players.size()));
						if(player != null)
							player.teleToLocation(185462, 20342, -3250);
					}
				}
			}
		return null;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = CharacterQuestDAO.getInstance().Q503_getLeaderVar(st, QuestState.VAR_COND, getLeader(st));
		switch(cond)
		{
			case 2:
				switch(npcId)
				{
					case ThunderWyrm1:
						if(Rnd.chance(20))
							giveItem(Th_Wyrm_Eggs, 10, st);
						break;

					case ThunderWyrm2:
						if(Rnd.chance(15))
							giveItem(Th_Wyrm_Eggs, 10, st);
						break;
					case Drake1:
						if(Rnd.chance(20))
							giveItem(Drake_Eggs, 10, st);
						break;
					case Drake2:
						if(Rnd.chance(25))
							giveItem(Drake_Eggs, 10, st);
						break;
					case BlitzWyrm:
						giveItem(Bl_Wyrm_Eggs, 10, st);
						break;
				}
				break;
			case 5:
				if(npcId == SpitefulSoulLeader && Rnd.chance(25))
					if(Rnd.chance(50))
					{
						if(CharacterQuestDAO.getInstance().Q503_getLeaderVar(st, "Kurtz", getLeader(st)) < 4)
							return null;
						giveItem(Spiteful_Soul_Vengeance, 40, st);
					}
					else
						giveItem(Spiteful_Soul_Energy, 10, st);
				break;
			case 10:
				switch(npcId)
				{
					case GraveGuard:
						if(Rnd.chance(15))
							st.addSpawn(GraveKeymaster, 120000);
						break;
					case GraveKeymaster:
						if(Rnd.chance(80))
							giveItem(Imp_Keys, 6, st);
						break;
					case ImperialGravekeeper:
						NpcInstance spawnedNpc = st.addSpawn(Coffer, 120000);
						Functions.npcSay(spawnedNpc, "Curse of the gods on the one that defiles the property of the empire!");
						setLeaderVar(st, "ImpGraveKeeper", "3");
						break;
				}
				break;
		}
		return null;
	}
}