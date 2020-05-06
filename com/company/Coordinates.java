package com.company;

/**
 * Класс хранения координат.
 */
public class Coordinates implements Comparable {
    private float x;
    private long y; //Максимальное значение поля: 870
    public Coordinates (float x , long y){
        this.x = x;
        this.y = y;
    }

    public float getX()
    {
        return this.x;
    }

    public long getY()
    {
        return this.y;
    }

    @Override
    public String toString()
    {
        return String.format("Coord: (%f, %d)", x, y);
    }

    @Override
    public int compareTo(Object o) {
        double d1 = Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
        double d2 = Math.sqrt(Math.pow(((Coordinates)o).x, 2) + Math.pow(((Coordinates)o).y, 2));
        if (d1 == d2)
            return 0;
        return d1 > d2 ? 1 : -1;
    }
}
