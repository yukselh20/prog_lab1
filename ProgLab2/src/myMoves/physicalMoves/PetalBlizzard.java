package myMoves.physicalMoves;

import ru.ifmo.se.pokemon.*;


public class PetalBlizzard extends PhysicalMove {
	
	public PetalBlizzard(double pow, double acc) {
		super(Type.GRASS, pow, acc);
	}
	
	@Override
	protected void applyOppDamage(Pokemon def, double damage) {
		super.applyOppDamage(def, power);
		
	}
	
	@Override // overridden original one
	protected String describe(){
		// class.pokemon.SimleMove
		String info[] = this.getClass().toString().split("\\.");
		return "Using "+ info[info.length-1];
		// does SimpleMove
		
	}
}
