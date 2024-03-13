import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Grid {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Main <length>");
            return;
        }

        String inputFile = "src/main/resources/data.txt";
        int length = Integer.parseInt(args[0]);
        int numThreads = 4;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            // Считываем размер матрицы
            int size = Integer.parseInt(br.readLine());
            int[][] adjacencyMatrix = new int[size][size];
            int row = 0;
            // Читаем матрицу смежности из файла
            while ((line = br.readLine()) != null) {
                String[] values = line.trim().split("\\s+");
                for (int i = 0; i < values.length; i++) {
                    adjacencyMatrix[row][i] = Integer.parseInt(values[i]);
                }
                row++;
            }

            // Используем класс RouteFinder для поиска наименьшего маршрута
            List<String> minRoute = RouteFinder.findCheapestRoute(adjacencyMatrix, length, numThreads);
            int cheapestCost = calculateRouteCost(adjacencyMatrix, minRoute);

            // Вывод результата
            System.out.println("Cheapest cost: " + cheapestCost);
            System.out.println("Route: " + minRoute);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для расчета стоимости маршрута
    private static int calculateRouteCost(int[][] adjacencyMatrix, List<String> route) {
        int cost = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            int from = getCityIndex(route.get(i));
            int to = getCityIndex(route.get(i + 1));
            cost += adjacencyMatrix[from][to];
        }
        return cost;
    }

    // Метод для получения индекса города из его имени
    private static int getCityIndex(String cityName) {
        for (Map.Entry<Integer, String> entry : RouteFinder.cityNames.entrySet()) {
            if (entry.getValue().equals(cityName)) {
                return entry.getKey();
            }
        }
        return -1;
    }
}
