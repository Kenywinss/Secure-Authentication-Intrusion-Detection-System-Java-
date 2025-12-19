public class User {
    private final String username;
    private final String passwordHash;
    private boolean locked;

    public User(String username, String passwordHash, boolean locked) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.locked = locked;
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }

    // users.txt line format: username|hash|locked
    public String toLine() {
        return username + "|" + passwordHash + "|" + locked;
    }

    public static User fromLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 3) return null;
        return new User(parts[0], parts[1], Boolean.parseBoolean(parts[2]));
    }
}

