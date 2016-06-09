package me.desht.dhutils.repo;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

public interface DHStorable {
	@NotNull String getName();

	@NotNull File getStorageFolder();

	@NotNull Map<String, Object> freeze();
}
