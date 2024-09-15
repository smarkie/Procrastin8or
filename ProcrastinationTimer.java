import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Procrastin8orApp {

    private static long lastActivityTime;
    private static final int WORK_DURATION = 1500000;  // 25 minutes in milliseconds
    private static final int BREAK_DURATION = 300000;  // 5 minutes in milliseconds
    private static Timer pomodoroTimer = new Timer();
    private static boolean isWorkTime = true;

    private static long totalActiveTime = 0;
    private static long totalIdleTime = 0;
    
    private static JTextArea logArea;

    public static void main(String[] args) {
        // Simplified without 'i' feature
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    // Create the main GUI
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Procrastin8or - Productivity Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 1));

        // Pomodoro Timer Panel
        JPanel pomodoroPanel = new JPanel();
        JLabel pomodoroLabel = new JLabel("Pomodoro Timer: 25:00");
        JButton startPomodoroButton = new JButton("Start");
        JButton resetPomodoroButton = new JButton("Reset");
        pomodoroPanel.add(pomodoroLabel);
        pomodoroPanel.add(startPomodoroButton);
        pomodoroPanel.add(resetPomodoroButton);

        // Productivity Tracker Panel
        JPanel productivityPanel = new JPanel();
        JLabel productivityLabel = new JLabel("Total Active Time: 0s, Total Idle Time: 0s");
        productivityPanel.add(productivityLabel);
        
        // Mouse Tracking Panel
        JPanel mousePanel = new JPanel();
        JLabel mouseLabel = new JLabel("Mouse Coordinates: X=0, Y=0");
        mousePanel.add(mouseLabel);

        // Session Log Panel
        JPanel logPanel = new JPanel();
        logArea = new JTextArea(5, 30);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        logPanel.add(scrollPane);

        // Add panels to main frame
        mainPanel.add(pomodoroPanel);
        mainPanel.add(productivityPanel);
        mainPanel.add(mousePanel);
        mainPanel.add(logPanel);
        frame.add(mainPanel);

        // Set up Pomodoro functionality
        startPomodoroButton.addActionListener(e -> startPomodoro(pomodoroLabel));
        resetPomodoroButton.addActionListener(e -> resetPomodoro(pomodoroLabel));

        // Set up mouse tracking
        Timer mouseTracker = new Timer();
        mouseTracker.schedule(new TimerTask() {
            @Override
            public void run() {
                Point location = MouseInfo.getPointerInfo().getLocation();
                mouseLabel.setText("Mouse Coordinates: X=" + location.x + " Y=" + location.y);
                updateLastActivity();
            }
        }, 0, 1000);  // Update every second

        // Set up activity/inactivity tracker
        Timer activityTracker = new Timer();
        activityTracker.schedule(new TimerTask() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - lastActivityTime;

                if (elapsedTime >= 5000) {  // 5 seconds threshold for idle time
                    totalIdleTime += elapsedTime;
                    logSession("Idle detected: " + (elapsedTime / 1000) + " seconds");
                    lastActivityTime = currentTime;
                } else {
                    totalActiveTime += elapsedTime;
                }

                // Update productivity panel
                productivityLabel.setText("Total Active Time: " + totalActiveTime / 1000 + "s, Total Idle Time: " + totalIdleTime / 1000 + "s");
                lastActivityTime = currentTime;
            }
        }, 0, 1000);  // Check every second

        frame.setVisible(true);
    }

    private static void startPomodoro(JLabel pomodoroLabel) {
        TimerTask task = new TimerTask() {
            int secondsRemaining = isWorkTime ? WORK_DURATION / 1000 : BREAK_DURATION / 1000;
            @Override
            public void run() {
                secondsRemaining--;
                int minutes = secondsRemaining / 60;
                int seconds = secondsRemaining % 60;
                pomodoroLabel.setText(String.format("Pomodoro Timer: %02d:%02d", minutes, seconds));
                
                if (secondsRemaining <= 0) {
                    isWorkTime = !isWorkTime;
                    this.cancel();
                    logSession(isWorkTime ? "Work session started." : "Break session started.");
                }
            }
        };
        pomodoroTimer.schedule(task, 0, 1000);
        logSession("Pomodoro session started.");
    }

    private static void resetPomodoro(JLabel pomodoroLabel) {
        pomodoroTimer.cancel();
        pomodoroTimer = new Timer();
        isWorkTime = true;
        pomodoroLabel.setText("Pomodoro Timer: 25:00");
        logSession("Pomodoro timer reset.");
    }

    private static void updateLastActivity() {
        lastActivityTime = System.currentTimeMillis();
    }

    private static void logSession(String event) {
        logArea.append(event + "\n");
        try (FileWriter writer = new FileWriter("session_log.txt", true)) {
            writer.write(new java.util.Date() + " - " + event + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
