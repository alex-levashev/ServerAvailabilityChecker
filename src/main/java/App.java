import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class App {
    private static Map<String, String> map;

    private static void makeMap(String[] args) {
        map = new HashMap<>();
        for (String arg : args) {
            if (arg.contains("-")) {
                if (arg.contains("=")) {
                    String tmp_key = arg.substring(1, arg.indexOf('='));
                    String tmp_val = arg.substring(arg.indexOf('=') + 1);
                    map.put(tmp_key, tmp_val);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException {
        makeMap(args);

        if (!map.containsKey("apiToken") || !map.containsKey("chatId") || !map.containsKey("file")) {
            System.out.println("Params error!");
            System.exit(0);
        }

        String telegramToken = map.get("apiToken");
        String chatId = map.get("chatId");
        String file = map.get("file");

        HashMap<String, String> serverList = Servers.readCSV(file);
        HashMap<String, Boolean> serverStatus = new HashMap<>();
        HashMap<String, LocalDateTime> serverStatusTime = new HashMap<>();
        Telegram telegramClient = new Telegram(telegramToken, chatId);
        boolean firstRunFlag = true;
        while(true) {
            for (HashMap.Entry<String, String> entry : serverList.entrySet()) {
                Boolean currentServerStatus =  Servers.isAlive(entry.getValue());
                LocalDateTime currentServerStatusTime = LocalDateTime.now();
                if(!serverStatus.containsKey(entry.getKey())) {
                    serverStatus.put(entry.getKey(), currentServerStatus);
                    serverStatusTime.put(entry.getKey(), currentServerStatusTime);
                } else {
                    if(serverStatus.get(entry.getKey()) != currentServerStatus) {
                        long timeDelta = Duration
                                .between(serverStatusTime.get(entry.getKey()), currentServerStatusTime).toMinutes();
                        String currentMessageText = "<b>" + entry.getKey() + "</b> is <b>UP</b>. Was DOWN for "
                                + Math.round(timeDelta) + " minutes";
                        if(!currentServerStatus) {
                            currentMessageText = "<b>" + entry.getKey() + "</b> is <b>DOWN</b>";
                        }
                        telegramClient.send(currentMessageText);
                        serverStatus.replace(entry.getKey(), currentServerStatus);
                        serverStatusTime.replace(entry.getKey(), currentServerStatusTime);
                    }
                }
            }
            if(firstRunFlag) {
                StringBuilder message = new StringBuilder();
                for (HashMap.Entry<String, Boolean> item : serverStatus.entrySet()) {
                    String serverStatusText = "UP";
                    if(!item.getValue()) {
                        serverStatusText = "DOWN";
                    }
                    message.append("<b>")
                            .append(item.getKey())
                            .append("</b> is <b>")
                            .append(serverStatusText)
                            .append("</b>\n");
                }
                telegramClient.send("<b>Server status</b>\n<pre>" + message + "</pre>");
                firstRunFlag = false;
            }
            Thread.sleep(1000*60*5);
        }
    }
}
