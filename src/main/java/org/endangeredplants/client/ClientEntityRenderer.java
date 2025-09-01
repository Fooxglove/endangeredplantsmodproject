package org.endangeredplants.client;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.entity.FallingPapeda;

@Mod.EventBusSubscriber(modid = Endangeredplants.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEntityRenderer {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // 为 FallingPapeda 注册渲染器，使用默认的 FallingBlockRenderer
            EntityRenderers.register(FallingPapeda.FALLING_PAPEDA.get(), FallingBlockRenderer::new);
            System.out.println("[DEBUG] Registered FallingPapeda renderer");
        });
    }

    // 备用注册方法 - 如果上面的不工作可以试试这个
    public static void registerEntityRenderers() {
        EntityRenderers.register(FallingPapeda.FALLING_PAPEDA.get(), FallingBlockRenderer::new);
        System.out.println("[DEBUG] Manually registered FallingPapeda renderer");
    }
}