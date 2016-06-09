package me.desht.dhutils.responsehandler;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResponseHandler {

	private final Plugin plugin;
	private final ConcurrentMap<String, ExpectBase> exp = new ConcurrentHashMap<>();

	public ResponseHandler(Plugin plugin) {
		this.plugin = plugin;
	}

	public void expect(@NotNull Player player, @NotNull ExpectBase data) {
		expect(player, data, null);
	}

	private void expect(@NotNull Player player, @NotNull ExpectBase data, @Nullable Player expectee) {
		data.setPlayerId(player.getUniqueId());
		data.setResponseHandler(this);
		if (expectee != null) {
			exp.put(genKey(expectee.getUniqueId(), data.getClass()), data);
		} else {
			exp.put(genKey(player.getUniqueId(), data.getClass()), data);
		}
	}

	@NotNull
	private String genKey(@NotNull UUID playerId, @NotNull Class<? extends ExpectBase> c) {
		return playerId.toString() + ":" + c.getName();
	}

	public boolean isExpecting(@NotNull Player player, @NotNull Class<? extends ExpectBase> action) {
		return exp.containsKey(genKey(player.getUniqueId(), action));
	}

	public void handleAction(@NotNull Player player, @NotNull Class<? extends ExpectBase> action) {
		ExpectBase e = exp.get(genKey(player.getUniqueId(), action));
		cancelAction(player, action);
		e.doResponse(player.getUniqueId());
	}

	public void cancelAction(@NotNull Player player, @NotNull Class<? extends ExpectBase> action) {
		exp.remove(genKey(player.getUniqueId(), action));
	}

	public <T extends ExpectBase> T getAction(@NotNull Player player, @NotNull Class<T> action) {
		return action.cast(exp.get(genKey(player.getUniqueId(), action)));
	}

	public Plugin getPlugin() {
		return plugin;
	}

}
