import com.cv.watson.Handler;

import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {
        String apiKey = "";
        Handler handler = new Handler(apiKey);
        String projectPath = System.getProperty("user.dir");
        String[] positiveDirectoryPath = {
                projectPath + "Your Positive Directory Paths"
        };
        String negativeDirectoryPath = projectPath + "Your Negative Directory Path";
        Map<String, Map<Double, Double>> map = handler.handle(negativeDirectoryPath, positiveDirectoryPath);
    }
}
