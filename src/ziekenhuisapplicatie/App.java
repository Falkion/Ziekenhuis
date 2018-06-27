/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ziekenhuisapplicatie;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Gebruiker
 */
public class App {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
                String connectionString = "jdbc:sqlserver://localhost:1433;user=testAccount;password=Welcome";
                try {
                    Connection conn = DriverManager.getConnection(connectionString);
                    new Frame();
                } catch (SQLException e) {
                    System.out.print("Mislukt: ");
                    System.out.println(e.getMessage());
                } catch (Exception ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
}
