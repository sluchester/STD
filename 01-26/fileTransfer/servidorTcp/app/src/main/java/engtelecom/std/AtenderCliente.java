package engtelecom.std;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public record AtenderCliente(Socket clientSocket) implements Runnable {

    @Override
    public void run() {
        try (
            clientSocket;
            var dis = new DataInputStream(clientSocket.getInputStream());
            var dos = new DataOutputStream(clientSocket.getOutputStream())
        ) {
            String endereco = clientSocket.getInetAddress().getHostAddress();
            int porta = clientSocket.getPort();

            System.out.printf("Cliente conectado: %s:%d%n", endereco, porta);

            String nomeArquivo = dis.readUTF();
            System.out.println("Arquivo solicitado: " + nomeArquivo);

            System.out.println("Diretório atual: " + Path.of("").toAbsolutePath());
            System.out.println("Arquivo solicitado: " + nomeArquivo);
            System.out.println("Caminho absoluto procurado: " + Path.of(nomeArquivo).toAbsolutePath());
            Path caminho = Path.of(nomeArquivo);

            if (Files.exists(caminho) && Files.isRegularFile(caminho)) {
                long tamanho = Files.size(caminho);

                dos.writeLong(tamanho);
                Files.copy(caminho, dos);
                dos.flush();

                System.out.printf("Enviado: %s (%d bytes)%n", nomeArquivo, tamanho);
            } else {
                dos.writeLong(-1);
                dos.flush();

                System.out.println("Arquivo não encontrado: " + nomeArquivo);
            }

        } catch (Exception e) {
            System.err.println("Erro ao atender cliente: " + e.getMessage());
        }
    }
}