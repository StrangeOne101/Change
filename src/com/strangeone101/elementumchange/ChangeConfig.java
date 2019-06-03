package com.strangeone101.elementumchange;

import com.projectkorra.projectkorra.configuration.Config;

import java.io.File;

public class ChangeConfig extends Config {

    private static ChangeConfig CONFIG;

    public ChangeConfig() {
        super(new File("config.yml"));

        addConfigDefaults();

        CONFIG = this;
    }

    public void addConfigDefaults() {
        this.get().addDefault("CooldownTime", (long)(1000 * 60 * 60 * 24)); //2 days
        this.get().addDefault("TreatChiDifferently", true); //If chi can't have elements


    }

    /**
     * Get the cooldown for changing elements
     * @return
     */
    public static long getCooldown() {
        return CONFIG.get().getLong("CooldownTime", (long)(1000 * 60 * 60 * 24));
    }

    /**
     * Whether chiblocking should be different to elements (so you can't have elements and chi at once)
     * @return
     */
    public static boolean isChiDifferent() {
        return CONFIG.get().getBoolean("TreatChiDifferently", true);
    }
}
