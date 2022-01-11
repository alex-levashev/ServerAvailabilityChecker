import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public abstract class Servers {

    public static HashMap<String, String> readCSV(String fileNameAndPath) {
        HashMap<String, String> map = new HashMap<>();
        try {
            FileInputStream fis=new FileInputStream(fileNameAndPath);
            Scanner sc=new Scanner(fis);
            while(sc.hasNextLine()) {
                String[] tmpStrArray = sc.nextLine().split(",");
                map.put(tmpStrArray[0], tmpStrArray[1]);
            }
            sc.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Boolean isAlive(String serverAndPort) {
        String[] tmpStringArray = serverAndPort.split(":");
        String serverAddress = tmpStringArray[0];
        Integer serverPort = Integer.valueOf(tmpStringArray[1]);
        try (Socket s = new Socket(serverAddress, serverPort)) {
            return true;
        } catch (IOException ex) {
            /* ignore */
        }
        return false;
    }


}
