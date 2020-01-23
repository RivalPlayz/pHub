package land.pvp.hub.manager;

import java.util.HashMap;
import java.util.Map;
import land.pvp.core.inventory.menu.Menu;
import land.pvp.hub.HubPlugin;
import land.pvp.hub.menus.ServerSelectorMenu;
import org.bukkit.inventory.Inventory;

public class MenuManager {
    private final Map<Class<? extends Menu>, Menu> menus = new HashMap<>();

    public MenuManager(HubPlugin plugin) {
        registerMenus(
                new ServerSelectorMenu(plugin)
        );
    }

    public Menu getMenu(Class<? extends Menu> clazz) {
        return menus.get(clazz);
    }

    public Menu getMatchingMenu(Inventory other) {
        for (Menu menu : menus.values()) {
            if (menu.getInventory().equals(other)) {
                return menu;
            }
        }

        return null;
    }

    public void registerMenus(Menu... menus) {
        for (Menu menu : menus) {
            menu.setup();
            menu.update();
            this.menus.put(menu.getClass(), menu);
        }
    }
}
