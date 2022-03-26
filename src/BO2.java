
import java.awt.BorderLayout;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class BO2 {
	
	private final static String QUEUE_NAME = "HOqueue";
	private final static int BO_ID = 2;
	private final static String[] COLUMN = {"ID","DATE","REGION","PRODUCT","QTY","COST","AMT","TAX","TOTAL","ENVOYE?"};
	private static JTable tab;

	public static void main(String[] args) throws Exception {
		ArrayList<Product_Sales> pssall = new ArrayList<Product_Sales>();
        extraireall(pssall); 
        
		JFrame frame = new JFrame("Branch Office 2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10,20));
        JLabel titre =new JLabel("Base de donnée du Branch Office 2.",SwingConstants.CENTER);
        frame.add(titre,BorderLayout.PAGE_START);
        JButton send = new JButton("Envoyer");
        JButton refresh = new JButton("Refraichir");
        JLabel status =new JLabel("59 secondes restantes pour le prochain enregistrement",SwingConstants.CENTER);
        JPanel buttonpanel= new JPanel();
        buttonpanel.setLayout(new GridLayout(2,2));
        buttonpanel.add(send);
        buttonpanel.add(status);
        buttonpanel.add(refresh);
        frame.add(buttonpanel,BorderLayout.PAGE_END);		 
        
        tab = new JTable();        
        JScrollPane scrollpane = new JScrollPane(tab);
        afficherTable();	 
        frame.add(scrollpane,BorderLayout.CENTER);	        
        frame.setSize(1000, 300);
        frame.setVisible(true);
        frame.revalidate();
        
        
 
        Timer time = new Timer();
        time.scheduleAtFixedRate(new TimerTask(){
        	int interval =60;
        	public void run() {
        		if (interval == 0) {
        			interval =60;
        			BO2.envoyer();
        			BO2.afficherTable();
        		}
        		interval--;
        		status.setText(""+interval+" secondes restantes pour le prochain enregistrement");
        	}
        },1000,1000);
        
        
        send.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	 envoyer(); 
		    }
		});
        
        
        refresh.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	 afficherTable();	  
		    }
		});
        
	}
	 public static void afficherTable() {
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
		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()){			
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
    		conn = DriverManager.getConnection("jdbc:mysql://localhost:3305/fsar_tp2_bo2","root","");
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
    		conn = DriverManager.getConnection("jdbc:mysql://localhost:3305/fsar_tp2_bo2","root","");
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


