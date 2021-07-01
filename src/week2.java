import java.util.*;
import java.lang.*;
import java.io.*;

interface User{
    void menu();
    void details();
    int rewards(int x);
}
class Zotato{

    private int deliveryCharge;
    private double companyBalance;
    private HashMap<Integer,Restaurant> r=new HashMap<>();
    private int noRes=0;
    private HashMap<Integer,Customer> c=new HashMap<>();
    private int noCus=0;
    private Scanner input=new Scanner(System.in);

    Zotato(){
        deliveryCharge=0;
        r=new HashMap<>();
        c=new HashMap<>();
        companyBalance=0;
    }
    public void updateCollection() {
        for (Map.Entry<Integer,Restaurant> e : r.entrySet()) {
            this.companyBalance+=e.getValue().returnToZotato();
            this.deliveryCharge+=e.getValue().returnDelivery();
        }
    }
    public void addRestaurant(String s,String add,boolean a,boolean f) {		//if a is true it is authentic, similarly for f
        Restaurant r1;
        if(a) {
            r1=new authenticRestaurant(s+" (Authentic)",add);
        }
        else if(f) {
            r1=new fastFoodRestaurant(s+" (Fast Food)",add);
        }
        else {
            r1=new Restaurant(s,add);
        }
        this.noRes++;
        r.put(noRes,r1);

    }
    public void addCustomer(String s1,String a,boolean e,boolean s) {
        Customer c1;
        if(e) {
            c1=new eliteCustomer(s1+" (Elite customer)",a);
        }
        else if(s) {
            c1=new specialCustomer(s1+" (Special customer)",a);
        }
        else {
            c1=new Customer(s1,a);
        }
        this.noCus++;
        c.put(noCus,c1);
    }
    public void enterAsOwner() {
        restList();

        int vari=input.nextInt();
        r.get(vari).menu();
    }
    public void enterAsCustomer() {

        cusList();
        int vari=input.nextInt();
        c.get(vari).setRestaurants(r);
        c.get(vari).menu();
    }
    public void userDetails() {
        System.out.println("1.Customer List");
        System.out.println("2.Restaurant List");

        int x=input.nextInt();
        if(x==1) {
            cusList();
            int vari=input.nextInt();
            c.get(vari).details();
        }
        else {
            restList();
            int vari=input.nextInt();
            r.get(vari).restLoc();
        }

    }
    public void restList() {
        for (Map.Entry<Integer,Restaurant> e : r.entrySet()) {
            System.out.println(e.getKey()+" "+e.getValue().getName());
        }
    }
    public void cusList() {
        for (Map.Entry<Integer,Customer> e : c.entrySet()) {
            System.out.println(e.getKey()+" "+e.getValue().getName());
        }
    }
    public void companyAccountDetails() {
        this.updateCollection();
        System.out.println("Total company balance - INR "+this.companyBalance);
        System.out.println("Total Delivery Charges Collected - INR "+this.deliveryCharge);

    }
    public void updateDelivery(int x) {
        this.deliveryCharge+=x;
    }
    public void companyBalance(int x) {
        this.companyBalance+=x;
    }

}
class Customer implements User{

    private String name;
    final private String address;
    private HashMap<Integer,Restaurant> r;
    protected HashMap<foodItem,Integer> f;
    protected int rewardPoints;
    protected double wallet=1000.0;
    protected Restaurant res;
    protected Scanner input=new Scanner(System.in);
    protected ArrayList<String> lastOrders;

    Customer(String s,String a){
        this.name=s;
        this.address=a;
        r=new HashMap<>();
        f=new HashMap<>();
        res=null;
        rewardPoints=0;
        lastOrders=new ArrayList<String>();
    }
    public void setRestaurants(HashMap<Integer,Restaurant> r1) {
        this.r=r1;
    }
    public void printLastOrders() {
        for(String x:lastOrders) {
            System.out.println(x);
        }
    }

