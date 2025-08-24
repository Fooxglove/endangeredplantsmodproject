package org.endangeredplants.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.block.SpecimenPresser;
import org.endangeredplants.client.gui.SpecimenPresserScreen;

@Mod.EventBusSubscriber(modid = Endangeredplants.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // 注册Screen
            MenuScreens.register(SpecimenPresser.SPECIMEN_PRESSER_MENU.get(), SpecimenPresserScreen::new);
        });
    }
}