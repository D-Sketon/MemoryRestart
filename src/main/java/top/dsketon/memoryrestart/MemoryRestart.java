package top.dsketon.memoryrestart;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import top.dsketon.memoryrestart.command.MainCommand;
import top.dsketon.memoryrestart.core.RunTask;

public final class MemoryRestart extends JavaPlugin {

    private RunTask runTask;
    private Plugin pluginConfig;

    public Plugin getPluginConfig() {
        return pluginConfig;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        MainCommand mainCommand = new MainCommand(this);
        getCommand("memoryrestart").setExecutor(mainCommand);
        getCommand("memoryrestart").setTabCompleter(mainCommand);
        pluginConfig = getProvidingPlugin(this.getClass());
        if (pluginConfig.getConfig().getBoolean("enable")) {
            getLogger().info("[MemoryRestart] Restart is enabled.");
            enableRunTask();
        } else {
            getLogger().info("[MemoryRestart] Restart is disabled.");
        }
    }

    @Override
    public void onDisable() {
        disableRunTask();
    }

    /**
     * 设置config并保存
     *
     * @param path  配置path
     * @param value 配置value
     */
    public void setConfig(String path, Object value) {
        pluginConfig.getConfig().set(path, value);
        pluginConfig.saveConfig();
    }

    /**
     * /mrestart enable 回调
     */
    public void enableRunTask() {
        if (runTask == null) {
            runTask = new RunTask(pluginConfig);
            runTask.startTask();
        }
    }

    /**
     * /mrestart disable 回调
     */
    public void disableRunTask() {
        if (runTask != null) {
            runTask.cancelCountDown();
            runTask.cancelTask();
            runTask = null;
        }
    }

    /**
     * /mrestart suspend 回调
     */
    public void suspendRunTask(CommandSender sender) {
        if (runTask != null) {
            runTask.cancelCountDown();
            runTask.resetTask();
            if (pluginConfig.getConfig().getBoolean("suspend.message.enable")) {
                showSuspendMessage(sender);
            }
            if (pluginConfig.getConfig().getBoolean("suspend.title.enable")) {
                showSuspendTitle(sender);
            }
        }
    }

    private void showSuspendTitle(CommandSender sender) {
        String title = pluginConfig.getConfig().getString("suspend.title.titleMsg");
        String subTitle = pluginConfig.getConfig().getString("suspend.title.subtitleMsg");
        if (title != null && subTitle != null) {
            title = ChatColor.translateAlternateColorCodes('&', title);
            subTitle = ChatColor.translateAlternateColorCodes('&', subTitle);

            int fadeIn = pluginConfig.getConfig().getInt("suspend.title.fadeIn");
            int stay = pluginConfig.getConfig().getInt("suspend.title.stay");
            int fadeOut = pluginConfig.getConfig().getInt("suspend.title.fadeOut");
            Player senderPlayer = null;
            if (sender instanceof Player) {
                senderPlayer = (Player) sender;
            }
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.equals(senderPlayer)) {
                    continue;
                }
                onlinePlayer.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
            }
        }
    }

    private void showSuspendMessage(CommandSender sender) {
        String message = pluginConfig.getConfig().getString("countDown.message.msg");
        if (message != null) {
            message = ChatColor.translateAlternateColorCodes('&', message);
            Player senderPlayer = null;
            if (sender instanceof Player) {
                senderPlayer = (Player) sender;
            }
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.equals(senderPlayer)) {
                    continue;
                }
                onlinePlayer.sendMessage(message);
            }
        }
    }


    /**
     * /mrestart reload 回调
     */
    public void updateConfigContext() {
        pluginConfig.reloadConfig();
        if (runTask != null) {
            runTask.setConfig(pluginConfig);
        }
        disableRunTask();
        if (pluginConfig.getConfig().getBoolean("enable")) {
            enableRunTask();
        }
    }

}
