package npc.model;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.gameserver.model.Creature;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.instances.SpecialMonsterInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class MeleonInstance extends SpecialMonsterInstance
{
	public final static int Young_Watermelon = 13271;
	public final static int Rain_Watermelon = 13273;
	public final static int Defective_Watermelon = 13272;
	public final static int Young_Honey_Watermelon = 13275;
	public final static int Rain_Honey_Watermelon = 13277;
	public final static int Defective_Honey_Watermelon = 13276;
	public final static int Large_Rain_Watermelon = 13274;
	public final static int Large_Rain_Honey_Watermelon = 13278;

	private HardReference<Player> _spawnerRef;

	public MeleonInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	public void setSpawner(Player spawner)
	{
		_spawnerRef = spawner.getRef();
	}

	public Player getSpawner()
	{
		return _spawnerRef.get();
	}

	@Override
	public void reduceCurrentHp(double i, Creature attacker, SkillEntry skill, int poleHitCount, boolean crit, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage)
	{
		if(attacker.getActiveWeaponInstance() == null)
			return;

		int weaponId = attacker.getActiveWeaponInstance().getItemId();

		if(getNpcId() == Defective_Honey_Watermelon || getNpcId() == Rain_Honey_Watermelon || getNpcId() == Large_Rain_Honey_Watermelon)
		{
			// Разрешенное оружие для больших тыкв:
			// 4202 Chrono Cithara
			// 5133 Chrono Unitus
			// 5817 Chrono Campana
			// 7058 Chrono Darbuka
			// 8350 Chrono Maracas
			if(weaponId != 4202 && weaponId != 5133 && weaponId != 5817 && weaponId != 7058 && weaponId != 8350)
				return;
			i = 1;
		}
		else if(getNpcId() == Rain_Watermelon || getNpcId() == Defective_Watermelon || getNpcId() == Large_Rain_Watermelon)
		{
			i = 5;
		}
		else
			return;

		super.reduceCurrentHp(i, attacker, skill, poleHitCount, crit, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
	}

	@Override
	public long getRegenTick()
	{
		return 0L;
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
}