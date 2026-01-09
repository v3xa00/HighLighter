package pl.example;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class RozszerzItemMod implements ModInitializer {

    public static final String MOD_ID = "rozszerzitem";

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                CommandManager.literal("rozszerz")
                    .then(CommandManager.argument("skala", DoubleArgumentType.doubleArg(0.1D, 16.0D))
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                            double scaleD = DoubleArgumentType.getDouble(ctx, "skala");
                            float scale = (float) scaleD;

                            ItemStack held = player.getMainHandStack();
                            if (held.isEmpty()) {
                                ctx.getSource().sendFeedback(() -> Text.literal("Nie trzymasz żadnego itemu w głównej ręce."), false);
                                return 0;
                            }

                            ScaleNbt.setScale(held, scale);
                            ctx.getSource().sendFeedback(
                                () -> Text.literal("Ustawiono skalę " + scale + " dla: " + held.getName().getString()),
                                false
                            );
                            return 1;
                        })
                    )
            );
        });
    }
}
