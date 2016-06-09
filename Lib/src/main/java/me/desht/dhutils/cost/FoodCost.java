package me.desht.dhutils.cost;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FoodCost extends Cost {

	public FoodCost(double quantity) {
		super(quantity);
	}

	@NotNull
	@Override
	public String getDescription() {
		return (int) getQuantity() + " hunger";
	}

	@Override
	public boolean isAffordable(@NotNull Player player) {
		return player.getFoodLevel() > getQuantity();
	}

	@Override
	public void apply(@NotNull Player player) {
		player.setFoodLevel((int) getAdjustedQuantity(player.getFoodLevel(), getQuantity(), 1, 20));
	}
}
