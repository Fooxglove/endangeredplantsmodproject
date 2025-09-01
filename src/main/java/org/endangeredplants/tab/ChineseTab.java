package org.endangeredplants.tab;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.block.*;
import org.endangeredplants.item.*;

public class ChineseTab {
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "endangeredplants" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Endangeredplants.MODID);
    // Creates a creative tab with the id "endangeredplants:example_tab" for the example item, that is placed after the combat tab
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("chineseendangeredplants", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .withSearchBar()
            .title(Component.translatable("creativetab.endangeredplants.chineseendangeredplants"))
            .icon(() -> new ItemStack(GinkgoLeaf.GINKGO_LEAF.get()))
            .displayItems((parameters, output) -> {
        output.accept(SpecimenBag.SPECIMEN_BAG.get());
        output.accept(CoastalRoseBushWithoutRosehips.COASTAL_ROSE_BUSH_WITHOUT_ROSEHIPS.get());
        output.accept(CoastalRoseBushWithRosehips.COASTAL_ROSE_BUSH_WITH_ROSEHIPS.get());
        output.accept(CoastalRoseSucker.COASTAL_ROSE_SUCKER.get());
        output.accept(CoastalRosehip.COASTAL_ROSEHIP.get());
        output.accept(CoastalRosePetals.COASTAL_ROSE_PETALS.get());
        output.accept(CoastalRoseSeeds.COASTAL_ROSE_SEEDS.get());
        output.accept(Glehnia.GLEHNIA.get());
        output.accept(GlehniaRoot.GLEHNIA_ROOT.get());
        output.accept(GlehniaSeeds.GLEHNIA_SEEDS.get());
        output.accept(WildKumquat.WILD_KUMQUAT.get());
        output.accept(WildKumquatBush.WILD_KUMQUAT_BUSH.get());
        output.accept(WildKumquatBushWithFlowers.WILD_KUMQUAT_BUSH_WITH_FLOWERS.get());
        output.accept(WildKumquatBushWithFruits.WILD_KUMQUAT_BUSH_WITH_FRUITS.get());
        output.accept(BaishanzuFirLeaves.BAISHANZU_FIR_LEAVES_ITEM.get());
        output.accept(BaishanzuFirLog.BAISHANZU_FIR_LOG_ITEM.get());
        output.accept(BaishanzuFirLog.STRIPPED_BAISHANZU_FIR_LOG_ITEM.get());
        output.accept(BaishanzuFirSapling.BAISHANZU_FIR_SAPLING.get());
        output.accept(CyatheaSporeBottle.CYATHEA_SPORE_BOTTLE.get());
        output.accept(CyatheaGametophyte.CYATHEA_GAMETOPHYTE.get());
        output.accept(CyatheaTop.CYATHEA_TOP.get());
        output.accept(SandFritillariaBulb.SAND_FRITILLARIA_BULB.get());
        output.accept(SandFritillariaBuds.SAND_FRITILLARIA_BUDS.get());
        output.accept(DendrobiumBlock.DENDROBIUM_BLOCK.get());
        output.accept(DendrobiumFlower.DENDROBIUM_FLOWER.get());
        output.accept(Dendrobium.DENDROBIUM.get());
        output.accept(DendrobiumPolysaccha.DENDROBIUM_POLYSACCHA.get());
        output.accept(ZangnanCupressusCone.ZANGNAN_CUPRESSUS_CONE.get());
        output.accept(ZangnanCupressusSapling.ZANGNAN_CUPRESSUS_SAPLING.get());
        output.accept(ZangnanCupressusLog.ZANGNAN_CUPRESSUS_LOG_ITEM.get());
        output.accept(ZangnanCupressusLeaves.ZANGNAN_CUPRESSUS_LEAVES_ITEM.get());
        output.accept(NypaFruitBlock.NYPA_FRUIT_BLOCK_ITEM.get());
        output.accept(Nypa.NYPA_ITEM.get());
        output.accept(NypaFruit.NYPA_FRUIT.get());
        output.accept(WildSoya.WILD_SOYA.get());
        output.accept(Soya.SOYA.get());
        output.accept(SoyaMilk.SOYA_MILK.get());
        output.accept(SoyaZher.SOYA_ZHER.get());
        output.accept(Kingdonia.KINGDONIA_ITEM.get());
        output.accept(FalseLitchiLog.FALSE_LITCHI_LOG_ITEM.get());
        output.accept(FalseLitchiLeaves.FALSE_LITCHI_LEAVES_ITEM.get());
        output.accept(FalseLitchiSeedling.FALSE_LITCHI_SEEDLING.get());
        output.accept(FalseLitchi.FALSE_LITCHI.get());
        output.accept(FalseLitchiStone.FALSE_LITCHI_STONE.get());
        output.accept(YewBark.YEW_BARK.get());
        output.accept(YewCone.YEW_CONE.get());
        output.accept(YewSeed.YEW_SEED.get());
        output.accept(YewLeaves.YEW_LEAVES_ITEM.get());
        output.accept(YewSapling.YEW_SAPLING.get());
        output.accept(YewLog.YEW_LOG_ITEM.get());
        output.accept(Paclitaxel.PACLITAXEL.get());
        output.accept(HonghePapedaLog.HONGHE_PAPEDA_LOG_ITEM.get());
        output.accept(HonghePapedaLeaves.HONGHE_PAPEDA_LEAVES_ITEM.get());
        output.accept(HonghePapeda.HONGHE_PAPEDA.get());
        output.accept(HonghePapedaSapling.HONGHE_PAPEDA_SAPLING.get());
        output.accept(HonghePapedaLeaf.HONGHE_PAPEDA_LEAF.get());
        output.accept(GinkgoCone.GINKGO_CONE.get());
        output.accept(GinkgoLeaf.GINKGO_LEAF.get());
        output.accept(GinkgoLeaves.GINKGO_FEMALE_LEAVES_ITEM.get());
        output.accept(GinkgoLeaves.GINKGO_MALE_LEAVES_ITEM.get());
        output.accept(GinkgoLog.GINKGO_LOG_ITEM.get());
        output.accept(GinkgoLog.STRIPPED_GINKGO_LOG_ITEM.get());
        output.accept(GinkgoSapling.GINKGO_SAPLING.get());
        output.accept(GinkgoSeed.GINKGO_SEED.get());
     // Add the example item to the tab. For your own tabs, this method is preferred over the event
    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
