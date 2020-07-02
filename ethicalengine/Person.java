/**
 * Authorship
 * COMP90041 Final Project
 * Author: YuWei Tsai
 * StudentID: 1071545
 * Username: yttsai
 */
/**========================CODE BEGIN==========================*/
package ethicalengine;

public class Person extends Character {

    public enum AgeCategory {
        BABY,
        CHILD,
        ADULT,
        SENIOR;
    }

    public enum Profession {
        DOCTOR,
        TEACHER,//I task
        CEO,
        SELLER,//I task
        CRIMINAL,
        HOMELESS,
        UNEMPLOYED,
        UNKNOWN;
    }

    private boolean isPregnant;
    //private AgeCategory category;
    private Profession profession = Profession.UNKNOWN;
    private boolean asYou = false;


    /**
     * Constructors
     * @param age
     * @param profession
     * @param gender
     * @param bodytype
     * @param isPregnant
     */
    public Person(int age, Profession profession, Gender gender, BodyType bodytype, boolean isPregnant) {
        super(age, gender, bodytype);
        this.profession = getAgeCategory() == AgeCategory.ADULT ? this.profession : Profession.UNKNOWN;
        this.isPregnant = (gender == Gender.FEMALE) ? isPregnant : false; //female can be true/false, otherwise false
    }

    public Person(Person otherPerson) {
        super(otherPerson.getAge(), otherPerson.getGender(), otherPerson.getBodyType());
        this.profession = otherPerson.profession;
        this.isPregnant = otherPerson.isPregnant;
    }


    /**
     * Methods Begin
     *
     * getters & setters & other basic methods
     */
    public AgeCategory getAgeCategory() {  //the way return enum
        int age = this.getAge();
        if (age >= 0 && age <= 4) {
            return AgeCategory.BABY;
        } else if (age >= 5 && age <= 16) {
            return AgeCategory.CHILD;
        } else if (age >= 17 && age <= 68) {
            return AgeCategory.ADULT;
        } else {
            return AgeCategory.SENIOR;
        }
    }

    public Profession getProfession() {
        return getAgeCategory() == AgeCategory.ADULT ? this.profession : Profession.UNKNOWN;
    }

    public boolean isPregnant() {
        return this.getGender() == Gender.FEMALE ? this.isPregnant : false;
    }

    public void setPregnant(boolean pregnant) {
        this.isPregnant = isPregnant();
    }

    public boolean isYou() {
        return this.asYou;
    }

    public void setAsYou(boolean isYou) {
        this.asYou = isYou;
    }


    @Override
    public String toString() {
        String str = "";
        str += this.isYou() ? "You " : "";
        str += this.getBodyType() + " ";
        str += this.getAgeCategory() + " ";
        str += this.getAgeCategory() == AgeCategory.ADULT ?
                this.getAgeCategory() + " " + this.profession : this.getAgeCategory();
        str += (this.getGender() == Gender.FEMALE && this.isPregnant == true) ?
                this.getGender() + " pregnant" : this.getGender();

        return str;
    }


}
/**========================CODE END==========================*/