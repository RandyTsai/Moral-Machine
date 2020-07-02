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
import java.util.*;
import java.text.DecimalFormat;

public class Audit {

    private int totalRun;
    private String AuditType;
    DecimalFormat DF1 = new DecimalFormat("##.0");
    DecimalFormat DF2 = new DecimalFormat("##.00");
    ArrayList<Scenario> scenarios = new ArrayList<Scenario>();
    Map<String, Double> Sorted_resultMap = new LinkedHashMap<>(); //this map stores final result to print, be inserted value in run() method


    /**
     * Constructor
     */
    public Audit() {
        this.totalRun =0;
        this.AuditType = "Algorithm";
    }

    public Audit(ArrayList<Scenario> scenarios){// for configuration csv use
        this.totalRun = scenarios.size();
        this.AuditType = "Algorithm";
        this.scenarios = scenarios;
    }


    /**
     * Methods Begin
     *
     * getters & setters & other basic required methods
     */
    public void setAuditType(String name) { this.AuditType = name; }

    public String getAuditType() { return this.AuditType; }

    @Override
    public String toString() {


        if (totalRun == 0) {
            return "no audit available";
        } else {

            String str = "";
            str += "======================================\n";
            str += "# "+getAuditType()+" Audit\n";
            str += "======================================\n";
            str += "- % SAVED AFTER " + totalRun + " RUNS\n";
            for (Map.Entry<String, Double> entry : Sorted_resultMap.entrySet()) {
                if (!entry.getKey().equals("averageAge")) str += entry.getKey() + ": " + entry.getValue() + "\n";
            }
            str += "--\naverage age: " + Sorted_resultMap.get("averageAge") + "\n";
            return str;
        }


    }

    public void printStatistic() {
        System.out.println(this.toString());
        //sc.nextLine();
    }

