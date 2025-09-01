package org.endangeredplants.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;

public class DendrobiumFlower {
    // Create a Deferred Register for items
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // Register the Dendrobium Flower item with properties suitable for bee breeding
    public static final RegistryObject<Item> DENDROBIUM_FLOWER = ITEMS.register("dendrobiumflower",
            () -> new Item(new Item.Properties()
                    // Properties that make it useful for bee breeding
                    .stacksTo(64)  // Standard stack size
                    // No food properties since it's only for bee breeding
            ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}