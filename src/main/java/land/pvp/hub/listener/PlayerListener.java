package land.pvp.hub.listener;

import land.pvp.core.CorePlugin;
import land.pvp.core.player.CoreProfile;
import land.pvp.core.player.rank.Rank;
import land.pvp.core.utils.message.CC;
import land.pvp.hub.HubPlugin;
import land.pvp.hub.menus.ServerSelectorMenu;
import land.pvp.hub.utils.constants.Items;
import land.pvp.hub.utils.constants.Locations;
import land.pvp.scoreboardapi.PlayerScoreboardUpdateEvent;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    private final HubPlugin plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getPlayerDataManager().createPlayerData(player.getUniqueId());

        player.teleport(Locations.SPAWN);
        player.setWalkSpeed(0.4F);

        PlayerInventory playerInventory = player.getInventory();

        playerInventory.clear();
        playerInventory.setArmorContents(null);
        playerInventory.setItem(4, Items.SERVER_SELECTOR_ITEM);
        playerInventory.setHeldItemSlot(4);

        player.sendMessage(CC.SEPARATOR);
        player.sendMessage(CC.GREEN + "Welcome to PvP Land!");
        player.sendMessage(CC.PRIMARY + "Pick a server with the server selector.");
        player.sendMessage("");
        player.sendMessage(CC.PRIMARY + "Discord: " + CC.SECONDARY + "discord.pvp.land");
        player.sendMessage(CC.PRIMARY + "Twitter: " + CC.SECONDARY + "twitter.com/pvp_land");
        player.sendMessage(CC.PRIMARY + "Store: " + CC.SECONDARY + "store.pvp.land");
        player.sendMessage(CC.SEPARATOR);

        CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

        if (profile.hasDonor()) {
            playerInventory.setArmorContents(Items.ARMOR);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        plugin.getPlayerDataManager().removePlayerData(player.getUniqueId());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (player.isOp() && player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryMove(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (player.isOp() && player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (event.getClickedInventory() == player.getInventory()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        Action action = event.getAction();

        switch (action) {
            case PHYSICAL:
                event.setCancelled(true);
                break;
            case RIGHT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
                if (!event.hasItem()) {
                    return;
                }

                event.setCancelled(true);

                Material type = event.getItem().getType();

                switch (type) {
                    case COMPASS:
                        plugin.getMenuManager().getMenu(ServerSelectorMenu.class).open(player);
                        break;
                }
                break;
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

        if (!profile.hasDonor()) {
            event.setCancelled(true);
            player.sendMessage(CC.RED + "Only donors can chat in the hub!");
            player.sendMessage(CC.PRIMARY + "Become a donor by buying a rank at " + CC.SECONDARY + "https://store.pvp.land" + CC.PRIMARY + ".");
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getFoodLevel() < 20) {
            event.setFoodLevel(20);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onScoreboardUpdate(PlayerScoreboardUpdateEvent event) {
        event.setTitle(CC.PRIMARY + CC.B + "PvP Land");
        event.setSeparator(CC.BOARD_SEPARATOR);

        Player player = event.getPlayer();
        CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
        Rank rank = profile.getRank();

        event.writeLine(CC.PRIMARY + "Rank: " + rank.getColor() + rank.getName());
        event.writeLine(CC.PRIMARY + "Online: " + CC.SECONDARY + plugin.getOnlineCount() + "/1,000");
    }

    @EventHandler
    public void onPreDoubleJump(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (player.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        Vector jumpVelocity = player.getLocation().getDirection().normalize().multiply(3).setY(2.0);

        player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 64.0F, 1.0F);
        player.setVelocity(jumpVelocity);
        player.setAllowFlight(false);

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (player.isOnline()) {
                player.setFlying(false);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPop(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getDamager();
        CoreProfile damagerProfile = CorePlugin.getInstance().getProfileManager().getProfile(damager.getUniqueId());

        if (!damagerProfile.hasDonor()) {
            return;
        }

        Player entity = (Player) event.getEntity();

        Vector popVelocityDirectional = damager.getLocation().getDirection().multiply(0.2).normalize();
        Vector popVelocityRelative = entity.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize().multiply(0.1);
        Vector popVelocity = new Vector(
                (popVelocityDirectional.getX() + popVelocityRelative.getX()) / 2.0,
                0.3 - Math.abs(entity.getVelocity().getY() / 2.0),
                (popVelocityDirectional.getZ() + popVelocityRelative.getZ()) / 2.0
        );

        ((CraftPlayer) entity).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityVelocity(
                ((CraftPlayer) entity).getHandle().getId(),
                popVelocity.getX(),
                popVelocity.getY(),
                popVelocity.getZ()
        ));
        ((CraftPlayer) damager).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityStatus(((CraftPlayer) entity).getHandle(), (byte) 2));

        damager.playSound(entity.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
        damager.sendMessage(CC.PRIMARY + "Pop!");

        entity.sendMessage(CC.PRIMARY + "You were popped by " + CC.SECONDARY + damager.getName() + CC.PRIMARY + "!");
    }
}
