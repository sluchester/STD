package engtelecom.std;

import java.net.ServerSocket;
import java.util.concurrent.Executors;

public class App {
    public static String versaoAtual;
    public static final int PORTA_TCP = 1234;
    public static final String ENDERECO_MULTICAST = "231.0.0.1";
    public static final int PORTA_MULTICAST = 1290;
    
    public static void main(String[] args) {
        versaoAtual = (args.length > 0) ? args[0] : "v1.0";

        System.out.println("Dispositivo IoT iniciado");
        System.out.println("Versão inicial: " + versaoAtual);
        System.out.printf("Servidor TCP ouvindo na porta: %d%n", PORTA_TCP);
        System.out.printf("Publicando multicast em %s:%d%n", ENDERECO_MULTICAST, PORTA_MULTICAST);

        var publicador = new PublicadorMulticast(
                ENDERECO_MULTICAST,
                PORTA_MULTICAST,
                PORTA_TCP
        );

        Thread.ofVirtual()
                .name("publicador-multicast")
                .start(publicador);

        try (
            ServerSocket serverSocket = new ServerSocket(PORTA_TCP);
            var executor = Executors.newVirtualThreadPerTaskExecutor()
        ) {
            while (!Thread.currentThread().isInterrupted()) {
                var clientSocket = serverSocket.accept();
                executor.submit(new AtenderAtualizacao(clientSocket));
            }
        } catch (Exception e) {
            System.err.println("Erro no dispositivo IoT: " + e.getMessage());
        }
    }
}