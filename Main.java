

import  javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;
import javax.swing.table.*;

public class Main extends JFrame {
    JLabel lb1, lb2, lb3, lb4, lb5, lb6;
    JTextField txtRoom, txtName, txtDep, txtDate, txtTotal, txtAmount;
    JButton btnLend, btnBro, btnReset, btnDel;
    JTable table;
    DefaultTableModel dtm;
    private static Connection con = null;

    Main() {
        super(System.getProperty("user.name") + "!");
        inti_components();
        set_row_from_database();
        addlistners();
        setSize(700, 350);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
    }
     private  boolean valid_operattion(){
        if(txtAmount.getText()== " ") return  false;
        if(txtName.getText() == " ") return  false;
        if(txtRoom.getText() == " ") return  false;
        if(txtDate.getText() == " ") return  false;
        if(txtDep.getText()  == " ") return  false;
        return  true;
     }
     private void update_data(String [] data,int id){
        try{
             if( con == null ) create_connection();
             String room = data[1];
             int x = Integer.parseInt(table.getValueAt(id,4).toString());
             x += Integer.parseInt(data[4]);
             String sql = "UPDATE LD SET Amount = " + x+ " WHERE RoomNo = "+ room ;
             PreparedStatement pt = con.prepareStatement(sql);
             int i = pt.executeUpdate();
             x = Integer.parseInt(table.getValueAt(id,4).toString());
             System.out.println(x);
        }
        catch(SQLException se){
             System.out.println(se.getMessage());
         }
     }
     private void addlistners() {
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtName.setText((String) table.getValueAt(row, 0));
                txtRoom.setText(table.getValueAt(row, 1).toString());
                txtDep.setText(table.getValueAt(row, 2).toString());
                txtDate.setText(table.getValueAt(row, 3).toString());
                txtAmount.setText(table.getValueAt(row, 4).toString());
            }
        });
        btnLend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (valid_operattion()) {
                    String[] data = new String[5];
                    data[0] = txtName.getText();
                    data[1] = txtRoom.getText();
                    data[2] = txtDep.getText();
                    data[3] = txtDate.getText();
                    data[4] = txtAmount.getText();
                    if (query_exist(data)== true) {
                        int id = table.getSelectedRow();
                        int x = Integer.parseInt(txtTotal.getText());
                        x += Integer.parseInt(txtAmount.getText());
                        update_data(data,id);
                        txtTotal.setText(String.valueOf(x));

                    } else {
                        dtm.addRow(data);
                        int x = Integer.parseInt(txtTotal.getText());
                        x = x + Integer.parseInt(data[4]);
                        txtTotal.setText(String.valueOf(x));
                    }

                }
                else {
                    System.out.println(" Invalid  Entry !!");
                }
            }
        });
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtAmount.setText("");
                txtName.setText("");
                txtRoom.setText("");
                txtDep.setText("");
                txtDate.setText("");
            }
        });

        btnDel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = table.getSelectedRow();
                int x = Integer.parseInt(txtTotal.getText());
                int y = Integer.parseInt((String) table.getModel().getValueAt(id,4));
                //System.out.println("x : "+ x +" y : " + y);
                if (delete_row_by_id(txtRoom.getText())) {
                    x = x - y;
                    System.out.println(x);
                    txtTotal.setText(Integer.toString(x));
                    dtm.removeRow(id);
                    txtRoom.setText("");
                    txtName.setText("");
                    txtDate.setText("");
                    txtDep.setText("");
                    txtAmount.setText("");
                }
            }
        });
    }

    private  boolean query_exist(String data[]) {
         try{
             if(con == null) create_connection();
             PreparedStatement pt = con.prepareStatement("insert into LD values(?,?,?,?,?)");
             pt.setString(1,data[0]);
             pt.setString(2,data[1]);
             pt.setString(3,data[2]);
             pt.setString(4,data[3]);
             pt.setInt(5,Integer.parseInt(data[4]));
             int  i = pt.executeUpdate();
             if( i == 1) return  false;
             return true;
        }
        catch (SQLException se){
              System.out.println(se.getMessage());
              return true;
        }
    }
        //--------------------------------------------------
        private boolean delete_row_by_id(String s ){
            if (con == null) {
                create_connection();
                return false;
            }
            try {
                Statement st = con.createStatement();
                int i = st.executeUpdate("delete from LD where RoomNo = " + s);
                if (i == 1) return true;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            return false;
        }

    //-------------------------------------------------
    private void insert_data(String[] data) {
        try {
            if (con == null) create_connection();
            PreparedStatement pt = con.prepareStatement("insert into LD values(Name,RoomNo,Department,Date,Amount)" + "(?,?,?,?,data[4])");
            pt.setString(0, data[0]);
            pt.setString(1, data[1]);
            pt.setString(2, data[2]);
            pt.setString(3, data[3]);
            //  pt.setInt(4, data[4]);
            int i = pt.executeUpdate();
            if (i == 0) {
                Statement stmt = null;
                ResultSet rs = null;
                int x = 0;
                try {
                    String st = "select * from LD";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(st);
                    int sum = 0;
                    while (rs.next()) {
                        if (rs.getString("RoomNO") == data[1]) {
                            x = Integer.parseInt(rs.getString("RoomNo"));
                        }

                    }
                } catch (SQLException e) {
                    System.out.print(e.getMessage());
                }

            } else {
                dtm.addRow(data);

            }

        } catch (SQLException e) {
            System.out.print(e.getMessage());

        }

    }

    //-------------------------------------------------
    private void set_row_from_database() {
        if (con == null) create_connection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String st = "select * from LD";
            stmt = con.createStatement();
            rs = stmt.executeQuery(st);
            int sum = 0;
            while (rs.next()) {
                String[] dataArray = new String[5];
                dataArray[0] = rs.getString("Name");
                dataArray[1] = rs.getString("RoomNo");
                dataArray[2] = rs.getString("Department");
                dataArray[3] = rs.getString("Date");
                String s = rs.getString("Amount");
                dataArray[4] = s;
                dtm.addRow(dataArray);
                sum += Integer.parseInt(s);
            }
            //String s = toString(sum);
            txtTotal.setText(String.valueOf(sum));
        } catch (SQLException e) {
            System.out.print("saan");
            System.out.print(e.getMessage());
        }


    }

    private void create_connection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/Lender", "root", "");
        } catch (ClassNotFoundException e) {

            System.out.print("Driver class Node found");
        } catch (SQLException e) {
            System.out.print(e.getMessage());
        }
    }

    private void inti_components() {
        lb1 = new JLabel("Room No.");
        lb2 = new JLabel("Name");
        lb3 = new JLabel("Department");
        lb4 = new JLabel("Date");
        lb5 = new JLabel("Total");
        lb6 = new JLabel("Amount");
        txtRoom = new JTextField(50);
        txtName = new JTextField(50);
        txtDep = new JTextField(50);
        txtDate = new JTextField(50);
        txtTotal = new JTextField(10);
        txtAmount = new JTextField(10);
        btnLend = new JButton("Lend");
        btnBro = new JButton("Borrow");
        btnReset = new JButton("Reset");
        btnDel = new JButton("Delete");

        //------------------------------------------------------------//
        JPanel main_panel, entry_panel, btn_panel, total_panel;
        entry_panel = new JPanel();
        entry_panel.setLayout(new GridLayout(5, 2));
        entry_panel.add(lb2);
        entry_panel.add(txtName);
        entry_panel.add(lb3);
        entry_panel.add(txtDep);
        entry_panel.add(lb1);
        entry_panel.add(txtRoom);
        entry_panel.add(lb4);
        entry_panel.add(txtDate);
        entry_panel.add(lb6);
        entry_panel.add(txtAmount);

        //------------------------------------------------------------------
        btn_panel = new JPanel();
        btn_panel.setLayout(new GridLayout(4, 1));
        btn_panel.add(btnLend);
        btn_panel.add(btnBro);
        btn_panel.add(btnReset);
        btn_panel.add(btnDel);
        //--------------------------------------------------------------------
        total_panel = new JPanel();
        total_panel.setLayout(new GridLayout(1, 1));
        total_panel.add(lb5);
        total_panel.add(txtTotal);
        main_panel = new JPanel();
        main_panel.setLayout(new GridLayout(1, 2));
        main_panel.add(entry_panel);
        main_panel.add(btn_panel);
        JPanel extra = new JPanel();
        extra.setLayout(new GridLayout(2, 1));
        extra.add(main_panel);
        extra.add(total_panel);
        this.add(extra, BorderLayout.SOUTH);
        //----------------------------------------------------------
        table = new JTable();
        dtm = new DefaultTableModel();
        dtm.addColumn("Name");
        dtm.addColumn("Room No.");
        dtm.addColumn("Department");
        dtm.addColumn("Date");
        dtm.addColumn("Amount");
        table.setModel(dtm);
        add(new JScrollPane(table), BorderLayout.CENTER);

    }

    public static void main(String... args) {
        Main ob = new Main();
    }

}
