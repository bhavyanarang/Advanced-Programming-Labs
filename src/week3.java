import java.util.*;
class Gameplay<T>{
    private int total;
    private HashMap<Integer,Player> p=new HashMap<Integer,Player>();
    private Random rand=new Random();
    private T user;
    private int round=0;
    private boolean[] alive;
    private int left;
    private Mafia mafia=new Mafia(0);
    private Detective detective=new Detective(0);
    private Healer healer=new Healer(0);
    private Scanner input=new Scanner(System.in);
    private ArrayList<Mafia> mfs;

    Gameplay(int t,T o){
        mfs=new ArrayList<Mafia>();
        this.total=t;
        alive=new boolean[total+1];
        this.user=o;
        p.put(1,(Player)this.user);
        round=1;
        alive=new boolean[total+1];
        for(int i=1;i<=total;i++) {
            alive[i]=true;
        }
        left=total;
    }
    public void startGame() {
        generatePlayer();
        boolean com=this.user instanceof Commoner;
        System.out.println("You are Player1.");
        System.out.println(user.toString());

        if(!com) {
            printSimilar();
        }
        while(!gameOver()) {
            startRound();
        }
    }
    private void remainingPlayers() {
        System.out.print(left+" players are remaining: ");
        for(int i=1;i<=total;i++) {
            if(alive[i]) {
                System.out.print("Player"+i+" ,");
            }
        }
        System.out.println(" are alive.");
    }
    private void startRound() {
        System.out.println("ROUND "+round++);
        remainingPlayers();

        int dead=0;
        int test=0;
        int heal=0;

        if(this.user instanceof Mafia && alive[1]) {
            dead=mafia.choosePlayerForRole(p,true,alive);
            while(p.get(dead) instanceof Mafia) {
                System.out.println("Mafia cannot kill a mafia");
                dead=mafia.choosePlayerForRole(p,true,alive);
            }
        }
        else {
            dead=mafia.choosePlayerForRole(p,false,alive);
            while(p.get(dead) instanceof Mafia) {
                dead=mafia.choosePlayerForRole(p,false,alive);
            }
        }
        if(this.user instanceof Detective && alive[1]) {
            test=detective.choosePlayerForRole(p,true,alive);
            while(p.get(test) instanceof Detective) {
                System.out.println("Detective cannot test a detective");
                test=detective.choosePlayerForRole(p,true,alive);
            }
        }
        else {
            test=detective.choosePlayerForRole(p,false,alive);
            while(p.get(test) instanceof Detective) {
                test=detective.choosePlayerForRole(p,false,alive);
            }
        }
        if(!detectivesAlive()) {
            test=0;
        }
        if(this.user instanceof Healer && alive[1]) {
            heal=healer.choosePlayerForRole(p,true,alive);
        }
        else {
            heal=healer.choosePlayerForRole(p,false,alive);
        }
        System.out.println("-- End of actions-- ");
        int votingDone=0;
        if(heal==dead && dead!=0 && alive[heal]) {
            p.get(heal).setHp(500); 				//if healing and target is same no one is killed and hp is set to 500
            System.out.println("No one is killed");
        }
        else {
            if(dead!=0 && alive[dead] && !(p.get(dead) instanceof Mafia)) {
                if(mafiaHp()>=p.get(dead).getHp()) {
                    int a=p.get(dead).getHp();
                    int b=mafiasLeft();
                    decreaseMafiasHp((int)a/b,a);
                    alive[dead]=false;
                    left-=1;
                    System.out.println("Player"+dead+" has died.");
                }
                else {
                    int a=p.get(dead).getHp();
                    int b=mafiasLeft();
                    decreaseMafiasHp((int)a/b,a);
                    System.out.println("No one died");
                }
            }
            if(heal!=0 && alive[heal]) {
                int var=p.get(heal).getHp();
                p.get(heal).setHp(500+var);
            }
            if(test!=0 && alive[test]) {
                System.out.println("Player"+test+" has been voted out.");
                alive[test]=false;
                left-=1;
            }
            else {
                conductVoting();
                votingDone=1;
            }
        }
        if(votingDone==0) {
            conductVoting();
            votingDone=1;
        }
    }
    private void conductVoting() {
        ArrayList<Integer> voting=new ArrayList<Integer>();
        int temp;
        if(alive[1]) {
            System.out.println("Select Player to vote out");
            int var=input.nextInt();
            while(var<0 || var>alive.length-1 || !alive[var]) {
                System.out.println("Choose a valid person to vote out");
                var=input.nextInt();
            }
            voting.add(var);
        }
        for(int i=2;i<=total;i++) {
            if(alive[i]) {
                voting.add(p.get(i).vote(alive));
            }
        }
        temp=voting.get(1);
        Collections.sort(voting);
        int cur=1;
        int ele=-1;
        int maxi=-1;
        for(int i=0;i<voting.size();i++) {
            if(i!=0) {
                if(voting.get(i)==voting.get(i-1)) {
                    cur+=1;
                }
                else {
                    maxi=Math.max(cur,maxi);
                    if(maxi==cur) {
                        ele=voting.get(i-1);
                    }
                    cur=1;
                }
            }
        }
        ele=temp;
        if(alive[ele]) {
            System.out.println("Player"+ele+ " has been voted out");
            alive[ele]=false;
            left-=1;
        }
    }
    private boolean detectivesAlive() {
        for(int i=1;i<=total;i++) {
            Player x=p.get(i);
            if(alive[i] && x.equals(detective)) {
                return true;
            }
        }
        return false;
    }
    private boolean gameOver() {
        int maf=0;
        for(int i=1;i<=total;i++) {
            Player x=p.get(i);
            if(x instanceof Mafia && alive[i]) {
                maf++;
            }
        }
        if(maf>=left-maf) {
            System.out.println("Mafias win");
            tellAll();
            return true;
        }
        else if(maf==0) {
            System.out.println("Mafias lose");
            tellAll();
            return true;
        }
        else {
            return false;
        }
    }
    private void tellAll() {
        for(int i=1;i<=total;i++) {
            Player x=p.get(i);
            if(x instanceof Mafia) {
                System.out.print("Player"+i+" , ");
            }
        }
        System.out.println("were Mafia");
        for(int i=1;i<=total;i++) {
            Player x=p.get(i);
            if(x instanceof Detective) {
                System.out.print("Player"+i+" , ");
            }
        }
        System.out.println("were Detectives");
        for(int i=1;i<=total;i++) {
            Player x=p.get(i);
            if(x instanceof Healer) {
                System.out.print("Player"+i+" , ");
            }
        }
        System.out.println("were Healer");
        for(int i=1;i<=total;i++) {
            Player x=p.get(i);
            if(x instanceof Commoner) {
                System.out.print("Player"+i+" , ");
            }
        }
        System.out.println("were Commoners");
    }
    private void decreaseMafiasHp(int dec,int y) {
        Collections.sort(mfs,new SortByHp());
        int overallDamage=y;
        int i=1;
        int maxi=total*3;
        while(overallDamage>0) {
            Player x=p.get(i);
            if(x instanceof Mafia && alive[i]) {
                int var=x.getHp();
                if(var>=dec) {
                    x.setHp(var-dec);
                    overallDamage-=dec;
                }
                else {
                    x.setHp(0);
                    overallDamage-=var;
                }
            }
            if(i<total) {
                i+=1;
            }
            else if(i==total){
                i=1;
            }
            maxi-=1;
            if(maxi==0) {
                overallDamage=0;
            }
        }
    }
    private int mafiasLeft() {
        int h=0;
        for(int i=1;i<=total;i++) {
            Player x=p.get(i);
            if(x instanceof Mafia && alive[i]) {
                h+=1;
            }
        }
        return h;
    }
    private int mafiaHp() {
        int h=0;
        for(int i=1;i<=total;i++) {
            Player x=p.get(i);
            if(x instanceof Mafia && alive[i]) {
                h+=x.getHp();
            }
        }
        return(h);
    }
    private void printSimilar() {
        ArrayList<Integer> arr=new ArrayList<Integer>();
        for(int i=2;i<=total;i++) {
            Player x=p.get(i);
            if(x instanceof Mafia && this.user instanceof Mafia) {
                arr.add(i);
            }
            else if(x instanceof Detective && this.user instanceof Detective) {
                arr.add(i);
            }
            else if(x instanceof Healer && this.user instanceof Healer){
                arr.add(i);
            }
        }
        if(this.user instanceof Mafia) {
            System.out.print("Other Mafias are: [ ");
            for(int i:arr) {
                System.out.print("Player "+i+" , ");
            }
            System.out.println("] ");
        }
        if(this.user instanceof Healer) {
            System.out.print("Other Healers are: [ ");
            for(int i:arr) {
                System.out.print("Player "+i+" , ");
            }
            System.out.println("] ");
        }
        if(this.user instanceof Detective) {
            System.out.print("Other Detectives are: [ ");
            for(int i:arr) {
                System.out.print("Player "+i+" , ");
            }
            System.out.println("] ");
        }
    }
    private void generatePlayer() {
        int healers=0;
        int mafias=0;
        int detectives=0;
        if(user instanceof Healer)
        {
            healers=Math.max(1,(int)total/10)-1;
        }
        else {
            healers=Math.max(1,(int)total/10);
        }
        if(user instanceof Mafia)
        {
            mafias=(int)total/5-1;
        }
        else {
            mafias=(int)total/5;
        }
        if(user instanceof Detective)
        {
            detectives=(int)total/5-1;
        }
        else {
            detectives=(int)total/5;
        }

        Set<Integer> s=new LinkedHashSet<Integer>();
        while(s.size()<total-1) {
            int x=2+rand.nextInt(total-1);
            s.add(x);
        }
        int start=2;
        Iterator<Integer> i=s.iterator();
        while(i.hasNext()) {
            if(healers>0) {
                healers-=1;
                p.put(i.next(),new Healer(start));
            }
            else if(mafias>0) {
                mafias-=1;
                Mafia nm=new Mafia(start);
                p.put(i.next(),nm);
                mfs.add(nm);
            }
            else if(detectives>0) {
                detectives-=1;
                p.put(i.next(),new Detective(start));
            }
            else {
                p.put(i.next(),new Commoner(start));
            }
            start+=1;
        }
    }
}
abstract class Player{
    protected int pNo;
    protected String type;
    protected int hp=0;
    protected Random rand=new Random();
    protected Scanner input=new Scanner(System.in);
    protected int playerNo;

