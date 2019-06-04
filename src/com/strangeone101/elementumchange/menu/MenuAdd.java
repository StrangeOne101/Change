package com.strangeone101.elementumchange.menu;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent.Result;
import com.strangeone101.easygui.MenuBase;
import com.strangeone101.easygui.MenuItem;
import com.strangeone101.elementumchange.ChangeConfig;
import com.strangeone101.elementumchange.util.DatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuAdd extends MenuBase {

	private Player player;
	public List<Element> elements = new ArrayList<Element>();
	private int count;
	private List<Element> toRemove = new ArrayList<Element>();

	public MenuAdd(Element... elementsToRemove) {
		super(ChangeConfig.getLang("Menu.ReAdd.Title"), 3);
		
		toRemove.addAll(Arrays.asList(elementsToRemove));
	}
	
	@Override
	public void openMenu(Player player) {
		this.player = player;
		
		
		
		count = DatabaseUtil.getElementCountFromPlayer(player);
		
		/*for (Element e : bPlayer.getElements()) {
			elements.add(e);
		}*/
		
		update();
		
		super.openMenu(player);
	}

	private void update() {
		//TODO check if the amount of elements in `elements` is >= count
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
		
		if (elements.size() >= count) {
			for (Element e : elements) {
				bPlayer.getElements().add(e);
				Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeElementEvent(player, player, e, Result.ADD));
			}
			
			for (Element e2 : toRemove) {
				bPlayer.getElements().remove(e2);
				Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeElementEvent(player, player, e2, Result.REMOVE));
			}
			
			GeneralMethods.saveElements(bPlayer);
			
			player.sendMessage(Element.AVATAR.getColor() + ChangeConfig.getLang("Menu.ReAdd.Confirm"));
			
			DatabaseUtil.setCooldown(player, System.currentTimeMillis());
			GeneralMethods.saveElements(bPlayer);
			closeMenu(player);
			return;
		}
		
		this.addMenuItem(getElementIcon(Element.FIRE), 2, 1);
		this.addMenuItem(getElementIcon(Element.WATER), 3, 1);
		this.addMenuItem(getElementIcon(Element.CHI), 4, 1);
		this.addMenuItem(getElementIcon(Element.EARTH), 5, 1);
		this.addMenuItem(getElementIcon(Element.AIR), 6, 1);
		//if so, add all elements to the player
		//save elements
		//update db
		
	}
	
	@SuppressWarnings("deprecation")
	public MenuItem getElementIcon(Element element) {
		MaterialData mat = new MaterialData(Material.BLACK_STAINED_GLASS);
		ChatColor color = ChatColor.RED;
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(this.player);
		final boolean has = bPlayer.hasElement(element);
		if (!has) {
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
		
		MenuItem item = new MenuItem(color + "" + ChatColor.BOLD + ChangeConfig.getLang("Menu.ReAdd.ItemTitle", element), mat) {

			@Override
			public void onClick(Player player) {
				if (!has) {
					if (elements.contains(element)) {
						elements.remove(element);
					} else {
						elements.add(element);
					}
					update();
				}
			}
			
		};
		
		if (has) {
			item.addDescription(ChatColor.RED + ChangeConfig.getLang("Menu.ReAdd.AlreadyHave"));
		} else {
			item.addDescription(ChatColor.GRAY + ChangeConfig.getLang("Menu.ReAdd.DoNotHave", element));
		}
		int c = count - elements.size();
		item.addDescription(ChatColor.GRAY + "");
		if (elements.contains(element)) {
			item.setEnchanted(true);
			item.addDescription(ChatColor.RED + "" + ChatColor.BOLD + ChangeConfig.getLang("Menu.ReAdd.Selected"));
		}
		String msg = ChangeConfig.getLang("Menu.ReAdd.Remain").replace("%amount%", c + "").replace("%plural%", (c == 0 ? "" : "s"));
		item.addDescription(ChatColor.RED + msg);
		
		return item;
	}

}
