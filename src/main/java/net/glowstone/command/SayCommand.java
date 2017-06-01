package net.glowstone.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Entity;

import java.util.Collections;

public class SayCommand extends BukkitCommand {

    public SayCommand() {
        super("say", "Say a message.", "/say <message ...>", Collections.emptyList());
        setPermission("minecraft.command.say");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        StringBuilder message = new StringBuilder("[").append(sender.getName() == null ? "Server" : sender.getName()).append("] ");
        boolean targetsSupported = sender instanceof Entity || sender instanceof BlockCommandSender;
        for (String arg : args) {
            if (arg.startsWith("@") && arg.length() >= 2 && targetsSupported) {
                // command targets
                Location location = sender instanceof Entity ? ((Entity) sender).getLocation() : ((BlockCommandSender) sender).getBlock().getLocation();
                CommandTarget target = new CommandTarget(sender, arg);
                Entity[] matched = target.getMatched(location);
                if (matched.length == 0) {
                    sender.sendMessage(ChatColor.RED + "Selector '" + arg + "' found nothing");
                    return false;
                }
                message.append(CommandUtils.prettyPrint(matched)).append(" ");
            } else {
                message.append(arg).append(" ");
            }
        }
        Bukkit.broadcastMessage(message.toString());
        return true;
    }
}
