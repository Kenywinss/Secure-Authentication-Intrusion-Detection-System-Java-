import java.io.FileWriter;
import java.time.LocalDateTime;

public class LoggerUtil {
    public static void log(String message) {
        try (FileWriter fw = new FileWriter("logs/security.log", true)) {
            fw.write(LocalDateTime.now() + " - " + message + System.lineSeparator());
        } catch (Exception e) {
            System.out.println("Logging failed: " + e.getMessage());
        }
    }
}
