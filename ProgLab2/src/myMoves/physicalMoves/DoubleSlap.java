package myMoves.physicalMoves;

import ru.ifmo.se.pokemon.*;

public class DoubleSlap extends PhysicalMove {
	private double initialPower;
	
	public DoubleSlap(double pow, double acc) {
		super(Type.NORMAL, pow, acc);
		
		this.initialPower = pow;
		
	}
	
    @Override
    protected void applyOppEffects(Pokemon p) {

        double chance = Math.random();
        int hits;
        
        if (chance < 0.375) {          // 3/8 chance for 2 hits
            hits = 2;
        } else if (chance < 0.75) {    // 3/8 chance for 3 hits
            hits = 3;
        } else if (chance < 0.875) {   // 1/8 chance for 4 hits
            hits = 4;
        } else {                       // 1/8 chance for 5 hits
            hits = 5;
        }
        this.hits = hits; // Store hits for describe method
        
        Effect e = new Effect().turns(hits).stat(Stat.ATTACK, (int)(hits*initialPower));
        p.addEffect(e);
    }

    private int hits; // To store number of hits for description

    
	@Override // overridden original one
	protected String describe(){
		String info[] = this.getClass().toString().split("\\.");
		return "Using "+ info[info.length-1] + "Hits "+ hits + " times";
		
	}
}
