/*
ophalen data van mensen in de active directory
select ad.Username, p.ContractEndDate
from AD.Export ad LEFT JOIN AfasProfit-Export p ON ad.Username_Pre2000 = p.EmployeeUsername
WHERE ad.Disabled = '0'

 */
package ziekenhuisapplicatie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class Handler {

    Connection conn;
    Frame frame;
    String connectString;
    String user;
    String pass;
    Statement stmt;
    String date = LocalDate.now().toString();

    public Handler(String connectString, String usr, String pwd) {
        String connectionString = connectString + ";" + usr + ";" + pwd;
        try {
            conn = DriverManager.getConnection(connectionString);
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            System.out.println("Verbinding Gemaakt");
        } catch (SQLException e) {
            System.out.print("Mislukt: ");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Executes query and returns ResultSet object
     *
     * @param stmt
     * @param query
     * @return
     */
    public ResultSet doQuery(String query) {
        ResultSet rs = null;

        System.out.println("Current Query:");
        System.out.println(query);
        try {
            rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return rs;
    }

    /**
     * Source: modified version of https://stackoverflow.com/a/35290713/2931464
     *
     * @param resultSet
     * @return
     * @throws SQLException
     */
    public int doResultSetCount(ResultSet resultSet) throws SQLException {
        try {
            int i = 0;
            while (resultSet.next()) {
                i++;
            }
            return i;
        } catch (SQLException e) {
            System.out.println("Error getting row count");
        }

        System.out.println("error:");
        resultSet.first();
        return 0;
    }

    /**
     * Signaal 1.2 Medewerker uit dienst in Profit, account is in AD actief
     *
     * @return
     */
    public ResultSet addDokter() {
        String query;

        query = "insert into [Dokter](Naam, Afdeling) " + "values" + "(" + frame.dokter1 + "," + frame.dokter2 + ")";
      
        System.out.println(query);
        return this.doQuery(query);
    }
    public ResultSet addPatient() {
        String query;

        query = "insert into '[Patient](Naam, Adres, Woonplaats, Verzekering) " 
                + "values" +  "(" + frame.patient1 + ","+  frame.patient2 + ","+ frame.patient3 + ","+ frame.patient4 + ")";
        System.out.println(query);
        return this.doQuery(query);
    }
    public ResultSet maakAfspraak() {
        String query;
        
        query = "insert into Dokter(NaamDokter, NaamPatient, Datum, Tijdstip) " 
                + "values (" + frame.afspraak1 + ","+ frame.afspraak2 + ","+ frame.afspraak3 + ","+ frame.afspraak4 + ") ";
        System.out.println(query);
        return this.doQuery(query);
    }
    public ResultSet showAfspraken() {
        String query;

        query = "select * "
                + "from Afspraken "
                + "where NaamDokter is " + frame.deAfspraken;
        System.out.println(query);
        return this.doQuery(query);
    }
    
    public ArrayList<String> dokterNamen() throws SQLException{
        ArrayList<String> namenDokter = new ArrayList<String>();
        ResultSet naamDokter = doQuery("select Naam from Dokter");
        
        while (naamDokter.next()) {
            namenDokter.add(naamDokter.getString("Naam"));
        }   
        return namenDokter;
    }
    
    public ArrayList<String> verkrijgNamen() throws SQLException{
        ArrayList<String> namenPatient = new ArrayList<String>();
        ResultSet naamPatient = doQuery("select Naam from Patient");
        
        while (naamPatient.next()) {
            namenPatient.add(naamPatient.getString(1));
        }   
        return namenPatient;
    }

}