package app;

import ui.ConsoleInterface;

/**
 * Точка входа в приложение.
 * Загружает конфигурацию и запускает консольный интерфейс.
 */
public class Main {
    public static void main(String[] args) {
        // Запуск консольного интерфейса
        ConsoleInterface console = new ConsoleInterface();
        console.run();
    }
}
/*
 git add .
 git commit -m "afa"
 git push
 */