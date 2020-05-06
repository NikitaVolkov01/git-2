package com.company;

/**
 * Класс описываюший событие.
 */
public class Event implements Comparable {
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private long ticketsCount; //Значение поля должно быть больше 0

    /**
     * Конструктор класса Event.
     * @param id
     * @param name
     * @param ticketsCount
     */
    public Event (int id , String name , long ticketsCount){
        this.id = id;
        this.name = name;
        this.ticketsCount = ticketsCount;
    }

    public int getId()
    {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public long getTicketsCount() {
        return ticketsCount;
    }

    @Override
    public String toString()
    {
        return String.format("Event: %d, %s (%d шт.)", id, name, ticketsCount);
    }

    @Override
    public int compareTo(Object o) {
        return this.name.compareTo(((Event)o).name);
    }
}
