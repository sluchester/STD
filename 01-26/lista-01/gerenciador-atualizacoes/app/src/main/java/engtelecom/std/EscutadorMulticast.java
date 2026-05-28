package engtelecom.std;

import java.net.DatagramPacket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;

public class EscutadorMulticast implements Runnable {

    private static final int BUFFER_SIZE = 1024;

    private final String enderecoMulticast;
    private final int portaMulticast;
    private final HashMap<String, String> dispositivos;

    public EscutadorMulticast(String enderecoMulticast, int portaMulticast, HashMap<String, String> dispositivos) {
        this.enderecoMulticast = enderecoMulticast;
        this.portaMulticast = portaMulticast;
        this.dispositivos = dispositivos;
    }

    @Override
    public void run() {
        try (var socket = new MulticastSocket(portaMulticast)) {

            var endereco = InetAddress.getByName(enderecoMulticast);
            var grupo = new InetSocketAddress(endereco, portaMulticast);

            entrarNoGrupoMulticast(socket, grupo);

            while (!Thread.currentThread().isInterrupted()) {
                byte[] buffer = new byte[BUFFER_SIZE];
                var pacote = new DatagramPacket(buffer, buffer.length);

                socket.receive(pacote);

                String mensagem = new String(
                        pacote.getData(),
                        0,
                        pacote.getLength(),
                        StandardCharsets.UTF_8
                ).strip();

                String enderecoOrigem = pacote.getAddress().getHostAddress();

                processarMensagem(enderecoOrigem, mensagem);
            }

        } catch (Exception e) {
            System.err.println("Erro no escutador multicast: " + e.getMessage());
        }
    }

    private void entrarNoGrupoMulticast(MulticastSocket socket, InetSocketAddress grupo) throws Exception {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface interfaceRede = interfaces.nextElement();

            if (!interfaceRede.isUp() || interfaceRede.isLoopback()) {
                continue;
            }

            if (!possuiEnderecoIpv4(interfaceRede)) {
                continue;
            }

            try {
                socket.joinGroup(grupo, interfaceRede);
            } catch (Exception e) {}
        }
    }

    private boolean possuiEnderecoIpv4(NetworkInterface interfaceRede) {
        Enumeration<InetAddress> enderecos = interfaceRede.getInetAddresses();

        while (enderecos.hasMoreElements()) {
            InetAddress endereco = enderecos.nextElement();

            if (!endereco.isLoopbackAddress() && !(endereco instanceof Inet6Address)) {
                return true;
            }
        }

        return false;
    }

    private void processarMensagem(String enderecoOrigem, String mensagem) {
        String[] partes = mensagem.split(";");

        if (partes.length != 2) {
            return;
        }

        String portaTcp = partes[0].strip();
        String versao = partes[1].strip();

        String chave = enderecoOrigem + ":" + portaTcp;

        try {
            dispositivos.put(chave, versao);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar tabela de dispositivos.");
        }
    }
}