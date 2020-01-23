package land.pvp.hub.listener;

import land.pvp.hub.utils.constants.Locations;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        event.setCancelled(true);

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            Player player = (Player) event.getEntity();

            player.teleport(Locations.SPAWN);
        }
    }
}
