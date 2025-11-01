package myMoves.statusMoves;

import ru.ifmo.se.pokemon.*;

public class DoubleTeam extends StatusMove{
	public DoubleTeam(double pow, double acc){
		super(Type.NORMAL,pow, acc);
	}
	
	
	@Override
	protected void applySelfEffects(Pokemon p) {
		Effect e = new Effect().stat(Stat.EVASION, 1);
		p.addEffect(e);
		System.out.println(p.toString() + " Evasion increased by 1 stage");

		
	}
	
	@Override // overridden original one
	protected String describe(){
		// class.pokemon.SimleMove
		String info[] = this.getClass().toString().split("\\.");
		return "Using "+ info[info.length-1];
		// does SimpleMove
		
	}
}
