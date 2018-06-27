/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ziekenhuisapplicatie;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author willem
 */
public class Frame {

    private Handler handler;
    private JSplitPane mainFrame;
    private JTable mainTable;
    JFrame jf = new JFrame();
    int rowCount;
    String dokter1;
    String dokter2;
    
    String patient1;
    String patient2;
    String patient3;
    String patient4;
           
    String afspraak1;
    String afspraak2;
    String afspraak3;
    String afspraak4;
    
    String deAfspraken;
    
    public Frame() throws Exception {

        Document document = getParameters();
        String conn = document.getElementsByTagName("ConnectString").item(0).getTextContent();
        String usr = document.getElementsByTagName("Username").item(0).getTextContent();
        String pwd = document.getElementsByTagName("Password").item(0).getTextContent();

        handler = new Handler(conn, usr, pwd);

        this.createMainView();
    }

    /**
     * Render the main frame
     */
    public void createMainView() {

        jf = new JFrame();

        jf.setSize(864, 576);
        jf.setTitle("Ziekenhuis Applicatie");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Left Sidebar
        JPanel OptionList = this.getQueryButtons();

        // Right Main area
        //JTable mainTable = this.getQueryToTable(handler.getUitDienstResult());
        mainFrame = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                OptionList, new JScrollPane(mainTable));

