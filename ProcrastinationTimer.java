import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class ProcrastinationTimer {

    private static long lastActivityTime;
    private static final int IDLE_THRESHOLD = 3000;  // 3 seconds for testing real-time detection
    private static Timer inactivityTimer = new Timer();
    private static Robot robot;

    public static void main(String[] args) {
        try {
            // Initialize the Robot class to track global mouse position
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        lastActivityTime = System.currentTimeMillis();

        // Set up timer to check for inactivity every 1 second
        inactivityTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkInactivity();
                trackMousePosition();  // Track mouse position globally
            }
        }, 0, 1000);  // Check every 1 second
    }

    // Track and print the current mouse position
    private static void trackMousePosition() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point location = pointerInfo.getLocation();
        System.out.println("Mouse Coordinates: X=" + location.x + " Y=" + location.y);
        updateLastActivity();  // Consider mouse movement as activity
    }

    // Update last activity time
    private static void updateLastActivity() {
        lastActivityTime = System.currentTimeMillis();
    }

    // Check if user has been inactive for too long
    private static void checkInactivity() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastActivityTime;

        if (elapsedTime >= IDLE_THRESHOLD) {
            showMotivationalMessage();
            lastActivityTime = currentTime; // Reset after showing message
        }
    }

    // Show a motivational or sarcastic message
    private static void showMotivationalMessage() {
        String[] messages = {
            "Another break? At this point, you might as well make it a hobby.",
            "Are you working, or just pretending?",
            "Procrastination level: Expert. If only you could get an Award for this.",
            "Working hard or hardly working? Oh, right, hardly working.",
            "Look at you go! Straight to nowhere. Keep it up!",
            "I hope procrastination is on your resume, because you're absolutely crushing it."
        };

        String message = messages[(int) (Math.random() * messages.length)];
        System.out.println("Inactivity detected: " + message);
    }
}
