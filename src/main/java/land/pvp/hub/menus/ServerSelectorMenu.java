package land.pvp.hub.menus;

import land.pvp.core.inventory.menu.Menu;
import land.pvp.core.utils.item.ItemBuilder;
import land.pvp.core.utils.message.CC;
import land.pvp.hub.HubPlugin;
import org.bukkit.Material;

public class ServerSelectorMenu extends Menu {
    private final HubPlugin plugin;

    public ServerSelectorMenu(HubPlugin plugin) {
        super(1, "Server Selector");
        this.plugin = plugin;
    }

    @Override
    public void setup() {
    }

    @Override
    public void update() {
//        setActionableItem(2, new ItemBuilder(Material.EXP_BOTTLE)
//                        .name(CC.PRIMARY + "???")
//                        .lore(CC.GREEN + "Coming soon!")
//                        .build(),
//                player -> {
//                    player.sendMessage(CC.YELLOW + "This secret gamemode is coming soon! Follow @PvP_Land on Twitter for updates.");
//                    player.closeInventory();
//                }
//        );
        setActionableItem(4, new ItemBuilder(Material.POTION)
                        .name(CC.PRIMARY + "Practice (NA)")
                        .lore(CC.GREEN + "Practice your PvP skills with a variety ",
                                CC.GREEN + "of kits in duels, events, and more!",
                                CC.PRIMARY + "Online: " + CC.SECONDARY + plugin.getServerPlayerCount("practice-na"))
                        .durability(16421)
                        .build(),
                player -> {
                    plugin.getBungeeChannelApi().connect(player, "practice-na");
                    player.closeInventory();
                }
        );
//        setActionableItem(6, new ItemBuilder(Material.POTION)
//                        .name(CC.PRIMARY + "Practice (EU)")
//                        .lore(
//                                CC.GREEN + "Connect to 'eu.pvp.land' to join this server!",
//                                CC.GREEN + "Online: " + CC.SECONDARY + plugin.getEuPrac()
//                        )
//                        .durability(16421)
//                        .build(),
//                player -> {
//                    player.sendMessage(CC.PRIMARY + "Connect to " + CC.SECONDARY + "eu.pvp.land" + CC.PRIMARY + " to join Practice (EU)!");
//                    player.closeInventory();
//                }
//        );
    }
}
