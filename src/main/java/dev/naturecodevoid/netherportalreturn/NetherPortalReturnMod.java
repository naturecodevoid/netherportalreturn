package dev.naturecodevoid.netherportalreturn;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.literal;


public class NetherPortalReturnMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("netherportalreturn");

    @Override
    public void onInitialize() {
        Data.load();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("netherportalreturn")
                            .requires(source -> source.hasPermissionLevel(2))
                            .then(
                                    literal("enable")
                                            .executes(context -> {
                                                if (!Data.enable())
                                                    context.getSource().sendMessage(Text.literal("netherportalreturn is already enabled").formatted(Formatting.RED));
                                                else
                                                    context.getSource().sendMessage(Text.literal("Enabled netherportalreturn").formatted(Formatting.GREEN));
                                                return 1;
                                            })
                            )
                            .then(
                                    literal("disable")
                                            .executes(context -> {
                                                if (!Data.disable())
                                                    context.getSource().sendMessage(Text.literal("netherportalreturn is already disabled").formatted(Formatting.RED));
                                                else
                                                    context.getSource().sendMessage(Text.literal("Disabled netherportalreturn").formatted(Formatting.GREEN));
                                                return 1;
                                            })
                            )
            );
        });
    }
}
