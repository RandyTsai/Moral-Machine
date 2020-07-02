/**
 * Authorship
 * COMP90041 Final Project
 * Author: YuWei Tsai
 * StudentID: 1071545
 * Username: yttsai
 */
/**========================CODE BEGIN==========================*/
package ethicalengine;

public abstract class Character {

    public enum Gender {
        FEMALE,
        MALE,
        UNKNOWN;
    }

    public enum BodyType {
        AVERAGE,
        ATHLETIC,
        OVERWEIGHT,
        UNSPECIFIED;
    }

    private int age;
    private Gender gender;
    private BodyType bodyType;


    /**
     * Constructors
     */
    public Character() {
        this.age = 0;
        this.gender = Gender.UNKNOWN;
        this.bodyType = BodyType.UNSPECIFIED;

    }

    public Character(int age, Gender gender, BodyType bodyType) {
        this.age = Math.abs(age); // always be >=0
        this.gender = gender;
        this.bodyType = bodyType;
    }

    public Character(Character c) {  //copy constructor???
        this.age = c.age;
        this.gender = c.gender;
        this.bodyType = c.bodyType;
    }


    /**
     * Getters
     */
    public int getAge() {
        return this.age;
    }

    public Gender getGender() {
        return this.gender;
    }

    public BodyType getBodyType() {
        return this.bodyType;
    }

    /**
     * Setters
     */
    public void setAge(int i) {
        this.age = i;
    }

    public void setGender(Gender g) {
        this.gender = g;
    }

    public void setBodyType(BodyType bt) {
        this.bodyType = bt;
    }

}
/**========================CODE END==========================*/