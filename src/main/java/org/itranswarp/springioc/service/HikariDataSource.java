package org.itranswarp.springioc.service;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Logger;
@Component
public  class HikariDataSource implements DataSource {
    private String jdbcUrl = "jdbc:mysql://cdb-jqb0laoy.cd.tencentcdb.com:10017/test1";
    private String userName = "lean";
    private String password = "123456";

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    private int maximumPoolSize;
    private boolean autoCommit;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public void query(String sql, Object[] args) throws SQLException{

        try(Connection connection =  DriverManager.getConnection(jdbcUrl, userName, password)){
            try(PreparedStatement ps = connection.prepareStatement(sql)){
                int index = 1;
                for(Object obj : args){
                    ps.setObject(index, args[index-1]);
                    index++;
                }
                try(ResultSet result = ps.executeQuery()){
                    while (result.next()){
                        int id = result.getInt("Id");
                        String username = result.getString("name");
                        System.err.println("Id:" + id + ",username:" + username);
                    }
                }
            }
        }

    }
}
