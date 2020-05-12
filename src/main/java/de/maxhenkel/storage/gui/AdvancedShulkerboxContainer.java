package de.maxhenkel.storage.gui;

import de.maxhenkel.storage.items.AdvancedShulkerBoxItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class AdvancedShulkerboxContainer extends Container {

    private IInventory shulkerboxInventory;
    private int numRows = 3;

    public AdvancedShulkerboxContainer(int id, PlayerInventory playerInventoryIn, IInventory shulkerboxInventory) {
        super(Containers.SHULKERBOX_CONTAINER, id);
        this.shulkerboxInventory = shulkerboxInventory;
        shulkerboxInventory.openInventory(playerInventoryIn.player);
        int i = (numRows - 4) * 18;

        for (int j = 0; j < numRows; j++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new AdvancedShulkerboxSlot(shulkerboxInventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for (int l = 0; l < 3; l++) {
            for (int j1 = 0; j1 < 9; j1++) {
                addSlot(new Slot(playerInventoryIn, j1 + l * 9 + 9, 8 + j1 * 18, 102 + l * 18 + i));
            }
        }

        int locked = getLockedSlot(playerInventoryIn.player);
        for (int i1 = 0; i1 < 9; i1++) {
            int x = 8 + i1 * 18;
            int y = 160 + i;
            if (i1 == locked) {
                addSlot(new LockedSlot(playerInventoryIn, i1, x, y));
            } else {
                addSlot(new Slot(playerInventoryIn, i1, x, y));
            }
        }

    }

    public AdvancedShulkerboxContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new ShulkerBoxItemInventory(playerInventory.player, getShulkerBox(playerInventory.player)));
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < this.numRows * 9) {
                if (!this.mergeItemStack(itemstack1, numRows * 9, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, numRows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        shulkerboxInventory.closeInventory(playerIn);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return shulkerboxInventory.isUsableByPlayer(playerIn);
    }

    public static int getLockedSlot(PlayerEntity player) {
        ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
        if (isOpenableShulkerBox(stack)) {
            return player.inventory.currentItem;
        }
        return -1;
    }

    public static ItemStack getShulkerBox(PlayerEntity player) {
        ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
        if (isOpenableShulkerBox(stack)) {
            return stack;
        }
        stack = player.getHeldItem(Hand.OFF_HAND);
        if (isOpenableShulkerBox(stack)) {
            return stack;
        }
        return null;
    }

    public static boolean isOpenableShulkerBox(ItemStack stack) {
        if (stack == null || stack.getCount() != 1) {
            return false;
        }

        if (stack.getItem() instanceof AdvancedShulkerBoxItem) {
            return true;
        }

        return false;
    }
}
