package com.strangeone101.elementumchange.menu;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.Element.SubElement;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent.Result;
import com.strangeone101.easygui.MenuBase;
import com.strangeone101.easygui.MenuItem;
import com.strangeone101.elementumchange.ChangeConfig;
import com.strangeone101.elementumchange.ChangePlugin;
import com.strangeone101.elementumchange.util.DatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class MenuChangeFinalize extends MenuBase {

	public Player player;
	public Element elementToChangeTo;

	public MenuChangeFinalize(Element element) {
		super(ChangeConfig.getLang("Menu.Remove.Title"), 3);
		
		this.elementToChangeTo = element;
	}
	
	@Override
	public void openMenu(Player player) {
		super.openMenu(player);
		
		this.player = player;
		
		this.addMenuItem(getElement(Element.FIRE), 2, 1);
		this.addMenuItem(getElement(Element.WATER), 3, 1);
		this.addMenuItem(getElement(Element.CHI), 4, 1);
		this.addMenuItem(getElement(Element.EARTH), 5, 1);
		this.addMenuItem(getElement(Element.AIR), 6, 1);
		
		this.addMenuItem(MenuChange.getHelp(), 26);
	}
	
	@SuppressWarnings("deprecation")
	public MenuItem getElement(Element element) {
		MaterialData mat = new MaterialData(Material.BLACK_STAINED_GLASS);
		ChatColor color = ChatColor.RED;
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(this.player);
		final boolean has = bPlayer.hasElement(element);
		if (has) {
			if (element == Element.FIRE) {
				mat = new MaterialData(Material.NETHERRACK);
				color = ChatColor.RED;
			} else if (element == Element.WATER) {
				mat = new MaterialData(Material.BLUE_TERRACOTTA);
				color = ChatColor.BLUE;
			} else if (element == Element.EARTH) {
				mat = new MaterialData(Material.GRASS_BLOCK);
				color = ChatColor.GREEN;
			} else if (element == Element.AIR) {
				mat = new MaterialData(Material.QUARTZ_BLOCK);
				color = ChatColor.GRAY;
			} else if (element == Element.CHI) {
				mat = new MaterialData(Material.YELLOW_TERRACOTTA);
				color = ChatColor.GOLD;
			}
		}
		
		
		MenuItem item = new MenuItem(color + "" + ChatColor.BOLD + ChangeConfig.getLang("Menu.Remove.ItemTitle", element), mat) {
			
			@Override
			public void onClick(Player clickedPlayer) {
				if (has) {
					bPlayer.getElements().remove(element);
					bPlayer.getElements().add(elementToChangeTo);
					
					Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeElementEvent(player, player, elementToChangeTo, Result.ADD));
					Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeElementEvent(player, player, element, Result.REMOVE));
					
					for (SubElement sub : Element.getAllSubElements()) {
						if (sub.getParentElement() == elementToChangeTo && bPlayer.hasSubElementPermission(sub)) {
							bPlayer.addSubElement(sub);
						}
					}
					GeneralMethods.removeUnusableAbilities(bPlayer.getName());
					
					player.sendMessage(elementToChangeTo.getColor() + ChangeConfig.getLang("Menu.Change.ConfirmationMsg", elementToChangeTo));
					
					DatabaseUtil.setCooldown(player, System.currentTimeMillis());
					GeneralMethods.saveElements(bPlayer);
					GeneralMethods.saveSubElements(bPlayer);
					closeMenu(player);
				}
			}
		};
		
		if (has) {
			ChangePlugin.lengthSplit(ChangeConfig.getLang("Menu.Remove.AlreadyHave", elementToChangeTo), ChatColor.GRAY, ChangeConfig.getWrapLength())
					.forEach(line -> item.addDescription(line));
			//item.addDescription(ChatColor.GRAY + "Click to remove this element and");
			//item.addDescription(ChatColor.GRAY + "change it for " + elementToChangeTo.getName() + elementToChangeTo.getType().getBending() + "!");
		} else {
			item.addDescription(ChatColor.RED + ChangeConfig.getLang("Menu.Remove.DoNotHave"));
		}
		
		return item;
	}

}