    @Override
    public void menu() {
        System.out.println("Welcome "+this.name);
        System.out.println("1. Select Restaurant");
        System.out.println("2. Checkout cart");
        System.out.println("3. Reward won");
        System.out.println("4. Print the recent orders");
        System.out.println("5.Exit");

        int ch=input.nextInt();

        int first=1;
        while(ch!=5) {
            if(first!=1) {
                System.out.println("1. Search Item");
                System.out.println("2. Checkout cart");
                System.out.println("3. Reward won");
                System.out.println("4. Print the recent orders");
                System.out.println("5.Exit");

                ch=input.nextInt();

            }
            else {

                first+=1;
                for (Map.Entry<Integer,Restaurant> e : r.entrySet()) {
                    System.out.println(e.getKey()+" "+e.getValue().getName());
                }
                int vari=input.nextInt();
                res=r.get(vari);
                //chooseRestaurant(r.get(vari));
            }

            switch(ch) {
                case 1:
                    chooseRestaurant(res);
                    break;
                case 2:
                    this.checkout();
                    break;
                case 3:
                    this.rewardPointsEarned();
                    break;
                case 4:
                    this.printLastOrders();
                    break;

                case 5:
                    System.out.println("EXIT");
                    break;
            }
        }
    }
    @Override
    public void details() {
        System.out.println(this.name+" , "+this.address+" , "+this.wallet);
    }

    @Override
    public int rewards(int finalAmount) {
        return res.rewards(finalAmount);
    }
    public void rewardPointsEarned() {
        System.out.println("Reward points earned are : "+this.rewardPoints);
    }
    public void cutTotal(double cost) {
        if(this.rewardPoints==0) {
            wallet-=cost;
        }
        else {
            if(this.rewardPoints>cost) {
                this.rewardPoints-=cost;
            }
            else {
                cost-=this.rewardPoints;
                this.rewardPoints=0;
                this.wallet-=cost;
            }
        }
    }
    public void checkout() {
        double cost=0;
        int total_items=0;
        String addFinal="";
        System.out.println("Items in Cart- ");
        for (Map.Entry<foodItem,Integer> e : f.entrySet())  {
            foodItem f1=e.getKey();
            f1.checkoutDetails(e.getValue());
            if(cost+f1.getPrice()*e.getValue()<wallet) {
                cost+=f1.getPrice()*e.getValue()*(1-(double)f1.getOffer()/100);
                total_items+=e.getValue();
                addFinal+=" Bought item: "+f1.getFname()+" , "+e.getValue();
            }
            else {
                System.out.println("Wallet does not have the required amount hence remove some items");
                for (Map.Entry<foodItem,Integer> e1 : f.entrySet()) {
                    foodItem f2=e1.getKey();
                    System.out.println("Do you want to remove? "+f2.getFname());
                    System.out.println("Enter 1 for yes else 0");
                    int ask=input.nextInt();
                    if(ask==1) {
                        f.remove(f2);
                    }
                }
            }
        }
        if(total_items==0) {
            System.out.println("No items in quantity.");
        }
        else {
            System.out.println();
            double x=res.discount(cost);
            cost=cost-x;
            int rewards=this.rewards((int)cost);

            res.updateTotal(cost);
            System.out.println("Delivery charge- INR 40/-");
            addFinal+=" from restaurant "+res.getName();
            cost+=40;
            res.updateDelivery(40);
            addFinal+=" Delivery charge 40";
            System.out.println("Total order value- INR "+cost);

            System.out.println("1. Proceed to Checkout. ");
            int vari=input.nextInt();

            if(vari==1) {
                cutTotal(cost);
                System.out.println(total_items+" items successfully bough for "+cost+" INR.");
                this.lastOrders.add(addFinal);
                cost=0;
                this.rewardPoints+=rewards;
                f.clear();
            }
        }
    }
    public double deliveryCharge(int cost) {
        return (cost+40);
    }
    public void chooseRestaurant(Restaurant r) {
        r.details();
        int itemno=input.nextInt();
        System.out.println("Enter item quantity (has an upper limit)");
        //you cannot order more than specified
        int qty=input.nextInt();
        foodItem f1=r.getItem(itemno);
        if(f.containsKey(f1)) {
            if(f1.getQuantity()<qty+f.get(f1)) {
                System.out.println("Maximum quanitity exceeded, adding maximum in cart");
                f.replace(f1,f1.getQuantity());
            }
            else {
                f.replace(f1,qty+f.get(f1));
            }
        }
        else {
            if(f1.getQuantity()<qty) {
                System.out.println("Maximum quanitity exceeded, adding maximum in cart");
                f.put(f1,f1.getQuantity());
            }
            else {
                f.put(f1,qty);
            }
        }
        System.out.println("Item added to cart");
    }
    public String getName() {
        return this.name;
    }
}
class eliteCustomer extends Customer{

