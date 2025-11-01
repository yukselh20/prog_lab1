package myMoves.specialMoves;

import ru.ifmo.se.pokemon.*;

public class IceBeam extends SpecialMove {
	
	public IceBeam(double pow, double acc){
		super(Type.ICE,pow, acc);
	}


	//Ice Beam deals damage and has a 10% chance of freezing the target.
	@Override
	protected void applyOppEffects(Pokemon p) {
		if(Math.random()<= 0.1) {
			Effect.freeze(p);
		}
		
	}

	
	@Override // overridden original one
	protected String describe(){
		// class.pokemon.SimleMove
		String info[] = this.getClass().toString().split("\\.");
		return "Using " + info[info.length-1];
		// does SimpleMove
		
	}
}
