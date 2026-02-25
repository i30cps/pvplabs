package dev.rotator.pvplabs;

import dev.rotator.pvplabs.customitems.CustomItemsListener;
import dev.rotator.pvplabs.customitems.CustomItemsManager;
import dev.rotator.pvplabs.game.DuelCommand;
import dev.rotator.pvplabs.game.GameBehaviorListener;
import dev.rotator.pvplabs.game.GameListener;
import dev.rotator.pvplabs.game.GameManager;
import dev.rotator.pvplabs.kit.InventoryIO;
import dev.rotator.pvplabs.kit.KitManager;
import dev.rotator.pvplabs.party.PartyCommand;
import dev.rotator.pvplabs.party.PartyListener;
import dev.rotator.pvplabs.party.PartyManager;
import fr.mrmicky.fastinv.FastInvManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;

public class PvPLabs extends JavaPlugin {
    private static PvPLabs mainInstance;
    private PartyManager partyManager;
    private GameManager gameManager;
    private KitManager kitManager;
    private Location spawnLocation;
    private CustomItemsManager customItemsManager;
    private World mainWorld;
    private BukkitAudiences adventure;

    @NonNull
    public BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

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
        this.adventure = BukkitAudiences.create(this);

        mainInstance = this;

        mainWorld = Bukkit.getWorlds().getFirst();

        partyManager = new PartyManager();
        gameManager = new GameManager();
        kitManager = new KitManager(new InventoryIO(new File(getDataFolder(), "kits")));
        customItemsManager = new CustomItemsManager();

        Bukkit.getPluginManager().registerEvents(new GameListener(), this);
        Bukkit.getPluginManager().registerEvents(new PartyListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomItemsListener(customItemsManager), this);
        Bukkit.getPluginManager().registerEvents(new GameBehaviorListener(gameManager), this);

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
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public CustomItemsManager getCustomItemsManager() {
        return customItemsManager;
    }
}
