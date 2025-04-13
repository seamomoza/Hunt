package Hunt.Main;

import Hunt.Command.GiveUpCommand;
import Hunt.Command.HuntCommand;
import Hunt.Event.Compass.CompassInteract;
import Hunt.Event.Death.BarrierKeep;
import Hunt.Event.Death.KillTheTarget;
import Hunt.Event.Compass.FireWorkDamage;
import Hunt.Event.TargetWin.EnderDragonDeath;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Hunt extends JavaPlugin {


    private static Hunt instance;
    @Override
    public void onEnable() {
        getCommand("hunt").setExecutor(new HuntCommand(this));
        getCommand("giveup").setExecutor(new GiveUpCommand(this));
        Bukkit.getPluginManager().registerEvents(new CompassInteract(), this);
        Bukkit.getPluginManager().registerEvents(new FireWorkDamage(), this);
        Bukkit.getPluginManager().registerEvents(new KillTheTarget(), this);
        Bukkit.getPluginManager().registerEvents(new BarrierKeep(), this);
        Bukkit.getPluginManager().registerEvents(new EnderDragonDeath(), this);
        instance = this; // ✅ 인스턴스 저장

    }

    public static Hunt getInstance() {
        return instance;
    }
}
