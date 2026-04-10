package engtelecom.std;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class App {
    public static void main(String[] args) throws IOException {
        String enderecoDoServidor = args[0];
        int portaDoServidor = Integer.parseInt(args[1]);
        String nomeArquivo = args[2]; // agora o nome do arquivo vem por argumento

        try (
            var socket = new Socket(enderecoDoServidor, portaDoServidor);
            var dos = new DataOutputStream(socket.getOutputStream());
            var dis = new DataInputStream(socket.getInputStream())
        ) {
            dos.writeUTF(nomeArquivo);
            dos.flush();

            long tamanho = dis.readLong();

            if (tamanho > 0) {
                try (var fos = new FileOutputStream("recebido_" + nomeArquivo)) {
                    byte[] buffer = new byte[4096];
                    long restante = tamanho;

                    while (restante > 0) {
                        int lidos = dis.read(buffer, 0, (int)Math.min(buffer.length, restante));
                        if (lidos == -1) {
                            throw new IOException("Conexão encerrada antes de receber o arquivo completo.");
                        }

                        fos.write(buffer, 0, lidos);
                        restante -= lidos;
                    }
                }

                System.out.printf("Arquivo recebido com sucesso: %s (%d bytes)%n",
                        "recebido_" + nomeArquivo, tamanho);
            } else {
                System.out.println("O servidor informou que o arquivo não existe: " + nomeArquivo);
            }
        }
    }
}