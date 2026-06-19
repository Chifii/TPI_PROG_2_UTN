package integrado.prog2.util;

import java.util.Scanner;

public class InputReader {
    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public int readMenuOption(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                int option = Integer.parseInt(raw);
                if (option < min || option > max) {
                    System.out.println("Opción fuera de rango. Probá de nuevo.");
                    continue;
                }
                return option;
            } catch (NumberFormatException ex) {
                System.out.println("Tenés que ingresar un número válido.");
            }
        }
    }

    public String readRequiredString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Este campo no puede quedar vacío.");
        }
    }

    public String readOptionalString(String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        return value.isEmpty() ? null : value;
    }

    public Long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                return Long.parseLong(raw);
            } catch (NumberFormatException ex) {
                System.out.println("Tenés que ingresar un número entero válido.");
            }
        }
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException ex) {
                System.out.println("Tenés que ingresar un número entero válido.");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim().replace(',', '.');
            try {
                return Double.parseDouble(raw);
            } catch (NumberFormatException ex) {
                System.out.println("Tenés que ingresar un número decimal válido.");
            }
        }
    }

    public boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim().toLowerCase();
            if (raw.equals("s") || raw.equals("si") || raw.equals("sí") || raw.equals("y") || raw.equals("yes")) {
                return true;
            }
            if (raw.equals("n") || raw.equals("no")) {
                return false;
            }
            System.out.println("Respondé con S/N.");
        }
    }

    public <E extends Enum<E>> E readEnum(String prompt, E[] values) {
        System.out.println(prompt);
        for (int i = 0; i < values.length; i++) {
            System.out.printf("%d. %s%n", i + 1, values[i]);
        }
        int option = readMenuOption("Seleccione: ", 1, values.length);
        return values[option - 1];
    }
}
