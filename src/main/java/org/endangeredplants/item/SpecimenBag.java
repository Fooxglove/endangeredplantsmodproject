package org.endangeredplants.item;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.block.YewLeaves;
import org.endangeredplants.item.YewCone;

import javax.annotation.Nullable;
import java.util.List;

public class SpecimenBag {
    // 创建物品注册器
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 注册标本袋物品
    public static final RegistryObject<Item> SPECIMEN_BAG = ITEMS.register("specimenbag",
            () -> new SpecimenBagItem(new Item.Properties()
                    .stacksTo(1)  // 不可堆叠，因为每个袋子有独立的存储
                    .durability(100)  // 可选：添加耐久度
            ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    // 标本袋物品类
    public static class SpecimenBagItem extends Item {
        private static final String STORAGE_TAG = "SpecimenStorage";
        private static final int MAX_STORAGE = 9; // 最大存储9个物品（类似小箱子）

        public SpecimenBagItem(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            Player player = context.getPlayer();
            ItemStack bagStack = context.getItemInHand();

            if (level.isClientSide || player == null) {
                return InteractionResult.SUCCESS;
            }

            Block clickedBlock = level.getBlockState(pos).getBlock();

            // 检查是否点击了YewLeaves方块
            if (clickedBlock == YewLeaves.YEW_LEAVES.get()) {
                // 创建YewCone物品
                ItemStack yewConeStack = new ItemStack(YewCone.YEW_CONE.get(), 1);

                // 尝试存储到标本袋中
                if (addItemToBag(bagStack, yewConeStack)) {
                    player.sendSystemMessage(Component.literal("成功收集到红豆杉球果标本！"));
                    return InteractionResult.SUCCESS;
                } else {
                    player.sendSystemMessage(Component.literal("标本袋已满！"));
                    return InteractionResult.FAIL;
                }
            }

            return super.useOn(context);
        }

        // 添加物品到标本袋
        private boolean addItemToBag(ItemStack bagStack, ItemStack itemToAdd) {
            CompoundTag bagTag = bagStack.getOrCreateTag();
            ListTag storageList = bagTag.getList(STORAGE_TAG, 10); // 10 = CompoundTag类型

            // 检查是否已满
            if (storageList.size() >= MAX_STORAGE) {
                return false;
            }

            // 尝试合并相同物品
            for (int i = 0; i < storageList.size(); i++) {
                CompoundTag slotTag = storageList.getCompound(i);
                ItemStack existingStack = ItemStack.of(slotTag);

                if (ItemStack.isSameItemSameTags(existingStack, itemToAdd)) {
                    int newCount = existingStack.getCount() + itemToAdd.getCount();
                    if (newCount <= existingStack.getMaxStackSize()) {
                        existingStack.setCount(newCount);
                        storageList.set(i, existingStack.save(new CompoundTag()));
                        bagTag.put(STORAGE_TAG, storageList);
                        return true;
                    }
                }
            }

            // 添加新物品槽位
            CompoundTag itemTag = new CompoundTag();
            itemToAdd.save(itemTag);
            storageList.add(itemTag);
            bagTag.put(STORAGE_TAG, storageList);
            return true;
        }

        // 获取存储的物品列表
        public List<ItemStack> getStoredItems(ItemStack bagStack) {
            CompoundTag bagTag = bagStack.getOrCreateTag();
            ListTag storageList = bagTag.getList(STORAGE_TAG, 10);

            List<ItemStack> items = new java.util.ArrayList<>();
            for (int i = 0; i < storageList.size(); i++) {
                CompoundTag slotTag = storageList.getCompound(i);
                ItemStack stack = ItemStack.of(slotTag);
                if (!stack.isEmpty()) {
                    items.add(stack);
                }
            }
            return items;
        }

        // 移除指定物品
        public boolean removeItem(ItemStack bagStack, ItemStack itemToRemove) {
            CompoundTag bagTag = bagStack.getOrCreateTag();
            ListTag storageList = bagTag.getList(STORAGE_TAG, 10);

            for (int i = 0; i < storageList.size(); i++) {
                CompoundTag slotTag = storageList.getCompound(i);
                ItemStack existingStack = ItemStack.of(slotTag);

                if (ItemStack.isSameItemSameTags(existingStack, itemToRemove)) {
                    int newCount = existingStack.getCount() - itemToRemove.getCount();
                    if (newCount <= 0) {
                        storageList.remove(i);
                    } else {
                        existingStack.setCount(newCount);
                        storageList.set(i, existingStack.save(new CompoundTag()));
                    }
                    bagTag.put(STORAGE_TAG, storageList);
                    return true;
                }
            }
            return false;
        }

        // 显示工具提示
        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
            super.appendHoverText(stack, level, tooltipComponents, isAdvanced);

            List<ItemStack> storedItems = getStoredItems(stack);
            tooltipComponents.add(Component.literal("存储物品: " + storedItems.size() + "/" + MAX_STORAGE));

            if (!storedItems.isEmpty()) {
                tooltipComponents.add(Component.literal("包含:"));
                for (ItemStack item : storedItems) {
                    tooltipComponents.add(Component.literal("  - " + item.getCount() + "x " + item.getHoverName().getString()));
                }
            }

            tooltipComponents.add(Component.literal("右键点击红豆杉叶子收集球果"));
        }

        // 检查标本袋是否为空
        public boolean isEmpty(ItemStack bagStack) {
            return getStoredItems(bagStack).isEmpty();
        }

        // 获取存储容量
        public int getStorageCapacity() {
            return MAX_STORAGE;
        }

        // 获取当前存储数量
        public int getCurrentStorageCount(ItemStack bagStack) {
            return getStoredItems(bagStack).size();
        }
    }
}