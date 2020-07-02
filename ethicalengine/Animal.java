/**
 * Authorship
 * COMP90041 Final Project
 * Author: YuWei Tsai
 * StudentID: 1071545
 * Username: yttsai
 */
/**========================CODE BEGIN==========================*/
package ethicalengine;

public class Animal extends Character {


    public enum enumSpecies { //different from document, this is work rather then required
        cat,
        dog,
        crocodile,
        tortoise,
        bird,
        rat;
    }

    private String Species;  //???I dont understand when this string be set,
    // and how it work to be audited from adding to enum list, due to the ambiguous description.
    private boolean isPet;


    /**
     * Constructors
     * @param espec
     * @param ispet
     */
    public Animal(enumSpecies espec, boolean ispet) {//different from document, this is work rather then required

        this.Species = espec.name();
        this.isPet = ispet;
    }

    public Animal(Animal otherAnimal) {
        super(otherAnimal.getAge(), otherAnimal.getGender(), otherAnimal.getBodyType());
        this.Species = otherAnimal.Species;
        this.isPet = otherAnimal.isPet;
    }


    /**
     * Methods Begin
     *
     * getters & setters & other basic methods
     */
    public String getSpecies() {
        return this.Species;
    }

    public void setSpecies(String species) {
        this.Species = getSpecies(); //what if no default value
    }

    public boolean isPet() {
        return this.isPet;
    }

    public void setPet(Boolean isPet) {
        this.isPet = isPet;
    }

    public String toString() {
        String str = "";
        str += getSpecies() + " ";
        str += isPet() == true ? "is pet" : "";
        return str;
    }


}
/**========================CODE END==========================*/