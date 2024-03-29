package com.strangeone101.easygui;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public abstract class MenuItem{

	private List<String> lore = new ArrayList<String>();
	private MenuBase menu;
	private int number;
	private MaterialData icon;
	private String text;
	private boolean shiftClicked = false;
	private boolean enchanted = false;
	private boolean rightClicked = false;
	private ItemStack cursor = null;
	
	public MenuItem(String text, MaterialData icon, int number) 
	{
		this.text = text;
        this.icon = icon;
        this.number = number;
	}
	
	public MenuItem(String string, MaterialData icon)
	{
		this(string, icon, 1);
	}
	
	public void setEnchanted(boolean bool)
	{
		this.enchanted = bool;
	}
	
	public MenuBase getMenu() {
        return menu;
    }

    public int getNumber() {
        return number;
    }

    public MaterialData getIcon() {
        return icon;
    }

    public String getText() {
        return text;
    }
	
	@SuppressWarnings("deprecation")
	public ItemStack getItemStack() 
	{
		ItemStack slot = new ItemStack(getIcon().getItemType(), getNumber(), getIcon().getData());
        ItemMeta meta = slot.getItemMeta();
        meta.setLore(lore);
        meta.setDisplayName(getText());
        slot.setItemMeta(meta);
        if (enchanted) {
        	slot.addUnsafeEnchantment(Enchantment.LUCK, 1);
        	meta = slot.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS );
            slot.setItemMeta(meta);
        }
        
        return slot;
	}

	/**
	 * Called when a player clicks on the item.
	 * @param player The player clicking
	 * */
	public abstract void onClick(Player player);
	
	public void setDescriptions(List<String> lines) {
		this.lore = lines;
	}
	
	public void addDescription(String line) {
		this.lore.add(line);
	}
	
	public void setMenu(MenuBase menu) {
		this.menu = menu;
	}
	
	protected void setShiftClick(boolean bool) {
		this.shiftClicked = bool;
	}
	
	protected void setCursor(ItemStack cursor) {
		this.cursor = cursor;
	}
	
	protected void setRightClicked(boolean rightClicked) {
		this.rightClicked = rightClicked;
	}
	
	public void setGlow(boolean bool) {
		this.enchanted = bool;
	}
	
	public boolean isShiftClicked() {
		return this.shiftClicked;
	}
	
	public boolean isRightClicked() {
		return rightClicked;
	}
	
	public boolean hasItemAtCursor() {
		return cursor != null;
	}
	
	public ItemStack getItemAtCursor() {
		return cursor;
	}

}

