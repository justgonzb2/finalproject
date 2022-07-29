package org.nanohttpd;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author justin
 */
public class mysqldatabase {

    private static final String ROOT = "root";
    private static final String PASSWORD = "password";

    private final String mysqlUrl;

    public mysqldatabase(String mysqlUrl) {
        this.mysqlUrl = mysqlUrl;
    }

    public temp getTemp(String id) {
        String select = "select * from temps where id = " + id;
        try ( Connection conn = DriverManager.getConnection(mysqlUrl, ROOT, PASSWORD)) {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            temp temp = new temp();
            while (resultSet.next()) {
                temp.setId(resultSet.getLong("ID"));
                temp.setTemp(resultSet.getInt("TEMP"));
            }
            return temp;
        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
        }
        return null;
    }

    public status getState() {
        String select = "select * from state";
        try ( Connection conn = DriverManager.getConnection(mysqlUrl, ROOT, PASSWORD)) {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            status state = new status();
            while (resultSet.next()) {
                String currentState = resultSet.getString("STATE");
                if (currentState != null) {
                    state.setOn(true);
                } else {
                    state.setOn(false);
                }
            }
            return state;
        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
        }
        return null;
    }

    public temp getTemperatureSetting(String setting) {
        String select = "select * from temps where setting = '" + setting + "'";
        try ( Connection conn = DriverManager.getConnection(mysqlUrl, ROOT, PASSWORD)) {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            temp temp = new temp();
            while (resultSet.next()) {
                temp.setId(resultSet.getLong("ID"));
                temp.setTemp(resultSet.getInt("TEMP"));
                temp.setTemp2(resultSet.getInt("TEMP2"));
                temp.setSetting(resultSet.getString("SETTING"));
            }
            return temp;
        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
        }
        return null;
    }

    public List<temp> getAllTemps() {
        List<temp> temps = new ArrayList<>();
        String select = "select * from temps";

        try ( Connection conn = DriverManager.getConnection(mysqlUrl, ROOT, PASSWORD)) {

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            while (resultSet.next()) {

                temp obj = new temp();
                obj.setId(resultSet.getLong("ID"));
                obj.setSetting(resultSet.getString("SETTING"));
                obj.setTemp(resultSet.getInt("TEMP"));
                obj.setTemp2(resultSet.getInt("TEMP2"));
                temps.add(obj);
            }

        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
        }
        return temps;
    }

    public String updateState(boolean value) {
        String update = null;

        if (value) {
            update = "update state set state = ''";
        } else {
            update = "update state set state = NULL";
        }

        try ( Connection conn = DriverManager.getConnection(mysqlUrl, ROOT, PASSWORD)) {
            Statement statement = (Statement) conn.createStatement();
            statement.execute(update);
        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
            return "Update state Failed\n";
        }

        return "Post state Failed\n";
    }

    public String addState(status state) {
        String insert = null;
        if (state.isOn()) {
            insert = "insert into state (state values ('')";
        } else {
            insert = "insert into state (state) values (NULL)";
        }

        try ( Connection conn = DriverManager.getConnection(mysqlUrl, ROOT, PASSWORD)) {
            Statement statement = (Statement) conn.createStatement();
            statement.execute(insert);
        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
            return "post didnt work\n";
        }

        return "";

    }

    public String updateTemp(temp temp) {
        String update = "update temps set temp = " +
                temp.getTemp() +
                ", temp2 = " +
                temp.getTemp2() +
                " where id = " +
                temp.getId();

        try ( Connection conn = DriverManager.getConnection(mysqlUrl, ROOT, PASSWORD)) {
            Statement statement = (Statement) conn.createStatement();
            statement.execute(update);
        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
            return "Post temp Failed\n";
        }
        return "Post temp Successful\n";
    }

    public String deleteTemp(String id) {
        String insert = "delete from temps where id = " + id;
        try ( Connection conn = DriverManager.getConnection(mysqlUrl, ROOT, PASSWORD)) {
            Statement statement = (Statement) conn.createStatement();
            statement.execute(insert);
        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
            return "Delete Failed\n";
        }
        return "Delete Successful\n";
    }

}
