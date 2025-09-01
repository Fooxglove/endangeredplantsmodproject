package org.endangeredplants;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.block.*;
import org.endangeredplants.client.ModClient;
import org.endangeredplants.effect.HalluEffect;
import org.endangeredplants.entity.FallingPapeda;
import org.endangeredplants.item.*;
import org.endangeredplants.tab.ChineseTab;

import org.endangeredplants.worldgen.ModConfiguredFeatures;


import org.slf4j.Logger;



// The value here should match an entry in the META-INF/mods.toml file
@Mod(Endangeredplants.MODID)
public class Endangeredplants {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "endangeredplants";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();



    public Endangeredplants() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        System.out.println("Endangeredplants mod loading...");

        // Register the Deferred Register to the mod event bus so blocks get registered
        //COAST.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        CoastalRoseBushWithoutRosehips.register(modEventBus);
        CoastalRoseBushWithRosehips.register(modEventBus);
        CoastalRoseSeeds.register(modEventBus);
        CoastalRosehip.register(modEventBus);
        CoastalRoseSucker.register(modEventBus);
        CoastalRosePetals.register(modEventBus);
        Glehnia.register(modEventBus);
        GlehniaRoot.register(modEventBus);
        GlehniaSeeds.register(modEventBus);
        WildKumquat.register(modEventBus);
        WildKumquatBush.register(modEventBus);
        WildKumquatBushWithFruits.register(modEventBus);
        WildKumquatBushWithFlowers.register(modEventBus);
        CoastalRosebushFloweredBlock.register(modEventBus);
        CoastalRosebushBlock.register(modEventBus);
        CoastalRoseSeedling.register(modEventBus);
        GlehniaBlock.register(modEventBus);
        BaishanzuFirSapling.register(modEventBus);
        BaishanzuFirLog.register(modEventBus);
        BaishanzuFirLeaves.register(modEventBus);
        BaishanzuFirSaplingBlock.register(modEventBus);
        CyatheaGametophyte.register(modEventBus);
        CyatheaSporeBottle.register(modEventBus);
        CyatheaGametophyteBlock.register(modEventBus);
        CyatheaTop.register(modEventBus);
        CyatheaLog.register(modEventBus);
        WildKumquatFloweredBlock.register(modEventBus);
        WildKumquatFruitedBlock.register(modEventBus);
        WildKumquatBlock.register(modEventBus);
        SandFritillariaBulb.register(modEventBus);
        SandFritillariaBlock.register(modEventBus);
        SandFritillariaBuds.register(modEventBus);
        Dendrobium.register(modEventBus);
        DendrobiumFlower.register(modEventBus);
        DendrobiumBlock.register(modEventBus);
        DendrobiumPolysaccha.register(modEventBus);
        ZangnanCupressusLeaves.register(modEventBus);
        ZangnanCupressusLog.register(modEventBus);
        ZangnanCupressusSapling.register(modEventBus);
        ZangnanCupressusCone.register(modEventBus);
        ZangnanCupressusSaplingBlock.register(modEventBus);
        Nypa.register(modEventBus);
        NypaFruit.register(modEventBus);
        NypaFruitBlock.register(modEventBus);
        Soya.register(modEventBus);
        SoyaZher.register(modEventBus);
        SoyaMilk.register(modEventBus);
        SoyaBlock.register(modEventBus);
        WildSoya.register(modEventBus);
        WildSoyaBlock.register(modEventBus);
        Kingdonia.register(modEventBus);
        FalseLitchi.register(modEventBus);
        FalseLitchiStone.register(modEventBus);
        FalseLitchiLog.register(modEventBus);
        FalseLitchiLeaves.register(modEventBus);
        FalseLitchiSeedling.register(modEventBus);
        FalseLitchiSeedlingBlock.register(modEventBus);
        HalluEffect.register(modEventBus);
        YewLeaves.register(modEventBus);
        YewLog.register(modEventBus);
        YewSeed.register(modEventBus);
        YewSaplingBlock.register(modEventBus);
        YewSapling.register(modEventBus);
        YewCone.register(modEventBus);
        YewBark.register(modEventBus);
        Paclitaxel.register(modEventBus);
        HonghePapedaBlock.register(modEventBus);
        HonghePapeda.register(modEventBus);
        HonghePapedaSapling.register(modEventBus);
        HonghePapedaSaplingBlock.register(modEventBus);
        HonghePapedaLeaves.register(modEventBus);
        HonghePapedaLog.register(modEventBus);
        HonghePapedaLeaf.register(modEventBus);
        FallingPapeda.register(modEventBus);
        GinkgoLeaves.register(modEventBus);
        GinkgoLog.register(modEventBus);
        GinkgoSeed.register(modEventBus);
        GinkgoLeaf.register(modEventBus);
        GinkgoCone.register(modEventBus);
        GinkgoSapling.register(modEventBus);
        GinkgoSaplingBlock.register(modEventBus);
        GinkgoPollenBottle.register(modEventBus);
        RoastedGinkgoSeed.register(modEventBus);
        SpecimenBag.register(modEventBus);
        SpecimenPresser.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        ChineseTab.register(modEventBus);




