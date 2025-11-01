package myMoves.statusMoves;

import ru.ifmo.se.pokemon.*;

public class Confide extends StatusMove {
	public Confide(double pow, double acc){
		super(Type.NORMAL,pow, acc);
	}

	@Override
	protected void applyOppEffects(Pokemon p) {
		Effect e = new Effect().stat(Stat.SPECIAL_ATTACK,-1);
		p.addEffect(e);
		System.out.println(p.toString() + " Enemy Special Attack decreased by 1 stage");
	}

	@Override // overridden original one
	protected String describe(){
		String info[] = this.getClass().toString().split("\\.");
		return "Using "+ info[info.length-1];
	}k
}