    eliteCustomer(String s,String a) {
        super(s,a);
    }
    @Override
    public double deliveryCharge(int cost) {
        return (cost);
    }

    @Override
    public void checkout() {
        double cost=0;
        int total_items=0;
        String addFinal="";
        System.out.println("Items in Cart- ");
        for (Map.Entry<foodItem,Integer> e : f.entrySet())  {
            foodItem f1=e.getKey();
            f1.checkoutDetails(e.getValue());
            if(cost+f1.getPrice()*e.getValue()<wallet) {
                cost+=f1.getPrice()*e.getValue()*(1-(double)f1.getOffer()/100);
                total_items+=e.getValue();
                addFinal+=" Bought item: "+f1.getFname()+" , "+e.getValue();
            }
            else {
                System.out.println("Wallet does not have the required amount hence remove some items");
                for (Map.Entry<foodItem,Integer> e1 : f.entrySet()) {
                    foodItem f2=e1.getKey();
                    System.out.println("Do you want to remove? "+f2.getFname());
                    System.out.println("Enter 1 for yes else 0");
                    int ask=input.nextInt();
                    if(ask==1) {
                        f.remove(f2);
                    }
                }
            }
        }
        if(total_items==0) {
            System.out.println("No items in quantity.");
        }
        else {
            System.out.println();
            double x=res.discount(cost);
            cost=cost-x;
            int reward=this.rewards((int)cost);

            if(cost>200) {
                cost-=50;
            }

            res.updateTotal(cost);
            System.out.println("Delivery charge- INR 0/-");
            addFinal+=" from restaurant "+res.getName();
            addFinal+=" Delivery charge 0";
            System.out.println("Total order value- INR "+cost);

            System.out.println("1. Proceed to Checkout. ");
            int vari=input.nextInt();

            if(vari==1) {
                cutTotal(cost);
                System.out.println(total_items+" items successfully bough for "+cost+" INR.");
                this.lastOrders.add(addFinal);
                cost=0;
                this.rewardPoints+=reward;
                f.clear();
            }

        }
    }

}
class specialCustomer extends Customer{

    specialCustomer(String s,String a) {
        super(s,a);
    }
    @Override
    public double deliveryCharge(int cost) {
        return (cost+20);
    }
    @Override
    public void checkout() {
        double cost=0;
        int total_items=0;
        String addFinal="";
        System.out.println("Items in Cart- ");
        for (Map.Entry<foodItem,Integer> e : f.entrySet())  {
            foodItem f1=e.getKey();
            f1.checkoutDetails(e.getValue());
            if(cost+f1.getPrice()*e.getValue()<wallet) {
                cost+=f1.getPrice()*e.getValue()*(1-(double)f1.getOffer()/100);
                total_items+=e.getValue();
                addFinal+=" Bought item: "+f1.getFname()+" , "+e.getValue();
            }
            else {
                System.out.println("Wallet does not have the required amount hence remove some items");
                for (Map.Entry<foodItem,Integer> e1 : f.entrySet()) {
                    foodItem f2=e1.getKey();
                    System.out.println("Do you want to remove? "+f2.getFname());
                    System.out.println("Enter 1 for yes else 0");
                    int ask=input.nextInt();
                    if(ask==1) {
                        f.remove(f2);
                    }
                }
            }
        }
        if(total_items==0) {
            System.out.println("No items in quantity.");
        }
        else {
            System.out.println();
            double x=res.discount(cost);
            cost=cost-x;
            int reward=this.rewards((int)cost);

            if(cost>200) {
                cost-=25;
            }

            res.updateTotal(cost);
            System.out.println("Delivery charge- INR 20/-");
            addFinal+=" from restaurant "+res.getName();
            cost+=20;
            res.updateDelivery(20);
            addFinal+=" Delivery charge 20";
            System.out.println("Total order value- INR "+cost);

            System.out.println("1. Proceed to Checkout. ");
            int vari=input.nextInt();

            if(vari==1) {
                cutTotal(cost);
                System.out.println(total_items+" items successfully bough for "+cost+" INR.");
                this.lastOrders.add(addFinal);
                cost=0;
                this.rewardPoints+=reward;
                f.clear();
            }

        }
    }


}
class Restaurant implements User{

