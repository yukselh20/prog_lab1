package myPokemons;

import myMoves.physicalMoves.DoubleSlap;
import myMoves.physicalMoves.Present;
import myMoves.specialMoves.DreamEater;
import myMoves.statusMoves.Swagger;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Blissey extends Pokemon{
	public Blissey(String name, int level) {
		super(name, level);
		
		super.setType(Type.NORMAL);
		super.setStats(255, 10, 10, 75, 135, 55);
		
		
		DreamEater dreamEater = new DreamEater(100,100);
		Present present = new Present(0,90);
		Swagger swagger = new Swagger(0,85);
		DoubleSlap doubleSlap = new DoubleSlap(15,85);
			
		
		super.setMove(dreamEater,present,swagger,doubleSlap);
	}

}
