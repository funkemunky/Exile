package anticheat.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksType;
import anticheat.utils.Color;

public class GUI {

	public Inventory mainGUI;
	public Inventory checksBannableGUI;
	public Inventory checksToggleGUI;
	public Inventory checksSetToggleGUI;
	public Inventory checksSetBannableGUI;
	String color;

	public GUI() {

		mainGUI = Bukkit.createInventory(null, 27, Color.Gold + Color.Bold + "Exile AntiCheat");
		checksBannableGUI = Bukkit.createInventory(null, 9, Color.Dark_Gray + "Choose a type to set bannable.");
		checksToggleGUI = Bukkit.createInventory(null, 9, Color.Dark_Gray + "Choose a type to toggle.");
		

		loadMainGUIItems();
	}

	private void loadMainGUIItems() {
		mainGUI.setItem(9, createItem(Material.BOOK, 1, "&6Toggle Bannable Checks", new String[] {"", "&fLeft Click &7to open &fGUI Editor", "&7for &fToggling Bans&7."}));
		mainGUI.setItem(11, createItem(Material.BOOK, 1, "&6Toggle Checks", new String[] {"", "&fLeft Click &7to open &fGUI Editor", "&7for &fEnabling/Disabling Checks&7."}));
		mainGUI.setItem(13, createItem(Material.ENCHANTED_BOOK, 1, "&cExile Info", new String[] {"", "", "&7Currently using &fExile v" + Exile.getAC().getDescription().getVersion(), "&7by &ffunkemunky&7.", "&fExile AntiCheat &7is a product of &aCode 66, LLC&7.", "", "&7Any questions or concerns?", "&fShift Left Click &7to receive &fInvite to the Code66 Discord."}));
		mainGUI.setItem(15, createItem(Material.BOOK, 1, "&6Reload Exile", new String[] {"", "&fLeft Click &7to &fReload Exile Configs&7."}));
		mainGUI.setItem(17, createItem(Material.BOOK, 1, "&6Reset Violations", new String[] {"", "&fLeft Click &7to", "&fReset all player Violations."}));
	}

	private void loadMainGUIGlass() {
		if (!Exile.getAC().getConfig().getBoolean("GUI.RGB")) {
			for(int i = 0 ; i < 27 ; i++) {
				if(mainGUI.getItem(i) == null || mainGUI.getItem(i).getType().equals(Material.STAINED_GLASS_PANE)) {
					mainGUI.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7));
				}
			}
		}
	}

	private void runRBG() {
		new BukkitRunnable() {
			public void run() {
				if (Exile.getAC().getConfig().getBoolean("GUI.RGB")) {
					for (int i = 0; i < 27; i++) {
						if(mainGUI.getItem(i) == null || mainGUI.getItem(i).getType().equals(Material.STAINED_GLASS_PANE)) {
							switch (color) {
							case "RED":
								mainGUI.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14));
								color = "ORANGE";
								break;
							case "ORANGE":
								mainGUI.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 1));
								color = "YELLOW";
								break;
							case "YELLOW":
								mainGUI.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 4));
								color = "LIME";
								break;
							case "LIME":
								mainGUI.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5));
								color = "GREEN";
								break;
							case "GREEN":
								mainGUI.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14));
								color = "CYAN";
								break;
							case "CYAN":
								mainGUI.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 9));
								color = "BLUE";
								break;
							case "BLUE":
								mainGUI.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 11));
								color = "INDIGO";
								break;
							case "INDIGO":
								mainGUI.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 10));
								color = "PURPLE";
								break;
							case "PURPLE":
								mainGUI.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14));
								color = "RED";
								break;
							default:
								color = "RED";
								break;
							}
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(Exile.getAC(), 0L, 10L);
	}
	
	public void openMainGUI(Player player) {
		player.openInventory(mainGUI);
	}
	
	public void openChecksBannableGUI(Player player) {
		checksBannableGUI.setItem(2, createItem(Material.REDSTONE_BLOCK, 1, "&cCombat"));
		checksBannableGUI.setItem(4, createItem(Material.REDSTONE_BLOCK, 1, "&cMovement"));
		checksBannableGUI.setItem(6, createItem(Material.REDSTONE_BLOCK, 1, "&cMiscellaneous"));
		
		player.openInventory(checksBannableGUI);
	}
	
	public void openBannableChecks(Player player, ChecksType type) {
		checksSetBannableGUI = Bukkit.createInventory(null, 45, Color.Dark_Gray + "Toggle Bans for: " + Color.Gold + type.getName());
		int i = 0;
		for(Checks check : Exile.getAC().getChecks().getDetections()) {
			if(check.getType() == type) {
				checksSetBannableGUI.setItem(i, createItem(check.isBannable() ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK, i + 1, check.isBannable() ? Color.Green + check.getName() : Color.Red + check.getName()));
				i++;
			}
		}
		player.openInventory(checksSetBannableGUI);
	}
	
	public void openChecksToggleGUI(Player player) {
		checksToggleGUI.setItem(2, createItem(Material.REDSTONE_BLOCK, 1, "&cCombat"));
		checksToggleGUI.setItem(4, createItem(Material.REDSTONE_BLOCK, 1, "&cMovement"));
		checksToggleGUI.setItem(6, createItem(Material.REDSTONE_BLOCK, 1, "&cMiscellaneous"));
		
		player.openInventory(checksToggleGUI);
	}
	
	public void openToggleChecks(Player player, ChecksType type) {
		checksSetToggleGUI = Bukkit.createInventory(null, 45, Color.Dark_Gray + "Toggle Checks for: " + Color.Gold + type.getName());
		int i = 0;
		for(Checks check : Exile.getAC().getChecks().getDetections()) {
			if(check.getType() == type) {
				checksSetToggleGUI.setItem(i, createItem(check.getState() ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK, i + 1, check.getState() ? Color.Green + check.getName() : Color.Red + check.getName()));
				i++;
			}
		}
		player.openInventory(checksSetToggleGUI);
	}

	public ItemStack createItem(Material material, int amount, String name, String... lore) {
		ItemStack thing = new ItemStack(material, amount);
		ItemMeta thingm = thing.getItemMeta();
		thingm.setDisplayName(Color.translate(name));
		List<String> loreList = new ArrayList<String>();
		for(String string : lore) {
			loreList.add(Color.translate(string));
		}
		thingm.setLore(loreList);
		thing.setItemMeta(thingm);
		return thing;
	}
	
	public boolean hasSameName(ItemStack item, String name) {
		if(item.getItemMeta().getDisplayName().equals(name)) {
			return true;
		}
		return false;
	}

}
