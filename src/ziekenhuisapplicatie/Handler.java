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
            this.stmt = conn.createStatement();
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
    public void addDokter(String naam, String afdeling) throws SQLException {
        stmt = conn.createStatement();
        stmt.executeUpdate("insert into Dokter (Naam, Afdeling) " + "values('" + naam + "','" + afdeling + "')");
         
    }
    public void addPatient(String naam, String adres, String woonplaats, String verzekering) throws SQLException {
        String query;

        query = "insert into Patient (Naam, Adres, Woonplaats, Verzekering) " 
                + "values " +  "('" + naam + "','"+  adres + "','"+ woonplaats + "','"+ verzekering + "')";
       
        stmt = conn.createStatement();
        stmt.executeUpdate(query);
    }
    public void maakAfspraak(String naam1, String naam2, String datum, String tijdstip) throws SQLException {
        String query;
        
        query = "insert into Afspraken (NaamDokter, NaamPatient, Datum, Time) " 
                + "values ('" + naam1 + "','"+ naam2 + "','"+ datum + "','"+ tijdstip + "') ";
        stmt = conn.createStatement();
        stmt.executeUpdate(query);
    }
    public ResultSet showAfspraken(String naam) {
        String query;
        query = "SELECT NaamPatient, Time  FROM Afspraken  WHERE NaamDokter LIKE  '" + naam + "% '";
        ResultSet set = doQuery(query);
        return set;
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