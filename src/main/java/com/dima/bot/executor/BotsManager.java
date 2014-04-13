package com.dima.bot.executor;

import com.dima.bot.settings.SettingsKeeper;

/**
 * User: CHEB
 */
public class BotsManager {

    private SettingsKeeper keeper;

    public BotsManager(SettingsKeeper keeper) {
        this.keeper = keeper;
    }

    public SettingsKeeper getKeeper() {
        return keeper;
    }



}