    private Scanner input=new Scanner(System.in);
    final private String name;
    final private String address;
    private ArrayList<foodItem> f;		//itemcode is noOfItems+1
    private int noOfItems;
    private int rewardPoints;
    private int discount;
    private int ordersTaken;
    protected double totalEarning=0;
    protected int deliveryAmt=0;

    Restaurant(String r,String a){
        this.name=r;
        this.address=a;
        f=new ArrayList<>();
        noOfItems=0;
        rewardPoints=0;
        discount=0;
        rewardPoints=0;
        ordersTaken=0;
    }

    public String getName() {
        return this.name;
    }
    public foodItem getItem(int x) {
        return f.get(x-1);
    }
    @Override
    public void menu() {
        System.out.println("Welcome "+this.name);

        System.out.println("1. Add item");
        System.out.println("2. Edit item");
        System.out.println("3. Print Rewards");
        System.out.println("4. Discount on bill value");
        System.out.println("5.Exit");

        int ch=input.nextInt();
        int first=1;
        while(ch!=5) {
            if(first!=1) {
                System.out.println("1. Add item");
                System.out.println("2. Edit item");
                System.out.println("3. Print Rewards");
                System.out.println("4. Discount on bill value");
                System.out.println("5.Exit");

                ch=input.nextInt();
            }
            else {
                first+=1;
            }

            switch(ch) {

                case 1:
                    System.out.println("Enter food item details");
                    System.out.println("Food name ");
                    input.nextLine();
                    String ss=input.nextLine();
                    System.out.println("Item price");
                    int x=input.nextInt();
                    System.out.println("Item quantity");
                    int y=input.nextInt();
                    System.out.println("Item category ");
                    input.nextLine();
                    String s11=input.nextLine();;
                    System.out.println("Offer");
                    int z=input.nextInt();

                    f.add(new foodItem(ss,x,y,s11,z,this.noOfItems+1));
                    noOfItems++;

                    System.out.print(this.noOfItems+" ");
                    f.get(noOfItems-1).itemDetails();

                    break;

                case 2:
                    this.details();

                    int vari=input.nextInt();	//chosen code

                    System.out.println("Choose an attribute to edit: ");
                    System.out.println("1. Food name");
                    System.out.println("2. Item price");
                    System.out.println("3. Item quantity");
                    System.out.println("4. Item category");
                    System.out.println("5. Offer");

                    int edit=input.nextInt();

                    if(edit==1) {
                        String s;
                        System.out.println("Enter new name ");
                        s=input.next();
                        f.get(vari-1).setFname(s);
                    }
                    else if(edit==4) {
                        String s;
                        System.out.println("Enter new category ");
                        s=input.next();
                        f.get(vari-1).setCategory(s);
                    }
                    else if(edit==2) {
                        System.out.println("Enter new price ");
                        int n=input.nextInt();
                        f.get(vari-1).setPrice(n);
                    }
                    else if(edit==3) {
                        System.out.println("Enter new quantity ");
                        int n=input.nextInt();
                        f.get(vari-1).setQuantity(n);
                    }
                    else if(edit==5) {
                        System.out.println("Enter new offer ");
                        int n=input.nextInt();
                        f.get(vari-1).setOffer(n);
                    }

                    System.out.print(vari+" "+this.name+" - ");
                    f.get(vari-1).itemDetails();
                    System.out.println();

                    break;

                case 3:

                    System.out.println("Reward Points : "+this.getRewards());
                    break;

                case 4:
                    System.out.println("Offers on bill value - ");
                    discount=input.nextInt();

                    break;

                case 5:
                    System.out.println("Exit");
                    break;

                default:
                    System.out.println("Wrong choice, enter a valid choice");
                    break;

            }

        }
    }
    public void restLoc() {
        System.out.println(this.name+" , "+this.address+" , Orders Taken: "+this.ordersTaken);
    }
    public void updateDelivery(int x) {
        this.deliveryAmt+=x;
    }
    @Override
    public void details() {

        System.out.println("Choose item by code");
        for(foodItem x:f) {
            System.out.print(x.getCode()+" "+this.name+" - ");
            x.itemDetails();
            //System.out.println();
        }

    }
    @Override
    public int rewards(int total) {

        int x=(int)total/100;
        x*=5;
        this.rewardPoints+=x;
        return(x);
    }
    public double returnToZotato() {
        double x=this.totalEarning;
        this.totalEarning=0;
        return x/100;
    }
    public void addRewards(int x) {
        this.rewardPoints+=x;
    }
    public void updateTotal(double x) {
        this.ordersTaken+=1;
        this.totalEarning+=x;
    }
    public double discount(double total) {
        return total*discount/100;
    }

