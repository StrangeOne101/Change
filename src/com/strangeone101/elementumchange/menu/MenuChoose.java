package com.strangeone101.elementumchange.menu;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.Element.SubElement;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent.Result;
import com.strangeone101.easygui.MenuBase;
import com.strangeone101.easygui.MenuItem;
import com.strangeone101.elementumchange.util.RunnablePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuChoose extends MenuBase
{
	public OfflinePlayer thePlayer;
	public MenuBase menu_ = null;
	public Player openPlayer;
	
	public MenuChoose(OfflinePlayer player) 
	{
		super("Please select an element!", 3);
		this.thePlayer = player;
	}
	
	public MenuChoose(OfflinePlayer player, MenuBase previousMenu)
	{
		this(player);
		this.menu_ = previousMenu;
	}
	
	public void update()
	{
		this.addMenuItem(this.getChooseElement(Element.FIRE), 2 + 9);
		this.addMenuItem(this.getChooseElement(Element.WATER), 3 + 9);
		this.addMenuItem(this.getChooseElement(Element.CHI), 4 + 9);
		this.addMenuItem(this.getChooseElement(Element.EARTH), 5 + 9);
		this.addMenuItem(this.getChooseElement(Element.AIR), 6 + 9);
		
		if (menu_ != null)
		{
			MenuItem item = new MenuItem(ChatColor.YELLOW + "Return to Menu", new MaterialData(Material.ARROW)) {
				@Override
				public void onClick(Player player) 
				{
					switchMenu(player, menu_);
				}
			};
			this.addMenuItem(item, 18);
		}
	}
	
	/*private class ChooseElementItem extends MenuItem
	{
		protected Element type;
		
		public ChooseElementItem(String itemName, MaterialData item, Element bending)
		{
			super(itemName, item);
			this.type = bending;
		}
		
		@Override
		public void onClick(Player player) 
		{
			
			MenuConfirm confirm = new MenuConfirm();
			
			getMenu().switchMenu(player, new MenuElementConfirm(type));
			//this.getItemStack().addEnchantment(Enchantment.SILK_TOUCH, 0);
			//this.addDescription(ChatColor.GREEN + "" + ChatColor.BOLD + "SELECTED!");
		}
	};*/
	
	@SuppressWarnings("deprecation")
	public MenuItem getChooseElement(final Element element)
	{
		MaterialData mat = new MaterialData(Material.STAINED_GLASS, (byte) 15);
		ChatColor color = ChatColor.RED;
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(thePlayer);
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
		
		final ChatColor finalColor = color;
		
		final MenuChoose instance = this;
		MenuConfirm confirm = new MenuConfirm((MenuBase)this, new RunnablePlayer() {
			@Override
			public void run(Player player) 
			{
				if (thePlayer instanceof Player && !(((Player)thePlayer).hasPermission("bending." + element.getName().toLowerCase())) && thePlayer.getName().equals(openPlayer.getName()))
				{
					player.sendMessage(ChatColor.RED + "You don't have permission to choose this element!");
					player.closeInventory();
					return;
				}
				BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(thePlayer);
				bPlayer.setElement(element);
				for (SubElement sub : Element.getAllSubElements()) {
					if (sub.getParentElement() == element && bPlayer.hasSubElementPermission(sub)) {
						bPlayer.addSubElement(sub);
					}
				}
				GeneralMethods.removeUnusableAbilities(bPlayer.getName());
				if (thePlayer instanceof Player) ((Player)thePlayer).sendMessage(finalColor + "You are now " + element.getName() + element.getType().getBender() + "!");
				GeneralMethods.saveElements(BendingPlayer.getBendingPlayer(thePlayer));
				if (thePlayer instanceof Player)
				{
					Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeElementEvent((Player)thePlayer, (Player)thePlayer, element, Result.CHOOSE));
				}
				player.closeInventory();
			}
		}, new RunnablePlayer() {
			
			@Override
			public void run(Player player) {
				switchMenu(player, instance);
				
			}
		}, Arrays.asList(new String[] {ChatColor.GRAY + "Are you sure you want to choose " + color + element.getName() + ChatColor.RESET + ChatColor.GRAY + "?", ChatColor.GRAY + "You won't be able to change for 2 days!"})
		, Arrays.asList(new String[] {ChatColor.GRAY + "Cancel and pick another element"}));
		MenuItem item = new MenuItem(color + "Choose " + ChatColor.BOLD + element.getName(), mat) {

			@Override
			public void onClick(Player player) 
			{
				if (BendingPlayer.getBendingPlayer(thePlayer) != null && !BendingPlayer.getBendingPlayer(thePlayer).getElements().isEmpty() && !openPlayer.hasPermission("bending.command.rechoose")) {
					{
						openPlayer.sendMessage(ChatColor.RED + "You don't have permission to change your element!");
						closeMenu(openPlayer);
					}
				}
				
				confirm.openMenu(openPlayer);
			}
		};
		item.setDescriptions(Arrays.asList(new String[] {ChatColor.GRAY + "Become a " + element.getName() + element.getType().getBender()}));
		
		return item;
	}
	
	protected List<String> getDesc(String line)
	{
		int maxLenght = 45;
		Pattern p = Pattern.compile("\\G\\s*(.{1,"+maxLenght+"})(?=\\s|$)", Pattern.DOTALL);
		Matcher m = p.matcher(line);
		List<String> l = new ArrayList<String>();
		while (m.find())
		{
			l.add(ChatColor.GRAY + m.group(1));
		}
		return l;
	}

	public void openMenu(Player player) 
	{
		openPlayer = player; 
		
		if (BendingPlayer.getBendingPlayer(thePlayer).isPermaRemoved())
		{
			if (openPlayer.getName().equals(thePlayer.getName()))
			{
				player.sendMessage(ChatColor.RED + "You cannot choose an element because your bending has been permanently removed!");
			}
			else
			{
				player.sendMessage(ChatColor.RED + "This player has had their bending permanently removed!");
			}
			closeMenu(player);
			return;
		}
		
		this.update();
		super.openMenu(player);
	}
}
