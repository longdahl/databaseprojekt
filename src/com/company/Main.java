package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class Main {
    private static String url;
    private static String username;
    private static String password;



    public static void main(String[] args) {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (java.lang.ClassNotFoundException e) {
            System.out.println(e);
        }


        url = "jdbc:postgresql://stampy.db.elephantsql.com:5432/zcqmkjrl";
        username = "zcqmkjrl";
        password = "waeDTsPXtvSV-zkz-S-mNlCV371Beryl";

        String answer = JOptionPane.showInputDialog(null, "Press 1 display your options");
        if(Integer.parseInt(answer) == 1){
            System.out.println("Press a for: List of all names of coaches and the team they belong to.");
            System.out.println("Press b for: List of all names of people (players and coachers) who are on a team, which has won at least one tournament.");
            System.out.println("Press c for: List of all names of teams and the number of players on that team.");
            System.out.println("Input any positive integer to get a list of the names of all tournaments, with at least that many participating teams");
        }
        String answer2 = JOptionPane.showInputDialog(null, "Press 1 display your options");
        if(answer2.equals("a")){
            System.out.println("Coaches | teams");
            String statement = "SELECT people.name, teams.name as team_name FROM people INNER JOIN coaches On(people.id = people_id) INNER JOIN teams On(coaches.team_id = teams.id)";
            getData(statement,2);

        }
        else if(answer2.equals("b")){
            System.out.println("players/coaches | teams_who_won");
            String statement = "Select name FROM people INNER JOIN playsOn On(id = playsOn.people_id) WHERE(playsOn.team_id IN (SELECT teams.id FROM teams,winners WHERE teams.id = winners.team_id)) UNION Select name FROM people INNER JOIN coaches On(id = coaches.people_id) WHERE(coaches.team_id IN (SELECT teams.id FROM teams,winners WHERE teams.id = winners.team_id))";
            getData(statement,1);
        }
        else if(answer2.equals("c")){
            System.out.println("team_name | number_of_players_on_team" );
            String statement = "SELECT name, count(*) FROM teams INNER JOIN playsOn On(id = team_id) GROUP BY NAME";
            getData(statement,2);
        }
        else if(Integer.parseInt(answer2) >= 0){
            System.out.println("tournament_name | number_of_participating_teams");
            String statement = "SELECT name,count(team_id) FROM participatesin INNER JOIN tournaments On(tournament_id = tournaments.id) GROUP BY name HAVING count(team_id) >=" + answer2;
            System.out.println(statement);
            getData(statement,2);
        }
        else{
            System.out.println("Input does not match the specfied criteria, please relaunch the application");
        }

    }
    private static void getData(String statement,int col){
        try {
            Connection db = DriverManager.getConnection(url, username, password);


            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery(statement);
            while (rs.next()) {

                if (col == 2){
                    System.out.print(rs.getString(1) + " | ");
                }
                System.out.println(rs.getString(col) + " ");
            }
            rs.close();
            st.close();


        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
