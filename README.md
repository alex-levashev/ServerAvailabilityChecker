# Lightweight Server Status Checker
Small tool for monitoring server health from the CSV file and send telegram message if status is changed.
## Run
JAR file should be run with parameters:
java -jar ServerAvailabilityChecker.jar --apiToken "TelegramApiToken" --chatId "TelegramChatId" --file "path-to-CSV-file"
## CSV file format example
Server name,IP,PORT\
#### Example:
Server 1,8.8.8.8:80\
Server 2,9.9.9.9:21
## Run as service in *nix
### Create service file
Create service file in **/usr/lib/systemd/system/**\
nano ./usr/lib/systemd/system/server_monitor.service
> [Unit]\
Description=Maintenance Queue Monitor\
[Service]\
ExecStart=/bin/bash /path/to/service_start.sh\
Restart=always\
Type=simple\
WorkingDirectory=path/to/\
User=root\
[Install]\
WantedBy=multi-user.target
### Create service start script
nano service_start.sh
<pre>#/bin/bash
/usr/bin/java -jar /path/to/ServerAvailabilityChecker.jar</pre>
### Create service stop script
nano service_stop.sh
<pre>#/bin/bash
process="ServerAvailabilityChecker.jar"
ps -ef | grep $process | grep -v grep | awk '{print $2}' | xargs kill</pre>