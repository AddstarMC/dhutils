package me.desht.dhutils.repo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import me.desht.dhutils.DHUtilsException;
import org.jetbrains.annotations.NotNull;

public class ObjectRepo {
	@NotNull
	private Map<String, DHStorable> repo = new HashMap<>();

	public ObjectRepo() {

	}

	public void store(@NotNull DHStorable object) throws IOException {
		String key = makeKey(object);

		if (repo.containsKey(key)) {
			throw new DHUtilsException("Already storing " + key);
		}

		repo.put(key, object);

		persist(object);
	}

	public <T extends DHStorable> T get(@NotNull Class<T> clazz, String name) {
		String key = makeKey(clazz, name);
		if (!repo.containsKey(key)) {
			throw new DHUtilsException("No such object: " + key);
		}
		return clazz.cast(repo.get(key));
	}

	public void remove(@NotNull DHStorable object) {
		String key = makeKey(object);
		if (!repo.containsKey(key)) {
			throw new DHUtilsException("No such object: " + key);
		}
		repo.remove(key);
		unpersist(object);
	}

	public boolean contains(@NotNull DHStorable object) {
		String key = makeKey(object);
		return repo.containsKey(key);
	}

	private void persist(@NotNull DHStorable object) throws IOException {
		if (object.getStorageFolder() == null) {
			// object doesn't want to be saved
			return;
		}
		File dest = new File(object.getStorageFolder(), object.getName() + ".yml");
		YamlConfiguration c = new YamlConfiguration();
		if (object instanceof ConfigurationSerializable) {
			c.set(object.getName(), object);
		} else {
			YamlConfiguration conf = new YamlConfiguration();
			expandMapIntoConfig(conf, object.freeze());
		}
		c.save(dest);
	}

	private void unpersist(@NotNull DHStorable object) {
		if (object.getStorageFolder() == null) {
			// object doesn't want to be saved
			return;
		}
		File dest = new File(object.getStorageFolder(), object.getName() + ".yml");
		dest.delete();
	}

	@NotNull
	private String makeKey(@NotNull DHStorable object) {
		String k = object.getClass().getCanonicalName() + ":" + object.getName();
		return k;
	}

	@NotNull
	private String makeKey(@NotNull Class<? extends DHStorable> clazz, String name) {
		String k = clazz.getCanonicalName() + ":" + name;
		return k;
	}

	@SuppressWarnings("unchecked")
	private static void expandMapIntoConfig(@NotNull ConfigurationSection conf, @NotNull Map<String, Object> map) {
		for (Entry<String, Object> e : map.entrySet()) {
			if (e.getValue() instanceof Map<?,?>) {
				ConfigurationSection section = conf.createSection(e.getKey());
				Map<String,Object> subMap = (Map<String, Object>) e.getValue();
				expandMapIntoConfig(section, subMap);
			} else {
				conf.set(e.getKey(), e.getValue());
			}
		}
	}
}
