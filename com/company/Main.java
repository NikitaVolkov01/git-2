

package com.company;

import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {
        String[] command;
        TicketList ticketList = new TicketList();
        ticketList.loadFile("input.json");
        Scanner input = new Scanner(System.in);
        boolean isrun = true;
        isrun = true;
        while(isrun){
            System.out.print("> ");
            String inputString = input.nextLine();
            try {
                isrun = ticketList.executeCommand(inputString);
            }
            catch (NotEnoughArgumentException ex)
            {
                System.out.println(ex.getMessage());
            }
            catch(NumberFormatException ex)
            {
                System.out.println("Ошибка в формате данных!");
            }
            System.out.println();
        }
    }
}