    public int getRewards() {
        return this.rewardPoints;	//ensure rewards is called before calling this
    }

    public double getDiscount() {
        return discount;
    }

    public double returnDelivery() {
        return this.deliveryAmt;
    }
}
class authenticRestaurant extends Restaurant{
    authenticRestaurant(String r,String a) {
        super(r,a);
    }
    @Override
    public double discount(double total) {
        double y=(double)total*super.getDiscount()/100;
        double x=total-y;
        if(x>100) {
            return(y+50);
        }
        else {
            return(y);
        }
    }
    @Override
    public int rewards(int total) {			//handle rewards here
        int x=(int)total/200;
        super.addRewards(x*25);
        //rewards=total*25/200
        return(x*25);
    }
}
class fastFoodRestaurant extends Restaurant{
    fastFoodRestaurant(String r,String a) {
        super(r,a);
    }
    @Override
    public int rewards(int total) {
        //rewards=total*10/150
        int x=(int)total/150;
        super.addRewards(x*10);
        return(x*10);
    }
}
class foodItem{
    private String fname;
    private int price;
    private int maxquantity;	//this is maximum quantity the person can buy
    private String category;
    private int offer;
    private final int code;

    foodItem(String n,int p,int q,String s,int o,int ite){
        this.fname=n;
        this.price=p;
        this.maxquantity=q;
        this.category=s;
        this.offer=o;
        this.code=ite;

    }
    public String getFname() {
        return this.fname;
    }
    public void setFname(String s) {
        this.fname=s;
    }
    public int getPrice() {
        return this.price;
    }
    public void setPrice(int p) {
        this.price=p;
    }
    public int getQuantity() {
        return this.maxquantity;
    }
    public void setQuantity(int q) {
        this.maxquantity=q;
    }
    public String getCategory() {
        return this.category;
    }
    public void setCategory(String s) {
        this.category=s;
    }
    public int getOffer() {
        return this.offer;
    }
    public void setOffer(int f) {
        this.offer=f;
    }
    public void itemDetails() {
        System.out.println(this.fname +" "+this.price+" "+this.maxquantity+" "+this.offer+"% off "+this.category);
    }
    public void checkoutDetails(int qty) {
        System.out.print(this.fname +" "+this.price+" "+qty+" "+this.offer+"% off "+this.category);

    }
    public int getCode() {
        return this.code;
    }
}
public class week2 {

    public static void main(String[] args) {
        Scanner input=new Scanner(System.in);
        int ch=0;
        Zotato z=new Zotato();
        z.addRestaurant("Shah","Delhi",true,false);
        z.addRestaurant("Ravi's","Mumbai",false,false);
        z.addRestaurant("The Chinese","Kolkata",true,false);
        z.addRestaurant("Wang's","Haryana",false,true);
        z.addRestaurant("Paradise","Agra",false,false);
        z.addCustomer("Ram","Chennai",true,false);
        z.addCustomer("Sam","Dubai",true,false);
        z.addCustomer("Tim","Gujarat",false,true);
        z.addCustomer("Kim","Chennai",false,false);
        z.addCustomer("Jim","Chennai",false,false);

        while(ch!=5) {
            System.out.println("Welcome to Zotato: ");
            System.out.println("1. Enter as Restaurant Owner");
            System.out.println("2. Enter as Customer ");
            System.out.println("3.Check User Details");
            System.out.println("4. Company Account details");
            System.out.println("5.Exit");

            ch=input.nextInt();

            switch(ch){

                case 1:
                    z.enterAsOwner();
                    break;

                case 2:

                    z.enterAsCustomer();
                    break;

                case 3:
                    z.userDetails();
                    break;

                case 4:
                    z.companyAccountDetails();
                    break;

                case 5:
                    System.out.println("Exit");
                    break;

                default:
                    System.out.println("Wrong choice, enter a valid choice");
                    break;

            }
        }
    }

}
