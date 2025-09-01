package org.endangeredplants.worldgen;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import org.endangeredplants.Endangeredplants;

public class ModBiomeModifiers {

    public static final ResourceKey<BiomeModifier> ADD_COASTAL_ROSE_BUSH =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
                    new ResourceLocation(Endangeredplants.MODID, "add_coastal_rose_bush"));

    public static final ResourceKey<BiomeModifier> ADD_GLEHNIA =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
                    new ResourceLocation(Endangeredplants.MODID, "add_glehnia"));

    public static final ResourceKey<BiomeModifier> ADD_CYATHEA =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
                    new ResourceLocation(Endangeredplants.MODID, "add_cyathea"));

    public static final ResourceKey<BiomeModifier> ADD_WILD_KUMQUAT =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
                    new ResourceLocation(Endangeredplants.MODID, "add_wild_kumquat"));

    public static final ResourceKey<BiomeModifier> ADD_NYPA =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
                    new ResourceLocation(Endangeredplants.MODID, "add_nypa"));

    public static final ResourceKey<BiomeModifier> ADD_SAND_FRITILLARIA =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
                    new ResourceLocation(Endangeredplants.MODID, "add_sand_fritillaria"));

    public static final ResourceKey<BiomeModifier> ADD_WILD_SOYA =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
                    new ResourceLocation(Endangeredplants.MODID, "add_wild_soya"));

    public static final ResourceKey<BiomeModifier> ADD_KINGDONIA =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
                    new ResourceLocation(Endangeredplants.MODID, "add_kingdonia"));

    public static final ResourceKey<BiomeModifier> ADD_BAISHANZU_FIR_TREE =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
                    new ResourceLocation(Endangeredplants.MODID, "add_baishanzu_fir_tree"));

    public static final ResourceKey<BiomeModifier> ADD_ZANGNAN_CUPRESSUS_TREE =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
                    new ResourceLocation(Endangeredplants.MODID, "add_zangnan_cupressus_tree"));

    public static final ResourceKey<BiomeModifier> ADD_FALSE_LITCHI_TREE =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
                    new ResourceLocation(Endangeredplants.MODID, "add_false_litchi_tree"));

    public static final ResourceKey<BiomeModifier> ADD_YEW_TREE =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
                    new ResourceLocation(Endangeredplants.MODID, "add_yew_tree"));

    public static final ResourceKey<BiomeModifier> ADD_HONGHE_PAPEDA_TREE =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
                    new ResourceLocation(Endangeredplants.MODID, "add_honghe_papeda_tree"));

    public static final ResourceKey<BiomeModifier> ADD_MALE_GINKGO_TREE =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
                    new ResourceLocation(Endangeredplants.MODID, "add_male_ginkgo_tree"));

    public static final ResourceKey<BiomeModifier> ADD_FEMALE_GINKGO_TREE =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
                    new ResourceLocation(Endangeredplants.MODID, "add_female_ginkgo_tree"));

    // 添加石斛生物群系修改器
    public static final ResourceKey<BiomeModifier> ADD_DENDROBIUM =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
                    new ResourceLocation(Endangeredplants.MODID, "add_dendrobium"));



    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        // Coastal Rose Bush - 保持原有设置
        context.register(ADD_COASTAL_ROSE_BUSH, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.BEACH),
                        biomes.getOrThrow(Biomes.STONY_SHORE),
                        biomes.getOrThrow(Biomes.SNOWY_BEACH)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.COASTAL_ROSE_BUSH_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        // Glehnia - 保持原有设置
        context.register(ADD_GLEHNIA, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.BEACH),
                        biomes.getOrThrow(Biomes.SNOWY_BEACH)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GLEHNIA_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        // Cyathea - 保持原有设置
        context.register(ADD_CYATHEA, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.JUNGLE),
                        biomes.getOrThrow(Biomes.SPARSE_JUNGLE),
                        biomes.getOrThrow(Biomes.BAMBOO_JUNGLE),
                        biomes.getOrThrow(Biomes.DARK_FOREST)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.CYATHEA_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        // Wild Kumquat - 保持原有设置
        context.register(ADD_WILD_KUMQUAT, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.BEACH)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.WILD_KUMQUAT_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_NYPA, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.MANGROVE_SWAMP)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.NYPA_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_SAND_FRITILLARIA, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.DESERT)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.SAND_FRITILLARIA_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_WILD_SOYA, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.PLAINS)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.WILD_SOYA_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_KINGDONIA, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.STONY_PEAKS),
                        biomes.getOrThrow(Biomes.JAGGED_PEAKS),
                        biomes.getOrThrow(Biomes.FROZEN_PEAKS),
                        biomes.getOrThrow(Biomes.TAIGA),
                        biomes.getOrThrow(Biomes.FOREST)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.KINGDONIA_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        // Dendrobium - 在森林生物群系中生成
        context.register(ADD_DENDROBIUM, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.JUNGLE),
                        biomes.getOrThrow(Biomes.SPARSE_JUNGLE),
                        biomes.getOrThrow(Biomes.BAMBOO_JUNGLE),
                        biomes.getOrThrow(Biomes.DARK_FOREST),
                        biomes.getOrThrow(Biomes.TAIGA),
                        biomes.getOrThrow(Biomes.OLD_GROWTH_SPRUCE_TAIGA),
                        biomes.getOrThrow(Biomes.OLD_GROWTH_PINE_TAIGA),
                        biomes.getOrThrow(Biomes.STONY_PEAKS)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.DENDROBIUM_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        // Baishanzu Fir Tree - 统一使用 VEGETAL_DECORATION
        context.register(ADD_BAISHANZU_FIR_TREE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.WINDSWEPT_HILLS),
                        biomes.getOrThrow(Biomes.WINDSWEPT_FOREST),
                        biomes.getOrThrow(Biomes.TAIGA),
                        biomes.getOrThrow(Biomes.OLD_GROWTH_SPRUCE_TAIGA),
                        biomes.getOrThrow(Biomes.SNOWY_TAIGA),
                        biomes.getOrThrow(Biomes.GROVE)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.BAISHANZU_FIR_TREE_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION // 修复：从 LOCAL_MODIFICATIONS 改为 VEGETAL_DECORATION
        ));

        // Zangnan Cupressus Tree - 统一使用 VEGETAL_DECORATION
        context.register(ADD_ZANGNAN_CUPRESSUS_TREE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.JUNGLE),
                        biomes.getOrThrow(Biomes.BAMBOO_JUNGLE)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ZANGNAN_CUPRESSUS_TREE_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION // 修复：从 LOCAL_MODIFICATIONS 改为 VEGETAL_DECORATION
        ));

        // False Litchi Tree - 确保使用 VEGETAL_DECORATION
        context.register(ADD_FALSE_LITCHI_TREE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.SAVANNA),
                        biomes.getOrThrow(Biomes.SAVANNA_PLATEAU),
                        biomes.getOrThrow(Biomes.WINDSWEPT_SAVANNA),
                        biomes.getOrThrow(Biomes.SPARSE_JUNGLE)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.FALSE_LITCHI_TREE_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        // Yew Tree - 确保使用 VEGETAL_DECORATION
        context.register(ADD_YEW_TREE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.TAIGA),
                        biomes.getOrThrow(Biomes.OLD_GROWTH_SPRUCE_TAIGA)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.YEW_TREE_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        // Honghe Papeda Tree - 确保使用 VEGETAL_DECORATION
        context.register(ADD_HONGHE_PAPEDA_TREE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.SAVANNA),
                        biomes.getOrThrow(Biomes.SAVANNA_PLATEAU),
                        biomes.getOrThrow(Biomes.SPARSE_JUNGLE)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.HONGHE_PAPEDA_TREE_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        // Male Ginkgo Tree - 确保使用 VEGETAL_DECORATION
        context.register(ADD_MALE_GINKGO_TREE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.PLAINS),
                        biomes.getOrThrow(Biomes.SUNFLOWER_PLAINS)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.MALE_GINKGO_TREE_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        // Female Ginkgo Tree - 确保使用 VEGETAL_DECORATION
        context.register(ADD_FEMALE_GINKGO_TREE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.PLAINS),
                        biomes.getOrThrow(Biomes.SUNFLOWER_PLAINS)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.FEMALE_GINKGO_TREE_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));


    }}