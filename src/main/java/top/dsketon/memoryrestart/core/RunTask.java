package top.dsketon.memoryrestart.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import top.dsketon.memoryrestart.MemoryRestart;

public class RunTask {

    private BukkitRunnable detectRunnable;
    private BukkitRunnable countdownRunnable;

    private Plugin config;
    private long totalMemory;

    private boolean isCountDown;

    public void setConfig(Plugin pluginConfig) {
        this.config = pluginConfig;
    }

    public RunTask(Plugin config) {
        setConfig(config);

        this.isCountDown = false;
    }

    public void startTask() {
        detectRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                totalMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024;
                if (totalMemory > config.getConfig().getLong("limitMemory") && !isCountDown) {
                    isCountDown = true;
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "[MemoryRestart] Allocated Memory: " + totalMemory + "MB. Ready to restart!");
                    countDown();
                }
            }
        };
        detectRunnable.runTaskTimerAsynchronously(MemoryRestart.getPlugin(MemoryRestart.class),
                config.getConfig().getLong("detectDelay") * 20,
                config.getConfig().getLong("detectInterval") * 20);
    }

    public void cancelTask() {
        if (detectRunnable != null) {
            detectRunnable.cancel();
        }
    }

    private void countDown() {
        countdownRunnable = new BukkitRunnable() {
            long delay = config.getConfig().getLong("restartDelay");

            @Override
            public void run() {
                if (delay == 0) {
                    isCountDown = false;
                    Bukkit.getScheduler().callSyncMethod(MemoryRestart.getPlugin(MemoryRestart.class), () ->
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart")
                    );
                    cancel();
                } else {

                    if (delay <= 10 || delay % 10 == 0) {
                        if (config.getConfig().getBoolean("countDown.message.enable")) {
                            showCountDownMessage(delay);
                        }
                        if (config.getConfig().getBoolean("countDown.title.enable")) {
                            showCountDownTitle(delay);
                        }
                    }
                    delay--;
                }
            }
        };
        countdownRunnable.runTaskTimerAsynchronously(MemoryRestart.getPlugin(MemoryRestart.class), 0, 20);
    }

    public void cancelCountDown() {
        if (countdownRunnable != null) {
            countdownRunnable.cancel();
            isCountDown = false;
        }
    }

    public void resetTask() {
        cancelTask();
        startTask();
    }

    private void showCountDownTitle(long delay) {
        String title = config.getConfig().getString("countDown.title.titleMsg");
        String subTitle = config.getConfig().getString("countDown.title.subtitleMsg");
        if (title != null && subTitle != null) {
            title = ChatColor.translateAlternateColorCodes('&', title);
            subTitle = ChatColor.translateAlternateColorCodes('&', subTitle);

            title = title.replace("{{leftTime}}", delay + "s");
            subTitle = subTitle.replace("{{leftTime}}", delay + "s");

            int fadeIn = config.getConfig().getInt("countDown.title.fadeIn");
            int stay = config.getConfig().getInt("countDown.title.stay");
            int fadeOut = config.getConfig().getInt("countDown.title.fadeOut");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
            }
        }
    }

    private void showCountDownMessage(long delay) {
        String message = config.getConfig().getString("countDown.message.msg");
        if (message != null) {
            message = ChatColor.translateAlternateColorCodes('&', message);
            message = message.replace("{{leftTime}}", delay + "s");
            Bukkit.broadcastMessage(message);
        }
    }

}
