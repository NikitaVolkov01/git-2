package com.company;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Класс управления коллекцией.
 */
public class TicketList{
    TreeSet<Ticket> set;
    List<String> history = new ArrayList<String>();
    Date initData;
    TicketList()
    {
        set = new TreeSet<Ticket>();
        initData = new Date();
    }

    /**
     * Читает в коллекуию данные из json-файла.
     * @param filename Имя файла
     * @throws IOException
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public void loadFile(String filename) throws IOException, ParseException, java.text.ParseException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String lines = br.lines().collect(Collectors.joining());
        Object obj = new JSONParser().parse(lines);
        JSONArray jsonArray = (JSONArray)obj;
        Iterator tickets = jsonArray.iterator();

        while(tickets.hasNext())
        {
            JSONObject o = (JSONObject)tickets.next();
            long id;
            String name;
            double cx;
            long cy;
            long price;
            String ticketType;
            long eventId;
            String eventName;
            long ticketsCount;
            Date date;
            id = (long)o.get("id");
            name = (String)o.get("name");
            cx = (double)((JSONObject)o.get("coordinates")).get("x");
            cy = (long)((JSONObject)o.get("coordinates")).get("y");
            DateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");
            date = (Date)dateFormat.parse((String)o.get("creationDate"));
            price = (long)o.get("price");
            ticketType = (String)o.get("type");
            eventId = (long)((JSONObject)o.get("event")).get("id");
            eventName = (String)((JSONObject)o.get("event")).get("name");
            ticketsCount = (long)((JSONObject)o.get("event")).get("ticketsCount");
            this.add(id, name, new Coordinates((float)cx, cy), date,
                    price, stringToType(ticketType), new Event((int)eventId, eventName, ticketsCount));
        }
    }

    /**
     * Сохраняет коллекцию в json-файл.
     * @param fileName Имя файла
     * @throws IOException
     */
    public void saveFile(String fileName) throws IOException {
        FileWriter fos = new FileWriter(fileName);

        /*Object obj = new JSONParser().parse(lines);
        JSONArray jsonArray = (JSONArray)obj;
        Iterator tickets = jsonArray.iterator();*/
        JSONObject jsonObject;
        JSONArray jsonArray = new JSONArray();
        for (Ticket t: this.set) {
            jsonObject = new JSONObject();
            jsonObject.put("id",t.getId());
            jsonObject.put("name",t.getName());

            JSONObject coordObject = new JSONObject();
            coordObject.put("x", t.getCoordinates().getX());
            coordObject.put("y", t.getCoordinates().getY());
            jsonObject.put("coordinates", coordObject);

            DateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");
            jsonObject.put("creationDate",dateFormat.format(t.getCreationDate()).toString());
            jsonObject.put("price",t.getPrice());
            jsonObject.put("type",t.getType().toString());

            JSONObject eventObject = new JSONObject();
            eventObject.put("id",t.getEvent().getId());
            eventObject.put("name",t.getEvent().getName());
            eventObject.put("ticketsCount",t.getEvent().getTicketsCount());
            jsonObject.put("event",eventObject);
            jsonArray.add(jsonObject);
        }
        fos.write(jsonArray.toJSONString());
        fos.close();
    }

    /**
     * Вводит строку с приглашением.
     * @param msg Приглашение для ввода.
     * @param isNull Может ли поле иметь значение null
     * @return
     */
    public String readString(String msg, boolean isNull)
    {
        String result;
        Scanner scan = new Scanner(System.in);
        do {
            System.out.print(msg);
            result = scan.nextLine();
        }while(!isNull && result.length() == 0);
        return result;
    }

    /**
     * Вводит число типа float с приглашением.
     * @param msg Приглашение для ввода.
     * @return
     */
    public Float readFloat(String msg)
    {
        String result = "";
        do {
            try
            {
                result = readString(msg, false);
                Float.parseFloat(result);
                break;
            }
            catch(NumberFormatException ex)
            {
                System.out.println("Ошибка формата данных, повторите ввод.");
            }
        } while(true);
        return Float.parseFloat(result);
    }

