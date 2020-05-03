package A;

import arc.util.Log;
import mindustry.Vars;
import mindustry.entities.type.Player;
import mindustry.gen.Call;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import static mindustry.Vars.netServer;
import static mindustry.Vars.playerGroup;

public class async extends Thread {
    private Thread MainT;
    private JSONObject data = new JSONObject();

    public async(Thread main) {
        MainT = main;
    }

    public void run() {
        Log.info("async started - Waiting 60 Seconds");
        Main.async = Thread.currentThread();
        try {
            TimeUnit.SECONDS.sleep(60);
        } catch (Exception e) {
        }
        Log.info("async running");
        while (MainT.isAlive()) {
            //sleep
            if (byteCode.has("async")) {
                data = byteCode.get("async");
                if (data.has("timer")) {
                    try {
                        TimeUnit.MINUTES.sleep(data.getInt("timer"));
                    } catch (Exception e) {
                    }
                } else {
                    Log.err("Invalid file - " + System.getProperty("user.home") + "/mind_db/async.cn - does not contain JSON key `timer`");
                    Log.info("Reset file using command `async-clear`");
                    try {
                        TimeUnit.MINUTES.sleep(10);
                    } catch (Exception e) {
                    }
                }
            } else {
                Log.err("Missing file - " + System.getProperty("user.home") + "/mind_db/async.cn");
                Log.info("Reset file using command `async-clear`");
                try {
                    TimeUnit.MINUTES.sleep(10);
                } catch (Exception e) {
                }
            }
            ///sleep
            for (Player p : Vars.playerGroup.all()) {
                if (data.has(p.uuid)) continue;
                Call.onWorldDataBegin(p.con);
                netServer.sendWorldData(p);
                Call.onInfoToast(p.con, "Auto-Sync completed.", 5);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
