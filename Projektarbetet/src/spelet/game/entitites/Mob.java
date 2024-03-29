package spelet.game.entitites;

import spelet.game.gfx.level.Level;
import spelet.game.gfx.level.tiles.Tile;

public abstract class Mob extends Entity{
	
	protected String name;
	protected double speed;
	protected int numSteps = 0;
	protected boolean isMoving;
	protected int movingDir = 1;
	protected int scale = 1;

	public Mob(Level level, String name, int x, int y, double speed) {
		super(level);		
		this.name = name;
		this.speed = speed;
		this.x = x;
		this.y = y;
	}
	
	public void move(int xa, int ya){
		if (xa != 0 && ya != 0){
			move(xa, 0);
			move(0, ya);
			numSteps--;
			return;
		}
		numSteps++;
		if ( !hasCollided(xa, ya)){
			if (ya < 0)
				movingDir = 0;
			if (ya > 0) 
				movingDir = 1;
			if (xa < 0)
				movingDir = 2;
			if (xa > 0)
				movingDir = 3;
			x += xa * speed;
			y += ya * speed;
		}
	}
	
	public abstract boolean hasCollided(int xa, int ya);
	
	protected boolean isSolidTile(int xa, int ya, int x, int y){
		if (level == null) { return false;}
		Tile lastTile = level.getTile((this.x + x)/8, (this.y + y)/8);
		Tile newTile = level.getTile((this.x + x + xa)/8, (this.y + y + ya)/8);
		if (! lastTile.equals(newTile) && newTile.isSolid()){
			return true;
		}
		return false;
	}
	
	public String getName(){
		return name;
	}
}
