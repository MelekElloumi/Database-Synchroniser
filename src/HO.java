import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import java.sql.*;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;


public class HO {
	private final static String QUEUE_NAME = "HOqueue";
	private final static String[] COLUMN = {"ID","DATE","REGION","PRODUCT","QTY","COST","AMT","TAX","TOTAL","BO ORIGINE"};
	static Product_Sales ps = null;
	
	public static void main(String[] args) throws Exception{
		    ArrayList<Product_Sales> pss = new ArrayList<Product_Sales>();
            
			
			JFrame frame = new JFrame("Head Office");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setLayout(new BorderLayout(10,20));
	        JLabel titre =new JLabel("Base de donnée du Head Office.",SwingConstants.CENTER);
	        frame.add(titre,BorderLayout.PAGE_START);
	        JButton save = new JButton("Enregistrer");
	        JButton refresh = new JButton("Refraichir");
	        JLabel status =new JLabel("Aucun enregistrement en attente.",SwingConstants.CENTER);
	        JPanel buttonpanel= new JPanel();
	        buttonpanel.setLayout(new GridLayout(2,2));
	        buttonpanel.add(save);
	        buttonpanel.add(status);
	        buttonpanel.add(refresh);
	        frame.add(buttonpanel,BorderLayout.PAGE_END);		
	        
	        JTable tab = new JTable();
	        JScrollPane scrollpane = new JScrollPane(tab);
	        afficherTable(tab);	 
	        frame.add(scrollpane,BorderLayout.CENTER);	        
	        frame.setSize(1000, 300);
	        frame.setVisible(true);
	        frame.revalidate();
	        
	        
	        
	        save.addActionListener(new ActionListener() {
			    @Override
			    public void actionPerformed(ActionEvent e) {
			    	 enregistrer(pss);
			    	 status.setText("Aucun enregistrement en attente.");
			    	 afficherTable(tab);	 
			    }
			});
	        
	        
	        
	        refresh.addActionListener(new ActionListener() {
			    @Override
			    public void actionPerformed(ActionEvent e) {
			    	 afficherTable(tab);	  
			    }
			});
			
		
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.queueDeclare(QUEUE_NAME, false,false,false,null);
			
			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				byte[] bytearray =delivery.getBody();
                try {
                     ps = (Product_Sales)deserialize(bytearray);
                     pss.add(ps);
                     status.setText(""+pss.size()+" enregistrements en attente.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
			};		
		channel.basicConsume(QUEUE_NAME, true, deliverCallback,consumerTag -> {});
		
	}

	
	
    public static Object deserialize (byte[] t) throws Exception
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(t);
        ObjectInputStream in = new ObjectInputStream(bis);
        return in.readObject(); 
    }
    
    
    
    public static void afficherTable(JTable tab) {
    	ArrayList<Product_Sales> pssall = new ArrayList<Product_Sales>();;
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
    		data.add(ps.getOrigine());	
    		dtm.addRow(data);
    	}
    	tab.setModel(dtm);
    	pssall.clear();
    	return;
    }
    
    
    public static void enregistrer(ArrayList<Product_Sales> pss) {
    	java.sql.Connection conn =null;
    	try{Class.forName("com.mysql.jdbc.Driver");
    		conn = DriverManager.getConnection("jdbc:mysql://localhost:3305/fsar_tp2_ho","root","");
    	}catch(Exception e){ e.printStackTrace();}
    	
    	PreparedStatement pstmt = null;
    	String sqlInsert = "INSERT INTO product_sales (id, date, region, product, qty, cost,amt,tax,total,origine) VALUES(?,?,?,?,?,?,?,?,?,?);";
    	while (pss.size()!=0) {   		
    		try {
    		pstmt = conn.prepareStatement(sqlInsert);   		
    		pstmt.setString(1,Integer.toString(pss.get(0).getId()));
    		pstmt.setString(2,pss.get(0).getDate());
    		pstmt.setString(3,pss.get(0).getRegion());
    		pstmt.setString(4,pss.get(0).getProduct());
    		pstmt.setString(5,Integer.toString(pss.get(0).getQty()));
    		pstmt.setString(6,Double.toString(pss.get(0).getCost()));
    		pstmt.setString(7,Double.toString(pss.get(0).getAmt()));
    		pstmt.setString(8,Double.toString(pss.get(0).getTax()));
    		pstmt.setString(9,Double.toString(pss.get(0).getTotal()));
    		pstmt.setString(10,Integer.toString(pss.get(0).getOrigine()));
    		pstmt.executeUpdate();
    		}catch (SQLException e2) { e2.printStackTrace(); }
    		pss.remove(0);
    	}
    }
    
    
    
    public static void extraireall(ArrayList<Product_Sales> pss){

		java.sql.Connection conn =null;
    	try{Class.forName("com.mysql.jdbc.Driver");
    		conn = DriverManager.getConnection("jdbc:mysql://localhost:3305/fsar_tp2_ho","root","");
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
						rsPs.getInt("origine")
					);
				pss.add(ps);
    		}
		}catch (SQLException e2) { e2.printStackTrace(); }
		return;
	}
}
