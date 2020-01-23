package land.pvp.hub;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.leonardosnt.bungeechannelapi.BungeeChannelApi;
import java.util.HashMap;
import java.util.Map;
import land.pvp.hub.listener.EntityListener;
import land.pvp.hub.listener.PlayerListener;
import land.pvp.hub.listener.WorldListener;
import land.pvp.hub.manager.InventoryListener;
import land.pvp.hub.manager.MenuManager;
import land.pvp.hub.manager.PlayerDataManager;
import land.pvp.hub.tasks.PlayerCountUpdateTask;
import land.pvp.hub.tasks.RainbowArmorUpdateTask;
import land.pvp.scoreboardapi.api.CustomScoreboard;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitScheduler;

@Getter
public class HubPlugin extends JavaPlugin {
    private final Map<String, Integer> serverPlayerCounts = new HashMap<>();
    @Getter
    private int onlineCount;
    @Getter
    private int euPrac;

    private BungeeChannelApi bungeeChannelApi;

    private PlayerDataManager playerDataManager;
    private MenuManager menuManager;

    @Override
    public void onEnable() {
        bungeeChannelApi = BungeeChannelApi.of(this);

        playerDataManager = new PlayerDataManager();
        menuManager = new MenuManager(this);

        registerListeners(
                new PlayerListener(this),
                new InventoryListener(this),
                new EntityListener(),
                new WorldListener()
        );

        new CustomScoreboard(this, 20);

        getServer().getMessenger().registerIncomingPluginChannel(this, "RedisBungee", new PluginMessageListener() {
            @Override
            public void onPluginMessageReceived(String channel, Player player, byte[] message) {
                if (!channel.equalsIgnoreCase("RedisBungee")) return;

                ByteArrayDataInput input = ByteStreams.newDataInput(message);
                String subchannel = input.readUTF();

                switch (subchannel) {
                    case "PlayerCount":
                        String name = input.readUTF();

                        switch (name) {
                            case "ALL":
                                HubPlugin.this.onlineCount = input.readInt();
                                break;
                            case "practice-eu":
                                HubPlugin.this.euPrac = input.readInt();
                                break;
                        }
                        break;
                }
            }
        });

        getServer().getMessenger().registerOutgoingPluginChannel(this, "RedisBungee");

        BukkitScheduler scheduler = getServer().getScheduler();

        scheduler.runTaskTimer(this, new RainbowArmorUpdateTask(this), 1L, 1L);
        scheduler.runTaskTimerAsynchronously(this, new PlayerCountUpdateTask(this), 1L, 20 * 3L);
        scheduler.runTaskTimerAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                Player player = Iterables.getFirst(getServer().getOnlinePlayers(), null);

                if (player == null) {
                    return;
                }

                ByteArrayDataOutput out = ByteStreams.newDataOutput();

                out.writeUTF("PlayerCount");
                out.writeUTF("ALL");

                player.sendPluginMessage(HubPlugin.this, "RedisBungee", out.toByteArray());

                ByteArrayDataOutput out2 = ByteStreams.newDataOutput();

                out2.writeUTF("PlayerCount");
                out2.writeUTF("practice-eu");

                player.sendPluginMessage(HubPlugin.this, "RedisBungee", out2.toByteArray());
            }
        }, 60L, 60L);
    }

    public void setServerPlayerCount(String serverName, int playerCount) {
        serverPlayerCounts.put(serverName, playerCount);
    }

    public int getServerPlayerCount(String serverName) {
        return serverPlayerCounts.getOrDefault(serverName, 0);
    }

    public int getEntirePlayerCount() {
        return getServerPlayerCount("ALL");
    }
}
