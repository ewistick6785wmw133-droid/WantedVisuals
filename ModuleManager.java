package net.wantedvisuals;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private static final List<Module> modules = new ArrayList<>();

    public static void init() {
        // Категория: Render
        modules.add(new Module("RedCrits", "Окрашивает частицы критов в ярко-красный", "Render"));
        modules.add(new Module("ClearWater", "Делает воду прозрачной", "Render"));
        modules.add(new Module("NoExplosions", "Отключает дым от ТНТ для FPS", "Render"));

        // Категория: Hud
        modules.add(new Module("FPS Display", "Отображает FPS на экране", "Hud"));
        modules.add(new Module("CPS Counter", "Показывает количество кликов", "Hud"));

        // Категория: Utility
        modules.add(new Module("FastChat", "Убирает задержку появления чата", "Utility"));

        // Категория: ReallyWorld
        modules.add(new Module("ChatFilter", "Очищает чат от спама донатов", "ReallyWorld"));
    }

    public static List<Module> getModules() {
        return modules;
    }
}
