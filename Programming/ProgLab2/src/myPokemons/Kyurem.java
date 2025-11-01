package myPokemons;

import myMoves.specialMoves.DracoMeteor;
import myMoves.specialMoves.DragonRage;
import myMoves.specialMoves.IceBeam;
import myMoves.statusMoves.Swagger;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Kyurem extends Pokemon {
	
	public Kyurem(String name, int level) {
		super(name,level);
		
		super.setType(Type.DRAGON,Type.ICE); //iki tane tipi var siteden bakıp yaz
		super.setStats(125, 130, 90, 130, 90, 95);
		
		
		DracoMeteor dracoMeteor = new DracoMeteor(130,90);
		IceBeam iceBeam = new IceBeam(90,100);
		DragonRage dragonRage = new DragonRage(0,100);
		Swagger swagger = new Swagger(0,85);
		
		super.setMove(dracoMeteor, iceBeam, dragonRage, swagger);
		
	}
	
	
	
}
