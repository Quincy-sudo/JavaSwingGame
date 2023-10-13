package Tile;

public class Tile {
    private int position;
    private TileType type;

    public Tile(int position, TileType type) {
        this.position = position;
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public TileType getType() {
        return type;
    }

}