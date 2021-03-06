package me.desht.dhutils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MiscUtil {
    @NotNull
    private static Map<String, String> prevColours = new HashMap<>();

    public static final String STATUS_COLOUR = ChatColor.AQUA.toString();
    public static final String ERROR_COLOUR = ChatColor.RED.toString();
    public static final String ALERT_COLOUR = ChatColor.YELLOW.toString();
    public static final String GENERAL_COLOUR = ChatColor.RESET.toString();

    private static final String BROADCAST_PREFIX = ChatColor.RED + "\u2731&- ";
    private static boolean colouredConsole = true;

    public static void init(Plugin plugin) {
    }

    public static void setColouredConsole(boolean coloured) {
        colouredConsole = coloured;
    }

    public static void errorMessage(@NotNull CommandSender sender, String string) {
        setPrevColour(sender.getName(), ERROR_COLOUR);
        message(sender, ERROR_COLOUR + string, Level.WARNING);
        prevColours.remove(sender.getName());
    }

    public static void statusMessage(@NotNull CommandSender sender, String string) {
        setPrevColour(sender.getName(), STATUS_COLOUR);
        message(sender, STATUS_COLOUR + string, Level.INFO);
        prevColours.remove(sender.getName());
    }

    public static void alertMessage(@NotNull CommandSender sender, String string) {
        setPrevColour(sender.getName(), ALERT_COLOUR);
        message(sender, ALERT_COLOUR + string, Level.INFO);
        prevColours.remove(sender.getName());
    }

    public static void generalMessage(@NotNull CommandSender sender, String string) {
        setPrevColour(sender.getName(), GENERAL_COLOUR);
        message(sender, GENERAL_COLOUR + string, Level.INFO);
        prevColours.remove(sender.getName());
    }

    public static void broadcastMessage(String string) {
        CommandSender sender = Bukkit.getConsoleSender();
        setPrevColour(sender.getName(), ALERT_COLOUR);
        Bukkit.getServer().broadcastMessage(parseColourSpec(sender, BROADCAST_PREFIX + string));
        prevColours.remove(sender.getName());
    }

    private static void setPrevColour(String name, String colour) {
        prevColours.put(name, colour);
    }

    private static String getPrevColour(String name) {
        String colour = prevColours.get(name);
        return colour == null ? GENERAL_COLOUR : colour;
    }

    public static void rawMessage(CommandSender sender, @NotNull String string) {
        boolean strip = sender instanceof ConsoleCommandSender && !colouredConsole;
        for (String line : string.split("\\n")) {
            if (strip) {
                sender.sendMessage(ChatColor.stripColor(line));
            } else {
                sender.sendMessage(line);
            }
        }
    }

    private static void message(CommandSender sender, @NotNull String string, Level level) {
        boolean strip = sender instanceof ConsoleCommandSender && !colouredConsole;
        for (String line : string.split("\\n")) {
            if (strip) {
                LogUtils.log(level, ChatColor.stripColor(parseColourSpec(sender, line)));
            } else {
                sender.sendMessage(parseColourSpec(sender, line));
            }
        }
    }

    public static String formatLocation(@NotNull Location loc) {
        return String.format("%d,%d,%d,%s", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
    }

    @NotNull
    public static Location parseLocation(@NotNull String arglist) {
        return parseLocation(arglist, null);
    }

    @NotNull
    public static Location parseLocation(@NotNull String arglist, CommandSender sender) {
        String s = sender instanceof Player ? "" : ",worldname";
        String args[] = arglist.split(",");

        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int z = Integer.parseInt(args[2]);
            World w = (sender instanceof Player) ? ((Player)sender).getWorld() : findWorld(args[3]);
            return new Location(w, x, y, z);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("You must specify all of x,y,z" + s + ".");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number in " + arglist);
        }
    }

    private static final Pattern colourPat = Pattern.compile("(?<!&)&(?=[0-9a-fA-Fk-oK-OrR])");

    public static String parseColourSpec(@NotNull String spec) {
        return parseColourSpec(null, spec);
    }

    public static String parseColourSpec(@Nullable CommandSender sender, @NotNull String spec) {
        String who = sender == null ? "*" : sender.getName();
        String res = colourPat.matcher(spec).replaceAll("\u00A7");
        return res.replace("&-", getPrevColour(who)).replace("&&", "&");
    }

    public static String unParseColourSpec(@NotNull String spec) {
        return spec.replaceAll("\u00A7", "&");
    }

    /**
     * Find the given world by name.
     *
     * @param worldName name of the world to find
     * @return the World object representing the world name
     * @throws IllegalArgumentException if the given world cannot be found
     */
    public static World findWorld(String worldName) {
        World w = Bukkit.getServer().getWorld(worldName);
        if (w != null) {
            return w;
        } else {
            throw new IllegalArgumentException("World " + worldName + " was not found on the server.");
        }
    }

    /**
     * Split the given string, but ensure single and double quoted sections of the string are
     * kept together.
     * E.g. the String 'one "two three" four' will be split into [ "one", "two three", "four" ]
     *
     * @param s the String to split
     * @return a List of items
     */
    @NotNull
    public static List<String> splitQuotedString(@NotNull String s) {
        List<String> matchList = new ArrayList<>();

        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher regexMatcher = regex.matcher(s);

        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                // Add double-quoted string without the quotes
                matchList.add(regexMatcher.group(1));
            } else if (regexMatcher.group(2) != null) {
                // Add single-quoted string without the quotes
                matchList.add(regexMatcher.group(2));
            } else {
                // Add unquoted word
                matchList.add(regexMatcher.group());
            }
        }

        return matchList;
    }

    /**
     * Return the given collection (of Comparable items) as a sorted list.
     * @param <T> a class that extends {@link Comparable}
     * @param c	the collection to sort
     * @return a list of the sorted items in the collection
     */
    @NotNull
    public static <T extends Comparable<? super T>> List<T> asSortedList(@NotNull Collection<T> c) {
        List<T> list = new ArrayList<>(c);
        java.util.Collections.sort(list);
        return list;
    }

    /**
     * Randomly split the given list into a number of smaller lists.
     * @param <T> the Class to List
     * @param list the list to split
     * @param nLists the number of smaller lists
     * @return an array of lists
     */
    @NotNull
    public static <T> List<T>[] splitList(@NotNull List<T> list, int nLists) {
        @SuppressWarnings("unchecked")
        List<T>[] res = (ArrayList<T>[]) new ArrayList[nLists];
        Collections.shuffle(list);
        for (int i = 0; i < list.size(); i++) {
            res[i % nLists].add(list.get(i));
        }
        return res;
    }

    /**
     * Get a list of all files in the given JAR (or ZIP) file within the given path, and with the
     * given extension.
     *
     * @param jarFile	the JAR file to search
     * @param path	the path within the JAR file to search
     * @param ext	desired extension, may be null
     * @return	an array of path names to the found resources
     * @throws IOException if the file cant be found
     */
    public static String[] listFilesinJAR(@NotNull File jarFile, @NotNull String path, @Nullable String ext) throws IOException {
        ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile));
        ZipEntry ze;

        List<String> list = new ArrayList<>();
        while ((ze = zip.getNextEntry()) != null ) {
            String entryName = ze.getName();
            if (entryName.startsWith(path) && ext != null && entryName.endsWith(ext)) {
                list.add(entryName);
            }
        }
        zip.close();

        return list.toArray(new String[list.size()]);
    }

    /**
     * Load a YAML file, enforcing UTF-8 encoding, and get the YAML configuration from it.
     *
     * @param file the file to load
     * @return the YAML configuration from that file
     * @throws InvalidConfigurationException  if the file cant be parsed
     * @throws IOException if the file isnt found
     */
    @NotNull
    public static YamlConfiguration loadYamlUTF8(@NotNull File file) throws InvalidConfigurationException, IOException {
        StringBuilder sb = new StringBuilder((int) file.length());

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        char[] buf = new char[1024];
        int l;
        while ((l = in.read(buf, 0, buf.length)) > -1) {
            sb = sb.append(buf, 0, l);
        }
        in.close();

        YamlConfiguration yaml = new YamlConfiguration();
        yaml.loadFromString(sb.toString());

        return yaml;
    }

    public static boolean looksLikeUUID(@NotNull String s) {
        return s.length() == 36 && s.charAt(8) == '-' && s.charAt(13) == '-' && s.charAt(18) == '-' && s.charAt(23) == '-';
    }
}
