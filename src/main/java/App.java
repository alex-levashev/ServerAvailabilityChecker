import java.io.UnsupportedEncodingException;
import java.util.HashMap;
//cat ./usr/lib/systemd/system/slackbot_mt_monitor.service
//        [Unit]
//        Description=Maintenance Queueu Monitor
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
    private final static String telegramToken = "";
    private final static String chatId = "";
    public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException {
        HashMap<String, String> serverList = Servers.readCSV("servers.csv");
        HashMap<String, Boolean> serverStatus = new HashMap<>();
        Telegram telegramClient = new Telegram(telegramToken, chatId);
        Boolean firstRunFlag = true;
        while(true) {
            for (HashMap.Entry<String, String> entry : serverList.entrySet()) {
                Boolean currentServerStatus =  Servers.isAlive(entry.getValue());
                if(!serverStatus.containsKey(entry.getKey())) {
                    System.out.println("Adding key " + entry.getKey() + " to the list of checked servers " +
                            "with status " + currentServerStatus.toString());
                    serverStatus.put(entry.getKey(), currentServerStatus);
                } else {
                    if(serverStatus.get(entry.getKey()) == currentServerStatus) {
                        System.out.println("Status of " + entry.getKey() + " is the same");
                    } else {
                        System.out.println("Status of " + entry.getKey() + " is changed from " + serverStatus.get(entry.getKey()) + " to " + currentServerStatus);
                        serverStatus.replace(entry.getKey(), currentServerStatus);
                        telegramClient.send("Status of <br>" + entry.getKey() + "</b> is changed from " + serverStatus.get(entry.getKey()) + " to " + currentServerStatus);
                    }
                }
            }
            if(firstRunFlag) {
                String message = new String();
                for (HashMap.Entry<String, Boolean> item : serverStatus.entrySet()) {
                    message += item.getKey() + ". Alive - " + item.getValue().toString().toUpperCase() + "\n";
                }
                telegramClient.send("<b>Server status</b>\n" + message);
                firstRunFlag = false;
            }
            Thread.sleep(1000*60*5);
        }
    }
}
