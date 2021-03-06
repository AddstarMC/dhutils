package me.desht.dhutils.cost;

import me.desht.dhutils.ExperienceManager;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ExperienceCost extends Cost {

	protected ExperienceCost(double quantity) {
		super(quantity);
	}

	@NotNull
	@Override
	public String getDescription() {
		return (int) getQuantity() + " XP";
	}

	@Override
	public boolean isAffordable(Player player) {
		ExperienceManager em = new ExperienceManager(player);
		return em.getCurrentExp() >= getQuantity();
	}

	@Override
	public void apply(Player player) {
		ExperienceManager em = new ExperienceManager(player);
		em.changeExp(-getQuantity());
	}
}
