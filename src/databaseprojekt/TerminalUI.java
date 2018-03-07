/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databasesproject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 *
 * @author me
 */
public class TerminalUI {
    private static CSDatabase db;
    private static final Scanner sc = new Scanner(System.in);
    
    private static void printMenu() throws SQLException {
        System.out.println("Choose a functionality by entering a letter:");
        System.out.println("  a) List all coaches and the team they belong to");
        System.out.println("  b) List all players who have won at least 1 tournament");
        System.out.println("  c) List all teams and the number of players on them");
        System.out.println("  d) List all tournaments with at least X teams participating");
        System.out.print("Enter [abcd]: ");
        String input = sc.next();
        switch (input.toLowerCase()) {
            case "a":
                ResultSet coaches = db.getCoaches();
                printTable(coaches);
                break;
            case "b":
                ResultSet winners = db.getWinningPlayers();
                printTable(winners);
                break;
            case "c":
                ResultSet teams = db.getTeamsAndPlayers();
                printTable(teams);
                break;
            case "d":
                System.out.print("Enter X: ");
                int numParticipants = sc.nextInt();
                System.out.println("");
                printTable(db.getTournaments(numParticipants));
                break;
            default:
                System.out.println("");
                printMenu();
        }
    }
    
    /**
     * Prints a ResultSet as a table
     */
    private static void printTable(ResultSet data) {
        try {
            ResultSetMetaData meta = data.getMetaData();
            int colCount = meta.getColumnCount();
            int[] colSizes = new int[colCount];
            ArrayList<String[]> rows = new ArrayList<>();
            // compute column sizes
            for (int i = 0; i < colCount; ++i) {
                colSizes[i] = meta.getColumnLabel(i + 1).length();
            }
            while (data.next()) {
                String[] values = new String[colCount];
                for (int i = 0; i < colCount; ++i) {
                    String val = data.getString(i + 1);
                    values[i] = val;
                    colSizes[i] = Integer.max(val.length(), colSizes[i]);
                }
                rows.add(values);
            }
            // print headers
            String[] headers = new String[colCount];
            String[] separators = new String[colCount];
            for (int i = 0; i < colCount; ++i) {
                headers[i] = meta.getColumnLabel(i + 1);
                separators[i] = String.join("", Collections.nCopies(colSizes[i], "="));
            }
            System.out.println(stringifyRow(headers, colSizes));
            System.out.println(stringifyRow(separators, colSizes));
            // print data
            for (String[] row : rows) {
                System.out.println(stringifyRow(row, colSizes));
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
    }
    
    /**
     * Turns a string array representing a row into a single string
     */
    private static String stringifyRow(String[] row, int[] colSizes) {
        String outp = "";
        for (int i = 0; i < row.length; ++i) {
            if (i != 0) { outp += " | "; }
            outp += String.format(
                    "%1$-"+colSizes[i]+"s",
                    row[i]
            );
        }
        return outp;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            db = new CSDatabase(
                    CSDatabase.DB_URL,
                    CSDatabase.DB_USER,
                    CSDatabase.DB_PASS
            );
            printMenu();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.print(e);
        }
        
    }
}
