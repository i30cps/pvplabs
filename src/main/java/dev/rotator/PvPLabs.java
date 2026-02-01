package dev.rotator;

import dev.rotator.customitems.CustomItemsListener;
import dev.rotator.customitems.CustomItemsManager;
import dev.rotator.game.DuelCommand;
import dev.rotator.game.GameListener;
import dev.rotator.game.GameManager;
import dev.rotator.kit.InventoryIO;
import dev.rotator.kit.KitManager;
import dev.rotator.party.PartyCommand;
import dev.rotator.party.PartyListener;
import dev.rotator.party.PartyManager;
import fr.mrmicky.fastinv.FastInvManager;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PvPLabs extends JavaPlugin {
    private static PvPLabs mainInstance;
    private PartyManager partyManager;
    private GameManager gameManager;
    private KitManager kitManager;
    private Location spawnLocation;
    private CustomItemsManager customItemsManager;
    private World mainWorld;

    private void configureWorld() {
        GameRule<Boolean>[] turnOn = new GameRule[] {
                GameRule.DO_IMMEDIATE_RESPAWN,
                GameRule.KEEP_INVENTORY,
                GameRule.DO_TILE_DROPS
        }, turnOff = new GameRule[] {
                GameRule.ANNOUNCE_ADVANCEMENTS,
                GameRule.BLOCK_EXPLOSION_DROP_DECAY,
                GameRule.DO_DAYLIGHT_CYCLE,
                GameRule.DO_FIRE_TICK,
                GameRule.DO_INSOMNIA,
                GameRule.DO_VINES_SPREAD,
                GameRule.DO_WEATHER_CYCLE,
                GameRule.MOB_GRIEFING,
                GameRule.DO_MOB_SPAWNING
        };
        for (GameRule<Boolean> rule : turnOn)
            mainWorld.setGameRule(rule, true);
        for (GameRule<Boolean> rule : turnOff)
            mainWorld.setGameRule(rule, false);

        mainWorld.setDifficulty(Difficulty.HARD);
        mainWorld.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
    }

    public void onEnable() {
        FastInvManager.register(this);

        mainInstance = this;

        mainWorld = Bukkit.getWorlds().getFirst();

        partyManager = new PartyManager();
        gameManager = new GameManager();
        kitManager = new KitManager(new InventoryIO(new File(getDataFolder(), "kits")));
        customItemsManager = new CustomItemsManager();

        Bukkit.getPluginManager().registerEvents(new GameListener(), this);
        Bukkit.getPluginManager().registerEvents(new PartyListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomItemsListener(customItemsManager), this);

        getCommand("party").setExecutor(new PartyCommand());
        getCommand("duel").setExecutor(new DuelCommand());
        getCommand("rotatordebug").setExecutor(new DebugCommand());

        configureWorld();

        spawnLocation = new Location(mainWorld, 0, 100, 0);
    }

    public static PvPLabs getMain() {
        return mainInstance;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }
    public GameManager getGameManager() {
        return gameManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public void onDisable() {

    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public CustomItemsManager getCustomItemsManager() {
        return customItemsManager;
    }
}
