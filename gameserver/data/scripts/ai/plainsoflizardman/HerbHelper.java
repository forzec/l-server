package ai.plainsoflizardman;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.util.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.World;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author VISTALL
 * @date 11:35/01.05.2012
 */
public class HerbHelper
{
	private final SkillEntry aura_hp03 = SkillTable.getInstance().getSkillEntry(6627, 3);
	private final SkillEntry aura_hp02 = SkillTable.getInstance().getSkillEntry(6626, 2);
	private final SkillEntry aura_hp01 = SkillTable.getInstance().getSkillEntry(6625, 1);

	private final SkillEntry aura_mp03 = SkillTable.getInstance().getSkillEntry(6630, 3);
	private final SkillEntry aura_mp02 = SkillTable.getInstance().getSkillEntry(6629, 2);
	private final SkillEntry aura_mp01 = SkillTable.getInstance().getSkillEntry(6628, 1);

	private final SkillEntry aura_special03 = SkillTable.getInstance().getSkillEntry(6640, 1);
	private final SkillEntry aura_special02 = SkillTable.getInstance().getSkillEntry(6638, 1);
	private final SkillEntry aura_special01 = SkillTable.getInstance().getSkillEntry(6636, 1);

	private final SkillEntry aura_bow01 = SkillTable.getInstance().getSkillEntry(6674, 1);

	private final SkillEntry aura_melee05 = SkillTable.getInstance().getSkillEntry(6639, 1);
	private final SkillEntry aura_melee04 = SkillTable.getInstance().getSkillEntry(6637, 1);
	private final SkillEntry aura_melee03 = SkillTable.getInstance().getSkillEntry(6635, 1);
	private final SkillEntry aura_melee02 = SkillTable.getInstance().getSkillEntry(6633, 1);
	private final SkillEntry aura_melee01 = SkillTable.getInstance().getSkillEntry(6631, 1);

	private static HerbHelper _instance;

	public static HerbHelper getInstance()
	{
		if(_instance == null)
			_instance = new HerbHelper();  // не менянь - иначе при обращении - скилы могут быть нулл(старт сервака)
		return _instance;
	}

	public static void give(final Creature actor, final Creature killer)
	{
		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				getInstance().give0(actor, killer);
			}
		}, 1000L);
	}

	public void give0(Creature actor, Creature killer)
	{
		if(killer == null)
			return;

		int rnd = Rnd.get(100);
		if(rnd <= 42)
		{
			int rnd2 = Rnd.get(100);
			if(rnd2 <= 7)
				castAura(actor, aura_hp03);
			else if(rnd2 <= 45)
				castAura(actor, aura_hp02);

			castAura(actor, aura_hp01);
		}

		if(rnd <= 11)
		{
			int rnd2 = Rnd.get(100);
			if(rnd2 <= 8 )
				castAura(actor, aura_mp03);
			else if( rnd2 <= 60)
				castAura(actor, aura_mp02);

			castAura(actor, aura_mp01);
		}

		if(rnd <= 25)
		{
			int rnd2 = Rnd.get(100);
			if(rnd2 <= 20 )
				aura_melee05.getEffects(killer, killer, false, false);
			else if (rnd2 <= 40)
				aura_bow01.getEffects(killer, killer, false, false);
			else if(rnd2 <= 60)
				aura_melee03.getEffects(killer, killer, false, false);
			else if(rnd2 <= 80)
				aura_melee02.getEffects(killer, killer, false, false);

			aura_melee01.getEffects(killer, killer, false, false);
		}

		if(rnd <= 10)
			aura_bow01.getEffects(killer, killer, false, false);

		if(rnd <= 1)
		{
			int rnd2 = Rnd.get(100);
			if(rnd2 <= 34)
			{
				aura_melee01.getEffects(killer, killer, false, false);
				aura_melee02.getEffects(killer, killer, false, false);
				aura_melee03.getEffects(killer, killer, false, false);
			}
			else if(rnd2 <= 67)
				aura_bow01.getEffects(killer, killer, false, false);

			castAura(actor, aura_hp03);
			castAura(actor, aura_mp03);
		}

		if(rnd <= 11)
		{
			int rnd2 = Rnd.get(100);
			if(rnd2 <= 3)
				aura_special03.getEffects(killer, killer, false, false);
			else if(rnd2 <= 6)
				aura_special02.getEffects(killer, killer, false, false);
			aura_special01.getEffects(killer, killer, false, false);
		}
	}

	private static void castAura(Creature actor, SkillEntry skillEntry)
	{
		for(NpcInstance npc : World.getAroundNpc(actor, skillEntry.getTemplate().getSkillRadius(), 100))
			if(!npc.isDead())
				skillEntry.getEffects(actor, npc, false, false);
	}
}
