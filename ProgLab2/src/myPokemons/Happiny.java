package myPokemons;

import myMoves.specialMoves.DreamEater;
import myMoves.statusMoves.Swagger;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Happiny extends Pokemon {
	public Happiny(String name, int level) {
		super(name, level);
		
		super.setType(Type.NORMAL);
		super.setStats(100, 5, 5, 15, 65, 30);
		
		DreamEater dreamEater = new DreamEater(100,100);
		Swagger swagger = new Swagger(0,85);
		
		
		super.setMove(dreamEater,swagger);
	}

}
