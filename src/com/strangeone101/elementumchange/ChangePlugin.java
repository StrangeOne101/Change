package com.strangeone101.elementumchange;

import com.strangeone101.easygui.MenuListener;
import com.strangeone101.elementumchange.util.DatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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

        super.onEnable();
    }
}
