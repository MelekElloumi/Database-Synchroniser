import java.io.Serializable;

public class Product_Sales implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String date;
	private String region;
	private String product;
	private int qty;
	private double cost;
	private double amt;
	private double tax;
	private double total;
	private int origine;
	
	public Product_Sales(int i,String d, String r, String p, int q, double c, double a, double t, double to, int o) {
		id=i;
		date=d;
		region=r;
		product=p;
		qty=q;
		cost=c;
		amt=a;
		tax=t;
		total=to;
		origine=o;
	}
	public int getId() {
		return id;
	}
	public String getDate() {
		return date;
	}
	public String getRegion() {
		return region;
	}
	public String getProduct() {
		return product;
	}
	public int getQty() {
		return qty;
	}
	public double getCost() {
		return cost;
	}
	public double getAmt() {
		return amt;
	}
	public double getTax() {
		return tax;
	}
	public double getTotal() {
		return total;
	}
	public int getOrigine() {
		return origine;
	}
	
	public int getEnvoie() {
		return origine;
	}
}