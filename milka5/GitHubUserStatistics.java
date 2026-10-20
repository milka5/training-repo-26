import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubUserStatistics {
    public static final String milka5 = System.getProperty("github.user", "octocat");

    public static void main(String[] args) {
        String username = (args != null && args.length > 0) ? args[0] : milka5;
        String apiUrl = "https://api.github.com/users/" + username;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) {
                System.out.println("Failed : HTTP error code : " + conn.getResponseCode());
                return;
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
            } finally {
                conn.disconnect();
            }

            String json = sb.toString();
            printStatistics(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printStatistics(String json) {
        String name = extractValue(json, "name");
        String publicRepos = extractValue(json, "public_repos");
        String followers = extractValue(json, "followers");
        String following = extractValue(json, "following");
        String createdAt = extractValue(json, "created_at");

        System.out.println("GitHub User Statistics:");
        System.out.println("Name: " + name);
        System.out.println("Public Repos: " + publicRepos);
        System.out.println("Followers: " + followers);
        System.out.println("Following: " + following);
        System.out.println("Account Created At: " + createdAt);
    }

    private static String extractValue(String json, String key) {
        String pattern = "\\\"" + Pattern.quote(key) + "\\\":(\\\"((?:\\\\.|[^\\\"\\\\])*)\\\"|\\d+|true|false|null)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(json);

        if (m.find()) {
            String value = m.group(1);
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1)
                        .replace("\\\"", "\"")
                        .replace("\\\\", "\\");
            }
            return value;
        }
        return "N/A";
    }
}

