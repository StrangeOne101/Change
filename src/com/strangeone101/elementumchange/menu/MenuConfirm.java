package com.strangeone101.elementumchange.menu;

import com.strangeone101.easygui.MenuBase;
import com.strangeone101.easygui.MenuItem;
import com.strangeone101.elementumchange.util.RunnablePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.List;

public class MenuConfirm extends MenuBase
{
	protected MenuBase prev;
	protected RunnablePlayer runConfirm;
	protected RunnablePlayer runCancel;
	protected List<String> loreConfirm;
	protected List<String> loreCancel;
	
	public MenuConfirm(MenuBase previousMenu, RunnablePlayer confirm, RunnablePlayer cancel, List<String> confirmLore, List<String> cancelLore)
	{
		super("Are you sure?", 3);
		this.prev = previousMenu;
		this.runConfirm = confirm;
		this.runCancel = cancel;
		this.loreConfirm = confirmLore;
		this.loreCancel = cancelLore;
		this.addMenuItem(getConfirm(), (this.getInventory().getSize() - 1) / 2 - 1);
		this.addMenuItem(getCancel(),(this.getInventory().getSize() - 1) / 2 + 1);
		
	}
	
	@SuppressWarnings("deprecation")
	protected MenuItem getConfirm()
	{
		MenuItem confirm = new MenuItem(ChatColor.GREEN + "" + ChatColor.BOLD + "YES", new MaterialData(Material.WOOL, (byte)5)) {
			@Override
			public void onClick(Player player) 
			{
				runConfirm.run(player);
			}
		};
		confirm.setDescriptions(loreConfirm);
		return confirm;
	}
	
	
	@SuppressWarnings("deprecation")
	private MenuItem getCancel()
	{
		MenuItem cancel = new MenuItem(ChatColor.RED + "" + ChatColor.BOLD + "NO", new MaterialData(Material.WOOL, (byte)14)) {
			@Override
			public void onClick(Player player) 
			{
				runCancel.run(player);
			}
		};
		cancel.setDescriptions(loreCancel);
		return cancel;
	}
}
