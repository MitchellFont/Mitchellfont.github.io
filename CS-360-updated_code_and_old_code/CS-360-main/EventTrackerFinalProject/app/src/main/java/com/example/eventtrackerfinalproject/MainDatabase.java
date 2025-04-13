package com.example.eventtrackerfinalproject;

import android.content.Context;
import android.widget.Toast;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MainDatabase {
    private Context context;
    
    public MainDatabase(Context context) {
        this.context = context;
        initializeDatabase();
    }
    
    private void initializeDatabase() {
        DatabaseConnection.getConnection(new DatabaseConnection.ConnectionCallback() {
            @Override
            public void onConnectionSuccess(Connection connection) {
                try {
                    Statement stmt = connection.createStatement();
                    String sql = "CREATE TABLE IF NOT EXISTS events (" +
                            "id SERIAL PRIMARY KEY, " +
                            "title TEXT NOT NULL, " +
                            "description TEXT, " +
                            "date TEXT NOT NULL, " +
                            "time TEXT NOT NULL, " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "views INTEGER DEFAULT 0)";
                    stmt.executeUpdate(sql);
                    
                    // Create analytics table
                    sql = "CREATE TABLE IF NOT EXISTS event_analytics (" +
                            "id SERIAL PRIMARY KEY, " +
                            "event_id INTEGER REFERENCES events(id), " +
                            "action TEXT NOT NULL, " +
                            "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
                    stmt.executeUpdate(sql);
                    
                    stmt.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            @Override
            public void onConnectionError(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    public void addReminder(Reminder reminder, DatabaseOperationCallback callback) {
        DatabaseConnection.getConnection(new DatabaseConnection.ConnectionCallback() {
            @Override
            public void onConnectionSuccess(Connection connection) {
                try {
                    String sql = "INSERT INTO events (title, description, date, time) VALUES (?, ?, ?, ?)";
                    PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    pstmt.setString(1, reminder.getTitle());
                    pstmt.setString(2, reminder.getDescription());
                    pstmt.setString(3, reminder.getDate());
                    pstmt.setString(4, reminder.getTime());
                    
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows > 0) {
                        ResultSet rs = pstmt.getGeneratedKeys();
                        if (rs.next()) {
                            long id = rs.getLong(1);
                            callback.onSuccess(id);
                        }
                    }
                    pstmt.close();
                    connection.close();
                } catch (SQLException e) {
                    callback.onError(e.getMessage());
                }
            }
            
            @Override
            public void onConnectionError(String error) {
                callback.onError(error);
            }
        });
    }
    
    public void getAllReminders(ReminderListCallback callback) {
        DatabaseConnection.getConnection(new DatabaseConnection.ConnectionCallback() {
            @Override
            public void onConnectionSuccess(Connection connection) {
                try {
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM events ORDER BY date, time");
                    
                    List<Reminder> reminders = new ArrayList<>();
                    while (rs.next()) {
                        Reminder reminder = new Reminder(
                                String.valueOf(rs.getInt("id")),
                                rs.getString("title"),
                                rs.getString("date"),
                                rs.getString("description"),
                                rs.getString("time")
                        );
                        reminders.add(reminder);
                    }
                    
                    rs.close();
                    stmt.close();
                    connection.close();
                    callback.onSuccess(reminders);
                } catch (SQLException e) {
                    callback.onError(e.getMessage());
                }
            }
            
            @Override
            public void onConnectionError(String error) {
                callback.onError(error);
            }
        });
    }
    
    public void updateReminder(String id, String title, String description, String date, String time, DatabaseOperationCallback callback) {
        DatabaseConnection.getConnection(new DatabaseConnection.ConnectionCallback() {
            @Override
            public void onConnectionSuccess(Connection connection) {
                try {
                    String sql = "UPDATE events SET title = ?, description = ?, date = ?, time = ? WHERE id = ?";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, title);
                    pstmt.setString(2, description);
                    pstmt.setString(3, date);
                    pstmt.setString(4, time);
                    pstmt.setInt(5, Integer.parseInt(id));
                    
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows > 0) {
                        callback.onSuccess(1);
                    } else {
                        callback.onError("No rows affected");
                    }
                    pstmt.close();
                    connection.close();
                } catch (SQLException e) {
                    callback.onError(e.getMessage());
                }
            }
            
            @Override
            public void onConnectionError(String error) {
                callback.onError(error);
            }
        });
    }
    
    public void deleteReminder(String id, DatabaseOperationCallback callback) {
        DatabaseConnection.getConnection(new DatabaseConnection.ConnectionCallback() {
            @Override
            public void onConnectionSuccess(Connection connection) {
                try {
                    String sql = "DELETE FROM events WHERE id = ?";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setInt(1, Integer.parseInt(id));
                    
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows > 0) {
                        callback.onSuccess(1);
                    } else {
                        callback.onError("No rows affected");
                    }
                    pstmt.close();
                    connection.close();
                } catch (SQLException e) {
                    callback.onError(e.getMessage());
                }
            }
            
            @Override
            public void onConnectionError(String error) {
                callback.onError(error);
            }
        });
    }
    
    public void logEventAction(String eventId, String action) {
        DatabaseConnection.getConnection(new DatabaseConnection.ConnectionCallback() {
            @Override
            public void onConnectionSuccess(Connection connection) {
                try {
                    String sql = "INSERT INTO event_analytics (event_id, action) VALUES (?, ?)";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setInt(1, Integer.parseInt(eventId));
                    pstmt.setString(2, action);
                    pstmt.executeUpdate();
                    pstmt.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            @Override
            public void onConnectionError(String error) {
                // Silent fail for analytics
            }
        });
    }
    
    public void getEventAnalytics(AnalyticsCallback callback) {
        DatabaseConnection.getConnection(new DatabaseConnection.ConnectionCallback() {
            @Override
            public void onConnectionSuccess(Connection connection) {
                try {
                    // Get most viewed events
                    String sql = "SELECT e.title, COUNT(a.id) as views " +
                            "FROM events e LEFT JOIN event_analytics a ON e.id = a.event_id " +
                            "WHERE a.action = 'view' " +
                            "GROUP BY e.title " +
                            "ORDER BY views DESC " +
                            "LIMIT 5";
                    
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    
                    List<AnalyticsData> analytics = new ArrayList<>();
                    while (rs.next()) {
                        analytics.add(new AnalyticsData(
                                rs.getString("title"),
                                rs.getInt("views")
                        ));
                    }
                    
                    // Get most common actions
                    sql = "SELECT action, COUNT(*) as count " +
                            "FROM event_analytics " +
                            "GROUP BY action " +
                            "ORDER BY count DESC";
                    
                    rs = stmt.executeQuery(sql);
                    List<ActionCount> actions = new ArrayList<>();
                    while (rs.next()) {
                        actions.add(new ActionCount(
                                rs.getString("action"),
                                rs.getInt("count")
                        ));
                    }
                    
                    rs.close();
                    stmt.close();
                    connection.close();
                    callback.onSuccess(analytics, actions);
                } catch (SQLException e) {
                    callback.onError(e.getMessage());
                }
            }
            
            @Override
            public void onConnectionError(String error) {
                callback.onError(error);
            }
        });
    }
    
    public interface DatabaseOperationCallback {
        void onSuccess(long result);
        void onError(String error);
    }
    
    public interface ReminderListCallback {
        void onSuccess(List<Reminder> reminders);
        void onError(String error);
    }
    
    public interface AnalyticsCallback {
        void onSuccess(List<AnalyticsData> analytics, List<ActionCount> actions);
        void onError(String error);
    }
    
    public static class AnalyticsData {
        public String eventTitle;
        public int views;
        
        public AnalyticsData(String eventTitle, int views) {
            this.eventTitle = eventTitle;
            this.views = views;
        }
    }
    
    public static class ActionCount {
        public String action;
        public int count;
        
        public ActionCount(String action, int count) {
            this.action = action;
            this.count = count;
        }
    }
}
