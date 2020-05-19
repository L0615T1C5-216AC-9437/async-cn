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
    private JSONObject list = new JSONObject();

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
        int timer = 10;
        int minutes = timer-1;
        int seconds = 59;
        String second = "";
        String minute = "";

        if (byteCode.has("async")) {
            data = byteCode.get("async");
            if (data.has("list")) {
                list = data.getJSONObject("list");
            }
            if (data.has("timer")) {
                timer = data.getInt("timer");
                minutes = timer-1;
                seconds = 59;
            }
        }
        while (MainT.isAlive()) {
            try {
                TimeUnit.SECONDS.sleep(1);
                seconds--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (seconds == -1) {
                if (minutes == 0) {
                    if (byteCode.has("async")) {
                        data = byteCode.get("async");
                        if (data.has("list")) {
                            list = data.getJSONObject("list");
                        }
                    }
                    for (Player p : Vars.playerGroup.all()) {
                        if (list.has(p.uuid)) continue;
                        Call.onWorldDataBegin(p.con);
                        netServer.sendWorldData(p);
                        Call.onInfoToast(p.con, "Auto-Sync completed.", 5);
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (byteCode.has("async")) {
                        data = byteCode.get("async");
                        if (data.has("list")) {
                            list = data.getJSONObject("list");
                        }
                        if (data.has("timer")) {
                            timer = data.getInt("timer");
                            minutes = timer-1;
                            seconds = 59;
                        }
                    }
                } else {
                    minutes--;
                    seconds = 59;
                }
            }
            if (seconds < 10) {
                second = "0";
            } else {
                second = "";
            }
            if (minutes < 10) {
                minute = "0";
            } else {
                minute = "";
            }
            for (Player p : playerGroup.all()) {
                if (list.has(p.uuid)) continue;
                Call.onInfoPopup(p.con, "Async in "+minute+minutes+":"+second+seconds, 1, 16, 10, 10, 10, 10);
            }
        }
    }
}
