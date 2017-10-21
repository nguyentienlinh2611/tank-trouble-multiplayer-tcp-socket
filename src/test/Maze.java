package test;

public class Maze {
	private static final int gridWidth = 7;
	//number of grid squares in the grid (along and down)
	private boolean[][][] walls = new boolean[2][gridWidth + 1][gridWidth + 1];
	public Maze(boolean[][][] walls) {
		this.walls = walls;
	}
	public boolean isWallLeft(int x, int y) {
		//inputs: x: x coordinate of the grid square being checked, y: y coordinate of the grid square being checked
		//output: true if there is a wall to the left of the square, false otherwise
		return walls[1][x][y];
	}

	public boolean isWallRight(int x, int y) {
		//inputs: x: x coordinate of the grid square being checked, y: y coordinate of the grid square being checked
		//output: true if there is a wall to the right of the square, false otherwise
		return walls[1][x + 1][y];
	}

	public boolean isWallAbove(int x, int y) {
		//inputs: x: x coordinate of the grid square being checked, y: y coordinate of the grid square being checked
		//output: true if there is a wall above the square, false otherwise
		return walls[0][y][x];
	}

	public boolean isWallBelow(int x, int y) {
		//inputs: x: x coordinate of the grid square being checked, y: y coordinate of the grid square being checked
		//output: true if there is a wall below the square, false otherwise
		return walls[0][y + 1][x];
	}
}
