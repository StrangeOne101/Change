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
import com.strangeone101.elementumchange.util.DatabaseUtil;
import com.strangeone101.elementumchange.util.RunnablePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MenuChange extends MenuBase {
	
	public Element elementToChangeTo = null;
	public Player player;

	public MenuChange() {
		super("Change your element", 3);
	}
	
	@Override
	public void openMenu(Player player) {
		this.player = player;
		
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
		
		if (bPlayer.getElements().size() == 0) {
			this.closeMenu(player);
			player.sendMessage(ChatColor.RED + "You can't change elements if you aren't a bender! Go to /choose instead!");
			return;
		}
		
		if (bPlayer.getElements().get(0) == Element.CHI && DatabaseUtil.getElementCountFromPlayer(player) >= 4 && ChangeConfig.isChiDifferent()) {
			MenuBase instance = this;
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GRAY + "Clicking YES will remove your chiblocking");
			lore.add(ChatColor.GRAY + "and replace it with all the elements. You");
			lore.add(ChatColor.GRAY + "will not be able to change back again for");
			lore.add(ChatColor.GRAY + "another two days.");
			
			//We open a confirmation menu because players might not realize switching to chi removes other
			//elements. This is just a simple YES/NO gui.
			MenuConfirm confirmation = new MenuConfirm(instance, new RunnablePlayer() {
				
				@Override
				public void run(Player clickedplayer) { //What happens when they click yes
					bPlayer.getElements().remove(Element.CHI);
					
					for (Element e : Arrays.asList(new Element[] {Element.FIRE, Element.WATER, Element.AIR, Element.EARTH})) {
						Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeElementEvent(player, player, e, Result.REMOVE));
						bPlayer.getElements().add(e);
						
						for (SubElement sub : Element.getAllSubElements()) {
							if (sub.getParentElement() == e && bPlayer.hasSubElementPermission(sub)) {
								bPlayer.addSubElement(sub);
							}
						}
					}
					GeneralMethods.saveElements(bPlayer);
					GeneralMethods.saveSubElements(bPlayer);
					
					player.sendMessage(Element.AVATAR.getColor() + "You removed your chi and are now all four elements!");
					DatabaseUtil.setCooldown(player, System.currentTimeMillis());
					player.closeInventory();
				}
				
			}, new RunnablePlayer() { //When they click no, switch to the previous menu

				@Override
				public void run(Player clickedplayer) {
					player.closeInventory();
				}
				
			}, lore, Arrays.asList(new String[] {ChatColor.GRAY + "Switch back to your elements another time"}));
			
			switchMenu(player, confirmation);
		}
		/*if (this.elementToChange == null) {
			if (bPlayer.getElements().size() == 0) {
				this.closeMenu(player);
				player.sendMessage(ChatColor.RED + "You can't change elements if you aren't a bender! Go to /choose instead!");
				return;
			} else if (bPlayer.getElements().size() == 1) {
				this.elementToChange = bPlayer.getElements().get(0);
			} else {
				//If they have more than one element, show them a GUI to select which one to remove.
				switchMenu(player, new MenuChangeOld());
				return;
			}
		}*/
		
		super.openMenu(player);
		
		update();
	}
	
	/**Update the GUI*/
	public void update() {
		/*if (this.elementToChange == null) {
			this.player.sendMessage(ChatColor.RED + "No element selected? How did this happen???");
			this.closeMenu(player);
			return;
		}*/
		
		this.addMenuItem(getElementIcon(Element.FIRE), 2, 1);
		this.addMenuItem(getElementIcon(Element.WATER), 3, 1);
		this.addMenuItem(getElementIcon(Element.CHI), 4, 1);
		this.addMenuItem(getElementIcon(Element.EARTH), 5, 1);
		this.addMenuItem(getElementIcon(Element.AIR), 6, 1);
		
		this.addMenuItem(getHelp(), 26);
		
	}
	
	@SuppressWarnings("deprecation")
	public MenuItem getElementIcon(Element element) {
		MaterialData mat = new MaterialData(Material.STAINED_GLASS, (byte) 15);
		ChatColor color = ChatColor.RED;
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(this.player);
		final boolean has = bPlayer.hasElement(element);
		if (!has) {
			if (element == Element.FIRE) {
				mat = new MaterialData(Material.NETHERRACK);
				color = ChatColor.RED;
			} else if (element == Element.WATER) {
				mat = new MaterialData(Material.STAINED_CLAY, (byte)11);
				color = ChatColor.BLUE;
			} else if (element == Element.EARTH) {
				mat = new MaterialData(Material.GRASS);
				color = ChatColor.GREEN;
			} else if (element == Element.AIR) {
				mat = new MaterialData(Material.QUARTZ_BLOCK);
				color = ChatColor.GRAY;
			} else if (element == Element.CHI) {
				mat = new MaterialData(Material.STAINED_CLAY, (byte)4);
				color = ChatColor.GOLD;
			}
		}
		
		MenuBase instance = this;
		
		
		MenuItem item = new MenuItem(color + "" + ChatColor.BOLD + "Change to " + element.getName(), mat) {

			@Override
			public void onClick(Player player) {
				if (has) return;
				if (elementToChangeTo == null) {
					elementToChangeTo = element;
					int count = DatabaseUtil.getElementCountFromPlayer(player);
					
					if (element == Element.CHI  && ChangeConfig.isChiDifferent()) { //So much work for just chi :P
						if (bPlayer.getElements().size() > 1) {
							List<String> lore = new ArrayList<String>();
							lore.add(ChatColor.GRAY + "Clicking YES will remove all your other");
							lore.add(ChatColor.GRAY + "elements and replace them with chi. You");
							lore.add(ChatColor.GRAY + "will not be able to change this again for");
							lore.add(ChatColor.GRAY + "another two days.");
							
							//We open a confirmation menu because players might not realize switching to chi removes other
							//elements. This is just a simple YES/NO gui.
							MenuConfirm confirmation = new MenuConfirm(instance, new RunnablePlayer() {
								
								@Override
								public void run(Player clickedplayer) { //What happens when they click yes
									switchToChi();
									DatabaseUtil.setCooldown(player, System.currentTimeMillis());
									player.closeInventory();
								}
								
							}, new RunnablePlayer() { //When they click no, switch to the previous menu

								@Override
								public void run(Player clickedplayer) {
									switchMenu(player, instance);
								}
								
							}, lore, Arrays.asList(new String[] {ChatColor.GRAY + "Cancel and pick another element"}));
							
							switchMenu(player, confirmation);
						} else {
							Element oldElement = bPlayer.getElements().get(0);
							bPlayer.getElements().remove(oldElement);
							bPlayer.getElements().add(element);
							
							Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeElementEvent(player, player, element, Result.ADD));
							Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeElementEvent(player, player, oldElement, Result.REMOVE));
							
							for (SubElement sub : Element.getAllSubElements()) {
								if (sub.getParentElement() == element && bPlayer.hasSubElementPermission(sub)) {
									bPlayer.addSubElement(sub);
								}
							}
							GeneralMethods.removeUnusableAbilities(bPlayer.getName());
							
							player.sendMessage(element.getColor() + "You changed your element and are now a " + element.getName() + element.getType().getBender() + "!");
							
							GeneralMethods.saveElements(bPlayer);
							GeneralMethods.saveSubElements(bPlayer);
							DatabaseUtil.setCooldown(player, System.currentTimeMillis());
							
							closeMenu(player);
						}
					} else { //All other elements
						if (bPlayer.getElements().get(0) == Element.CHI) { //If they want to switch FROM chi
							count = DatabaseUtil.getElementCountFromPlayer(player);
								
							if (count == 1) {
								elementToChangeTo = element;
								Element oldElement = bPlayer.getElements().get(0);
								bPlayer.getElements().remove(oldElement);
								bPlayer.getElements().add(element);
								
								Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeElementEvent(player, player, element, Result.ADD));
								Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeElementEvent(player, player, oldElement, Result.REMOVE));
								
								for (SubElement sub : Element.getAllSubElements()) {
									if (sub.getParentElement() == element && bPlayer.hasSubElementPermission(sub)) {
										bPlayer.addSubElement(sub);
									}
								}
								GeneralMethods.removeUnusableAbilities(bPlayer.getName());
								player.sendMessage(element.getColor() + "You changed your element and are now a " + element.getName() + element.getType().getBender() + "!");
								
								DatabaseUtil.setCooldown(player, System.currentTimeMillis());
								GeneralMethods.saveElements(bPlayer);
								GeneralMethods.saveSubElements(bPlayer);
								closeMenu(player);
							} else {
								MenuAdd menu = new MenuAdd(Element.CHI);
								menu.elements.add(element);
								switchMenu(player, menu);
							}
						} else { //If it's an element to an element, YAY! No complicated shit!
							if (bPlayer.getElements().size() == 1) {
								elementToChangeTo = element;
								Element oldElement = bPlayer.getElements().get(0);
								bPlayer.getElements().remove(oldElement);
								bPlayer.getElements().add(element);
								
								for (SubElement sub : Element.getAllSubElements()) {
									if (sub.getParentElement() == element && bPlayer.hasSubElementPermission(sub)) {
										bPlayer.addSubElement(sub);
									}
								}
								GeneralMethods.removeUnusableAbilities(bPlayer.getName());
								
								Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeElementEvent(player, player, element, Result.ADD));
								Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeElementEvent(player, player, oldElement, Result.REMOVE));
								
								player.sendMessage(element.getColor() + "You changed your element and are now a " + element.getName() + element.getType().getBender() + "!");
								
								DatabaseUtil.setCooldown(player, System.currentTimeMillis());
								GeneralMethods.saveElements(bPlayer);
								GeneralMethods.saveSubElements(bPlayer);
								closeMenu(player);
							} else {
								//If they have more than one element, show them a GUI to select which one to remove.
								switchMenu(player, new MenuChangeFinalize(element));
								return;
							}
						}
					}
				}
				
			}
			
			/*@Override
			public void onClick(Player clickedPlayer) {
				if (!has) {
					if (elementToChange == null) {
						player.sendMessage(ChatColor.RED + "Something went wrong! Please report to Strange!");
						return;
					}
					
					bPlayer.getElements().remove(elementToChange);
					bPlayer.getElements().add(element);
					
					GeneralMethods.saveElements(bPlayer);
					
					Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeElementEvent(player, player, element, Result.ADD));
					Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeElementEvent(player, player, elementToChange, Result.REMOVE));
					
					player.sendMessage(element.getColor() + "You changed your element and are now a " + element.getName() + element.getType().getBender() + "!");
					
					DatabaseUtil.setCooldown(player, System.currentTimeMillis());
					
					closeMenu(player);
				}
			}*/
		};
		
		if (has) {
			item.addDescription(ChatColor.RED + "You already have this element!");
		} else {
			item.addDescription(ChatColor.GRAY + "Click to become a " + color + element.getName() + element.getType().getBender());
		}
		
		return item;
		
	}
	
	public static MenuItem getHelp() {
		MenuItem item = new MenuItem(ChatColor.YELLOW + "What do I do?", new MaterialData(Material.PAPER)) {
			@Override
			public void onClick(Player player) {} //Do nothing, as help is displayed in the lore
		};
		
		item.addDescription(ChatColor.GRAY + "Just select what element you want to change");
		item.addDescription(ChatColor.GRAY + "to. If you have more than one element, you");
		item.addDescription(ChatColor.GRAY + "will be prompted on which element to remove.");
		item.addDescription(ChatColor.GRAY + "Easy, right? :D");
		
		return item;
	}
	
	private void switchToChi() {
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
		DatabaseUtil.setElementCount(player, bPlayer.getElements().size());
		
		List<Element> toRemove = new ArrayList<Element>();
		for (Element e : bPlayer.getElements()) {
			if (e == Element.FIRE || e == Element.AIR || e == Element.EARTH || e == Element.WATER) {
				toRemove.add(e);
				
				Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeElementEvent(player, player, e, Result.REMOVE));
			}
		}
		bPlayer.getElements().removeAll(toRemove);
		bPlayer.getElements().add(Element.CHI);
		for (SubElement sub : Element.getAllSubElements()) {
			if (sub.getParentElement() == Element.CHI && bPlayer.hasSubElementPermission(sub)) {
				bPlayer.addSubElement(sub);
			}
		}
		GeneralMethods.removeUnusableAbilities(bPlayer.getName());
		GeneralMethods.saveElements(bPlayer);
		GeneralMethods.saveSubElements(bPlayer);
		
		player.sendMessage(Element.CHI.getColor() + "You changed your element and are now a " + Element.CHI.getName() + Element.CHI.getType().getBender() + "!");
	}

}
