package com.strangeone101.elementumchange;

import com.projectkorra.projectkorra.Element;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ChangeConfig {

    private static ChangeConfig CONFIG;

    private FileConfiguration config;
    private File file;

    public ChangeConfig() {
        //super(new File("config.yml"));

        this.file = new File(ChangePlugin.getInstance().getDataFolder(), "config.yml");
        this.config = YamlConfiguration.loadConfiguration(file);

        this.create();
        try {
            this.config.load(file);
            this.config.options().copyDefaults(true);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        addConfigDefaults();

        CONFIG = this;
    }

    public void addConfigDefaults() {
        this.config.addDefault("CooldownTime", (long)(1000 * 60 * 60 * 24)); //2 days
        this.config.addDefault("TreatChiDifferently", true); //If chi can't have elements
        this.config.addDefault("LoreLengthWrapping", 45); //How many characters until we make a new line in lores

        this.config.addDefault("Language.NoElements", "You can't change elements if you aren't a bender! Use /b choose instead");
        this.config.addDefault("Language.Menu.Change.Title", "Change your element");
        this.config.addDefault("Language.Menu.Change.Item", "Change to %element%");
        this.config.addDefault("Language.Menu.Change.ChiConfirm", "Clicking YES will remove your chiblocking and replace it with all the elements. You will not be able to change back again for another two days.");
        this.config.addDefault("Language.Menu.Change.ChiConfirm2", "Clicking YES will remove all your elements and replace it with chiblocking. You will not be able to change back again for another two days.");
        this.config.addDefault("Language.Menu.Change.ChiCancel", "Cancel and pick another element");
        this.config.addDefault("Language.Menu.Change.ChiCancelAll", "Switch back to your elements another time");
        this.config.addDefault("Language.Menu.Change.ChiRemove", "You removed your chi and are now all four elements!");
        this.config.addDefault("Language.Menu.Change.ConfirmationMsg", "You changed your element and are now a %bender%!");
        this.config.addDefault("Language.Menu.Change.AlreadyHave", "You already have this element!");
        this.config.addDefault("Language.Menu.Change.DoNotHave", "Click to become a %bender%");
        this.config.addDefault("Language.Menu.Change.HelpTitle", "What do I do?");
        this.config.addDefault("Language.Menu.Change.HelpLore", "Just select what element you want to change to. If you have more than one element, you will be prompted on which element to remove. Easy, right? :D");

        this.config.addDefault("Language.Menu.Confirm.Title", "Are you sure?");
        this.config.addDefault("Language.Menu.Confirm.Yes", "YES");
        this.config.addDefault("Language.Menu.Confirm.No", "NO");

        this.config.addDefault("Language.Menu.Remove.Title", "Select an element to remove");
        this.config.addDefault("Language.Menu.Remove.DoNotHave", "You don't have this element!");
        this.config.addDefault("Language.Menu.Remove.AlreadyHave", "Click to remove this element and change it for %element%");
        this.config.addDefault("Language.Menu.Remove.ItemTitle", "Remove your %bending%");

        this.config.addDefault("Language.Menu.ReAdd.Title", "Choose your elements");
        this.config.addDefault("Language.Menu.ReAdd.Confirm", "You changed your element and are now a multi-bender!");
        this.config.addDefault("Language.Menu.ReAdd.ItemTitle", "Change to %element%");
        this.config.addDefault("Language.Menu.ReAdd.Selected", "ELEMENT SELECTED");
        this.config.addDefault("Language.Menu.ReAdd.Remain", "You can select %amount% more element%plural%");
        this.config.addDefault("Language.Menu.ReAdd.AlreadyHave", "You already have this element!");
        this.config.addDefault("Language.Menu.ReAdd.DoNotHave", "Become a %bender%!");

        this.config.addDefault("Language.Menu.Choose.Title", "Please select an element!");
        this.config.addDefault("Language.Menu.Choose.NoPermission", "You don't have permission to choose this element!");
        this.config.addDefault("Language.Menu.Choose.Confirm", "You are now a %bender%!");
        this.config.addDefault("Language.Menu.Choose.ConfirmationYes", "Are you sure you want to choose %element%? You won't be able to change for 2 days!");
        this.config.addDefault("Language.Menu.Choose.ConfirmationNo", "Are you sure you want to choose %element%? You won't be able to change for 2 days!");
        this.config.addDefault("Language.Menu.Choose.PermRemoved", "You cannot choose an element because your bending has been permanently removed!");
        this.config.addDefault("Language.Menu.Choose.PermRemovedOther", "This player has had their bending permanently removed!");

        this.config.addDefault("Language.Command.OnCooldown", "You can change your element again in %time%");
        this.config.addDefault("Language.Command.NotAPlayer", "You must be a player to run this command!");
        this.config.addDefault("Language.Command.NoPlayerFound", "Player %player% not found!");
        this.config.addDefault("Language.Command.CooldownReset", "The /change cooldown has been reset for user %player%");
        this.config.addDefault("Language.Command.CooldownUsage", "Usage is /change reset <user>");


        try {
            this.config.options().copyDefaults(true);
            this.config.save(this.file);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a file for the {@link FileConfiguration} object. If there are
     * missing folders, this method will try to create them before create a file
     * for the config.
     */
    public void create() {
        if (!this.file.getParentFile().exists()) {
            try {
                this.file.getParentFile().mkdir();
                ChangePlugin.getInstance().getLogger().info("Generating new directory for " + this.file.getName() + "!");
            }
            catch (final Exception e) {
                ChangePlugin.getInstance().getLogger().info("Failed to generate directory!");
                e.printStackTrace();
            }
        }

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
                ChangePlugin.getInstance().getLogger().info("Generating new " + this.file.getName() + "!");
            }
            catch (final Exception e) {
                ChangePlugin.getInstance().getLogger().info("Failed to generate " + this.file.getName() + "!");
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the cooldown for changing elements
     * @return
     */
    public static long getCooldown() {
        return CONFIG.config.getLong("CooldownTime", (long)(1000 * 60 * 60 * 24));
    }

    public static int getWrapLength() {
        return CONFIG.config.getInt("LoreLengthWrapping", 45);
    }

    /**
     * Whether chiblocking should be different to elements (so you can't have elements and chi at once)
     * @return
     */
    public static boolean isChiDifferent() {
        return CONFIG.config.getBoolean("TreatChiDifferently", true);
    }

    public static String getLang(String path) {
        return CONFIG.config.getString("Language." + path);
    }

    public static String getLang(String path, Element element, boolean color) {
        return CONFIG.config.getString("Language." + path, path + " missing")
                .replace("%bender%", (color ? element.getColor() : "" ) + element.getName() + element.getType().getBender())
                .replace("%bending%", (color ? element.getColor() : "" ) + element.getName() + element.getType().getBending())
                .replace("%bend%", (color ? element.getColor() : "" ) + element.getName() + element.getType().getBend())
                .replace("%element%", (color ? element.getColor() : "" ) + element.getName());
    }

    public static String getLang(String path, Element element) {
        return getLang(path, element, false);
    }


}