    /**
     * Вводит число типа long с приглашением в ограниченом диапазоне значений.
     * @param msg Приглашение для воода.
     * @param min Нижняя граница.
     * @param max Верхняя граница.
     * @return
     */
    public Long readLong(String msg, Long min, Long max)
    {
        String result = "";
        do {
            try
            {
                result = readString(msg, false);
                if (Long.parseLong(result) > max)
                    throw new MaxValueException("Значение поля должно быть меньше "+max.toString());
                if (Long.parseLong(result) < min)
                    throw new MaxValueException("Значение поля должно быть больше "+min.toString());
                break;
            }
            catch(NumberFormatException ex)
            {
                System.out.println("Ошибка формата данных, повторите ввод.");
            }
            catch(MaxValueException ex)
            {
                System.out.println(ex.getMessage());
            }
        } while(true);
        return Long.parseLong(result);
    }

    /**
     * Вводит значение типа с приглашением.
     * @return
     */
    public TicketType readTicketType()
    {
        String result = "";
        TicketType tt;
        do {
            System.out.print("Возможные типы:\nVIP\nUSUAL\nBUDGETAR\n");
            result = readString("Введите музыкальный жанр: ", false);
            tt = stringToType(result);
        }
        while(tt == null);
        return tt;
    }

    /**
     * Вводит объект с клавиатуры или из строки.
     * @param id
     * @param inputString
     * @return
     * @throws IOException
     * @throws java.text.ParseException
     */
    public Ticket fieldEnter(Integer id, String inputString) throws IOException, java.text.ParseException {
        Scanner input;
        if (inputString.length() == 0) {
            input = new Scanner(System.in);
        }
        else
        {
            input = new Scanner(inputString);
        }
        String name = "";
        float cx = 0.0f;
        long cy = 0;
        long price = 0;
        String ticketType = "";
        TicketType type = null;
        String eventName = "";
        long ticketsCount = 0;
        Date date = new Date();
        Event event = null;

        if (inputString.length() == 0) {
            name = readString("Введите имя: ", false);
            System.out.println("Введите координаты: ");
            cx = readFloat("x:");
            cy = readLong("y: ", Long.MIN_VALUE, 670L);
            price = readLong("Введите стоимость: ", 0L, Long.MAX_VALUE);
            type = readTicketType();
            eventName = readString("Введите название: ", true);
            if (eventName.length() != 0) {
                ticketsCount = readLong("Введите кол-во: ", 0L, Long.MAX_VALUE);
                event = new Event(getNextEventId(), eventName, ticketsCount);
            }
        }
        else
        {
            Pattern p1 = Pattern.compile("[{}\\s]+");
            Matcher m = p1.matcher(inputString);
            String[] elements = m.replaceAll("").trim().split(",");

            name = elements[0];
            cx = Float.parseFloat(elements[1]);
            cy = Long.parseLong(elements[2]);
            date = new SimpleDateFormat("dd.mm.yyyy").parse(elements[3]);
            price = Long.parseLong(elements[4]);
            ticketType = elements[5];
            type = TicketList.stringToType(ticketType);

            eventName = elements[6].replaceAll("\"", "");
            if (eventName.length() != 0) {
                ticketsCount = Long.parseLong(elements[7]);
                event = new Event(getNextEventId(), eventName, ticketsCount);
            }
        }

        if (id == null)
        {
            id = this.set.stream().max((a, b) -> (Integer.compare(a.getId(), b.getId()))).get().getId();
            return new Ticket(id + 1, name, new Coordinates(cx, cy), date, price, type, event);
        }
        else
        {
            return new Ticket((int)id, name, new Coordinates(cx, cy), date, price, type, event);
        }
    }

