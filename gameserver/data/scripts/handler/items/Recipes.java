package handler.items;

import java.util.Collection;

import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.data.xml.holder.RecipeHolder;
import org.mmocore.gameserver.model.Playable;
import org.mmocore.gameserver.model.Player;
import org.mmocore.gameserver.model.Recipe;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.items.ItemInstance;
import org.mmocore.gameserver.network.l2.components.SystemMsg;
import org.mmocore.gameserver.network.l2.s2c.RecipeBookItemList;
import org.mmocore.gameserver.network.l2.s2c.SystemMessage;


public class Recipes extends ScriptItemHandler
{
	private static int[] _itemIds = null;

	public Recipes()
	{
		Collection<Recipe> rc = RecipeHolder.getInstance().getRecipes();
		_itemIds = new int[rc.size()];
		int i = 0;
		for(Recipe r : rc)
			_itemIds[i++] = r.getRecipeId();
	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;
		Player player = (Player) playable;

		Recipe rp = RecipeHolder.getInstance().getRecipeByRecipeItem(item.getItemId());
		if(rp.isDwarvenRecipe())
		{
			if(player.getDwarvenRecipeLimit() > 0)
			{
				if(player.getDwarvenRecipeBook().size() >= player.getDwarvenRecipeLimit())
				{
					player.sendPacket(SystemMsg.NO_FURTHER_RECIPES_MAY_BE_REGISTERED);
					return false;
				}

				if(rp.getLevel() > player.getSkillLevel(Skill.SKILL_CRAFTING))
				{
					player.sendPacket(SystemMsg.YOUR_CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE);
					return false;
				}
				if(player.hasRecipe(rp))
				{
					player.sendPacket(SystemMsg.THAT_RECIPE_IS_ALREADY_REGISTERED);
					return false;
				}
				if(!player.getInventory().destroyItem(item, 1L))
				{
					player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
					return false;
				}
				// add recipe to recipebook
				player.registerRecipe(rp, true);
				player.sendPacket(new SystemMessage(SystemMsg.S1_HAS_BEEN_ADDED).addItemName(item.getItemId()));
				player.sendPacket(new RecipeBookItemList(player, true));
				return true;
			}
			else
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_REGISTER_A_RECIPE);
		}
		else if(player.getCommonRecipeLimit() > 0)
		{
			if(player.getCommonRecipeBook().size() >= player.getCommonRecipeLimit())
			{
				player.sendPacket(SystemMsg.NO_FURTHER_RECIPES_MAY_BE_REGISTERED);
				return false;
			}
			if(player.hasRecipe(rp))
			{
				player.sendPacket(SystemMsg.THAT_RECIPE_IS_ALREADY_REGISTERED);
				return false;
			}
			if(!player.getInventory().destroyItem(item, 1L))
			{
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
				return false;
			}
			player.registerRecipe(rp, true);
			player.sendPacket(new SystemMessage(SystemMsg.S1_HAS_BEEN_ADDED).addItemName(item.getItemId()));
			player.sendPacket(new RecipeBookItemList(player, false));
			return true;
		}
		else
			player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_REGISTER_A_RECIPE);
		return false;
	}

	@Override
	public int[] getItemIds()
	{
		return _itemIds;
	}
}