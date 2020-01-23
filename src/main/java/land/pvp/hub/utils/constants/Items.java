package land.pvp.hub.utils.constants;

import land.pvp.core.utils.item.ItemBuilder;
import land.pvp.core.utils.message.CC;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class Items {
    public static final ItemStack SERVER_SELECTOR_ITEM = new ItemBuilder(Material.COMPASS).name(CC.PRIMARY + "Server Selector").build();
    public static final ItemStack[] ARMOR = new ItemStack[]{
            new ItemBuilder(Material.LEATHER_BOOTS).enchant(Enchantment.DURABILITY, 10).build(),
            new ItemBuilder(Material.LEATHER_LEGGINGS).enchant(Enchantment.DURABILITY, 10).build(),
            new ItemBuilder(Material.LEATHER_CHESTPLATE).enchant(Enchantment.DURABILITY, 10).build(),
            new ItemBuilder(Material.LEATHER_HELMET).enchant(Enchantment.DURABILITY, 10).build()
    };
}
