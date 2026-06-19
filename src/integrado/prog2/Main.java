package integrado.prog2;

import integrado.prog2.app.ConsoleApplication;
import integrado.prog2.service.FoodStoreService;
import integrado.prog2.util.InputReader;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            FoodStoreService service = new FoodStoreService();
            InputReader inputReader = new InputReader(scanner);
            ConsoleApplication app = new ConsoleApplication(service, inputReader);
            app.start();
        }
    }
}
