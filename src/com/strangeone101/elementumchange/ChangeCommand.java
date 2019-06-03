package com.strangeone101.elementumchange;

import com.projectkorra.projectkorra.command.PKCommand;
import com.strangeone101.elementumchange.menu.MenuChange;
import com.strangeone101.elementumchange.util.DatabaseUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ChangeCommand extends PKCommand implements CommandExecutor {

	//public static long cooldownPeriod = 1000 * 60 * 60 * 24 * 2; //1000ms x 60s x 60m x 24h x 2d

	public ChangeCommand() {
		super("change", "/b change", "Change your element", new String[] {});
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
		if (args.length == 0 || !sender.hasPermission("bending.change.removecooldowns")) {
			if (sender instanceof Player) {
				long cooldown = DatabaseUtil.getCooldownFromPlayer((Player) sender);
				if (cooldown > 0 && !sender.hasPermission("bending.command.rechoose")) {
					sender.sendMessage(ChatColor.RED + "You can change your element again in " + getTime(cooldown));
				} else {
					MenuChange menu = new MenuChange();
					menu.openMenu((Player) sender);
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You must be a player to run this command!");
			}
		} else if (args[0].equalsIgnoreCase("force") || args[0].equalsIgnoreCase("reset")) {
			if (args.length == 1) {
				sender.sendMessage(ChatColor.RED + "Usage is /change reset <user>");
			} else if (Bukkit.getOfflinePlayer(args[1]) == null) {
				sender.sendMessage(ChatColor.RED + "User not found!");
			} else {
				UUID id = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
				DatabaseUtil.resetCooldown(id);
				sender.sendMessage(ChatColor.GREEN + "The /change cooldown has been reset for user " + Bukkit.getOfflinePlayer(args[1]).getName());
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Usage is /change reset <user>");
		}
		
		
		return true;
	}

	private String getTime(long cooldown) {
		cooldown = cooldown += 1000 * 60; //Add another minute to the clock. That way, it never shows 0m remaining
		long minuteLong = 1000 * 60;
		long hourLong = 1000 * 60 * 60;
		long dayLong = 1000 * 60 * 60 * 24;
		
		int minutes = (int) (cooldown / minuteLong) % 60;
		int hours = (int) (cooldown / hourLong) % 24;
		int days = (int) (cooldown / dayLong);
		
		String s = "";
		
		if (days > 0) s = s + days + "d ";
		if (hours > 0 || days > 0) s = s + hours + "h ";
		s = s + minutes + "m ";
		
		return s;
	}

	@Override
	public void execute(CommandSender commandSender, List<String> list) {
		onCommand(commandSender, null, "change", (String[]) list.toArray());
	}
}