    /**
     * Добавляет объект, если значение его цены больше, чем у наибольшего элемента этой коллекции.
     * @param t
     */
    public void addIfMax(Ticket t)
    {
        long price = this.set.stream().max((a, b) -> (Long.compare(a.getPrice(), b.getPrice()))).get().getPrice();
        if (t.getPrice() > price)
            set.add(t);
    }

    /**
     * Добавляет объект, если значение его цены меньше, чем у наименьшего элемента этой коллекции.
     * @param t
     */
    public void addIfMin(Ticket t)
    {
        long price = this.set.stream().min((a, b) -> (Long.compare(a.getPrice(), b.getPrice()))).get().getPrice();
        if (t.getPrice() < price)
            set.add(t);
    }

    /**
     * Выводит коллекцию, отсортированную по определённому полю.
     * @param type
     */
    void printFieldAscendingType(String type)
    {
        switch (type)
        {
            case "id":
                set.stream().sorted(Comparator.comparingInt(Ticket::getId)).forEach(o->System.out.print(o));
                break;
            case "name":
                set.stream().sorted(Comparator.comparing(Ticket::getName)).forEach(o->System.out.print(o));
                break;
            case "coordinates":
                set.stream().sorted(Comparator.comparing(Ticket::getCoordinates)).forEach(o->System.out.print(o));
                break;
            case "creationDate":
                set.stream().sorted(Comparator.comparing(Ticket::getCreationDate)).forEach(o->System.out.print(o));
                break;
            case "price":
                set.stream().sorted(Comparator.comparing(Ticket::getPrice)).forEach(o->System.out.print(o));
                break;
            case "type":
                set.stream().sorted(Comparator.comparing(Ticket::getType)).forEach(o->System.out.print(o));
                break;
            case "event":
                set.stream().sorted(Comparator.comparing(Ticket::getEvent)).forEach(o->System.out.print(o));
                break;
            default:
                System.out.print("Введённое поле не существует!");
        }
    }

    /**
     * Выполняет команду.
     * @param commandLine
     * @return
     * @throws IOException
     * @throws java.text.ParseException
     * @throws NotEnoughArgumentException
     */
    public boolean executeCommand(String commandLine) throws IOException, java.text.ParseException, NotEnoughArgumentException {
        String[] command;
        command = commandLine.split("[\\s]+", 3);

        switch(command[0])
        {
            case "help":
                System.out.printf("Команды\n");
                System.out.printf("info: вывести в стандартный поток вывода информацию о коллекции.");

                history.add("help");
                break;
            case "info":
                System.out.printf("Тип коллекции: TreeSet\nКол-во элементов: %d\nДата инициализации: %s", set.size(), initData.toString());
                history.add("info");
                break;
            case "show":
                show();

                history.add("show");
                break;
            case "add":
                if (command.length == 2)
                    set.add(fieldEnter(null, command[1]));
                if (command.length == 1)
                    set.add(fieldEnter(null, ""));

                history.add("add");
                break;
            case "update":
                if (command.length < 2)
                    throw new NotEnoughArgumentException("Недостаточно параметров для команды");
                int id = Integer.parseInt(command[1]);
                if (command.length == 3)
                    set.add(fieldEnter(id, command[2]));
                if (command.length < 3)
                    set.add(fieldEnter(id, ""));
                history.add("update");
                break;
            case"remove_by_id":
                if (command[1].length() == 0)
                    throw new NotEnoughArgumentException("Недостаточно параметров для команды");
                id = Integer.parseInt(command[1]);
                removeById(id);
                System.out.println("Элемент удалён!");

                history.add("remove_by_id");
                break;
            case"clear":
                clear();

                history.add("clear");
                break;
            case "save":
                saveFile("input.json");
                System.out.println("Данные сохранены!");

                history.add("save");
                break;
            case"execute_script":
                executeScriptFile(command[1]);

                history.add("execute_script");
                break;
            case"exit":
                return false;
            case"add_if_max":
                if (command.length == 2)
                    addIfMax(fieldEnter(null, command[1]));
                if (command.length == 1)
                    addIfMax(fieldEnter(null, ""));
                history.add("add_if_max");
                break;
            case "add_if_min":
                if (command.length == 2)
                    addIfMin(fieldEnter(null, command[1]));
                if (command.length == 1)
                    addIfMin(fieldEnter(null, ""));
                history.add("add_if_min");
                break;
            case"history":
                int i = 0;
                while(i < 11 && i < history.size())
                {
                    System.out.println(history.get(i));
                    i++;
                }
                history.add("history");
                break;
            case"remove_any_by_price":
                long price = Long.parseLong(command[1]);
                int tmpSize = this.size();
                removeAnyByPrice(price);
                if (this.size() == tmpSize)
                {
                    System.out.println("Элемент не найден.");
                }
                else
                {
                    System.out.println("Элемент удалён");
                }
                history.add("remove_any_by_price");
                break;
            case"filter_starts_with_name":
                filterStartsWithName(command[1]);

                history.add("filter_satarts_with_name");
                break;
            case"print_field_ascending_type":
                printFieldAscendingType(command[1]);
                history.add("print_field_ascending_type");
                break;
            default:
                System.out.printf("Команда не распознана\n");
                break;
        }
        return true;
    }

