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



    
}
