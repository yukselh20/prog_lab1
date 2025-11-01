package myPokemons;

import myMoves.statusMoves.Confide;
import myMoves.statusMoves.DoubleTeam;
import myMoves.statusMoves.Rest;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Petilil extends Pokemon {
	
	public Petilil(String name, int level) {
		super(name,level);
		
		super.setType(Type.GRASS); //one type. = grass
		super.setStats(45, 35, 50, 70, 50, 30);
		
		
		Confide confide = new Confide(0,0);
		DoubleTeam doubleTeam = new DoubleTeam(0,0);
		Rest rest = new Rest(0,0);
		
		super.setMove(rest,doubleTeam,confide);
	}

}
