package myMoves.specialMoves;

import ru.ifmo.se.pokemon.*;


public class DreamEater extends SpecialMove{
	public DreamEater(double pow, double acc) {
		super(Type.PSYCHIC, pow, acc);
		
	}

	
	@Override
	protected void applyOppDamage(Pokemon def, double pow) {
		if(def.getCondition()==Status.SLEEP) {
			super.applyOppDamage(def, pow);
		}
	}
	@Override
	protected void applySelfDamage(Pokemon att, double pow) {
		att.setMod(Stat.HP, (int) (-(power / 2)));
	}
	
	@Override // overridden original one
	protected String describe(){
		String info[] = this.getClass().toString().split("\\.");
		return "Using "+ info[info.length-1];

	}

}
