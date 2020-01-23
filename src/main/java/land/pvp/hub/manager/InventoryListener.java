package land.pvp.hub.manager;

import land.pvp.core.inventory.menu.Menu;
import land.pvp.core.inventory.menu.action.Action;
import land.pvp.hub.HubPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

@RequiredArgsConstructor
public class InventoryListener implements Listener {
    private final HubPlugin plugin;

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (player.getGameMode() != GameMode.SURVIVAL || event.getClickedInventory() == null
                || event.getClickedInventory() == player.getInventory() || event.getCurrentItem() == null
                || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        Menu menu = plugin.getMenuManager().getMatchingMenu(event.getClickedInventory());

        if (menu != null) {
            Action action = menu.getAction(event.getSlot());

            if (action != null) {
                event.setCancelled(true);
                action.onClick(player);
            }
        }
    }
}
