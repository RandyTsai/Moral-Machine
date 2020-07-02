/**
 * Authorship
 * COMP90041 Final Project
 * Author: YuWei Tsai
 * StudentID: 1071545
 * Username: yttsai
 */
/**========================CODE BEGIN==========================*/
package ethicalengine;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class ScenarioGenerator {

    private int dftMax = 5, dftMin = 1;
    private int PassengerCountMin = dftMin, PassengerCountMax = dftMax;
    private int PedestrianCountMin = dftMin, PedestrianCountMax = dftMax;

    private Person P;
    private Animal A;
    private Scenario S;
    Random rd = new Random();


    /**
     * Constructors
     */
    public ScenarioGenerator() {
        rd.setSeed(rd.nextLong());
    } //set the seed randomly

    public ScenarioGenerator(long seed) {
        rd.setSeed(seed);
    } //set the seed if you call & passIn

    public ScenarioGenerator(long seed, int passengerCountMinimum, int passengerCountMaximum,
                             int pedestrianCountMinimum, int pedestrianCountMaximum) { //set the seed and all value as passedIn
        rd.setSeed(seed);
        if (passengerCountMaximum > passengerCountMinimum) {
            this.PassengerCountMax = passengerCountMaximum;
            this.PassengerCountMin = passengerCountMinimum;
        } else {
            System.out.println("Min cannot larger than Max");
        }
        if (pedestrianCountMaximum > pedestrianCountMinimum) {
            this.PedestrianCountMax = pedestrianCountMaximum;
            this.PedestrianCountMin = pedestrianCountMinimum;
        } else {
            System.out.println("Min cannot larger than Max");
        }
    }


    /**
     * Methods Begin
     *
     * getters & setters & other basic methods
     */
    public void setPassengerCountMin(int min) {
        this.PassengerCountMin = min;
    }

    public void setPassengerCountMax(int max) {
        this.PassengerCountMax = max;
    }

    public void setPedestrianCountMin(int min) {
        this.PedestrianCountMin = min;
    }

    public void setPedestrianCountMax(int max) {
        this.PedestrianCountMax = max;
    }

    /**
     * new a person with random features by every invoke
     * @return
     */
    public Person getRandomPerson() {
        //Randomize all characteristics of a person
        int age = (int) (rd.nextDouble() * 100); //within 100y old
        int gd = (int) Math.round(rd.nextDouble() * (Person.Gender.values().length - 1));
        int bt = (int) Math.round(rd.nextDouble() * (Person.BodyType.values().length - 1));
        int pf = (int) Math.round(rd.nextDouble() * (Person.Profession.values().length - 1));
        boolean isPreg = rd.nextBoolean();

        //Construct a person with random characteristic
        //Person(int age, Profession profession, Gender gender, BodyType bodytype, boolean isPregnant)
        P = new Person(age, Person.Profession.values()[pf], Person.Gender.values()[gd], Person.BodyType.values()[bt], isPreg);
        return P;
    }

    /**
     * new an animal with random features by every invoke
     * @return
     */
    public Animal getRandomAnimal() {
        //Randomize all characteristics of an animal
        int spc = (int) Math.round(rd.nextDouble() * (Animal.enumSpecies.values().length - 1));
        boolean isPet = rd.nextBoolean();

        //Construct an animal with random characteristic
        //Animal(int age, Gender g, BodyType b, enumSpecies espec, boolean ispet)
        A = new Animal(Animal.enumSpecies.values()[spc], isPet);
        return A;
    }


    /**
     * Generate method is to generate scenarios and handle random parameters inputted in
     * @return returns a newly created instance Scenario
     */
    public Scenario generate() {

        //generate random number of Passenger(all human)
        ArrayList liPsg = new ArrayList();
        int totalNumOfPsg = (int) (rd.nextDouble() * PassengerCountMax + PassengerCountMin); //at least one Psg
        for (int i = 0; i < totalNumOfPsg; i++) {
            liPsg.add(getRandomPerson());
        }
        //System.out.println("PassengerNum= "+totalNumOfPsg);


        //generate random number of Pedestrians(with Max 5) & allocate person+animal randomly(human+animal)
        ArrayList liPdt = new ArrayList();
        int totalNumOfPdst = (int) (rd.nextDouble() * PedestrianCountMax + PedestrianCountMin);//at least one Pdg
        int NumOfPerson = (int) Math.round(rd.nextDouble() * totalNumOfPdst);//if only 1 pdt,along with randomDouble<0.5 , 0.4x*1 rounded=0, will only an animal
        //so if you are random being in pdt but pdt only an animal, you neither wont be in scenario, so your negative probability in scenario is higher.
        int NumOfAnimal = totalNumOfPdst - NumOfPerson;
        for (int i = 0; i < NumOfPerson; i++) {
            liPdt.add(getRandomPerson());
        }
        for (int i = 0; i < NumOfAnimal; i++) {
            liPdt.add(getRandomAnimal());
        }
        //System.out.println("PedestrianNum= "+totalNumOfPdst+" with "+NumOfPerson+" human and "+NumOfAnimal+" animals");


        //allocating where you are, 0=you absence & do nothing, 1=you in car, 2=you on road    !!!!yeah happy solving
        int allocate_of_user = (int) Math.round(rd.nextDouble() * 2);
        if (allocate_of_user == 1) {
            ((Person) liPsg.get(0)).setAsYou(true);  //set the first psg is you.    !!!!!important Cast Obj into Person!!!!
        } else if (allocate_of_user == 2) {
            for (int i = 0; i < liPdt.size(); i++) {
                if (liPdt.get(i) instanceof Person) {//check whether a person
                    ((Person) liPdt.get(i)).setAsYou(true);
                    break; // only set once than break rather than set all of people of pdt is you
                }
            }
        }

        //Scenario(ArrayList<Character> passengers, ArrayList<Character> pedestrians, boolean isLegalCrossing)
        S = new Scenario(liPsg, liPdt, rd.nextBoolean()); //isLegalCrossing = red or green light;
        return S;
    }


}
/**========================CODE END==========================*/