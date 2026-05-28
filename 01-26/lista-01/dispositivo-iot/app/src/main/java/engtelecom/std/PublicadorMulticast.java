package engtelecom.std;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class PublicadorMulticast implements Runnable {

    private final String enderecoMulticast;
    private final int portaMulticast;
    private final int portaTcp;

    public PublicadorMulticast(String enderecoMulticast, int portaMulticast, int portaTcp) {
        this.enderecoMulticast = enderecoMulticast;
        this.portaMulticast = portaMulticast;
        this.portaTcp = portaTcp;
    }

    @Override
    public void run() {
        try (var socket = new DatagramSocket()) {

            var endereco = InetAddress.getByName(enderecoMulticast);

            while (!Thread.currentThread().isInterrupted()) {
                String mensagem = portaTcp + ";" + App.versaoAtual;

                byte[] buffer = mensagem.getBytes(StandardCharsets.UTF_8);

                var pacote = new DatagramPacket(
                        buffer,
                        buffer.length,
                        endereco,
                        portaMulticast
                );

                socket.send(pacote);
                
                System.out.println("Publicação multicast enviada: " + mensagem);
                Thread.sleep(10000);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Publicador multicast interrompido.");
        } catch (Exception e) {
            System.err.println("Erro no publicador multicast: " + e.getMessage());
        }
    }
}