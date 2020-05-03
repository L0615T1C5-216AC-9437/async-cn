package A;

import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.Strings;
import mindustry.entities.type.Player;
import mindustry.game.EventType;
import mindustry.plugin.Plugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class Main extends Plugin {
    //Var
    public static JSONObject data = new JSONObject();
    public static JSONObject list = new JSONObject();
    public static Thread async;
    Boolean enabled = false;

    ///Var
    //on start
    public Main() {
        if (!byteCode.hasDir("mind_db")) {
            byteCode.mkdir("mind_db");
        }
        if (!byteCode.has("async")) {
            data = new JSONObject();
            data.put("timer", 10);
            byteCode.make("async", data);
        }
        data = byteCode.get("async");
        if (data == null) {
            Log.err("Invalid file - " + System.getProperty("user.home") + "/mind_db/async.cn");
            Log.info("Reset file using command `async-clear`");
            return;
        }
        enabled = true;
        A.async a = new A.async(Thread.currentThread());
        a.setDaemon(false);
        a.start();

        Events.on(EventType.WorldLoadEvent.class, event -> {
            if (enabled) {
                if (!async.isAlive()) {
                    A.async b = new A.async(Thread.currentThread());
                    b.setDaemon(false);
                    b.start();
                }
            }
        });
    }

    public void registerServerCommands(CommandHandler handler) {
        handler.register("async-clear", "generates the default async.cn file", arg -> {
            data = new JSONObject();
            data.put("timer", 10);
            list = new JSONObject();
            list.put("uuid1","");
            list.put("uuid2","");
            list.put("uuid3","");
            data.put("list", list);
            if (byteCode.save("async", data) != null) Log.info("Successfully created " + System.getProperty("user.home") + "/mind_db/async.cn");
        });
        handler.register("async-timer", "[ever-x-minutes]", "Changes how often Auto-Sync", arg -> {
            if (arg.length == 0) {
                if (byteCode.has("async")) {
                    data = byteCode.get("async");
                    if (data.has("timer")) {
                        Log.info("timer set to every " + data.getInt("timer") + " minutes.");
                    } else {
                        Log.err("async.cn does not contain key `timer`");
                    }
                }
            } else {
                if (byteCode.has("async")) {
                    data = byteCode.get("async");
                    if (Strings.canParseInt(arg[0])) {
                        byteCode.putInt("async", "timer", Strings.parseInt(arg[0]));
                    } else {
                        Log.err("Value must be a integer!");
                    }
                }
            }
        });
    }
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("async", "toggle auto-sync", (arg, player) -> {
            if (byteCode.has("async")) {
                data = byteCode.get("async");
                if (data == null) {
                    Log.err("Invalid file - " + System.getProperty("user.home") + "/mind_db/async.cn");
                    Log.info("Reset file using command `async-clear`");
                    return;
                }
                if (!data.has("list")) {
                    list = new JSONObject();
                    list.put("uuid1","");
                    list.put("uuid2","");
                    list.put("uuid3","");
                    byteCode.putJObject("async", "list", list);
                } else {
                    list = data.getJSONObject("list");
                }
                if (list.has(player.uuid)) {
                    if (data.has("timer")) {
                        player.sendMessage("Auto-Sync enabled - sync every " + data.getInt("timer") + " minutes.");
                    } else {
                        player.sendMessage("Auto-Sync enabled - sync every 10 minutes.");
                    }
                    list.remove(player.uuid);
                } else {
                    player.sendMessage("Auto-Sync disabled.");
                    list.put(player.uuid,"");
                }
                data.put("list", list);
                byteCode.save("async", data);
            }
        });
    }

}