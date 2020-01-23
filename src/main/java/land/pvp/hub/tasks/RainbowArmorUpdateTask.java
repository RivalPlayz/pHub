package land.pvp.hub.tasks;

import land.pvp.core.CorePlugin;
import land.pvp.core.player.CoreProfile;
import land.pvp.hub.HubPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

@RequiredArgsConstructor
public class RainbowArmorUpdateTask implements Runnable {
    private final HubPlugin plugin;
    private double currentValue;

    private static double getValue(double hue, double speed) {
        return (hue + speed) % 1.0;
    }

    private static Color getColor(java.awt.Color rgb) {
        return Color.fromBGR(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
    }

    private static java.awt.Color getRGB(double hue) {
        return java.awt.Color.getHSBColor((float) hue, 1.0f, 1.0f);
    }

    private static void setArmor(Player player, double hue, double gradientSpeed) {
        java.awt.Color rgb = getRGB(hue);

        Color helmetColor = getColor(rgb);
        hue = getValue(hue, gradientSpeed);
        rgb = getRGB(hue);

        Color chestplateColor = getColor(rgb);
        hue = getValue(hue, gradientSpeed);
        rgb = getRGB(hue);

        Color leggingsColor = getColor(rgb);
        hue = getValue(hue, gradientSpeed);
        rgb = getRGB(hue);

        Color bootsColor = getColor(rgb);

        PlayerInventory playerInventory = player.getInventory();

        ItemStack helmet = playerInventory.getHelmet();
        ItemStack chestplate = playerInventory.getChestplate();
        ItemStack leggings = playerInventory.getLeggings();
        ItemStack boots = playerInventory.getBoots();

        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
        LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();

        helmetMeta.setColor(helmetColor);
        helmet.setItemMeta(helmetMeta);

        chestplateMeta.setColor(chestplateColor);
        chestplate.setItemMeta(chestplateMeta);

        leggingsMeta.setColor(leggingsColor);
        leggings.setItemMeta(leggingsMeta);

        bootsMeta.setColor(bootsColor);
        boots.setItemMeta(bootsMeta);

        playerInventory.setHelmet(helmet);
        playerInventory.setChestplate(chestplate);
        playerInventory.setLeggings(leggings);
        playerInventory.setBoots(boots);
    }

    @Override
    public void run() {
        double speed = 0.0020000000949949026 * 4;
        double gradientSpeed = 0.05000000074505806;

        currentValue = getValue(currentValue, speed);

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

            if (!profile.hasDonor()) {
                continue;
            }

            PlayerInventory playerInventory = player.getInventory();

            if (playerInventory.getBoots().getType() == Material.LEATHER_BOOTS
                    && playerInventory.getLeggings().getType() == Material.LEATHER_LEGGINGS
                    && playerInventory.getChestplate().getType() == Material.LEATHER_CHESTPLATE
                    && playerInventory.getHelmet().getType() == Material.LEATHER_HELMET) {
                setArmor(player, currentValue, gradientSpeed);
            }
        }
    }
}
