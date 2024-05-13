package com.kamesuta.ecoscoreboard;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class EcoScoreboard extends JavaPlugin {
    private Scoreboard board;
    private Economy econ;

    private Objective ecoObjective;

    @Override
    public void onEnable() {
        // Plugin startup logic

        // スコアボードマネージャーとボードの初期化
        board = Bukkit.getScoreboardManager().getMainScoreboard();

        // Vaultの初期化
        if (!setupEconomy()) {
            getLogger().severe("Vaultが見つかりませんでした。");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // コンフィグの初期化
        saveDefaultConfig();
        String scoreboardName = getConfig().getString("scoreboard", "money");

        // スコアボードの初期化
        ecoObjective = board.getObjective(scoreboardName);
        if (ecoObjective == null) {
            ecoObjective = board.registerNewObjective(scoreboardName, Criteria.DUMMY, "所持金");
        }

        // 定期的にスコアボードを更新
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ecoObjective.getScore(player.getName()).setScore((int) econ.getBalance(player));
            }
        }, 0, 40);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Vaultの初期化
     *
     * @return 初期化に成功したかどうか
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

}
