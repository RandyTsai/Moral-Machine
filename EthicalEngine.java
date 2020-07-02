/**
 * Authorship
 * COMP90041 Final Project
 * Author: YuWei Tsai
 * StudentID: 1071545
 * Username: yttsai
 */
/**========================CODE BEGIN==========================*/
import ethicalengine.*;
import ethicalengine.Character;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

public class EthicalEngine {

    public static final String ENCODE = "utf-8";
    public static Scanner SC = new Scanner(System.in);



    public enum Decision {
        PEDESTRIANS,
        PASSENGERS;
    }



    /**
     * the main method
     * @param args
     */
    public static void main(String[] args) {




        /**Print Welcome message*/
        try {
            BufferedReader br = new BufferedReader(new FileReader("welcome.ascii"));
            int iLine = 0;
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch( IOException e ){
            System.out.println(e.getMessage());
        }

        /**Ask consent*/
        System.out.println("Do you consent to have your decisions saved to a file? (yes/no)");
        boolean toSaveFile;
        String userConsent=SC.nextLine();
        while(true){
            if(userConsent.equalsIgnoreCase("yes")){
                toSaveFile = true;
                break;
            }else if(userConsent.equalsIgnoreCase("no")){
                toSaveFile = false;
                break;
            }else{
                try{
                    throw new InvalidInputException();
                }catch (InvalidInputException e){System.out.println(e.getMessage());}
                userConsent=SC.nextLine();
            }
        }







        /**!!!!!!NOTICE!!!!! I didnt done the interaction part, So you either run file by manually select below, or  */
        /**started to open and read csv configuration file **with manually select the file name** */
        try{

            InputStreamReader isr = new InputStreamReader( new FileInputStream("config.csv"));//fix file, yet done point5 "Interactive Scenario"
            BufferedReader reader = new BufferedReader(isr);
            //BufferedWriter writer = new BufferedWriter( new FileWriter("./fileOutput.csv"));


            /**Started to read by line*/
            String line;
            boolean isLegalCross=false;
            ArrayList<Scenario> inputScenarios = new ArrayList<Scenario>();
            ArrayList<Character> inputPassenger = new ArrayList<Character>();
            ArrayList<Character> inputPedestrian = new ArrayList<Character>();
            int lineCount = 0;
            int scenarioCount = 0;
            while((line = reader.readLine()) !=null){
                lineCount++;

                String words[] = line.split(",|:");//split words by different symbol
                //if words in a row exceed 10, warning and skip
                if(words.length>10){
                    try{
                        throw new InvalidDataFormatException(lineCount);
                    }catch (InvalidDataFormatException e){System.out.println(e.getMessage());}
                    continue;
                }
                //System.out.println(words.length);



                /**started to judge the first word in each line as keyword*/
                if(words[0].equalsIgnoreCase("scenario")){
                    if(scenarioCount>0){//means there are psg/pdt values stored for the former scenario
                        Scenario S = new Scenario(inputPassenger, inputPedestrian, isLegalCross);
                        inputScenarios.add(S);
                    }
                    scenarioCount++;
                    inputPassenger = new ArrayList<Character>();
                    inputPedestrian = new ArrayList<Character>();
                    if( words[1].equalsIgnoreCase("green")) isLegalCross = true;

                }else if(words[0].equalsIgnoreCase("person")){

                    Character.Gender gd= null;
                    for(Character.Gender tmpEnum : Character.Gender.values()){
                        if(words[1].equalsIgnoreCase(tmpEnum.name())) gd = tmpEnum;
                    }//run out of list but no match? gd will be null
                    if(gd==null){//means invalid Characteristic
                        try{
                            throw new InvalidCharacteristicException(lineCount);
                        }catch (InvalidCharacteristicException e){
                            System.out.println(e.getMessage());
                        }
                        gd= Character.Gender.UNKNOWN; //assign default value
                    }


                    int age = 0;
                    try{
                        age = Integer.parseInt(words[2]);
                        //throw new NumberFormatException(lineCount);
                    }catch (NumberFormatException e){
                        System.out.println("WARNING: invalid characteristic in config file in line < "+lineCount+" >");
                    }


                    Character.BodyType bdt=null;
                    for(Character.BodyType tmpEnum : Character.BodyType.values()){
                        if(words[3].equalsIgnoreCase(tmpEnum.name())) bdt = tmpEnum;
                    }
                    if(bdt==null){//means invalid Characteristic
                        try{
                            throw new InvalidCharacteristicException(lineCount);
                        }catch (InvalidCharacteristicException e){
                            System.out.println(e.getMessage()+"bodytype");
                        }
                        bdt= Character.BodyType.UNSPECIFIED; //assign default value
                    }


                    Person.Profession pf=null;
                    for(Person.Profession tmpEnum : Person.Profession.values()){
                        if(words[4].equalsIgnoreCase(tmpEnum.name())) pf = tmpEnum;
                    }//profession do not have to specify


                    boolean isPreg = Boolean.parseBoolean(words[5]);

                    boolean isYou = Boolean.parseBoolean(words[6]);

                    //Construct a person with random characteristic
                    //Person(int age, Profession profession, Gender gender, BodyType bodytype, boolean isPregnant)
                    Person P = new Person(age, pf, gd, bdt, isPreg);
                    if(isYou) P.setAsYou(true);
                    if(words[9].equalsIgnoreCase("passenger")) {inputPassenger.add(P);}else{inputPedestrian.add(P);}

                }else if(words[0].equalsIgnoreCase("animal")){

                    Animal.enumSpecies spc=null;
                    for(Animal.enumSpecies tmpEnum : Animal.enumSpecies.values()){
                        if(words[7].equalsIgnoreCase(tmpEnum.name())) spc = tmpEnum;
                    }
                    if(spc==null){//means invalid Characteristic
                        try{
                            throw new InvalidCharacteristicException(lineCount);
                        }catch (InvalidCharacteristicException e){
                            System.out.println(e.getMessage()+"species");
                        }
                        spc = Animal.enumSpecies.dog; //assign default value
                    }


                    boolean isPet = Boolean.parseBoolean(words[8]);

                    //Construct an animal with random characteristic
                    //Animal(int age, Gender g, BodyType b, enumSpecies espec, boolean ispet)
                    Animal A = new Animal(spc, isPet);
                    if(words[9].equalsIgnoreCase("passenger")) {inputPassenger.add(A);}else{inputPedestrian.add(A);}

                }

            }
            Scenario S = new Scenario(inputPassenger, inputPedestrian, isLegalCross); //use aggregated data above to construct scenario
            inputScenarios.add(S);

            Audit AD = new Audit(inputScenarios);
            AD.run();
            AD.printStatistic();
            AD.printToFile("");


        }catch (FileNotFoundException e){ e.printStackTrace();
        }catch (IOException e ){e.printStackTrace();}





        /**!!!!!!NOTICE!!!!! or run random scenario by doing below, and block the read file code above */
        /*
        Audit AD = new Audit();
        AD.run(30);
        AD.printStatistic();
        AD.run(20);
        AD.printStatistic();
        AD.run(40);
        AD.printStatistic();
        AD.printToFile("");
        */



    }






    /**
     * decide method, conditional judgement of passengers or pedestrians are survived
     * @param scenario
     * @return
     */
    public static Decision decide(Scenario scenario) {
        boolean psgIsAnyPregnant = false;
        boolean pdtIsAnyPregnant = false;

        //condition1: anyone in either side is pregnant, be saved
        for (ethicalengine.Character c : scenario.getPassengers()) {
            if (c instanceof Person && ((Person) c).isPregnant()) psgIsAnyPregnant = true;
        }
        for (ethicalengine.Character c : scenario.getPedestrians()) {
            if (c instanceof Person && ((Person) c).isPregnant()) pdtIsAnyPregnant = true;
        }
        if (psgIsAnyPregnant) return Decision.PASSENGERS;
        if (pdtIsAnyPregnant) return Decision.PEDESTRIANS;

        //no one pregnant
        if (scenario.isLegalCrossing()) {// condition2: green light, pedestrian has right. condition3: so there is any human pedestrian, be saved
            for (ethicalengine.Character c : scenario.getPedestrians()) {
                if (c instanceof Person) return Decision.PEDESTRIANS;
            }
            return Decision.PASSENGERS;
        } else {// condition4: red light, passenger has right. condition5: but there is any child, cannot bump
            for (ethicalengine.Character c : scenario.getPedestrians()) {
                if (c instanceof Person && ((Person) c).getAgeCategory().equals(Person.AgeCategory.CHILD))
                    return Decision.PEDESTRIANS;
            }
            return Decision.PASSENGERS;
        }

    }


}






/**
 * customized Exception
 */
class InvalidDataFormatException extends Exception{
    public InvalidDataFormatException(int linecount) {
        super("WARNING: invalid data format in config file in line < "+linecount+" >");
    }
}

class InvalidCharacteristicException extends Exception{
    public InvalidCharacteristicException(int linecount){
        super("WARNING: invalid characteristic in config file in line < "+linecount+" >");
    }
}

class InvalidInputException extends Exception{
    public InvalidInputException(){
        super("Invalid response. Do you consent to have your decisions saved to a file? (yes/no)");
    }
}


/**========================CODE END==========================*/