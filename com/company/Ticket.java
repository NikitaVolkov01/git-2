package com.company;

import java.util.Date;

public class Ticket implements Comparable{
    private int id; //
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private long price; //Значение поля должно быть больше 0
    private TicketType type; //Поле может быть null
    private Event event; //Поле может быть null

    public Ticket(int id, String name, Coordinates coordinates, Date creationDate, long price, TicketType type, Event event) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.price = price;
        this.type = type;
        this.event = event;
    }
    public int getId()
    {
        return this.id;
    }

    public long getPrice()
    {
        return this.price;
    }

    public String getName()
    {
        return this.name;
    }

    public Event getEvent()
    {
        return this.event;
    }

    public Coordinates getCoordinates() {return this.coordinates;}

    public java.util.Date getCreationDate() {return this.creationDate;}

    public TicketType getType() {return this.type;}


    @Override
    public String toString()
    {
        return String.format("Name: %s\nID: %d\n%s\nDate: %s\nPrice: %d\nType: %s\n%s\n",
                name, id, coordinates, creationDate.toString(), price, type, event);
    }

    @Override
    public int compareTo(Object o) {
        return this.id - ((Ticket)o).id;
    }
}
