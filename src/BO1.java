
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class BO1 {
	
	private final static String QUEUE_NAME = "HOqueue";
	private final static int BO_ID = 1;
	private final static String[] COLUMN = {"ID","DATE","REGION","PRODUCT","QTY","COST","AMT","TAX","TOTAL","ENVOYE?"};

	public static void main(String[] args) throws Exception {
		ArrayList<Product_Sales> pssall = new ArrayList<Product_Sales>();
        extraireall(pssall); 
        
		JFrame frame = new JFrame("Branch Office 1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10,20));
        JLabel titre =new JLabel("Base de donnée du Branch Office 1.",SwingConstants.CENTER);
        frame.add(titre,BorderLayout.PAGE_START);
        JButton send = new JButton("Envoyer");
        JButton refresh = new JButton("Refraichir");
        JPanel buttonpanel= new JPanel();
        buttonpanel.setLayout(new GridLayout(1,2));
        buttonpanel.add(send);
        buttonpanel.add(refresh);
        frame.add(buttonpanel,BorderLayout.PAGE_END);
        
        JTable tab = new JTable();        
        JScrollPane scrollpane = new JScrollPane(tab);
        afficherTable(tab);	 
        frame.add(scrollpane,BorderLayout.CENTER);	        
        frame.setSize(1000, 300);
        frame.setVisible(true);
        frame.revalidate();
        
        
        send.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	 envoyer(); 
		    }
		});
        
        
        refresh.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	 afficherTable(tab);	  
		    }
		});
        
	}
	
	
	 public static void afficherTable( JTable tab) {
		    ArrayList<Product_Sales> pssall = new ArrayList<Product_Sales>();
    	 	extraireall(pssall);
	    	DefaultTableModel dtm = new DefaultTableModel(0,10);
	    	dtm.setColumnIdentifiers(COLUMN);
	    	Product_Sales ps = null;
	    	for (int i=0;i<pssall.size();i++) {
	    		ps=pssall.get(i);
	        	Vector<Object> data = new Vector<Object>();
	    		data.add(ps.getId());
	    		data.add(ps.getDate());
	    		data.add(ps.getRegion());
	    		data.add(ps.getProduct());
	    		data.add(ps.getQty());
	    		data.add(ps.getCost());
	    		data.add(ps.getAmt());
	    		data.add(ps.getTax());
	    		data.add(ps.getTotal());
	    		data.add(ps.getEnvoie());
	    		dtm.addRow(data);
	    	}
	    	tab.setModel(dtm);
	    	tab.getColumnModel().getColumn(tab.getColumnModel().getColumnIndex("ENVOYE?")).setCellRenderer(new StatusEnvoie());
	    	pssall.clear();
	    	return;
	    }
	 
	 
	 
	public static void envoyer() {
		
		ArrayList<Product_Sales> pss = extraire();
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try (Connection connection = factory.newConnection();
		Channel channel = connection.createChannel()){			
			channel.queueDeclare(QUEUE_NAME, false,false,false,null);
			
			for (int i=0;i<pss.size();i++) {
				channel.basicPublish("", QUEUE_NAME, null, getByteArray(pss.get(i)));
				
			}
			
		}catch (Exception e) {}

	}
	
	
	public static byte[] getByteArray (Product_Sales t) throws Exception
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(t);
        return bos.toByteArray();

    }
	
	
	public static ArrayList<Product_Sales> extraire(){
		ArrayList<Product_Sales> pss = new ArrayList<Product_Sales>();
		java.sql.Connection conn =null;
    	try{Class.forName("com.mysql.jdbc.Driver");
    		conn = DriverManager.getConnection("jdbc:mysql://localhost:3305/fsar_tp2_bo1","root","");
    	}catch(Exception e){ e.printStackTrace();}
		PreparedStatement pstmt = null;
		Product_Sales ps = null;
		String sqlSelect = "select * from Product_Sales where envoie=0;";
		String sqlUpdate = "update Product_Sales set envoie=1 where envoie=0";
    	try {
    		pstmt=conn.prepareStatement(sqlSelect);
    		ResultSet rsPs=pstmt.executeQuery();
			while(rsPs.next()){
				ps=new Product_Sales(rsPs.getInt("Id"),
						rsPs.getString("Date"),
						rsPs.getString("Region"),
						rsPs.getString("Product"),
						rsPs.getInt("Qty"),
						rsPs.getDouble("Cost"),
						rsPs.getDouble("Amt"),
						rsPs.getDouble("Tax"),
						rsPs.getDouble("Total"),
						BO_ID
					);
				pss.add(ps);
    		}
			pstmt=conn.prepareStatement(sqlUpdate);
			pstmt.executeUpdate();
		}catch (SQLException e2) { e2.printStackTrace(); }
				
		return pss;
	}
	
	
    public static  void extraireall(ArrayList<Product_Sales> pss){
		java.sql.Connection conn =null;
    	try{Class.forName("com.mysql.jdbc.Driver");
    		conn = DriverManager.getConnection("jdbc:mysql://localhost:3305/fsar_tp2_bo1","root","");
    	}catch(Exception e){ e.printStackTrace();}
		PreparedStatement pstmt = null;
		Product_Sales ps = null;
		String sqlSelect = "select * from Product_Sales;";
    	try {
    		pstmt=conn.prepareStatement(sqlSelect);
    		ResultSet rsPs=pstmt.executeQuery();
			while(rsPs.next()){
				ps=new Product_Sales(rsPs.getInt("Id"),
						rsPs.getString("Date"),
						rsPs.getString("Region"),
						rsPs.getString("Product"),
						rsPs.getInt("Qty"),
						rsPs.getDouble("Cost"),
						rsPs.getDouble("Amt"),
						rsPs.getDouble("Tax"),
						rsPs.getDouble("Total"),
						rsPs.getInt("Envoie")
					);
				pss.add(ps);
    		}
		}catch (SQLException e2) { e2.printStackTrace(); }
		return;
	}

}


class StatusEnvoie extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
		JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		if (l.getText().equals("1")) {
			 l.setText("OUI");
			 l.setBackground(Color.GREEN);
		}
		else {
			l.setText("NON");
			l.setBackground(Color.RED);
		}
		return l;
	}
}
