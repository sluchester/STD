package engtelecom.std;

import java.net.ServerSocket;
import java.util.concurrent.Executors;

public class App {
    public static void main(String[] args) {
        int porta = (args.length > 0) ? Integer.parseInt(args[0]) : 12345;

        System.out.printf("Servidor ouvindo na porta: %d%n", porta);

        try (
            ServerSocket serverSocket = new ServerSocket(porta);
            var executor = Executors.newVirtualThreadPerTaskExecutor()
        ) {
            while (!Thread.currentThread().isInterrupted()) {
                var clientSocket = serverSocket.accept();
                executor.submit(new AtenderCliente(clientSocket));
            }
        } catch (Exception e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }
}