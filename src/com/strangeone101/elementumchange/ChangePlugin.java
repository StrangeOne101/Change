package com.strangeone101.elementumchange;

import com.strangeone101.easygui.MenuListener;
import com.strangeone101.elementumchange.util.DatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePlugin extends JavaPlugin {

    private static ChangePlugin INSTANCE;

    public static ChangePlugin getInstance() {
        return INSTANCE;
    }


    @Override
    public void onEnable() {
        INSTANCE = this;
        new ChangeConfig();

        DatabaseUtil.createDB(); //Setup DB

        new MenuListener(this); //Register the GUI listener

        Bukkit.getPluginCommand("change").setExecutor(new ChangeCommand()); //Register the command

        getLogger().info("Changed enabled! Thanks for using! :)");
    }

    /**
     * Makes a description list for items
     * .*/
    public static List<String> lengthSplit(String line, ChatColor color, int length) {
        Pattern p = Pattern.compile("\\G\\s*(.{1,"+length+"})(?=\\s|$)", Pattern.DOTALL);
        Matcher m = p.matcher(line);
        List<String> l = new ArrayList<String>();
        while (m.find()) {
            l.add(color + m.group(1));
        }
        return l;
    }
}
