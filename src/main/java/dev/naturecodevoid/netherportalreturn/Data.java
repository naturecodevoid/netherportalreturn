package dev.naturecodevoid.netherportalreturn;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;

import static dev.naturecodevoid.netherportalreturn.NetherPortalReturnMod.LOGGER;

public class Data {
    private static final Gson gson = new Gson();
    private static Path dataPath;
    private static DataInner data = new DataInner();

    public static void load() {
        dataPath = FabricLoader.getInstance().getConfigDir().resolve("netherportalreturn.data");
        if (Files.exists(dataPath))
            try {
                LOGGER.info("Loading data");
                data = gson.fromJson(Files.readString(dataPath), DataInner.class);
            } catch (IOException | JsonSyntaxException e) {
                LOGGER.error("Failed to load data, falling back to defaults", e);
            }
        saveNoThread();
    }

    private static void saveNoThread() {
        try {
            Files.writeString(dataPath, gson.toJson(data));
        } catch (IOException e) {
            LOGGER.error("Failed to save data", e);
        }
    }

    private static void save() {
        new Thread(Data::saveNoThread).start();
    }

    public static boolean enabled() {
        return data.enabled;
    }

    public static boolean enable() {
        if (data.enabled) return false;
        data.enabled = true;
        save();
        return true;
    }

    public static boolean disable() {
        if (!data.enabled) return false;
        data.enabled = false;
        save();
        return true;
    }

    public static void putPlayerPosition(UUID key, Vec3d value) {
        data.playerPositions.put(key, value);
        save();
    }

    @Nullable
    public static Vec3d removePlayerPosition(UUID key) {
        var value = data.playerPositions.remove(key);
        save();
        return value;
    }

    private static class DataInner {
        boolean enabled = true;
        HashMap<UUID, Vec3d> playerPositions = new HashMap<>();
    }
}
