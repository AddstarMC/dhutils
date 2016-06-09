package me.desht.dhutils;

import org.jetbrains.annotations.NotNull;

public interface PluginVersionListener {
	void onVersionChanged(int oldVersion, int newVersion);

	@NotNull String getPreviousVersion();

	void setPreviousVersion(String currentVersion);
}
