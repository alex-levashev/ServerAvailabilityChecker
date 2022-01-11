import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

//cat ./usr/lib/systemd/system/slackbot_mt_monitor.service
//        [Unit]
//        Description=Maintenance Queue Monitor
//        [Service]
//        ExecStart=/bin/bash /var/www/html/slackbot-java/service_start.sh
//        Restart=always
//        Type=simple
//        WorkingDirectory=/var/www/html/slackbot-java/
//        User=root
//        [Install]
//        WantedBy=multi-user.target
//cat service_stop.sh
//        #/bin/bash
//        process="SlackNotificationBot.jar"
//        ps -ef | grep $process | grep -v grep | awk '{print $2}' | xargs kill
//cat service_start.sh
//        #/bin/bash
//        /usr/bin/java -jar /var/www/html/slackbot-java/SlackNotificationBot.jar
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
        Telegram telegramClient = new Telegram(telegramToken, chatId);
        boolean firstRunFlag = true;
        while(true) {
            for (HashMap.Entry<String, String> entry : serverList.entrySet()) {
                Boolean currentServerStatus =  Servers.isAlive(entry.getValue());
                if(!serverStatus.containsKey(entry.getKey())) {
                    serverStatus.put(entry.getKey(), currentServerStatus);
                } else {
                    if(serverStatus.get(entry.getKey()) != currentServerStatus) {
                        serverStatus.replace(entry.getKey(), currentServerStatus);
                        telegramClient.send("Status of <b>" + entry.getKey() + "</b> is changed from <b>"
                                + serverStatus.get(entry.getKey()) + "</b> to <b>" + currentServerStatus + "</b>");
                    }
                }
            }
            if(firstRunFlag) {
                String message = "";
                for (HashMap.Entry<String, Boolean> item : serverStatus.entrySet()) {
                    message += "<b>" + item.getKey() + "</b>. Alive - <b>"
                            + item.getValue().toString().toUpperCase() + "</b>\n";
                }
                telegramClient.send("<b>Server status</b>\n" + message);
                firstRunFlag = false;
            }
            Thread.sleep(1000*60*5);
        }
    }
}
