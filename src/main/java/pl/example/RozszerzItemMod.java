package pl.example;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RozszerzItemMod implements ModInitializer {

    public static final String MOD_ID = "rozszerzitem";

    // Reset "uzbrojony" per-gracz (bez timeoutu).
    private static final Set<UUID> RESET_ARMED = ConcurrentHashMap.newKeySet();

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                CommandManager.literal("rozszerz")

                    // /rozszerz <skala>
                    .then(CommandManager.argument("skala", DoubleArgumentType.doubleArg(0.1D, 16.0D))
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                            float scale = (float) DoubleArgumentType.getDouble(ctx, "skala");

                            ItemStack held = player.getMainHandStack();
                            if (held.isEmpty()) {
                                ctx.getSource().sendFeedback(() -> Text.literal(
                                    "Nie trzymasz żadnego itemu w głównej ręce."
                                ), false);
                                return 0;
                            }

                            ScaleNbt.setScale(held, scale);
                            ctx.getSource().sendFeedback(() -> Text.literal(
                                "Ustawiono skalę " + scale + " dla: " + held.getName().getString()
                            ), false);
                            return 1;
                        })
                    )

                    // /rozszerz usuń  (alias: /rozszerz usun)
                    .then(CommandManager.literal("usuń")
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                            return removeScaleFromMainHand(ctx.getSource(), player);
                        })
                    )
                    .then(CommandManager.literal("usun")
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                            return removeScaleFromMainHand(ctx.getSource(), player);
                        })
                    )

                    // /rozszerz reset
                    .then(CommandManager.literal("reset")
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                            RESET_ARMED.add(player.getUuid());

                            ctx.getSource().sendFeedback(() -> Text.literal(
                                "Reset jest uzbrojony. Aby potwierdzić, wpisz: /rozszerz reset confirm\n" +
                                "To usunie ustawioną skalę ze wszystkich itemów w twoim ekwipunku i endercheście."
                            ), false);
                            return 1;
                        })

                        // /rozszerz reset confirm
                        .then(CommandManager.literal("confirm")
                            .executes(ctx -> {
                                ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                                UUID id = player.getUuid();

                                if (!RESET_ARMED.remove(id)) {
                                    ctx.getSource().sendFeedback(() -> Text.literal(
                                        "Najpierw wpisz /rozszerz reset, a dopiero potem /rozszerz reset confirm."
                                    ), false);
                                    return 0;
                                }

                                int clearedInv = clearScaleFromInventory(player.getInventory());
                                int clearedEnder = clearScaleFromInventory(player.getEnderChestInventory());

                                // Dla pewności zaznacz zmiany
                                player.getInventory().markDirty();
                                player.getEnderChestInventory().markDirty();

                                int total = clearedInv + clearedEnder;
                                ctx.getSource().sendFeedback(() -> Text.literal(
                                    "Reset zakończony. Usunięto skalę z " + total + " stacków " +
                                    "(ekwipunek: " + clearedInv + ", enderchest: " + clearedEnder + ")."
                                ), false);

                                return 1;
                            })
                        )
                    )
            );
        });
    }

    private static int removeScaleFromMainHand(net.minecraft.server.command.ServerCommandSource source, ServerPlayerEntity player) {
        ItemStack held = player.getMainHandStack();
        if (held.isEmpty()) {
            source.sendFeedback(() -> Text.literal("Nie trzymasz żadnego itemu w głównej ręce."), false);
            return 0;
        }

        float before = ScaleNbt.getScale(held);
        if (before == 1.0f) {
            source.sendFeedback(() -> Text.literal(
                "Ten item nie ma ustawionej skali (albo jest równa 1.0)."
            ), false);
            return 0;
        }

        ScaleNbt.clearScale(held);
        source.sendFeedback(() -> Text.literal(
            "Usunięto skalę z: " + held.getName().getString()
        ), false);
        return 1;
    }

    private static int clearScaleFromInventory(Inventory inv) {
        int cleared = 0;
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (stack == null || stack.isEmpty()) continue;

            float scale = ScaleNbt.getScale(stack);
            if (scale != 1.0f) {
                ScaleNbt.clearScale(stack);
                cleared++;
            }
        }
        return cleared;
    }
}
