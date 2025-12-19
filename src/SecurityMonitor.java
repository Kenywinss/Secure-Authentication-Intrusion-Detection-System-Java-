import java.util.HashMap;
import java.util.Map;

public class SecurityMonitor {
    private final Map<String, Integer> failed = new HashMap<>();
    private final int lockThreshold;

    public SecurityMonitor(int lockThreshold) {
        this.lockThreshold = lockThreshold;
    }

    public int recordFailure(String username) {
        int count = failed.getOrDefault(username, 0) + 1;
        failed.put(username, count);
        return count;
    }

    public void clearFailures(String username) {
        failed.remove(username);
    }

    public boolean shouldLock(String username) {
        return failed.getOrDefault(username, 0) >= lockThreshold;
    }
}