    protected int vote(boolean alive[]) {
        return(validRandom(alive));
    }
    protected int validInput(boolean alive[]) {
        int n=input.nextInt();
        if(n<0 ||n>alive.length-1 || !alive[n]) {
            while(n>alive.length-1 || !alive[n] || n<0) {
                System.out.println("Either the player you entered has already died or is out of bounds. Choose someone else.");
                n=input.nextInt();
            }
        }
        return n;
    }
    protected int validRandom(boolean alive[]) {		//invalid is not considered
        int n=1+rand.nextInt(alive.length-1);
        if(n<0 ||n>alive.length-1 || !alive[n]) {
            while(n>alive.length-1 || !alive[n]) {
                n=1+rand.nextInt(alive.length-1);
            }
        }
        return n;
    }
    public String toString() {				//tostring object class
        String v="";
        v+="You are a ";
        if(this instanceof Mafia) {
            v+="Mafia. ";
        }
        else if(this instanceof Detective) {
            v+="Detective. ";
        }
        else if(this instanceof Healer) {
            v+="Healer. ";
        }
        else if(this instanceof Commoner) {
            v+="Commoner. ";
        }
        return(v);
    }
    public boolean equals(Object o) {		//equals for object class
        if(o!=null && getClass()==o.getClass() ) {
            return(true);
        }
        return false;
    }
    abstract int getHp();
    abstract void setHp(int h);
    abstract int choosePlayerForRole(HashMap<Integer,Player> p,boolean b,boolean alive[]);

}
class SortByHp implements Comparator<Player>{	//Comparator for sorting mafias on basis of hp
    @Override
    public int compare(Player o1, Player o2) {
        return o1.getHp()-o2.getHp();
    }
}
class Commoner extends Player{
    Commoner(int n){
        super();
        hp=1000;
        playerNo=n;
    }
    @Override
    public int getHp(){
        return hp;
    }
    @Override
    public void setHp(int h){
        this.hp=h;
    }

