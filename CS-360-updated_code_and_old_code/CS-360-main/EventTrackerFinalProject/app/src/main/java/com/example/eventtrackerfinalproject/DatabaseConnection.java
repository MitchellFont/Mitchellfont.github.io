package com.example.eventtrackerfinalproject;

import android.os.AsyncTask;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String TAG = "DatabaseConnection";
    
    public interface ConnectionCallback {
        void onConnectionSuccess(Connection connection);
        void onConnectionError(String error);
    }
    
    public static void getConnection(ConnectionCallback callback) {
        new AsyncTask<Void, Void, Connection>() {
            @Override
            protected Connection doInBackground(Void... voids) {
                try {
                    Class.forName("org.postgresql.Driver");
                    return DriverManager.getConnection(
                            Constants.DB_URL,
                            Constants.DB_USER,
                            Constants.DB_PASSWORD);
                } catch (ClassNotFoundException | SQLException e) {
                    Log.e(TAG, "Connection error", e);
                    return null;
                }
            }
            
            @Override
            protected void onPostExecute(Connection connection) {
                if (connection != null) {
                    callback.onConnectionSuccess(connection);
                } else {
                    callback.onConnectionError("Failed to connect to database");
                }
            }
        }.execute();
    }
}
