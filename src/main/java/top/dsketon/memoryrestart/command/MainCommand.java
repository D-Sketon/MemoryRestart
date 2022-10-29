package top.dsketon.memoryrestart.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import top.dsketon.memoryrestart.MemoryRestart;

import java.util.ArrayList;
import java.util.List;


public class MainCommand implements TabExecutor {

    private final MemoryRestart memoryRestart;
    private final List<String> commands = new ArrayList<>();


    public MainCommand(MemoryRestart memoryRestart) {
        this.memoryRestart = memoryRestart;
        this.commands.add("reload");
        this.commands.add("suspend");
        this.commands.add("enable");
        this.commands.add("disable");
        this.commands.add("status");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("MemoryRestart.memoryrestart")) {
            if (args.length == 1) {
                String arg0 = args[0];
                switch (arg0) {
                    case "reload":
                        memoryRestart.updateConfigContext();
                        sender.sendMessage(ChatColor.GREEN + "[MemoryRestart] Reload successfully!");
                        break;
                    case "disable":
                        memoryRestart.setConfig("enable", false);
                        memoryRestart.disableRunTask();
                        sender.sendMessage(ChatColor.GREEN + "[MemoryRestart] Disable successfully!");
                        break;
                    case "enable":
                        memoryRestart.setConfig("enable", true);
                        memoryRestart.enableRunTask();
                        sender.sendMessage(ChatColor.GREEN + "[MemoryRestart] Enable successfully!");
                        break;
                    case "suspend":
                        memoryRestart.suspendRunTask(sender);
                        sender.sendMessage(ChatColor.YELLOW + "[MemoryRestart] The restart has been suspended!");
                        break;
                    case "status":
                        sender.sendMessage(ChatColor.GREEN + "[MemoryRestart] Allocated Memory: " +
                                Runtime.getRuntime().totalMemory() / 1024 / 1024 + "MB");
                        sender.sendMessage(ChatColor.GREEN + "[MemoryRestart] Limited Memory: " +
                                memoryRestart.getPluginConfig().getConfig().getLong("limitMemory") + "MB");
                        sender.sendMessage(ChatColor.GREEN + "[MemoryRestart] Plugin status: " +
                                (memoryRestart.getPluginConfig().getConfig().getBoolean("enable") ? "enable" : "disable"));
                        break;
                    default:
                        sender.sendMessage(ChatColor.RED + "[MemoryRestart] Unknown parameter!");
                        break;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "[MemoryRestart] Unknown parameter!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "[MemoryRestart] No permission!");
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return commands;
        }
        return null;
    }
}
