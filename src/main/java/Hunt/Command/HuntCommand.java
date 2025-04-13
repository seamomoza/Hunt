package Hunt.Command;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class HuntCommand implements CommandExecutor {

    private final Plugin plugin;
    private BukkitRunnable currentTask = null;

    public static Player currentTarget; // 💥 여기에 저장!

    public HuntCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "/hunt <플레이어>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "해당 플레이어를 찾을 수 없습니다.");
            return true;
        }

        World world = target.getWorld(); // 타겟 기준으로 월드 지정
        int borderSize = 8000;

        // 월드보더 설정
        WorldBorder border = world.getWorldBorder();
        border.setCenter(0, 0);
        border.setSize(borderSize);

        // 타이머 시작 (타이틀)
        new BukkitRunnable() {
            int countdown = 3;

            @Override
            public void run() {
                if (countdown <= 0) {
                    // 3초 후 플레이어 산개
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        double x = (Math.random() - 0.5) * borderSize;
                        double z = (Math.random() - 0.5) * borderSize;
                        Location loc = new Location(world, x, world.getHighestBlockYAt((int) x, (int) z) + 1, z);
                        p.teleport(loc);
                        p.sendTitle(ChatColor.GREEN + "게임 시작!", "", 10, 40, 10);
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    }

                    // 타겟 저장
                    currentTarget = target;

                    // 나침반 실시간 추적 시작
                    if (currentTask != null) currentTask.cancel();
                    target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 3 * 20, 0, false, false));

                    currentTask = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!target.isOnline()) {
                                Bukkit.broadcastMessage(ChatColor.RED + "[Hunt] 타겟이 오프라인 상태입니다. 추적을 중단합니다.");
                                cancel();
                                return;
                            }

                            Location targetLoc = target.getLocation();
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.setCompassTarget(targetLoc);
                            }
                        }
                    };
                    currentTask.runTaskTimer(plugin, 0L, 20L);

                    Bukkit.broadcastMessage(ChatColor.YELLOW + "나침반이 " + target.getName() + "을(를) 실시간으로 추적합니다!");

                    cancel();
                    return;
                }

                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(String.valueOf(countdown), "", 0, 20, 10);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                }

                countdown--;
            }
        }.runTaskTimer(plugin, 0L, 20L); // 1초 간격

        return true;
    }
}