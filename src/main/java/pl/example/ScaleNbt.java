package pl.example;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public final class ScaleNbt {
    private ScaleNbt() {}

    public static final String KEY = "rozszerz_scale";

    public static float getScale(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 1.0f;

        NbtComponent custom = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (custom == null) return 1.0f;

        NbtCompound nbt = custom.copyNbt();
        if (!nbt.contains(KEY)) return 1.0f;

        float v = nbt.getFloat(KEY);
        if (Float.isNaN(v) || Float.isInfinite(v) || v <= 0f) return 1.0f;
        return v;
    }

    public static void setScale(ItemStack stack, float scale) {
        if (stack == null || stack.isEmpty()) return;

        NbtCompound nbt = getCustomDataCopy(stack);
        nbt.putFloat(KEY, scale);

        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public static void clearScale(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;

        NbtComponent custom = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (custom == null) return;

        NbtCompound nbt = custom.copyNbt();
        if (!nbt.contains(KEY)) return;

        nbt.remove(KEY);

        if (nbt.isEmpty()) {
            stack.remove(DataComponentTypes.CUSTOM_DATA);
        } else {
            stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        }
    }

    private static NbtCompound getCustomDataCopy(ItemStack stack) {
        NbtComponent custom = stack.get(DataComponentTypes.CUSTOM_DATA);
        return custom != null ? custom.copyNbt() : new NbtCompound();
    }
}
