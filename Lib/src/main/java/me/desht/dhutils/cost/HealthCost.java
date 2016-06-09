package me.desht.dhutils.cost;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HealthCost extends Cost {

	protected HealthCost(double quantity) {
		super(quantity);
	}

	@NotNull
	@Override
	public String getDescription() {
		return getQuantity() + " health";
	}

	@Override
	public boolean isAffordable(@NotNull Player player) {
		return player.getHealth() > getQuantity();
	}

	@Override
	public void apply(@NotNull Player player) {
		double min = getQuantity() > player.getMaxHealth() ? 0.0 : 1.0;
		player.setHealth(getAdjustedQuantity((int) player.getHealth(), getQuantity(), min, player.getMaxHealth()));
	}

}
