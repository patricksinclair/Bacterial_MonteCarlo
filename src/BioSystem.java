import com.sun.xml.internal.org.jvnet.mimepull.MIMEConfig;

import java.util.ArrayList;
import java.util.Random;

public class BioSystem {

    //the number of microhabitats contained within the system
    private int L;
    private int K;
    //the steepness of the antibiotic gradient
    private double alpha;
    //the concentration of antibiotic for uniform gradients
    private double c;
    private double timeElapsed;
    //bolean which is true when the system mutates to the final possible genotype
    private boolean resistanceReached;

    Random rand = new Random();

    private Microhabitat[] microhabitats;

    public BioSystem(int L, int K, double alpha){
        this.L = L;
        this.K = K;
        this.alpha = alpha;
        this.microhabitats = new Microhabitat[L];
        this.timeElapsed = 0.;
        this.resistanceReached = false;

        for(int i = 0; i < L; i++){

            double c_i = Math.exp(alpha*(double)i) - 1.;
            microhabitats[i] = new Microhabitat(K, c_i);
        }
        //fills the first microhabitat with bacteria of genotype 1.
        microhabitats[0].fillWithWildType();
    }

    //This constructor is used to create a BioSystem with a uniform drug concentration of value c
    //the given alpha value is just a placeholder
    public BioSystem(int L, int K, double alpha, double c){
        this.L = L;
        this.K = K;
        t
    }


    public int getL(){return L;}
    public void setL(int L){this.L = L;}

    public int getK(){return K;}
    public void setK(int K){this.K = K;}

    public double getAlpha(){return alpha;}
    public void setAlpha(double alpha){this.alpha = alpha;}

    public double getC(){return c;};
    public void setC(double c){this.c = c;}

    public double getTimeElapsed(){return timeElapsed;}

    public boolean getResistanceReached(){return resistanceReached;}
    public void setResistanceReached(boolean resistanceReached){this.resistanceReached = resistanceReached;}

    //returns the total number of bacteria in the system
    public int getCurrentPopulation(){
        int runningTotal = 0;

        for(int i = 0; i < L; i++){
            if(microhabitats[i].getN() > 0){
                runningTotal += microhabitats[i].getN();
            }
        }
        return runningTotal;
    }


    public Microhabitat getMicrohabitat(int i){
        return microhabitats[i];
    }

    public Bacteria getBacteria(int l, int k){
        return microhabitats[l].getBacteria(k);
    }

    //migrates a specific bacterium to the next microhabitat
    public void migrate(int currentL, int bacteriumIndex){

        //ensures that migration can't occur past the last habitat and migration can't happen to habitats
        //which are at capacity
        if(currentL < (L-1) &&
                (microhabitats[currentL+1].getN() < microhabitats[currentL+1].getK())) {

            ArrayList<Bacteria> source = microhabitats[currentL].getPopulation();
            ArrayList<Bacteria> destination = microhabitats[currentL + 1].getPopulation();

            destination.add(source.remove(bacteriumIndex));
        }
    }


    //"kills" a bacterium by removing it from the habitat
    public void death(int currentL, int bacteriumIndex){
        microhabitats[currentL].removeABacterium(bacteriumIndex);
    }


    //replicates a bacterium by creating a new bacteria with the same genotype, unless mutation occurs.
    public void replicate(int currentL, int bacteriumIndex){

        //the bacterium which is going to be replicated and its associated genotype
        Bacteria parentBac = microhabitats[currentL].getBacteria(bacteriumIndex);
        int m = parentBac.getM();

        //only allows replication if the habitat isn't at carrying capacity
        if(microhabitats[currentL].getN() < microhabitats[currentL].getK()) {

            //these are used to determine whether or not the replicated bacterium is a mutant
            double mu = parentBac.getMu();
            double s = rand.nextDouble();

            Bacteria childBac = new Bacteria(m);
            if(s < mu/2.) {
                childBac.increaseGenotype();
            } else if(s >= mu/2. && s < mu) {
                childBac.decreaseGenotype();
            }
            microhabitats[currentL].addABacterium(childBac);
            if(childBac.getM() == childBac.getFinalM()) resistanceReached = true;
            //if(childBac.getM() >2  && currentL > 3) System.out.println("random genotype: " + childBac.getM());
        }
    }



    public void performAction(){

        //selects a random microhabitat from the system, but only if it's populated
        int microHabIndex = rand.nextInt(L);
        Microhabitat randMicroHab = getMicrohabitat(microHabIndex);

        whileloop:
        while(true) {

            microHabIndex = rand.nextInt(L);
            randMicroHab = getMicrohabitat(microHabIndex);

            if(randMicroHab.getN() > 0) break whileloop;
        }

        //System.out.println("microhab pop rand "+microHabIndex+": "+ randMicroHab.getN());
        //selects a random bacteria from the random microhabitat
        int N = randMicroHab.getN(), K = randMicroHab.getK();
        double c = randMicroHab.getC();
        int bacteriaIndex = rand.nextInt(N);
        Bacteria randBac =  randMicroHab.getBacteria(bacteriaIndex);

        double migRate = randBac.getB();
        double deaRate = randBac.getD();
        double repliRate = randBac.replicationRate(c, N, K);
        double R_max = 1.2;

        double rando = rand.nextDouble()*R_max;

        if(rando < migRate) migrate(microHabIndex, bacteriaIndex);
        else if(rando >= migRate && rando < (migRate + deaRate)) death(microHabIndex, bacteriaIndex);
        else if(rando >= (migRate + deaRate) && rando < (migRate + deaRate + repliRate)) replicate(microHabIndex, bacteriaIndex);

        timeElapsed += 1./((double)getCurrentPopulation()*R_max);
    }


    public static void displayPopulationNumbers(){

        int L = 500;
        int K = 100;
        double alpha = 0.05;

        BioSystem bs = new BioSystem(L, K, alpha);

        int counter = 0;

        while(true){

            bs.performAction();

            /*if(counter%1000 == 0){
                for(int i = 0; i < L; i++){
                    System.out.println("Habitat "+(i+1)+": "+bs.microhabitats[i].getN());
                }
            }*/

            counter++;
        }
    }


    public static void timeTilTotalResistance(){

        int L = 500;
        int K = 100;
        //no. of repetitions the experiments are averaged over
        int nReps = 1;

        double initAlpha = 0.001;
        double finalAlpha = 0.1;
        double increment = 0.002;

        int noOfPoints = (int)((finalAlpha - initAlpha)/increment);
        ArrayList<Double> alphaVals = new ArrayList<Double>(noOfPoints);
        ArrayList<Double> tVals = new ArrayList<Double>(noOfPoints);


        for(double alpha = initAlpha; alpha < finalAlpha; alpha+=increment){

            double runningTotal = 0.;
            for(int i = 0; i < nReps; i++) {

                BioSystem bs = new BioSystem(L, K, alpha);

                while(!bs.getResistanceReached()) {
                    bs.performAction();
                }

                runningTotal += bs.getTimeElapsed();
                System.out.println("Current repition: "+i);
            }

            alphaVals.add(alpha);
            tVals.add(runningTotal/(double)nReps);
            System.out.println("Time taken for alpha = "+alpha+": "+runningTotal/(double)nReps);
        }


        Toolbox.write2ArrayListsToFile(alphaVals, tVals, "immunityTimes_newMu_2");
        System.out.println("Experiment complete");
    }



}
