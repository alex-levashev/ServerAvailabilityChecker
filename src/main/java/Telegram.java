import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class Telegram {
    private static String apiToken;
    private static String chatId;
    public Telegram(String apiToken, String chatId) {
        this.apiToken = apiToken;
        this.chatId = chatId;
    }

    public void send(String message) throws UnsupportedEncodingException {
        String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&parse_mode=HTML&text=%s";
        urlString = String.format(urlString, apiToken, chatId, URLEncoder.encode(message, "UTF-8"));
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            InputStream is = new BufferedInputStream(conn.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
