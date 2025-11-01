package myPokemons;

import myMoves.physicalMoves.PetalBlizzard;
import myMoves.statusMoves.Confide;
import myMoves.statusMoves.DoubleTeam;
import myMoves.statusMoves.Rest;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Lilligant extends Pokemon {
	public Lilligant(String name, int level) {
		super(name,level);
		
		super.setType(Type.GRASS);
		super.setStats(70,60,75,110,75,90);
		
		Confide confide = new Confide(0,0);
		DoubleTeam doubleTeam = new DoubleTeam(0,0);
		Rest rest = new Rest(0,0);
		PetalBlizzard petalBlizzard = new PetalBlizzard(90,100);
		
		super.setMove(rest,doubleTeam,confide,petalBlizzard);
	}
}
