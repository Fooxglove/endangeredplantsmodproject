package org.endangeredplants.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;

public class HonghePapedaLeaf {
    // 创建物品注册器
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 注册红河橙叶子物品
    public static final RegistryObject<Item> HONGHE_PAPEDA_LEAF = ITEMS.register("honghepapedaleaf",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)  // 标准堆叠大小
                    // 无特殊属性
            ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}