import java.util.Random;

public class NutrientSystem extends BioSystem{

    private int L, K;
    private double alpha, timeElapsed;
    //no. of food units per
    private int S;
    private boolean resistanceReached;

    private Microhabitat[] microhabitats;
    Random random = new Random();

    public NutrientSystem(int L, int K, double alpha, int S){
        super(L, K, alpha);
        this.S = S;
        this.microhabitats = new Microhabitat[L];
        this.timeElapsed = 0.;
        this.resistanceReached = false;

        for(int i = 0; i < L; i++){
            double c_i = Math.exp(alpha*(double)i) - 1.;
            microhabitats[i] = new Microhabitat(K, c_i, S);
        }
        microhabitats[0].fillWithWildType();
    }

    public int getS(){return S;}
    public void setS(int S){this.S = S;}


    public void replicate_Nutrient(int currentL, int bacteriumIndex){
        super.replicate(currentL, bacteriumIndex);
        microhabitats[currentL].consumeNutrients();
    }

    @Override
    public void performAction(){

        //selects a random bacteria from the total population
        int randomIndex = rand.nextInt(getCurrentPopulation());
        int indexCounter = 0;
        int microHabIndex = 0;
        int bacteriaIndex = 0;

        forloop:
        for(int i = 0; i < getL(); i++){

            if((indexCounter + microhabitats[i].getN()) < randomIndex){

                indexCounter += microhabitats[i].getN();
                continue forloop;
            }
            else{
                microHabIndex = i;
                bacteriaIndex = randomIndex - indexCounter;
                if(bacteriaIndex > 0) bacteriaIndex -= 1;
                break forloop;
            }
        }

        Microhabitat randMicroHab = microhabitats[microHabIndex];
        int S = randMicroHab.getS(); double c = randMicroHab.getC(), K_prime = randMicroHab.getK_prime();
        Bacteria randBac = randMicroHab.getBacteria(bacteriaIndex);

        double migRate = randBac.getB();
        double deaRate = randBac.getD();
        double repliRate = randBac.replicationRate_Nutrients(c, K_prime, S);
        double R_max = 1.2;
        double rando = rand.nextDouble()*R_max;

        if(rando < migRate) migrate(microHabIndex, bacteriaIndex);
        else if(rando >= migRate && rando < (migRate + deaRate)) death(microHabIndex, bacteriaIndex);
        else if(rando >= (migRate + deaRate) && rando < (migRate + deaRate + repliRate)) replicate_Nutrient(microHabIndex, bacteriaIndex);

        timeElapsed += 1./((double)getCurrentPopulation()*R_max);
    }

}
