import java.util.*;
import java.lang.*;
import java.io.*;

class Manager{
    private ArrayList<healthCareInstitute> h;
    private int numberOfHospital;
    private Patient[] p;
    private int enteredPatients=0;
    private int totalPatients;
    private int admittedPatients;
    private boolean[] removedPatient;

    public Manager(int n) {
        this.h=new ArrayList<healthCareInstitute>();
        this.numberOfHospital=0;
        this.p=new Patient[n];
        this.totalPatients=n;
        this.admittedPatients=0;
        this.removedPatient=new boolean[n];
    }
    public void addInstitute(healthCareInstitute h1) {
        h.add(h1);
        numberOfHospital++;

        int start=0;

        while(start<totalPatients && h1.getStatus()) {
            if(!p[start].isAdmitted()) {

                if(p[start].getOxygenLevel()>=h1.getAcceptedOxygenLevel()) {
                    admitPatient(p[start],h1,start);
                }
            }
            start+=1;
        }

        if(h1.getStatus()) {
            int exitt=0;
            start=0;
            while(exitt!=1 && h1.getVacantBeds()>0) {
                if(!p[start].isAdmitted()) {
                    if(p[start].getBodyTemp()>=h1.getAcceptedBodyTemp()) {
                        admitPatient(p[start],h1,start);
                    }
                }
                start+=1;
                if(start==totalPatients) {
                    exitt=1;
                }
            }
        }

    }
    public boolean allAdmitted() {
        if(admittedPatients==totalPatients) {
            return(true);
        }
        else {
            return(false);
        }
    }
    private void admitPatient(Patient pat,healthCareInstitute hea,int index) {

        admittedPatients+=1;

        Scanner input=new Scanner(System.in);

        System.out.println("Recovery days for patient with Id: "+Integer.toString(index+1));
        int reco=input.nextInt();

        p[index].setAdmitted(hea.getName());
        hea.decreaseVacantBeds(p[index],reco);

    }
    public void addPatient(Patient newp) {
        p[enteredPatients]=newp;
        enteredPatients++;
    }
    public void removedPatients() {

        System.out.println("The ID's of removed patients are as follows: ");

        for(int i=0;i<totalPatients;i++) {
            Patient p1=p[i];
            if(p[i].isAdmitted() && removedPatient[i]==false) {
                System.out.println(Integer.toString(i+1));
                removedPatient[i]=true;
            }
        }
    }
    public void allPatientsDisplay() {
        for(int i=0;i<totalPatients;i++) {
            if(removedPatient[i]==false) {
                p[i].nameAndId();
            }
        }
    }
    public void closedInstitutes() {

        System.out.println(" Names of Closed Institutes are ");

        int start=0;
        ArrayList<healthCareInstitute> temp=new ArrayList<healthCareInstitute>();
        while(start<numberOfHospital) {
            healthCareInstitute h1=h.get(start);
            if(h1.getStatus()==false) {
                System.out.println(h1.getName());
                temp.add(h1);
            }
            start+=1;
        }

        for(healthCareInstitute i: temp) {
            h.remove(i);
            numberOfHospital-=1;
        }
    }
    public void patientRecovery(String s) {

        boolean found=false;
        int start=0;

        while(found!=true && start<numberOfHospital) {
            healthCareInstitute h1=h.get(start);
            System.out.println(h1.getName());
            if(s.equals(h1.getName())) {

                found=true;
            }
            else {
                start+=1;
            }
        }

        if(found==false && start==numberOfHospital) {
            System.out.println("This institute does not exist");

        }
        else {
            healthCareInstitute h1=h.get(start);
            h1.recoveryDates();
        }

    }
    public void uidDetails(int id) {
        p[id-1].allDetails();
    }
    public void instituteDetails(String s) {

        boolean found=false;
        int start=0;

        while(found!=true && start<numberOfHospital) {
            healthCareInstitute h1=h.get(start);
            if(s.equals(h1.getName())) {

                found=true;
            }
            else {
                start+=1;
            }
        }

        if(found==false && start==numberOfHospital) {
            System.out.println("This institute does not exist");

        }
        else {
            healthCareInstitute h1=h.get(start);
            h1.displayInstituteDetails();
        }

    }
    public void currentlyAdmitting() {
        int vari=0;
        for(int i=0;i<numberOfHospital;i++) {
            healthCareInstitute h1=h.get(i);

            if(h1.getStatus()) {
                vari+=1;
            }
        }

        System.out.println("Number of Hospital currently admitting patients are: "+vari);

    }

    public void nonAdmittedPatients() {

        System.out.println("Number of patients not admitted is: "+Integer.toString(totalPatients-admittedPatients));
    }
}
class Patient{
    final private int age,uid;
    final private String name;
    private float bodyTemperature;
    private int oxygenLevel;
    private boolean admitted;		//true means admitted and false means not yet admitted
    private String instituteName;

