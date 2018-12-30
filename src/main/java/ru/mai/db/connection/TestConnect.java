package ru.mai.db.connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestConnect {
    private Statement statement = null;

    public void start() {
        try {
            DatabaseConnection.getInstance();
            statement = DatabaseConnection.getInstance().getConnection().createStatement();

            ResultSet rs = statement.executeQuery("select * from plain");
            while(rs.next()){
                System.out.println(rs.getString(2)+" "+rs.getString(3));
            }
        } catch (SQLException e) {
        }finally{
            try {
                DatabaseConnection.getInstance().getConnection().close();
                statement.close();
            } catch (SQLException e) {
            }
        }
    }

}

