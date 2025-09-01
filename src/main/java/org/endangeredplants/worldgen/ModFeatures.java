package org.endangeredplants.worldgen;

import net.minecraftforge.eventbus.api.IEventBus;

public class ModFeatures {
    // 移除 DeferredRegister，因为 ConfiguredFeature 通过数据生成处理
    // 这个类现在只是一个占位符，实际的特征在 ModConfiguredFeatures 中定义

    public static void register(IEventBus eventBus) {
        // 不需要注册任何东西，ConfiguredFeature 通过数据生成系统处理
    }
}