package Hunt.Main;

import Hunt.Command.HuntCommand;
import Hunt.Event.FireWork.FireWorkDamage;
import Hunt.Event.Compass.HuntListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Hunt extends JavaPlugin {


    private static Hunt instance;
    @Override
    public void onEnable() {
        getCommand("hunt").setExecutor(new HuntCommand(this));
        Bukkit.getPluginManager().registerEvents(new HuntListener(), this);
        Bukkit.getPluginManager().registerEvents(new FireWorkDamage(), this);
        instance = this; // ✅ 인스턴스 저장

    }

    public static Hunt getInstance() {
        return instance;
    }
}
