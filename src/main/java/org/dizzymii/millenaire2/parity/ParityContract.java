package org.dizzymii.millenaire2.parity;

/**
 * Startup parity contracts that represent baseline rewrite guarantees.
 */
public enum ParityContract {

    CONTENT_DEPLOYED(true, "content_deployed"),
    WORLD_DATA_AVAILABLE(true, "world_data_available"),
    VILLAGE_GENERATION_ENABLED(true, "village_generation_enabled"),
    CULTURES_LOADED(true, "cultures_loaded"),
    GOALS_INITIALIZED(true, "goals_initialized"),
    BIOME_MAPPING_LOADED(true, "biome_mapping_loaded"),
    ECONOMY_CONFIG_LOADED(true, "economy_config_loaded"),
    DIPLOMACY_CONFIG_LOADED(true, "diplomacy_config_loaded"),
    QUEST_DATA_LOADED(false, "quest_data_loaded");

    private final boolean critical;
    private final String id;

    ParityContract(boolean critical, String id) {
        this.critical = critical;
        this.id = id;
    }

    public boolean isCritical() {
        return critical;
    }

    public String getId() {
        return id;
    }
}
