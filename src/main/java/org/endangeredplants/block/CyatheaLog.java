package org.endangeredplants.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;

import java.util.List;
import java.util.Random;

public class CyatheaLog {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 定义4×4×16的碰撞箱（Y轴高度为16）
    private static final VoxelShape SHAPE = Block.box(6, 0, 6, 10, 16, 10);

    // 桫椤原木方块注册（简化版，无去皮功能）
    public static final RegistryObject<Block> CYATHEA_LOG = BLOCKS.register(
            "cyathealog",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0F)
                    .sound(net.minecraft.world.level.block.SoundType.WOOD)) {

                // 重写掉落物逻辑 - 掉落2-4根木棍
                @Override
                public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
                    Random random = new Random();
                    int stickCount = 2 + random.nextInt(3); // 2-4根
                    return List.of(new ItemStack(Items.STICK, stickCount));
                }

                // 重写碰撞箱形状
                @Override
                public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter world, net.minecraft.core.BlockPos pos, CollisionContext context) {
                    return SHAPE;
                }

                // 可选：如果需要，也可以重写其他碰撞相关方法
                @Override
                public VoxelShape getCollisionShape(BlockState state, net.minecraft.world.level.BlockGetter world, net.minecraft.core.BlockPos pos, CollisionContext context) {
                    return SHAPE;
                }

                // 重写方块被移除时的逻辑，添加连锁破坏功能
                @Override
                public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
                    // 如果不是同一个方块类型的替换，则执行连锁破坏逻辑
                    if (!state.is(newState.getBlock())) {
                        // 检查并连锁破坏上方的桫椤方块
                        checkAndBreakAbove(world, pos);
                    }
                    super.onRemove(state, world, pos, newState, isMoving);
                }

                // 递归检查并破坏上方的桫椤方块
                private void checkAndBreakAbove(Level world, BlockPos currentPos) {
                    if (world.isClientSide) return; // 只在服务端执行

                    BlockPos abovePos = currentPos.above();
                    BlockState aboveState = world.getBlockState(abovePos);

                    // 检查上方是否是桫椤原木或桫椤顶部
                    if (isCyatheaBlock(aboveState)) {
                        // 获取该方块的掉落物（模拟空手破坏）
                        LootParams.Builder lootBuilder = new LootParams.Builder((net.minecraft.server.level.ServerLevel) world);
                        List<ItemStack> drops = aboveState.getDrops(lootBuilder);

                        // 生成掉落物实体
                        for (ItemStack drop : drops) {
                            if (!drop.isEmpty()) {
                                ItemEntity itemEntity = new ItemEntity(world,
                                        abovePos.getX() + 0.5,
                                        abovePos.getY() + 0.5,
                                        abovePos.getZ() + 0.5,
                                        drop);
                                world.addFreshEntity(itemEntity);
                            }
                        }

                        // 破坏上方方块
                        world.destroyBlock(abovePos, false);

                        // 递归检查更上方的方块
                        checkAndBreakAbove(world, abovePos);
                    }
                }

                // 判断是否是桫椤相关方块
                private boolean isCyatheaBlock(BlockState state) {
                    Block block = state.getBlock();
                    // 这里需要根据你的实际方块注册名称来判断
                    // 假设你有 CyatheaTop.CYATHEA_TOP 这样的引用
                    return block == CYATHEA_LOG.get() ||
                            isCyatheaTop(block) ||
                            isCyatheaLog(block);
                }

                // 辅助方法：检查是否是桫椤顶部
                private boolean isCyatheaTop(Block block) {
                    // 这里需要根据你的 CyatheaTop 类来实现
                    // 例如：return block == CyatheaTop.CYATHEA_TOP.get();
                    // 临时实现，你需要根据实际情况修改
                    String blockName = ForgeRegistries.BLOCKS.getKey(block).toString();
                    return blockName.contains("cyathea") && blockName.contains("top");
                }

                // 辅助方法：检查是否是其他桫椤原木
                private boolean isCyatheaLog(Block block) {
                    // 如果有多种桫椤原木，在这里添加判断
                    String blockName = ForgeRegistries.BLOCKS.getKey(block).toString();
                    return blockName.contains("cyathea") && blockName.contains("log");
                }
            }
    );

    // 物品注册
    public static final RegistryObject<Item> CYATHEA_LOG_ITEM = ITEMS.register(
            "cyathealog",
            () -> new BlockItem(CYATHEA_LOG.get(), new Item.Properties())
    );

    // 简化的注册方法
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}