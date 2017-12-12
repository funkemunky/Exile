package anticheat.gui;

import java.awt.Desktop;
import java.net.URI;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksType;
import anticheat.utils.Color;

public class GUIListener implements Listener {

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		GUI gui = Exile.getAC().getGUIManager();
		Player player = (Player) event.getWhoClicked();
		ItemStack clickedItem = event.getCurrentItem();

		if (event.getInventory().getName().equals(Color.Gold + Color.Bold + "Exile AntiCheat")) {
			if (event.getCurrentItem().getItemMeta().getDisplayName().equals(Color.Gold + "Reload Exile")) {
				ItemStack item = event.getCurrentItem();
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(Color.Gray + Color.Italics + "Working...");
				item.setItemMeta(meta);
				Exile.getAC().reloadConfig();
				meta.setDisplayName(Color.Green + "Reloaded!");
				item.setItemMeta(meta);
				new BukkitRunnable() {
					public void run() {
						meta.setDisplayName(Color.Gold + "Reload Exile");
						item.setItemMeta(meta);
					}
				}.runTaskLaterAsynchronously(Exile.getAC(), 30L);
			}
			if (gui.hasSameName(clickedItem, Color.Gold + "Reset Violations")) {
				ItemStack item = event.getCurrentItem();
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(Color.Gray + Color.Italics + "Working...");
				item.setItemMeta(meta);
				Exile.getAC().clearVLS();
				meta.setDisplayName(Color.Green + "Successfully Reset Violations!");
				item.setItemMeta(meta);
				new BukkitRunnable() {
					public void run() {
						meta.setDisplayName(Color.Gold + "Reset Violations");
						item.setItemMeta(meta);
					}
				}.runTaskLaterAsynchronously(Exile.getAC(), 30L);
			}
			if (gui.hasSameName(clickedItem, Color.Gold + "Toggle Checks")) {
				gui.openChecksToggleGUI(player);
			}
			if (gui.hasSameName(clickedItem, Color.Gold + "Toggle Bannable Checks")) {
				gui.openChecksBannableGUI(player);
			}
			if (gui.hasSameName(clickedItem, Color.Red + "Exile Info")
					&& event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				openDiscord();
			}
			event.setCancelled(true);
		}
		if(event.getInventory().equals(gui.checksBannableGUI)) {
			if(gui.hasSameName(clickedItem, Color.Red + "Combat")) {
				gui.openBannableChecks(player, ChecksType.COMBAT);
			}
			if(gui.hasSameName(clickedItem, Color.Red + "Movement")) {
				gui.openBannableChecks(player, ChecksType.MOVEMENT);
			}
			if(gui.hasSameName(clickedItem, Color.Red + "Miscellaneous")) {
				gui.openBannableChecks(player, ChecksType.OTHER);
			}
			event.setCancelled(true);
		}
		if(event.getInventory().equals(gui.checksToggleGUI)) {
			if(gui.hasSameName(clickedItem,  Color.Red + "Combat")) {
				gui.openBannableChecks(player, ChecksType.COMBAT);
			}
			if(gui.hasSameName(clickedItem, Color.Red + "Movement")) {
				gui.openBannableChecks(player, ChecksType.MOVEMENT);
			}
			if(gui.hasSameName(clickedItem, Color.Red  + "Miscellaneous")) {
				gui.openBannableChecks(player, ChecksType.OTHER);
			}
			event.setCancelled(true);
		}
		if(event.getInventory().getName().contains(Color.Dark_Gray + "Toggle Checks for:")) {
			Checks check = Exile.getAC().getChecks().getCheckByName(Color.strip(clickedItem.getItemMeta().getDisplayName()));
			if(check != null) {
				check.setState(check.getState() ? false : true);
			    event.setCurrentItem(gui.createItem(check.getState() ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK, event.getCurrentItem().getAmount(), check.getState() ? Color.Green + check.getName() : Color.Red + check.getName()));
			}
			event.setCancelled(true);
		}
		if(event.getInventory().getName().contains(Color.Dark_Gray + "Toggle Bans for:")) {
			Checks check = Exile.getAC().getChecks().getCheckByName(Color.strip(clickedItem.getItemMeta().getDisplayName()));
			if(check != null) {
				check.setBannable(check.isBannable() ? false : true);
			    event.setCurrentItem(gui.createItem(check.isBannable() ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK, event.getCurrentItem().getAmount(), check.isBannable() ? Color.Green + check.getName() : Color.Red + check.getName()));
			}
			event.setCancelled(true);
		}
	}

	public void openDiscord() {
		try {
			Desktop d = Desktop.getDesktop();
			d.browse(new URI("[url]https://discord.gg/x6DZf3c[/url]"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