    public void printToFile(String filepath) {
        if(filepath.equals("")){
            try {
                //write object to file
                FileOutputStream fos = new FileOutputStream("result.txt",true);//specify path and filename, "true" means appended if file already exist
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(this.toString());//print toString() method to the target
                oos.flush();
                oos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                File folder = new File("./"+filepath);
                if(!folder.exists()){
                    System.out.println("ERROR: could not print results. Target directory does not exist.");//or folder.mkdir();  construct folder with path
                    System.exit(0);
                }

                //write object to file
                FileOutputStream fos = new FileOutputStream(filepath+"\\user.log",true);//specify path and filename, "true" means appended if file already exist
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(this.toString());//print toString() method to the target
                oos.flush();
                oos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }



    }




    /**
     * the run method handles running scenarios and finally output a overall sorted_scores map
     * @param runs
     */
    public void run(int runs) {

        this.totalRun += runs;
        ScenarioGenerator SG = new ScenarioGenerator();

        /**started to access scenarios and count*/
        Map<String, HashMap> livePerson = new HashMap();
        Map<String, HashMap> liveAnimal = new HashMap();
        Map<String, HashMap> aliveMap = buildEptMap(livePerson, liveAnimal, "livePerson", "liveAnimal");

        Map<String, HashMap> diePerson = new HashMap();
        Map<String, HashMap> dieAnimal = new HashMap();
        Map<String, HashMap> dieMap = buildEptMap(diePerson, dieAnimal, "diePerson", "dieAnimal");
        int countSavedInRed=0, countSavedInGreen=0, countDiedInRed=0, countDiedInGreen=0;
        for (int i = 0; i < runs; i++) {
            //System.out.println("this is "+i+" run");
            Scenario tmpS = SG.generate();//get scenario
            EthicalEngine.Decision tmpD = EthicalEngine.decide(tmpS);//get decision
            //System.out.println(tmpD+" live~\n");

            /**through Passengers or Pedestrian into live or dead counting*/
            if (tmpD == EthicalEngine.Decision.PASSENGERS) {//passengers survived
                if(tmpS.isLegalCrossing()){//green light
                    countSavedInGreen+=tmpS.getPassengerCount();
                    countDiedInGreen+=tmpS.getPedestrianCount();
                }else{//red light
                    countSavedInRed+=tmpS.getPassengerCount();
                    countDiedInRed+=tmpS.getPedestrianCount();
                }
                ArrayList<ethicalengine.Character> liveChracter = tmpS.getPassengers();
                aliveMap = this.countLive(liveChracter, aliveMap);//prevent overwrite
                ArrayList<ethicalengine.Character> deadChracter = tmpS.getPedestrians();
                dieMap = this.countDie(deadChracter, dieMap);
            } else { //pedestrians survived
                if(tmpS.isLegalCrossing()){
                    countSavedInGreen+=tmpS.getPedestrianCount();
                    countDiedInGreen+=tmpS.getPassengerCount();
                }else{
                    countSavedInRed+=tmpS.getPedestrianCount();
                    countDiedInGreen+=tmpS.getPassengerCount();
                }
                ArrayList<ethicalengine.Character> liveChracter = tmpS.getPedestrians();
                aliveMap = this.countLive(liveChracter, aliveMap);
                ArrayList<ethicalengine.Character> deadChracter = tmpS.getPassengers();
                dieMap = this.countDie(deadChracter, dieMap);
            }
        }
        /* check all original count map*/
        //System.out.println("aliveList= \n"+aliveMap);
        //System.out.println("dieList= \n"+dieMap+"\n\n");
        //System.out.println("total survived in green= "+countSavedInGreen);
        //System.out.println("total survived in red= "+countSavedInRed);
        //System.out.println("total died in green= "+countDiedInGreen);
        //System.out.println("total died in red= "+countDiedInRed);


        /**through Passengers or Pedestrian into live or dead counting*/
        livePerson = aliveMap.get("livePerson");
        liveAnimal = aliveMap.get("liveAnimal");
        diePerson = dieMap.get("diePerson");
        dieAnimal = dieMap.get("dieAnimal");

        HashMap<String, Double> aggregatedHuman = new HashMap<String, Double>();
        HashMap<String, Double> aggregatedAnimal = new HashMap<String, Double>();
        aggregatedHuman = (HashMap<String, Double>) aggregate(aggregatedHuman, (HashMap<String, HashMap>)livePerson, (HashMap<String, HashMap>)diePerson);//cast to hashmapDouble than assign back
        aggregatedAnimal = (HashMap<String, Double>) aggregate(aggregatedAnimal, (HashMap<String, HashMap>)liveAnimal, (HashMap<String, HashMap>)dieAnimal);
        /*check all aggregated map of Ratio with all features*/
        //System.out.println("aggregatedHuman= \n"+aggregatedHuman);
        //System.out.println("aggregatedAnimal= \n"+aggregatedAnimal);

        /**merge aggregatedHuman and aggregatedAnimal into a final map to be printed*/
        Map<String, Double> resultMap = new HashMap<String, Double>(aggregatedHuman);
        aggregatedAnimal.forEach((key, value) -> resultMap.merge(key, value, Double::sum));
        //add red/green survival ratio
        double surviveRatioInRed= (countSavedInRed+countDiedInRed)==0? 0: (double)countSavedInRed / (countSavedInRed+countDiedInRed);
        double surviveRatioInGreen = (countSavedInGreen+countDiedInGreen)==0? 0:(double)countSavedInGreen / (countSavedInGreen+countDiedInGreen);
        resultMap.put("green", Double.parseDouble(DF2.format(surviveRatioInGreen)));
        resultMap.put("red", Double.parseDouble(DF2.format(surviveRatioInRed)));
        //System.out.println("\nresultMap = \n"+resultMap);

        /**sort resultMap by value*/
        resultMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEachOrdered(x -> Sorted_resultMap.put(x.getKey(), x.getValue()));
        //System.out.println("\n\nSorted_resultMap=\n"+Sorted_resultMap);

    }

    /**
     * the run method "for csv file" handles running scenarios and finally output a overall sorted_scores map
     */
    public void run() {

        /**started to access scenarios and count*/
        Map<String, HashMap> livePerson = new HashMap();
        Map<String, HashMap> liveAnimal = new HashMap();
        Map<String, HashMap> aliveMap = buildEptMap(livePerson, liveAnimal, "livePerson", "liveAnimal");

        Map<String, HashMap> diePerson = new HashMap();
        Map<String, HashMap> dieAnimal = new HashMap();
        Map<String, HashMap> dieMap = buildEptMap(diePerson, dieAnimal, "diePerson", "dieAnimal");
        int countSavedInRed=0, countSavedInGreen=0, countDiedInRed=0, countDiedInGreen=0;
        for (int i = 0; i < this.scenarios.size(); i++) {
            //System.out.println("this is "+i+" run");
            Scenario tmpS = this.scenarios.get(i);//get scenario
            EthicalEngine.Decision tmpD = EthicalEngine.decide(tmpS);//get decision
            //System.out.println(tmpD+" live~\n");

            /**through Passengers or Pedestrian into live or dead counting*/
            if (tmpD == EthicalEngine.Decision.PASSENGERS) {//passengers survived
                if(tmpS.isLegalCrossing()){//green light
                    countSavedInGreen+=tmpS.getPassengerCount();
                    countDiedInGreen+=tmpS.getPedestrianCount();
                }else{//red light
                    countSavedInRed+=tmpS.getPassengerCount();
                    countDiedInRed+=tmpS.getPedestrianCount();
                }
                ArrayList<ethicalengine.Character> liveChracter = tmpS.getPassengers();
                aliveMap = this.countLive(liveChracter, aliveMap);//prevent overwrite
                ArrayList<ethicalengine.Character> deadChracter = tmpS.getPedestrians();
                dieMap = this.countDie(deadChracter, dieMap);
            } else { //pedestrians survived
                if(tmpS.isLegalCrossing()){
                    countSavedInGreen+=tmpS.getPedestrianCount();
                    countDiedInGreen+=tmpS.getPassengerCount();
                }else{
                    countSavedInRed+=tmpS.getPedestrianCount();
                    countDiedInGreen+=tmpS.getPassengerCount();
                }
                ArrayList<ethicalengine.Character> liveChracter = tmpS.getPedestrians();
                aliveMap = this.countLive(liveChracter, aliveMap);
                ArrayList<ethicalengine.Character> deadChracter = tmpS.getPassengers();
                dieMap = this.countDie(deadChracter, dieMap);
            }
        }
        /* check all original count map*/
        //System.out.println("aliveList= \n"+aliveMap);
        //System.out.println("dieList= \n"+dieMap+"\n\n");
        //System.out.println("total survived in green= "+countSavedInGreen);
        //System.out.println("total survived in red= "+countSavedInRed);
        //System.out.println("total died in green= "+countDiedInGreen);
        //System.out.println("total died in red= "+countDiedInRed);


        /**through counted liveList and dieList into aggregating live ratio*/
        livePerson = aliveMap.get("livePerson");
        liveAnimal = aliveMap.get("liveAnimal");
        diePerson = dieMap.get("diePerson");
        dieAnimal = dieMap.get("dieAnimal");

        HashMap<String, Double> aggregatedHuman = new HashMap<String, Double>();
        HashMap<String, Double> aggregatedAnimal = new HashMap<String, Double>();
        //System.out.println("\n\ncheck\n"+diePerson);
        aggregatedHuman = (HashMap<String, Double>) aggregate(aggregatedHuman, (HashMap<String, HashMap>)livePerson, (HashMap<String, HashMap>)diePerson);//cast to hashMapDouble than assign back
        aggregatedAnimal = (HashMap<String, Double>) aggregate(aggregatedAnimal, (HashMap<String, HashMap>)liveAnimal, (HashMap<String, HashMap>)dieAnimal);
        /*check all aggregated map of Ratio with all features*/
        //System.out.println("aggregatedHuman= \n"+aggregatedHuman);
        //System.out.println("aggregatedAnimal= \n"+aggregatedAnimal);

        /**merge aggregatedHuman and aggregatedAnimal into a final map to be printed*/
        Map<String, Double> resultMap = new HashMap<String, Double>(aggregatedHuman);
        aggregatedAnimal.forEach((key, value) -> resultMap.merge(key, value, Double::sum));
        //add red/green survival ratio
        double surviveRatioInRed= (countSavedInRed+countDiedInRed)==0? 0: (double)countSavedInRed / (countSavedInRed+countDiedInRed);
        double surviveRatioInGreen = (countSavedInGreen+countDiedInGreen)==0? 0:(double)countSavedInGreen / (countSavedInGreen+countDiedInGreen);
        resultMap.put("green", Double.parseDouble(DF2.format(surviveRatioInGreen)));
        resultMap.put("red", Double.parseDouble(DF2.format(surviveRatioInRed)));
        //System.out.println("\nresultMap = \n"+resultMap);

        /**sorting resultMap by value*/
        resultMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEachOrdered(x -> Sorted_resultMap.put(x.getKey(), x.getValue()));
        //System.out.println("\n\nSorted_resultMap=\n"+Sorted_resultMap);

    }




    /**
     * pairing two passed_in maps (ex.livePerson vs diePerson), extract each bottom_count of each map, calculate ratio
     * @param resultMap
     * @param liveC
     * @param dieC
     * @return
     */
    public Map aggregate(HashMap<String, Double> resultMap, HashMap<String, HashMap> liveC, HashMap<String, HashMap> dieC) {

        for (Map.Entry<String, HashMap> featuresL : liveC.entrySet()) {

            //System.out.println("feature.getkey= "+ featuresL.getKey() ); //試pint檢查key
            Map<String, HashMap> typeCount = featuresL.getValue();//ageCount,  prCount, bdtCount{}...etc

            Map featureD = dieC.get(featuresL.getKey()); //Access diePerson feature
            //System.out.println("featureD= " +featureD);

            if (featuresL.getKey().equals("ageCount")) {
                int totalPpl = (int) (liveC.get("ageCount").get("count")) + (int) (dieC.get("ageCount").get("count"));
                int totalAge = (int) (liveC.get("ageCount").get("total")) + (int) (dieC.get("ageCount").get("total"));
                resultMap.put("averageAge", algorithm_1(totalAge, totalPpl));
                resultMap.put("person", algorithm_2( (int)(liveC.get("ageCount").get("count")), (int)(dieC.get("ageCount").get("count")) ));
                continue;
            } else if (featuresL.getKey().equals("isPnCount")) {
                int L = (int) (liveC.get("isPnCount").get("true"));
                int D = (int) (dieC.get("isPnCount").get("true"));
                resultMap.put("pregnant", algorithm_2(L, D));
                continue;
            } else if (featuresL.getKey().equals("isUCount")) {
                int L = (int) (liveC.get("isUCount").get("true"));
                int D = (int) (dieC.get("isUCount").get("true"));
                resultMap.put("you", algorithm_2(L, D));
                continue;
            }//if isPet also need to in the list, keep writing.
            typeCount.forEach((String nameL, Object tmp) -> { //second loop lambda



                int countL = (int) tmp; //tmp need to catch by object then able to cast, dont know why
                //System.out.println(nameL+": "+ countL);
                int countD = (Integer) featureD.get(nameL); //Access diePerson>>feature>>typeCount
                //System.out.println("countD"+nameL+": "+countD);

                if (countL != 0 || countD != 0) { //Calculate surviveRatio from both count
                    //System.out.println( "RATIO= " + algorithm_2(countL, countD) );
                    if (nameL.equals("totalNumAnimal")){
                        resultMap.put("animal", algorithm_2(countL, countD));
                    }else{
                        resultMap.put(nameL.toLowerCase(), algorithm_2(countL, countD));
                    }

                }

            });

        }
        return resultMap;
    }

    /**
     * helpers for aggregate
     * @param numerator
     * @param denominator
     * @return
     */
    public Double algorithm_1(int numerator, int denominator) {
        return denominator==0 ? 0: Double.parseDouble(DF1.format((double) numerator / denominator));
    }

    public Double algorithm_2(int live, int dead) {
        return (live+dead)==0 ? 0: Double.parseDouble(DF2.format( (double)live / (live + dead)));
    }




    /**
     * statistic of live Characters, count how many every feature present
     * @param liveChracter
     * @param aliveList
     * @return
     */
    public Map countLive(ArrayList<ethicalengine.Character> liveChracter, Map aliveList) {

        for (ethicalengine.Character c : liveChracter) {
            if (c instanceof Person) {
                Map livePerson = (HashMap) aliveList.get("livePerson");

                Map ageCount = (HashMap) livePerson.get("ageCount");
                ageCount = updateMapCount(ageCount, "total", c.getAge());
                ageCount = updateMapCount(ageCount, "count", 1);

                Map GdCount = (HashMap) livePerson.get("GdCount");
                GdCount = updateMapCount(GdCount, c.getGender().name(), 1);

                Map BdtCount = (HashMap) livePerson.get("BdtCount");
                BdtCount = updateMapCount(BdtCount, c.getBodyType().name(), 1);

                Map AcCount = (HashMap) livePerson.get("AcCount");
                AcCount = updateMapCount(AcCount, ((Person) c).getAgeCategory().name(), 1);

                Map PfCount = (HashMap) livePerson.get("PfCount");
                PfCount = updateMapCount(PfCount, ((Person) c).getProfession().name(), 1);

                Map isPnCount = (HashMap) livePerson.get("isPnCount");
                isPnCount = updateMapCount(isPnCount, Boolean.toString(((Person) c).isPregnant()), 1);

                Map isUCount = (HashMap) livePerson.get("isUCount");
                isUCount = updateMapCount(isUCount, Boolean.toString(((Person) c).isYou()), 1);
            } else {
                Map liveAnimal = (HashMap) aliveList.get("liveAnimal");

                Map SpcCount = (HashMap) liveAnimal.get("SpcCount");
                SpcCount = updateMapCount(SpcCount, ((Animal) c).getSpecies(), 1);
                SpcCount = updateMapCount(SpcCount, "totalNumAnimal", 1);

                /*yet to use
                Map isPtCount = (HashMap)liveAnimal.get("isPtCount");
                isPtCount = updateMapCount(isPtCount, Boolean.toString(((Animal) c).isPet()), 1);
                */
            }
        }
        return aliveList;
    }

    /**
     * statistic of die Characters, count how many every feature present
     * @param deadChracter
     * @param dieList
     * @return
     */
    public Map countDie(ArrayList<ethicalengine.Character> deadChracter, Map dieList) {

        for (ethicalengine.Character c : deadChracter) {
            if (c instanceof Person) {
                Map diePerson = (HashMap) dieList.get("diePerson");

                Map ageCount = (HashMap) diePerson.get("ageCount");
                ageCount = updateMapCount(ageCount, "total", c.getAge());
                ageCount = updateMapCount(ageCount, "count", 1);

                Map GdCount = (HashMap) diePerson.get("GdCount");
                GdCount = updateMapCount(GdCount, c.getGender().name(), 1);

                Map BdtCount = (HashMap) diePerson.get("BdtCount");
                BdtCount = updateMapCount(BdtCount, c.getBodyType().name(), 1);

                Map AcCount = (HashMap) diePerson.get("AcCount");
                AcCount = updateMapCount(AcCount, ((Person) c).getAgeCategory().name(), 1);

                Map PfCount = (HashMap) diePerson.get("PfCount");
                PfCount = updateMapCount(PfCount, ((Person) c).getProfession().name(), 1);

                Map isPnCount = (HashMap) diePerson.get("isPnCount");
                isPnCount = updateMapCount(isPnCount, Boolean.toString(((Person) c).isPregnant()), 1);

                Map isUCount = (HashMap) diePerson.get("isUCount");
                isUCount = updateMapCount(isUCount, Boolean.toString(((Person) c).isYou()), 1);
            } else {
                Map dieAnimal = (HashMap) dieList.get("dieAnimal");

                Map SpcCount = (HashMap) dieAnimal.get("SpcCount");
                SpcCount = updateMapCount(SpcCount, ((Animal) c).getSpecies(), 1);
                SpcCount = updateMapCount(SpcCount, "totalNumAnimal", 1);

                /*yet to use
                Map isPtCount = (HashMap)dieAnimal.get("isPtCount");
                isPtCount = updateMapCount(isPtCount, Boolean.toString(((Animal) c).isPet()), 1);
                */
            }
        }
        return dieList;
    }

    /**
     * helpers for statistics
     * @param map
     * @param key
     * @param addValue
     * @return
     */
    public Map updateMapCount(Map map, String key, int addValue) {
        map.putIfAbsent(key, 0); //if key doesnt exists, set 0
        map.replace(key, (int) map.get(key) + addValue); //if key exists, accumulate it
        return map;
    }

    public Map buildEptMap(Map<String, HashMap> PersonM, Map<String, HashMap> AnimalM, String innerMapNameP, String innerMapNameA) {

        Map<String, HashMap> resultMap = new HashMap();

        Map<String, Integer> ageCount = new HashMap();//Age
        ageCount.putIfAbsent("total", 0);
        ageCount.putIfAbsent("count", 0);
        PersonM.put("ageCount", (HashMap) ageCount);

        Map<String, Integer> GdCount = new HashMap(); //Gender
        for (Character.Gender s : Character.Gender.values()) {
            GdCount.putIfAbsent(s.name(), 0);
        }
        PersonM.put("GdCount", (HashMap) GdCount);

        Map<String, Integer> BdtCount = new HashMap();//BodyType
        for (Character.BodyType s : Character.BodyType.values()) {
            BdtCount.putIfAbsent(s.name(), 0);
        }
        PersonM.put("BdtCount", (HashMap) BdtCount);

        Map<String, Integer> AcCount = new HashMap();//AgeCategory
        for (Person.AgeCategory s : Person.AgeCategory.values()) {
            AcCount.putIfAbsent(s.name(), 0);
        }
        PersonM.put("AcCount", (HashMap) AcCount);

        Map<String, Integer> PfCount = new HashMap();//Profession
        for (Person.Profession s : Person.Profession.values()) {
            PfCount.putIfAbsent(s.name(), 0);
        }
        PersonM.put("PfCount", (HashMap) PfCount);

        Map<String, Integer> isPnCount = new HashMap();//Pregnant
        isPnCount.putIfAbsent("true", 0);
        isPnCount.putIfAbsent("false", 0);
        PersonM.put("isPnCount", (HashMap) isPnCount);

        Map<String, Integer> isUCount = new HashMap();//isYou
        isUCount.putIfAbsent("true", 0);
        isUCount.putIfAbsent("false", 0);
        PersonM.put("isUCount", (HashMap) isUCount);
        //so livePerson[ age{}, Gender:{female:0, Male:2}, Bdt{}, Ac{}, Pf{}, isPn{}, isU{} ]



        Map<String, Integer> SpcCount = new HashMap();//Species
        for (Animal.enumSpecies s : Animal.enumSpecies.values()) {
            SpcCount.putIfAbsent(s.name(), 0);
        }
        SpcCount.putIfAbsent("totalNumAnimal", 0);
        AnimalM.put("SpcCount", (HashMap) SpcCount);

        /*
        Map<String, Integer> isPtCount = new HashMap();//isPet
        isPtCount.putIfAbsent("true",0 );
        isPtCount.putIfAbsent("false",0 );
        AnimalM.put("isPtCount", (HashMap)isPtCount);
        */
        //so liveAnimal[ Spc{}, isPt{} ]


        resultMap.put(innerMapNameP, (HashMap) PersonM);
        resultMap.put(innerMapNameA, (HashMap) AnimalM);
        return resultMap;
    }




}
/**========================CODE END==========================*/