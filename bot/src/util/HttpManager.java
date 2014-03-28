package util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

/**
 * User: ShemiareiD
 * Date: 3/27/14
 * Time: 1:12 PM
 */
public class HttpManager {

    private Agent agentHeaderValue = Agent.MOZILLA;

    public String doGet(String url) {
        return executeQeury(url,"",false);
    }

    public String doPost(String url, Map params) {
        return executeQeury(url,"",true);
    }

    /**
     *
     * @param params
     * @return
     */
    private String assembleParams(Map params) {
        StringBuilder paramsStr = new StringBuilder();
        Set<String> paramsNames = params.keySet();
        for(String paramName : paramsNames) {
            if (paramsStr.length() != 0)
                paramsStr.append("&");
            paramsStr.append(paramName).append("=")
                    .append(params.get(paramName));
        }
        return paramsStr.toString();
    }

    /**
     *
     *
     * @param targetURL
     * @param urlParameters
     * @return
     */
    private String executeQeury(String targetURL, String urlParameters, boolean postFlag) {
        if(targetURL == null) {
            return null;
        }

        try {

            URL obj = new URL(targetURL);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            if (postFlag) {
                conn.setRequestMethod("POST");
            } else {
                conn.setRequestMethod("GET");
            }

            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.addRequestProperty("User-Agent", agentHeaderValue.context);

            if (postFlag) {
                String urlParams = new String(urlParameters.getBytes(),
                        Charset.forName("UTF-8"));
                conn.setRequestProperty("Content-Length",
                        "" + Integer.toString(urlParams.getBytes().length));

                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Send request
                DataOutputStream wr = new DataOutputStream(
                        conn.getOutputStream());
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(wr, "UTF-8"));
                writer.write(urlParams);
                writer.flush();
                writer.close();
            }

            boolean redirect = false;

            // normally, 3xx is redirect
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }

            if (redirect) {

                // get redirect url from "location" header field
                String newUrl = conn.getHeaderField("Location");

                // get the cookie if need, for login
                String cookies = conn.getHeaderField("Set-Cookie");

                // open the new connnection again
                conn = (HttpURLConnection) new URL(newUrl).openConnection();
                conn.setRequestProperty("Cookie", cookies);
                conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.addRequestProperty("User-Agent", agentHeaderValue.context);

            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer html = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                html.append(inputLine);
            }
            in.close();

            return html.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setAgent(Agent agent) {
        if(agent != null) {
            agentHeaderValue = agent;
        }
    }

    public enum Agent {
        MOZILLA("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0"),
        CHROME("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36");

        private String context;

        private Agent(String context) {
            this.context = context;
        }
    }
}