        if (FMLEnvironment.dist == Dist.CLIENT) {
            ModClient.register(modEventBus);
        }

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.addListener(this::clientSetup);
        });

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        //modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }



    private static RegistrySetBuilder createRegistrySet() {
        return new RegistrySetBuilder()
                .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap);
    }






    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));


    }


    // Add the example block item to the building blocks tab
    //private void addCreative(BuildCreativeModeTabContentsEvent event) {
        //if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(EXAMPLE_BLOCK_ITEM);
    //}


    // 在主 Mod 类或 ModSetup 类中
    private void clientSetup(final FMLClientSetupEvent event) {
        // 客户端渲染器注册
        org.endangeredplants.client.ClientEntityRenderer.registerEntityRenderers();
        System.out.println("[DEBUG] Client setup completed");
    }



    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

        }




        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            ItemBlockRenderTypes.setRenderLayer(CoastalRosebushFloweredBlock.COASTAL_ROSE_BUSH_FLOWERED_BLOCK.get(),
                    RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(CoastalRoseSeedling.COASTAL_ROSE_SEEDLING.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(CoastalRosebushBlock.COASTAL_ROSE_BUSH_BLOCK.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(GlehniaBlock.GLEHNIA_BLOCK.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(BaishanzuFirLeaves.BAISHANZU_FIR_LEAVES.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(BaishanzuFirSaplingBlock.BAISHANZU_FIR_SAPLING_BLOCK.get(),RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(CyatheaGametophyteBlock.CYATHEA_GAMETOPHYTE_BLOCK.get(),RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(CyatheaTop.CYATHEA_TOP.get(),RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(CyatheaLog.CYATHEA_LOG.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(WildKumquatBlock.WILD_KUMQUAT_BLOCK.get(),RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(WildKumquatFruitedBlock.WILD_KUMQUAT_FRUITED_BLOCK.get(),RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(WildKumquatFloweredBlock.WILD_KUMQUAT_FLOWERED_BLOCK.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(SandFritillariaBlock.SAND_FRITILLARIA_BLOCK.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(DendrobiumBlock.DENDROBIUM_BLOCK.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ZangnanCupressusSaplingBlock.ZANGNAN_CUPRESSUS_SAPLING_BLOCK.get(),RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(Nypa.NYPA.get(),RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(NypaFruitBlock.NYPA_FRUIT_BLOCK.get(),RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(WildSoyaBlock.WILD_SOYA_BLOCK.get(),RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(SoyaBlock.SOYA_BLOCK.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(Kingdonia.KINGDONIA_BLOCK.get(),RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(FalseLitchiSeedlingBlock.FALSE_LITCHI_SEEDLING_BLOCK.get(),RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(YewSaplingBlock.YEW_SAPLING_BLOCK.get(),RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(HonghePapedaBlock.HONGHE_PAPEDA_BLOCK.get(),RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(HonghePapedaSaplingBlock.HONGHE_PAPEDA_SAPLING_BLOCK.get(),RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(GinkgoSaplingBlock.GINKGO_SAPLING_BLOCK.get(),RenderType.cutout());
        }
    }
}