        jf.add(mainFrame);
        jf.setVisible(true);
    }

    public JPanel getQueryButtons() {
        JPanel list = new JPanel();
        list.setLayout(new GridLayout(25, 1));
        
        JButton[] buttons = new JButton[5];

        buttons[0] = new JButton("Schrijf dokter in");
        buttons[1] = new JButton("Schrijf patient in");
        buttons[2] = new JButton("Maak afspraak");
        buttons[3] = new JButton("Show afspraken");

        class ClickListener1 implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent event) {
                dokterFrame();
            }
        }
        
        class ClickListener2 implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent event) {
                patientFrame();
                jf.setVisible(false);
                createMainView();
            }
        }
        class ClickListener3 implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    afspraakFrame();
                } catch (SQLException ex) {
                    Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                }
                jf.setVisible(false);
                createMainView();

            }
        }
        class ClickListener4 implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent event) {
                
                try {
                    weergevenAfspraken();
                } catch (SQLException ex) {
                    Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        ActionListener cl1 = new ClickListener1();
        ActionListener cl2 = new ClickListener2();
        ActionListener cl3 = new ClickListener3();
        ActionListener cl4 = new ClickListener4();

        buttons[0].addActionListener(cl1);
        buttons[1].addActionListener(cl2);
        buttons[2].addActionListener(cl3);
        buttons[3].addActionListener(cl4);
        
        for (JButton i : buttons) {
            if (i != null) {
                list.add(i);
            }
        }
        
        return list;
    }

    /**
     * Gives a JTable with all table data
     *
     * @param rs
     * @return
     */
    public JTable getQueryToTable(ResultSet rs) {
        JTable table = null;
        Object[][] data = null;

        try {
            // metadata for column count
            ResultSetMetaData rsmd = rs.getMetaData();

            // count amount of rows
            rs.last();
            rowCount = rs.getRow();
            rs.beforeFirst();
            
            data = new Object[rowCount-1][rsmd.getColumnCount()];

            // get column names
            String[] columnNames = new String[rsmd.getColumnCount()];
            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                // add columns
                columnNames[i] = rsmd.getColumnName(i + 1);
                //data[0][i] = columnNames[i];
            }

            // add data
            System.out.println(rs.getMetaData());
            try {
                if (!rs.next()) {
                    System.out.println("No records found");
                } else {
                    int index = 0;

                    while (rs.next()) {
                        System.out.print("Record found: ");
                        if (index < rowCount) {
                            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                                data[index][i] = rs.getString(i + 1);

                                System.out.print(rs.getString(i + 1));
                                System.out.print(" ");
                            }
                            System.out.println("");
                            index++;
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("SQL Exception:");
                System.out.println(e.getMessage());
            }

            //System.out.println(rs.getArray(columnNames[0]));
            DefaultTableModel model = new DefaultTableModel(data, columnNames);

            table = new JTable(model);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return table;
    }

    /**
     * Source: https://stackoverflow.com/a/14968272
     *
     * Returns document containing DB parameters from the XML file
     *
     * @return Document
     */
    private static Document getParameters() throws ParserConfigurationException, SAXException, IOException {
        File parameters = new File("src\\ziekenhuisapplicatie\\DataParameters.xml");
        DocumentBuilderFactory documentBuilderFactory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder
                = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(parameters);

        return document;
    }
    
    public void dokterFrame(){
        JFrame dokterFrame = new JFrame("Dokter");
        dokterFrame.setSize(300, 150);
        JPanel panel = new JPanel();
        
        panel.setLayout(null);

        JLabel naamLabel = new JLabel("Naam");
        naamLabel.setBounds(10, 10, 80, 25);
        panel.add(naamLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(100, 10, 160, 25);
        panel.add(userText);

        JLabel afdelingLabel = new JLabel("Afdeling");
        afdelingLabel.setBounds(10, 40, 80, 25);
        panel.add(afdelingLabel);

        JTextField afdelingText = new JTextField(20);
        afdelingText.setBounds(100, 40, 160, 25);
        panel.add(afdelingText);

        JButton toevoegButton = new JButton("Toevoegen");
        toevoegButton.setBounds(10, 80, 80, 25);
        panel.add(toevoegButton);
        class ClickListener1 implements ActionListener {
            

            @Override
            public void actionPerformed(ActionEvent e) {
               dokter1 = userText.getText();
               dokter2 = afdelingText.getText();
               handler.addDokter();
               System.exit(0);
               createMainView();        
            }
        
        }
        ActionListener click1 = new ClickListener1();
        toevoegButton.addActionListener(click1);
        dokterFrame.add(panel);
        dokterFrame.setVisible(true);
    }
    
    public void patientFrame(){
        JFrame patientFrame = new JFrame("Patient");
        patientFrame.setSize(300, 300);
        JPanel panel = new JPanel();
        
        panel.setLayout(null);

        JLabel naamLabel = new JLabel("Naam");
        naamLabel.setBounds(10, 10, 80, 25);
        panel.add(naamLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(100, 10, 160, 25);
        panel.add(userText);

        JLabel adresLabel = new JLabel("Adres");
        adresLabel.setBounds(10, 40, 80, 25);
        panel.add(adresLabel);

        JTextField adresText = new JTextField(20);
        adresText.setBounds(100, 40, 160, 25);
        panel.add(adresText);
        
        JLabel woonplaatsLabel = new JLabel("Woonplaats");
        woonplaatsLabel.setBounds(10, 70, 80, 25);
        panel.add(woonplaatsLabel);

        JTextField woonplaatsText = new JTextField(20);
        woonplaatsText.setBounds(100, 70, 160, 25);
        panel.add(woonplaatsText);
        
        JLabel verzekeringLabel = new JLabel("Verzekering");
        verzekeringLabel.setBounds(10, 100, 80, 25);
        panel.add(verzekeringLabel);

        JTextField verzekeringText = new JTextField(20);
        verzekeringText.setBounds(100, 100, 160, 25);
        panel.add(verzekeringText);
        
        JButton toevoegButton = new JButton("Toevoegen");
        toevoegButton.setBounds(10, 140, 80, 25);
        panel.add(toevoegButton);
        class ClickListener1 implements ActionListener {
            

            @Override
            public void actionPerformed(ActionEvent e) {
               patient1 = userText.getText();
               patient2 = adresText.getText();
               patient3 = woonplaatsText.getText();
               patient4 = verzekeringText.getText();
               handler.addPatient();
               patientFrame.setVisible(false);
            }
        
        }
        ActionListener click2 = new ClickListener1();
        toevoegButton.addActionListener(click2);
        patientFrame.add(panel);
        patientFrame.setVisible(true);
    }
    
    public void afspraakFrame() throws SQLException{
        JFrame afspraakFrame = new JFrame("Afspraak");
        afspraakFrame.setSize(300, 300);
        JPanel panel = new JPanel();
        
        panel.setLayout(null);

        JLabel naamDokterLabel = new JLabel("Naam Dokter");
        naamDokterLabel.setBounds(10, 10, 80, 25);
        panel.add(naamDokterLabel);

        JComboBox<String> dokter = new JComboBox<String>(new Vector<String>(handler.dokterNamen()));
        dokter.setBounds(100, 10, 160, 25);
        System.out.println(dokter);
        panel.add(dokter);

        JLabel naamPatientLabel = new JLabel("Naam Patient");
        naamPatientLabel.setBounds(10, 40, 80, 25);
        panel.add(naamPatientLabel);

        JComboBox<String> patient = new JComboBox<String>(new Vector(handler.verkrijgNamen()));
        patient.setBounds(100, 40, 160, 25);
        panel.add(patient);
        
        JLabel datumLabel = new JLabel("Datum");
        datumLabel.setBounds(10, 70, 80, 25);
        panel.add(datumLabel);
        
        DateFormat format = new SimpleDateFormat("dd.mm.yyyy");
        
        JFormattedTextField datumText = new JFormattedTextField(format);
        datumText.setBounds(100, 70, 160, 25);
        panel.add(datumText);
        
        JLabel tijdLabel = new JLabel("Verzekering");
        tijdLabel.setBounds(10, 100, 80, 25);
        panel.add(tijdLabel);

        JTextField verzekeringText = new JTextField(20);
        verzekeringText.setBounds(100, 100, 160, 25);
        panel.add(verzekeringText);
        
        JButton toevoegButton = new JButton("Toevoegen");
        toevoegButton.setBounds(10, 140, 80, 25);
        panel.add(toevoegButton);
        class ClickListener1 implements ActionListener {
            

            @Override
            public void actionPerformed(ActionEvent e) {
               patient1 = dokter.getSelectedItem().toString();
               patient2 = patient.getSelectedItem().toString();
               patient3 = datumText.getText();
               patient4 = verzekeringText.getText();
               handler.maakAfspraak();
               afspraakFrame.setVisible(false);
            }
        
        }
        ActionListener click3 = new ClickListener1();
        toevoegButton.addActionListener(click3);
        afspraakFrame.add(panel);
        afspraakFrame.setVisible(true);
    }
    
    public void weergevenAfspraken() throws SQLException{
        JFrame afspraakFrame = new JFrame("Afspraak");
        afspraakFrame.setSize(300, 200);
        JPanel panel = new JPanel();
        
        panel.setLayout(null);

        JLabel naamDokterLabel = new JLabel("Naam Dokter");
        naamDokterLabel.setBounds(10, 10, 80, 25);
        panel.add(naamDokterLabel);
        
        ArrayList<String> arrayList = new ArrayList<>(handler.dokterNamen());
        String[] array = arrayList.toArray(new String[arrayList.size()]);
        JComboBox<String> dokter = new JComboBox<String>(array);
        dokter.setBounds(100, 10, 160, 25);
        panel.add(dokter);
        JButton toevoegButton = new JButton("Selecteren");
        toevoegButton.setBounds(10, 110, 80, 25);
        panel.add(toevoegButton);
        class ClickListener1 implements ActionListener {
            

            @Override
            public void actionPerformed(ActionEvent e) {
               deAfspraken = dokter.getSelectedItem().toString();
               mainTable = getQueryToTable(handler.showAfspraken());
               System.exit(0);
            }
        
        }
        ActionListener click4 = new ClickListener1();
        toevoegButton.addActionListener(click4);
        afspraakFrame.add(panel);
        afspraakFrame.setVisible(true);
    }
    
}
