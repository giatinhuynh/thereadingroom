package com.thereadingroom.model.dao;

import com.thereadingroom.model.dao.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * BaseDAO provides common database operations for all DAOs in the application.
 * It encapsulates the basic methods for interacting with the database, ensuring consistent handling of connections.
 */
public abstract class BaseDAO {

    /**
     * Execute a generic SQL update statement, such as INSERT, UPDATE, or DELETE.
     * This method handles setting parameters and executing the update.
     *
     * @param sql    The SQL statement to execute.
     * @param params Parameters to be set in the prepared statement.
     * @return boolean indicating if the operation was successful (true if rows affected > 0)
     */
    protected boolean executeUpdate(String sql, Object... params) {
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set parameters for the prepared statement
            setPreparedStatementParams(pstmt, params);

            // Execute the update and check if rows were affected
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;  // Return true if rows were updated
        } catch (SQLException e) {
            System.out.println("Error executing update: " + e.getMessage());
            return false;  // Return false in case of an error
        }
    }

    /**
     * Execute a SQL query and map the ResultSet to a desired object type using the provided mapper function.
     *
     * @param sql    The SQL query to execute.
     * @param mapper Function to map the ResultSet to the desired return type.
     * @param params Parameters to be set in the prepared statement.
     * @param <T>    The return type mapped from the ResultSet.
     * @return The result of the query, mapped to the desired type.
     */
    protected <T> T executeQuery(String sql, Function<ResultSet, T> mapper, Object... params) {
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set parameters for the prepared statement
            setPreparedStatementParams(pstmt, params);

            // Execute the query and map the result set using the provided function
            try (ResultSet rs = pstmt.executeQuery()) {
                return mapper.apply(rs);  // Apply the mapper function to the ResultSet
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
            return null;  // Return null in case of an error
        }
    }

    /**
     * Execute a batch update using the provided SQL and a list of parameter arrays.
     * This is useful for inserting or updating multiple rows at once in a single operation.
     *
     * @param sql        The SQL statement to execute.
     * @param paramsList A list of parameter arrays, where each array corresponds to the parameters for one statement.
     * @return boolean indicating if the batch operation was successful (true if executed without errors).
     */
    protected boolean executeBatchUpdate(String sql, Iterable<Object[]> paramsList) {
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Loop through each set of parameters and add them to the batch
            for (Object[] params : paramsList) {
                setPreparedStatementParams(pstmt, params);
                pstmt.addBatch();
            }

            // Execute the batch of updates
            pstmt.executeBatch();
            return true;  // Return true if batch execution was successful
        } catch (SQLException e) {
            System.out.println("Error executing batch update: " + e.getMessage());
            return false;  // Return false in case of an error
        }
    }

    /**
     * Set the parameters for a prepared statement.
     * This method abstracts the repetitive process of setting prepared statement parameters.
     *
     * @param pstmt  The prepared statement to set parameters on.
     * @param params The parameters to be set.
     * @throws SQLException in case of any SQL errors during parameter setting.
     */
    protected void setPreparedStatementParams(PreparedStatement pstmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);  // Set each parameter in the statement
        }
    }
}
