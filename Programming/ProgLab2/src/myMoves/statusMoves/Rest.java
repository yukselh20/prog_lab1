package myMoves.statusMoves;

import ru.ifmo.se.pokemon.*;

public class Rest extends StatusMove {
	
	public Rest(double pow, double acc){
		super(Type.PSYCHIC, pow, acc);
	}

    @Override
    protected void applySelfEffects(Pokemon p) {
        // Put user to sleep for 2 turns
        Effect.sleep(p);
        Effect.sleep(p);
        // Calculate and apply full healing (negative value for healing)
        double maxHP = p.getStat(Stat.HP);
        double currentHP = p.getHP();
        double healAmount = maxHP - currentHP;
        p.setMod(Stat.HP, -(int)healAmount);
    }
	
	@Override // overridden original one
	protected String describe(){
		String info[] = this.getClass().toString().split("\\.");
		return "Using "+ info[info.length-1];		
	}

}
