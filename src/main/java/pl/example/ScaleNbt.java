package pl.example;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public final class ScaleNbt {
    private ScaleNbt() {}

    public static final String KEY = "rozszerz_scale";

    public static float getScale(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 1.0f;
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(KEY)) return 1.0f;

        float v = nbt.getFloat(KEY);
        if (Float.isNaN(v) || Float.isInfinite(v) || v <= 0f) return 1.0f;
        return v;
    }

    public static void setScale(ItemStack stack, float scale) {
        if (stack == null || stack.isEmpty()) return;
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putFloat(KEY, scale);
    }

    public static void clearScale(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return;
        nbt.remove(KEY);
        if (nbt.isEmpty()) stack.setNbt(null);
    }
}
