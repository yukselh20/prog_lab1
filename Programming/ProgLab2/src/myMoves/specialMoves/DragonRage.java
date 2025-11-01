package myMoves.specialMoves;

import ru.ifmo.se.pokemon.*;

public class DragonRage extends SpecialMove{

	
	public DragonRage(double pow, double acc){
		super(Type.DRAGON,pow, acc);
	}
	
	@Override
	protected void applyOppDamage(Pokemon def, double damage) {
		//super.applyOppDamage(def, damage);
		def.setMod(Stat.HP, 40);
		
	}
	
	@Override // overridden original one
	protected String describe(){
		// class.pokemon.SimleMove
		String info[] = this.getClass().toString().split("\\.");
		return "Using " + info[info.length-1];
		// does SimpleMove
		
	}
}
