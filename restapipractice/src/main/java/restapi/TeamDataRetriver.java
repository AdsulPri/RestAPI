package restapi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
//java-8 strictly
class Result {

	/*
	 * Complete the 'getTotalGoals' function below.
	 *
	 * The function is expected to return an INTEGER. The function accepts following
	 * parameters: 1. STRING team 2. INTEGER year
	 */

	public static int getTotalGoals(String team, int year) throws IOException {

		final String EndPoint = "https://jsonmock.hackerrank.com/api/football_matches";

		System.out.println("Endpoint : " + EndPoint);
		int totalGoalsAtHome = getPageGoals(
				String.format(EndPoint + "?team1=%s&year=%d", URLEncoder.encode(team, "UTF-8"), year), 0, "team1", 1);

		int totalGoalsAtVisiting = getPageGoals(
				String.format(EndPoint + "?team2=%s&year=%d", URLEncoder.encode(team, "UTF-8"), year), 0, "team2", 1);

		return totalGoalsAtHome + totalGoalsAtVisiting;

	}

	private static int getPageGoals(String request, int totalgoals, String team, int page) throws IOException {

		URL url = new URL(request + "&page=" + page);

		System.out.println("URL : " + url);

		HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();

		httpUrlConnection.setRequestMethod("GET");
		httpUrlConnection.setConnectTimeout(5000);
		httpUrlConnection.setReadTimeout(5000);
		httpUrlConnection.addRequestProperty("Content-Type", "application/json");

		int status = httpUrlConnection.getResponseCode();

		InputStream in = (status < 200 || status > 299) ? httpUrlConnection.getErrorStream()
				: httpUrlConnection.getInputStream();

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
		script += "var total_pages = obj.total_pages;";
		script += "var total_goals = obj.data.reduce" + "(function(accumulator, current)"
				+ "{ return accumulator+parseInt(current." + team + "goals);}, 0);";

		try {
			engine.eval(script);
		} catch (ScriptException ex) {
			ex.printStackTrace();
		}

		if (engine.get("total_pages") == null)
			throw new RuntimeException("No data found");

		int totalPages = (int) engine.get("total_pages");
		totalgoals += (int) Double.parseDouble(engine.get("total_goals").toString());

		return (page < totalPages) ? getPageGoals(request, totalgoals, team, page + 1) : totalgoals;

	}

}

public class TeamDataRetriver {
	public static void main(String[] args) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

		String team = bufferedReader.readLine();

		int year = Integer.parseInt(bufferedReader.readLine().trim());

		int result = Result.getTotalGoals("Barcelona", 2011);

		bufferedWriter.write(String.valueOf(result));
		bufferedWriter.newLine();

		bufferedReader.close();
		bufferedWriter.close();

	}
}
