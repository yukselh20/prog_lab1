package myPokemons;

import myMoves.physicalMoves.Present;
import myMoves.specialMoves.DreamEater;
import myMoves.statusMoves.Swagger;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Chansey extends Pokemon {
	public Chansey(String name, int level) {
		super(name, level);
		
		super.setType(Type.NORMAL);
		super.setStats(250, 5, 5, 35, 105, 50);
		
		DreamEater dreamEater = new DreamEater(100,100);
		Present present = new Present(0,90);
		Swagger swagger = new Swagger(0,85);
			
		
		super.setMove(dreamEater,present,swagger);
	}
	

}
