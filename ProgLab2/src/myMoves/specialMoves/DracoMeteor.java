package myMoves.specialMoves;

import ru.ifmo.se.pokemon.*;

public class DracoMeteor extends SpecialMove {
	
	public DracoMeteor(double pow, double acc){
		super(Type.DRAGON,pow, acc);
	}
	
	@Override
	protected void applySelfEffects(Pokemon p) {
		//Draco Meteor deals damage but lowers the user's Special 
		//Attack by two stages after attacking.
		Effect e = new Effect().stat(Stat.SPECIAL_ATTACK,-2);
		p.addEffect(e);		
		System.out.println(p.toString() + " Special Attack -2");
	}
	
	@Override // overridden original one
	protected String describe(){
		// class.pokemon.SimleMove
		String info[] = this.getClass().toString().split("\\.");
		return "Using " + info[info.length-1];
		// does SimpleMove
		
	}
}
