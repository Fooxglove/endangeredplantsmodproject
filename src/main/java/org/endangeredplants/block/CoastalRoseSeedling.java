package org.endangeredplants.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.item.CoastalRoseSucker;

import java.util.Collections;
import java.util.List;

public class CoastalRoseSeedling {
    // 延迟注册器
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 幼苗方块类，添加生长逻辑和破坏逻辑
    public static class SeedlingBlock extends Block {
        public SeedlingBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        // 启用随机刻
        @Override
        public boolean isRandomlyTicking(BlockState state) {
            return true;
        }

        // 随机刻逻辑
        @Override
        public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            super.randomTick(state, level, pos, random);

            // 检查光照条件是否足够
            if (level.getRawBrightness(pos, 0) >= 9) {
                // 随机生长概率
                if (random.nextInt(10000) < 1) {
                    // 转换为成熟的玫瑰丛
                    level.setBlock(pos, CoastalRosebushBlock.COASTAL_ROSE_BUSH_BLOCK.get().defaultBlockState(), 3);
                }
            }
        }

        // 破坏时的逻辑
        @Override
        public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
            super.playerWillDestroy(level, pos, state, player);

            // 检查是否使用剪刀
            boolean usingShears = player.getMainHandItem().getItem() instanceof ShearsItem;

            if (!usingShears) {
                // 不是用剪刀破坏时扣血
                if (!level.isClientSide() && !player.isCreative()) {
                    player.hurt(level.damageSources().generic(), 2.0F);
                }

                // 防止掉落物品
                if (!level.isClientSide() && level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
                    level.destroyBlock(pos, false);
                }
            }
        }

        // 获取掉落物品 - 新版本Minecraft使用getDrops方法
        @Override
        public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
            // 检查是否使用剪刀（通过LootContext获取工具）
            if (builder.getParameter(LootContextParams.TOOL) != null &&
                    builder.getParameter(LootContextParams.TOOL).getItem() instanceof ShearsItem) {
                // 使用剪刀时掉落幼苗物品
                return Collections.singletonList(new ItemStack(CoastalRoseSucker.COASTAL_ROSE_SUCKER.get()));
            }
            // 否则不掉落
            return Collections.emptyList();
        }

        // 用于创造模式选取物品
        @Override
        public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
            return new ItemStack(CoastalRoseSucker.COASTAL_ROSE_SUCKER.get());
        }
    }

    // 注册幼苗方块
    public static final RegistryObject<Block> COASTAL_ROSE_SEEDLING = BLOCKS.register("coastalroseseedling",
            () -> new SeedlingBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .instabreak()
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noCollission()
                    .noOcclusion()
                    .randomTicks()
            ));

    // 注册方块的物品形式
    public static final RegistryObject<Item> COASTAL_ROSE_SEEDLING_ITEM = ITEMS.register(
            "coastalroseseedling",
            () -> new BlockItem(
                    COASTAL_ROSE_SEEDLING.get(),
                    new Item.Properties()
            )
    );

    // 注册到事件总线
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}