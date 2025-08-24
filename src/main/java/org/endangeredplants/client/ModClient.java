package org.endangeredplants.client; // 请替换为你的实际包名

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.endangeredplants.effect.HalluEffect;


@OnlyIn(Dist.CLIENT)
public class ModClient {

    public static void register(IEventBus modEventBus) {
        // 注册客户端相关的内容
        modEventBus.addListener(ModClient::onRegisterClientReloadListeners);

        // 注册事件监听器
        MinecraftForge.EVENT_BUS.register(ModClient.class);
    }

    private static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        // 这里可以注册客户端资源重载监听器（如果需要）
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.hasEffect(HalluEffect.HALLU.get())) {
            // 应用扭曲效果
            float intensity = 0.5f; // 扭曲强度
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, new ResourceLocation("minecraft", "textures/misc/nausea.png"));
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, intensity);
            // 这里需要添加实际的渲染代码
        }
    }
}