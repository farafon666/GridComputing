import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteFinder {

    private static int minCost = Integer.MAX_VALUE;
    private static Object lock = new Object();
    private static List<String> minRoute = new ArrayList<>();
    public static Map<Integer, String> cityNames;

    static {
        cityNames = new HashMap<>();
        cityNames.put(0, "A");
        cityNames.put(1, "B");
        cityNames.put(2, "C");
        cityNames.put(3, "D");
    }

    // Метод для поиска наименьшего маршрута
    public static List<String> findCheapestRoute(int[][] adjacencyMatrix, int length, int numThreads) {
        int numCities = adjacencyMatrix.length;

        // Создаем потоки для параллельного поиска маршрутов
        Thread[] threads = new Thread[numThreads];

        // Создаем и запускаем потоки
        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                // Каждый поток ищет маршруты для своего подмножества городов
                for (int j = threadIndex; j < numCities; j += numThreads) {
                    String[] route = new String[length];
                    route[0] = cityNames.get(j);
                    boolean[] visited = new boolean[numCities];
                    visited[j] = true;
                    dfs(adjacencyMatrix, route, visited, length, 1, j, 0);
                }
            });
            threads[i].start(); // Запуск потока
        }

        // Ждем завершения всех потоков
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return minRoute;
    }

    // Метод для рекурсивного поиска маршрута
    private static void dfs(int[][] graph, String[] route, boolean[] visited, int length, int count, int lastNode, int cost) {
        if (count == length) { // Если достигнута нужная длина маршрута
            synchronized (lock) { // Блокируем доступ к общим данным для многопоточности
                if (cost < minCost) { // Сравниваем текущую стоимость с минимальной
                    minCost = cost;
                    minRoute.clear();
                    for (String city : route) {
                        minRoute.add(city);
                    }
                }
            }
            return;
        }

        // Рекурсивно ищем следующий город для маршрута
        for (int i = 0; i < graph.length; i++) {
            if (!visited[i] && graph[lastNode][i] != 0) {
                visited[i] = true;
                route[count] = cityNames.get(i);
                dfs(graph, route, visited, length, count + 1, i, cost + graph[lastNode][i]);
                visited[i] = false;
            }
        }
    }
}
