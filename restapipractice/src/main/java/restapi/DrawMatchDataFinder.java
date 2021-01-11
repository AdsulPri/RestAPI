
package restapi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

class Result2 {

    /*
     * Complete the 'getNumDraws' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts INTEGER year as parameter.
     */

    public static int getNumDraws(int year) throws IOException {
        
        final String EndPoint = "https://jsonmock.hackerrank.com/api/football_matches?year="+year;
        int totalDraws =0;
        final int MaxScore=10;
        
        for (int score=0; score<=MaxScore; score++)
        {
            totalDraws+=getTotalNumDraws(String.format(EndPoint+"&team1goals=%d&team2goals=%d", score, score));
        }
        
        return totalDraws;

    }
    
    private static int getTotalNumDraws(String request) throws IOException
    {
        URL url=new URL(request);
        System.out.println("URL : "+url);
        HttpURLConnection httpUrlConnection=(HttpURLConnection) url.openConnection();
        
        httpUrlConnection.setRequestMethod("GET");
        httpUrlConnection.setConnectTimeout(5000);
        httpUrlConnection.setReadTimeout(5000);
        httpUrlConnection.addRequestProperty("Content-Type", "application/json");
        
        int status=httpUrlConnection.getResponseCode();
        
        InputStream in =(status<200 || status >299)?
        httpUrlConnection.getErrorStream() : httpUrlConnection.getInputStream();
        
        
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String response;
        StringBuffer responseContent = new StringBuffer();

        while ((response = br.readLine()) != null)
            responseContent.append(response);

        br.close();
        httpUrlConnection.disconnect();

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        
        String script = "var obj = JSON.parse('" + responseContent.toString() + "');";
        script += "var total= obj.total;";

        try {
            engine.eval(script);
        } catch (ScriptException ex) {
            ex.printStackTrace();
        }

        if (engine.get("total") == null)
            throw new RuntimeException("No data found");      
        
        
        return (int) engine.get("total");
    }

}

public class DrawMatchDataFinder {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int year = Integer.parseInt(bufferedReader.readLine().trim());

        int result = Result2.getNumDraws(year);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
