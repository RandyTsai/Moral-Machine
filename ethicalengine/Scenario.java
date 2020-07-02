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

public class Scenario {

    private ArrayList<Character> pedestrians = new ArrayList<Character>();
    private ArrayList<Character> passengers = new ArrayList<Character>();
    private boolean LegalCrossing;


    /**
     * Constructors
     */
    public Scenario(ArrayList<Character> passengers, ArrayList<Character> pedestrians, boolean isLegalCrossing) {
        this.passengers = passengers;
        this.pedestrians = pedestrians;
        this.LegalCrossing = isLegalCrossing;
    }


    /**
     * Methods Begin
     *
     * getters & setters & other basic methods
     */
    public boolean hasYouInCar() {
        for (Character c : this.passengers) {
            if (c instanceof Person) {
                if (((Person) c).isYou()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasYouInLane() {
        for (Character c : this.passengers) {
            if (c instanceof Person) {
                if (((Person) c).isYou()) {
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<Character> getPassengers() {
        return this.passengers;
    }

    public ArrayList<Character> getPedestrians() {
        return this.pedestrians;
    }

    public boolean isLegalCrossing() {
        return this.LegalCrossing;
    }


    public void setLegalCrossing(boolean isLegalCrossing) {
        this.LegalCrossing = isLegalCrossing;
    }

    public int getPassengerCount() {
        return this.passengers.size();
    }

    public int getPedestrianCount() {
        return this.pedestrians.size();
    }

    @Override
    public String toString() {

        String str = "";
        str += "======================================\n";
        str += "# Scenario\n";
        str += "======================================\n";
        str += "Legal Crossing: " + (this.LegalCrossing ? "yes\n" : "no\n");// not sure need check
        str += "Passengers (" + this.passengers.size() + ")\n";
        for (Character c : this.passengers) {      // not sure need check
            str += "- " + c.toString() + "\n";
        }
        str += "Pedestrians (" + this.pedestrians.size() + ")\n";
        for (Character c : this.pedestrians) {     // not sure need check
            str += "- " + c.toString() + "\n";
        }
        return str;
    }

}
/**========================CODE END==========================*/