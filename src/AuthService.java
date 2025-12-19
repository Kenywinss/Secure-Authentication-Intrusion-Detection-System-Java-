import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

public class AuthService {
    private static final String USERS_FILE = "data/users.txt";

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hashing error");
        }
    }

    public static Map<String, User> loadUsers() {
        ensureFiles();
        Map<String, User> users = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(USERS_FILE));
            for (String line : lines) {
                User u = User.fromLine(line.trim());
                if (u != null) users.put(u.getUsername(), u);
            }
        } catch (IOException ignored) {}
        return users;
    }

    public static void saveUsers(Map<String, User> users) {
        ensureFiles();
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(USERS_FILE))) {
            for (User u : users.values()) {
                bw.write(u.toLine());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not save users");
        }
    }

    public static boolean register(Map<String, User> users, String username, String password) {
        if (users.containsKey(username)) return false;
        String hash = hashPassword(password);
        users.put(username, new User(username, hash, false));
        saveUsers(users);
        LoggerUtil.log("REGISTER success: " + username);
        return true;
    }

    public static boolean authenticate(Map<String, User> users, String username, String password) {
        User u = users.get(username);
        if (u == null) return false;
        if (u.isLocked()) return false;

        String hash = hashPassword(password);
        return u.getPasswordHash().equals(hash);
    }

    private static void ensureFiles() {
        try {
            Files.createDirectories(Paths.get("data"));
            Files.createDirectories(Paths.get("logs"));
            if (!Files.exists(Paths.get(USERS_FILE))) Files.createFile(Paths.get(USERS_FILE));
            if (!Files.exists(Paths.get("logs/security.log"))) Files.createFile(Paths.get("logs/security.log"));
        } catch (IOException e) {
            throw new RuntimeException("File setup error");
        }
    }
}
