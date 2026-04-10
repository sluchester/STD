package engtelecom.std;

import module java.base; // Java 25: importação de módulo

public class ServidorUdp {
private static final int PORTA = 9876;
private static final int BUFFER_SIZE = 1024;
private static final String RESPOSTA = "Olá, eu sou o servidor UDP!";

record PacoteRecebido(String mensagem, InetAddress origem, int portaOrigem) {}

sealed interface Resultado permits Resultado.Sucesso, Resultado.Falha {
    record Sucesso(PacoteRecebido pacote) implements Resultado {}
    record Falha(String motivo) implements Resultado {}
}

private static Resultado receberPacote(DatagramSocket socket, byte[] buffer) {
    var pacote = new DatagramPacket(buffer, buffer.length);
    try {
        socket.receive(pacote);
        String msg = new String(pacote.getData(), 0, pacote.getLength(), StandardCharsets.UTF_8);
        return new Resultado.Sucesso(new PacoteRecebido(msg, pacote.getAddress(), pacote.getPort()));
    } catch (IOException e) {
        return new Resultado.Falha(e.getMessage());
    }
}

private static void enviarResposta(DatagramSocket socket, InetAddress destino, int portaDestino) throws IOException {
    byte[] bytes = RESPOSTA.getBytes(StandardCharsets.UTF_8);
    socket.send(new DatagramPacket(bytes, bytes.length, destino, portaDestino));
}

public static void iniciarServidor() {
    System.out.printf("""
        Servidor UDP iniciado
        Escutando na porta %d com buffer de %d bytes...
        Pressione CTRL+C para encerrar.
    """, PORTA, BUFFER_SIZE);

    try (var socket = new DatagramSocket(PORTA)) {
        byte[] buffer = new byte[BUFFER_SIZE];
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("Aguardando pacote...");
                
            switch (receberPacote(socket, buffer)) {
                case Resultado.Sucesso(var p) -> {
                    System.out.printf("Mensagem de %s:%d -> %s%n",
                            p.origem().getHostAddress(), p.portaOrigem(), p.mensagem());
                        enviarResposta(socket, p.origem(), p.portaOrigem());
                }
                case Resultado.Falha(var motivo) ->
                    System.err.println("Falha ao receber pacote: " + motivo);
                }
            }
    } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
    }
}
public static void main(String[] args) {
    // Criando uma thread virtual para o servidor UDP
    try {
        Thread.ofVirtual()
            .name("servidor-udp")
            .start(AppUdpServer::iniciarServidor)
            .join();
        } catch(InterruptedException e){
            e.printStackTrace();
        } // Aguarda a thread do servidor terminar (neste caso, ela roda indefinidamente)
    }
}