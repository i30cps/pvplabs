package dev.rotator.pvplabs.util;

import com.google.gson.Gson;
import dev.rotator.pvplabs.game.ctf.CTFMap;

import java.io.FileReader;
import java.io.IOException;

public class MapLoader {
    private static final Gson gson = new Gson();

    public static CTFMap loadMap(String filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, CTFMap.class);
        }
    }
}
