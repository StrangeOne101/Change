package com.strangeone101.elementumchange.util;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.storage.DBConnection;
import com.strangeone101.elementumchange.ChangeConfig;
import com.strangeone101.elementumchange.ChangePlugin;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseUtil {
	
	public static String databaseType = ProjectKorra.plugin.getConfig().getString("Storage.engine");
	
	public static void createDB() {
		if (!DBConnection.sql.tableExists("pk_changecooldowns")) {
			ChangePlugin.getInstance().getLogger().info("Creating database to store change cooldowns");
			
			if (databaseType.equalsIgnoreCase("mysql")) {
				String query = "CREATE TABLE `pk_changecooldowns` (" + "`uuid` varchar(36) NOT NULL," + "`player` varchar(16) NOT NULL," + "`cooldown` BIGINT DEFAULT 0, `elementCount` TINYINT DEFAULT 1, PRIMARY KEY (uuid));";
				DBConnection.sql.modifyQuery(query, false);
			} else { //SQLite
				String query = "CREATE TABLE `pk_changecooldowns` (" + "`uuid` TEXT(36) PRIMARY KEY," + "`player` TEXT(16)," + "`cooldown` BIGINT DEFAULT 0, `elementCount` TINYINT DEFAULT 1);";
				DBConnection.sql.modifyQuery(query, false);
			}

			ChangePlugin.getInstance().getLogger().info("Database created!");
		}
	}
	
	public static long getCooldownFromPlayer(Player player) {
		ResultSet rs2 = DBConnection.sql.readQuery("SELECT * FROM pk_changecooldowns WHERE uuid = '" + player.getUniqueId() + "'");
		try {
			if (rs2.next()) {
				long cooldown = rs2.getLong("cooldown");
				
				long c = (cooldown + ChangeConfig.getCooldown()) - System.currentTimeMillis();
				return c < 0 ? 0 : c;
			} else {
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;	
	}
	
	public static void setCooldown(Player player, long cooldown) {
		ResultSet rs2 = DBConnection.sql.readQuery("SELECT * FROM pk_changecooldowns WHERE uuid = '" + player.getUniqueId() + "'");
		
		try {
			if (rs2.next()) {
				DBConnection.sql.modifyQuery("UPDATE pk_changecooldowns SET cooldown = " + cooldown + " WHERE uuid = '" + player.getUniqueId() + "'");
				DBConnection.sql.modifyQuery("UPDATE pk_changecooldowns SET player = '" + player.getName() + "' WHERE uuid = '" + player.getUniqueId() + "'");
			} else {
				DBConnection.sql.modifyQuery("INSERT INTO pk_changecooldowns (uuid, player, cooldown) VALUES ('" + player.getUniqueId() + "', '" + player.getName() + "', " + cooldown + ")");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void resetCooldown(UUID uuid) {
		ResultSet rs2 = DBConnection.sql.readQuery("SELECT * FROM pk_changecooldowns WHERE uuid = '" + uuid.toString() + "'");
		
		try {
			if (rs2.next()) {
				DBConnection.sql.modifyQuery("UPDATE pk_changecooldowns SET cooldown = 0 WHERE uuid = '" + uuid.toString() + "'");
			} else {
				DBConnection.sql.modifyQuery("INSERT INTO pk_changecooldowns (uuid, cooldown) VALUES ('" + uuid + "', 0)");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static int getElementCountFromPlayer(Player player) {
		ResultSet rs2 = DBConnection.sql.readQuery("SELECT * FROM pk_changecooldowns WHERE uuid = '" + player.getUniqueId() + "'");
		try {
			if (rs2.next()) {
				byte count = rs2.getByte("elementCount");
				
				return count;
			} else {
				return 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;	
	}
	
	public static void setElementCount(Player player, int count) {
		ResultSet rs2 = DBConnection.sql.readQuery("SELECT * FROM pk_changecooldowns WHERE uuid = '" + player.getUniqueId() + "'");
		
		try {
			if (rs2.next()) {
				DBConnection.sql.modifyQuery("UPDATE pk_changecooldowns SET elementCount = " + count + " WHERE uuid = '" + player.getUniqueId() + "'");
				DBConnection.sql.modifyQuery("UPDATE pk_changecooldowns SET player = '" + player.getName() + "' WHERE uuid = '" + player.getUniqueId() + "'");
			} else {
				DBConnection.sql.modifyQuery("INSERT INTO pk_changecooldowns (uuid, player, elementCount) VALUES ('" + player.getUniqueId() + "', '" + player.getName() + "', " + count + ")");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