    @Override
    public int choosePlayerForRole(HashMap<Integer,Player> p,boolean b,boolean alive[]) {
        //no role
        return 0;
    }
}
class Healer extends Player{
    Healer(int n){
        super();
        hp=800;
        playerNo=n;
    }
    @Override
    public void setHp(int h){
        this.hp=h;
    }
    @Override
    public int getHp(){
        return hp;
    }
    @Override
    public int choosePlayerForRole(HashMap<Integer, Player> p,boolean b,boolean alive[]) {
        int heal=0;
        if(b) {
            System.out.print("Choose a player to heal ");
            heal=validInput(alive);
        }
        else {
            heal=validRandom(alive);
        }
        System.out.println("Healers have chosen someone to heal.");
        return heal;
    }
}
class Detective extends Player{
    Detective(int n){
        super();
        hp=800;
        playerNo=n;
    }
    @Override
    public void setHp(int h){
        this.hp=h;
    }
    @Override
    public int getHp(){
        return hp;
    }
    @Override
    public int choosePlayerForRole(HashMap<Integer, Player> p,boolean b,boolean alive[]) {	//check that the query should not be a detective
        int test=0;
        int f=0;
        if(b) {
            System.out.print("Choose a player to test ");
            test=validInput(alive);

            if(p.get(test) instanceof Mafia) {
                System.out.println("Player "+test+" is a mafia");
                f=test;
            }
            else {
                System.out.println("Player "+test+" is not a mafia");
            }
        }
        else {
            test=validRandom(alive);
        }
        System.out.println("Detectives have chosen a player to test");		//this can be printed multiple times if invalid input is chosen
        return f;		//f=player number if mafia else 0
    }
}
class Mafia extends Player{
    Mafia(int n){
        super();
        hp=2500;
        playerNo=n;
    }
    @Override
    public void setHp(int h){
        this.hp=h;
    }
    @Override
    public int getHp(){
        return hp;
    }
    @Override
    public int choosePlayerForRole(HashMap<Integer, Player> p,boolean b,boolean alive[]) {
        int target=0;
        if(b) {
            System.out.print("Choose a target: ");
            target=validInput(alive);
        }
        else {
            target=validRandom(alive);
            System.out.println("Mafias have chosen a target. ");	//this can be printed multiple times if invalid input is chosen
        }
        return target;
    }
}
public class week3 {
    static Scanner input=new Scanner(System.in);
    static Random rand=new Random();
    public static void main(String[] args) {

        int n=0,f=0;
        int type=0;
        System.out.println("Welcome to Mafia");

        while(f!=1) {
            System.out.print("Enter Number of Players : ");
            n=input.nextInt();
            if(n>=6) {
                f=1;
            }
            else {
                System.out.println("Number of players should be greater than 5");
            }
        }
        f=0;
        System.out.println("Choose a Character");
        System.out.println("1) Mafia");
        System.out.println("2) Detective");
        System.out.println("3) Healer");
        System.out.println("4) Commoner");
        System.out.println("5) Assign Randomly");
        while(f!=1) {
            type=input.nextInt();
            if(type<6 && type>0) {
                f=1;
            }
            else {
                System.out.println("Enter a valid type between 1 to 5");
            }
        }
        if(type==5) {
            type=1+rand.nextInt(4);
        }
        if(type==1) {
            Mafia m=new Mafia(1);
            Gameplay<Mafia> g=new Gameplay<Mafia>(n,m);
            g.startGame();
        }
        else if(type==2) {
            Detective d=new Detective(1);
            Gameplay<Detective> g=new Gameplay<Detective>(n,d);
            g.startGame();
        }
        else if(type==3) {
            Healer h=new Healer(1);
            Gameplay<Healer> g=new Gameplay<Healer>(n,h);
            g.startGame();
        }
        else {
            Commoner c=new Commoner(1);
            Gameplay<Commoner> g=new Gameplay<Commoner>(n,c);
            g.startGame();
        }
    }
}