    public Patient(String[] x,int id){
        uid=id;
        name=x[0];
        bodyTemperature=Float.parseFloat(x[1]);
        oxygenLevel=Integer.parseInt(x[2]);
        age=Integer.parseInt(x[3]);
        admitted=false;
        instituteName=null;
    }

    public void nameAndId(){
        System.out.println("ID: "+this.uid+" Name: "+this.name);
    }

    public void allDetails() {
        System.out.println("ID: " + this.uid+" Name: "+this.name + " Age: "+this.age);
        System.out.println(" Temperature: " + this.bodyTemperature+" Oxygen Level: "+this.oxygenLevel );

        if(admitted==true) {
            System.out.println("Admitted Institute name "+instituteName);
        }
        else {
            System.out.println("Not admitted yet");
        }

    }
    public boolean isAdmitted() {
        return(admitted);
    }
    public float getBodyTemp() {
        return(bodyTemperature);
    }
    public int getOxygenLevel() {
        return(oxygenLevel);
    }
    public void setAdmitted(String s) {
        admitted=true;
        this.instituteName=s;
    }
    public String getName() {
        return(name);
    }

}
class healthCareInstitute{

    final private String name;
    final private int acceptedOxygenLevel;
    final private float acceptedBodyTemp;
    private int bedsVacant;
    private boolean status=true;		//true for admitting and false for close
    private ArrayList<Patient> listOfPatients=new ArrayList<Patient>();
    private ArrayList<Integer> recoveryDaysList=new ArrayList<Integer>();
    private int patientsNumber=0;

    public healthCareInstitute(String s,int o,float t,int b) {
        this.acceptedBodyTemp=t;
        this.acceptedOxygenLevel=o;
        this.bedsVacant=b;
        this.name=s;
    }

    public void displayInstituteDetails() {
        System.out.println(" Name: "+this.name+" Accepted oxygen level: (>=)"+this.acceptedOxygenLevel);
        System.out.println(" Accepted body temperature (<=): "+this.acceptedBodyTemp+" Vacant Beds: "+this.bedsVacant);

        if(status==true) {
            System.out.println("Admission status: Open");
        }
        else {
            System.out.println("Admission status: Closed");
        }
    }
    public String getName() {
        return(name);
    }
    public boolean getStatus() {
        return(status);
    }
    public int getVacantBeds() {
        return(bedsVacant);
    }
    public int getAcceptedOxygenLevel() {
        return(acceptedOxygenLevel);
    }
    public float getAcceptedBodyTemp() {
        return(acceptedBodyTemp);
    }
    private void setVacantBeds() {
        bedsVacant-=1;
    }
    public void decreaseVacantBeds(Patient p1,int recoveryDays) {
        listOfPatients.add(p1);
        recoveryDaysList.add(recoveryDays);
        setVacantBeds();
        patientsNumber++;
        if(bedsVacant==0) {
            status=false;
        }
    }
    public void recoveryDates() {
        for(int i=0;i<patientsNumber;i++) {
            System.out.println(listOfPatients.get(i).getName()+" recovery time in days : "+recoveryDaysList.get(i));
        }
    }

}
public class week1 {

    public static void main(String[] args) {
        Scanner input=new Scanner(System.in);
        int n=input.nextInt();
        Manager manager=new Manager(n);


        for(int i=0;i<n;i++) {
            String[] fin=new String[4];

            for(int j=0;j<4;j++) {
                fin[j]=input.next();
            }
            //System.out.println(fin[0]);
            Patient p=new Patient(fin,i+1);
            manager.addPatient(p);
        }

        while(manager.allAdmitted()!=true) {
            int ch=input.nextInt();

            switch(ch) {
                case 1:
                    System.out.println("Enter institute name");
                    String s=input.next();

                    System.out.println("Enter accepted body temperature");
                    float temp=input.nextFloat();

                    System.out.println("Enter accepted oxygen partner");
                    int oxyg=input.nextInt();

                    System.out.println("Enter number of beds available");
                    int beds=input.nextInt();

                    healthCareInstitute h1=new healthCareInstitute(s,oxyg,temp,beds);

                    manager.addInstitute(h1);

                    System.out.println("Name "+s);
                    System.out.println("Temperature criteria "+temp);
                    System.out.println("Oxygen Level "+oxyg);
                    System.out.println("No of available beds "+beds);


                    break;

                case 2:
                    manager.removedPatients();
                    break;

                case 3:
                    manager.closedInstitutes();
                    break;

                case 4:
                    manager.nonAdmittedPatients();
                    break;

                case 5:
                    manager.currentlyAdmitting();
                    break;

                case 6:
                    String inp6=input.next();
                    manager.instituteDetails(inp6);

                    break;

                case 7:
                    System.out.println("Enter id of patient: ");
                    int inp7=input.nextInt();

                    manager.uidDetails(inp7);

                    break;

                case 8:
                    manager.allPatientsDisplay();
                    break;

                case 9:
                    String inp9=input.next();
                    manager.patientRecovery(inp9);

                    break;

                default:
                    break;
            }
        }

    }

}