    /**
     * Выолняет построчно скрипт в файле.
     * @param fileName
     * @throws IOException
     * @throws java.text.ParseException
     * @throws NotEnoughArgumentException
     */
    public void executeScriptFile(String fileName) throws IOException, java.text.ParseException, NotEnoughArgumentException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = br.readLine()) != null)
        {
            executeCommand(line);
        }
    }

    /**
     * Преобразует строку в перечислимый тип TicketType.
     * @param type
     * @return
     */
    public static TicketType stringToType(String type){
        switch(type){
            case "VIP":
                return TicketType.VIP;
            case "USUAL":
                return TicketType.USUAL;
            case "BUDGETAR":
                return TicketType.BUDGETARY;
        }
        return null;
    }

    /**
     * Добавляет или обновляет элемент в коллекцию.
     * @param id
     * @param name
     * @param coordinates
     * @param creationDate
     * @param price
     * @param type
     * @param event
     */
    public void add (long id, String name, Coordinates coordinates, Date creationDate, long price, TicketType type , Event event ){
        set.add(new Ticket((int)id, name, coordinates, creationDate, price, type, event));
    }

    /**
     * Добавляет или обновляет элемент в коллекцию.
     * @param name
     * @param coordinates
     * @param creationDate
     * @param price
     * @param type
     * @param event
     */
    public void add (String name, Coordinates coordinates, Date creationDate, long price, TicketType type , Event event ) {
        int id;
        id = this.set.stream().max((a, b) -> (Integer.compare(a.getId(), b.getId()))).get().getId();
        set.add(new Ticket(id + 1, name, coordinates, creationDate, price, type, event));
    }

    public void show()
    {
        if (set.size() == 0)
            System.out.println("Список пуст!");

        for (Ticket t: set) {
            System.out.println(t);
        }
    }

    public void clear()
    {
        this.set.clear();
    }

    public int size()
    {
        return this.set.size();
    }

    public void removeById(int id)
    {
        this.set.removeIf(o -> o.getId() == id);
    }

    public void removeAnyByPrice(long price)
    {
        for (Ticket t: this.set) {
            if (t.getPrice() == price)
            {
                set.remove(t);
                break;
            }
        }
    }

    public void filterStartsWithName(String name)
    {
        for (Ticket t: this.set) {
            if (t.getName().startsWith(name)) {
                System.out.println(t);
            }
        }
    }

    public int getNextEventId()
    {
        // Проверка на null

        Event e = set.stream().max((a, b) -> (Integer.compare(a.getEvent().getId(), b.getEvent().getId()))).get().getEvent();
        if (e == null)
            return 1;
        return e.getId() + 1;
    }
}
