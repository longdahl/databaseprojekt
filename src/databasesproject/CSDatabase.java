/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databasesproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author me
 */
public class CSDatabase {
    protected static final String DB_URL = "jdbc:postgresql://stampy.db.elephantsql.com:5432/zcqmkjrl";
    protected static final String DB_USER = "zcqmkjrl";
    protected static final String DB_PASS = "waeDTsPXtvSV-zkz-S-mNlCV371Beryl";
    
    private final Connection connection;
    private final PreparedStatement coachesStatement;
    private final PreparedStatement winnersStatement;
    private final PreparedStatement teamsStatement;
    private final PreparedStatement tournamentsStatement;
    
    public CSDatabase(String dbUrl, String dbUser, String dbPass)
            throws ClassNotFoundException, SQLException
    {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
        coachesStatement = connection.prepareStatement(
                "SELECT people.name, teams.name AS team " +
                        "FROM people " +
                        "INNER JOIN coaches ON (people.id = people_id) " +
                        "INNER JOIN teams ON (team_id = teams.id)"
        );
        winnersStatement = connection.prepareStatement(
                "SELECT name FROM people WHERE EXISTS (" +
                    "SELECT 1 FROM " +
                        "(SELECT * FROM playsOn UNION SELECT * FROM coaches) AS peopleTeamRelation " +
                    "WHERE EXISTS (" +
                        "SELECT 1 FROM winners WHERE winners.team_id = peopleTeamRelation.team_id" +
                    ") AND people_id = people.id" +
                ");"
        );
        teamsStatement = connection.prepareStatement(
                "SELECT name, COUNT(people_id) AS num_players " +
                        "FROM teams " +
                            "INNER JOIN playsOn ON (id = team_id) "+
                        "GROUP BY name"
        );             
        tournamentsStatement = connection.prepareStatement(
                "SELECT name FROM tournaments " +
                        "INNER JOIN participatesIn ON (tournament_id = id) " +
                        "GROUP BY name HAVING COUNT(team_id) >= ?"
        );
    }
    
    /**
     * Gets the name of every coach, and the team they are coaching
     * @return A ResultSet consisting of two columns, name and team name
     * @throws SQLException 
     */
    public ResultSet getCoaches() throws SQLException {
        return coachesStatement.executeQuery();
    }
    
    /**
     * Gets the name of every player that has won at least 1 tournament
     * @return A ResultSet consisting of a single column, name
     * @throws SQLException 
     */
    public ResultSet getWinningPlayers() throws SQLException {
        return winnersStatement.executeQuery();
    }
    
    /**
     * Gets the name of every team, and the number of players on the team
     * @return A ResultSet consisting of two columns, name and number of players
     * @throws SQLException 
     */
    public ResultSet getTeamsAndPlayers() throws SQLException {
        return teamsStatement.executeQuery();
    }
    
    /**
     * Gets the name of every tournament that has at least numParticipants participants
     * @param numParticipants
     * @return A ResultSet consisting of a single column, the name of the tournaments
     * @throws SQLException
     */
    public ResultSet getTournaments(int numParticipants) throws SQLException {
        tournamentsStatement.setInt(1, numParticipants);
        return tournamentsStatement.executeQuery();
    }
}
