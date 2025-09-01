package org.endangeredplants.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;

public class YewBark {
    // 创建物品注册器
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 注册红豆杉树皮物品
    public static final RegistryObject<Item> YEW_BARK = ITEMS.register("yewbark",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)  // 标准堆叠大小
                    // 无特殊属性
            ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}