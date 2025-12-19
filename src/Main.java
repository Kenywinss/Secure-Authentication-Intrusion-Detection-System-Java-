import java.util.Map;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    Map<String, User> users = AuthService.LoadUsers();
    SecurityMonitor monitor = new SecurityMonitor(3);

   While (True) {
      System.out.println("\n=== Secure Auth & Intrusion Detection ===");
            System.out.println("1) Register");
            System.out.println("2) Login");
            System.out.println("3) Exit");
            System.out.print("Choose: ");
     String choice = sc.NextLine().trim();

       if (choice.equals("1")) {
                System.out.print("New username: ");
                String u = sc.nextLine().trim();
                System.out.print("New password: ");
                String p = sc.nextLine();

                boolean ok = AuthService.register(users, u, p);
                System.out.println(ok ? "Registered successfully." : "Username already exists.");
            } else if (choice.equals("2")) {
                System.out.print("Username: ");
                String u = sc.nextLine().trim();
                System.out.print("Password: ");
                String p = sc.nextLine();

                User user = users.get(u);
                if (user == null) {
                    LoggerUtil.log("LOGIN failed (unknown user): " + u);
                    System.out.println("Invalid credentials.");
                    continue;
                }

                if (user.isLocked()) {
                    LoggerUtil.log("LOGIN blocked (locked): " + u);
                    System.out.println("Account is locked. Contact admin.");
                    continue;
                }

                boolean ok = AuthService.authenticate(users, u, p);
                if (ok) {
                    monitor.clearFailures(u);
                    LoggerUtil.log("LOGIN success: " + u);
                    System.out.println("Login successful. Welcome, " + u + "!");
                } else {
                    int attempts = monitor.recordFailure(u);
                    LoggerUtil.log("LOGIN failed (" + attempts + "): " + u);

                    if (monitor.shouldLock(u)) {
                        user.setLocked(true);
                        AuthService.saveUsers(users);
                        LoggerUtil.log("ACCOUNT LOCKED: " + u);
                        System.out.println("Too many failed attempts. Account locked.");
                    } else {
                        System.out.println("Invalid credentials. Attempts: " + attempts + "/3");
                    }
                }
            } else if (choice.equals("3")) {
                System.out.println("Goodbye.");
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }

        sc.close();
    }
}
