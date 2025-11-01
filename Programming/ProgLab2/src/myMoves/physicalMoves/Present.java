package myMoves.physicalMoves;

import ru.ifmo.se.pokemon.*;

public class Present extends PhysicalMove{
	private double initialPower; 
	
	public Present(double pow, double acc) {
		super(Type.NORMAL, pow, acc);
		
		this.initialPower = pow;
	}
	
    @Override
    protected void applyOppEffects(Pokemon p) {
        // Random chance to either deal damage or heal
        if (Math.random() < 0.8) {
            // Deal random damage between 40-120 power
            this.initialPower = 40 + (int)(Math.random() * 81);
        } else {
            // Heal for 1/4 max HP
            double healing = p.getStat(Stat.HP) * 0.25;
            p.setMod(Stat.HP, -(int)healing);
        }
    }
    
	@Override // overridden original one
	protected String describe(){
		String info[] = this.getClass().toString().split("\\.");
		return "Using " + info[info.length-1];
		
	}
	

}
