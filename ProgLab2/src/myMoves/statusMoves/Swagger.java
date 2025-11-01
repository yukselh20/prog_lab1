package myMoves.statusMoves;

import ru.ifmo.se.pokemon.*;

public class Swagger extends StatusMove{
	
	public Swagger(double pow, double acc) {
		super(Type.NORMAL, pow, acc);
		
	}
	
	@Override
	protected void applyOppEffects(Pokemon p) {
		Effect.confuse(p);
		
	}
	
	@Override
	protected void applySelfEffects(Pokemon p) {
		Effect e = new Effect().stat(Stat.ATTACK, 2);
		p.addEffect(e);
		System.out.println(p.toString() + " attack increased 2 points");
		
	}
	
	@Override // overridden original one
	protected String describe(){
		String info[] = this.getClass().toString().split("\\.");
		return "Using " + info[info.length-1];
	}

}
