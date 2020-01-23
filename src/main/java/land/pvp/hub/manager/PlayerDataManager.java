package land.pvp.hub.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import land.pvp.hub.player.HubPlayerData;

public class PlayerDataManager {
    private final Map<UUID, HubPlayerData> hubPlayerData = new HashMap<>();

    public void createPlayerData(UUID id) {
        hubPlayerData.put(id, new HubPlayerData());
    }

    public HubPlayerData getPlayerData(UUID id) {
        return hubPlayerData.get(id);
    }

    public void removePlayerData(UUID id) {
        hubPlayerData.remove(id);
    }
}
