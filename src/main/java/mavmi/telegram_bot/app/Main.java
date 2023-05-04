package mavmi.telegram_bot.app;

import mavmi.telegram_bot.telegram_bot.Bot;

import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        System.out.print("Enter bot token: ");
        Bot bot = new Bot(new Scanner(System.in).nextLine());
        bot.run();
    }
}
