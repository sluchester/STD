package engtelecom.std;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

//import org.checkerframework.checker.units.qual.s;

public record AtenderAtualizacao(Socket clientSocket) implements Runnable {

    private int compararVersoes(String novaVersao, String versaoAtual) {
        int[] nova = separarVersao(novaVersao);
        int[] atual = separarVersao(versaoAtual);

        if (nova[0] > atual[0]) {
            return 1;
        }

        if (nova[0] < atual[0]) {
            return -1;
        }

        if (nova[1] > atual[1]) {
            return 1;
        }

        if (nova[1] < atual[1]) {
            return -1;
        }

        return 0;
    }

    private int[] separarVersao(String versao) {
        String versaoSemV = versao.replace("v", "");
        String[] partes = versaoSemV.split("\\.");

        int maior = Integer.parseInt(partes[0]);
        int menor = Integer.parseInt(partes[1]);

        return new int[]{maior, menor};
    }

    @Override
    public void run() {
        try (
            clientSocket;
            var dis = new DataInputStream(clientSocket.getInputStream());
            var dos = new DataOutputStream(clientSocket.getOutputStream())
        ) {
            String endereco = clientSocket.getInetAddress().getHostAddress();
            int porta = clientSocket.getPort();

            System.out.printf("Gerenciador conectado: %s:%d%n", endereco, porta);

            String novaVersao = dis.readUTF();

            System.out.println("Nova versao recebida: " + novaVersao);
            System.out.println("Versao anterior: " + App.versaoAtual);

            int resultadoComparacao = compararVersoes(novaVersao, App.versaoAtual);

            if (resultadoComparacao > 0) {

                String versaoAnterior = App.versaoAtual;
                App.versaoAtual = novaVersao;

                System.out.printf("Versao atualizada: %s -> %s%n", versaoAnterior, App.versaoAtual);

                dos.writeUTF("Atualizacao aplicada com sucesso. Versao atual: " + App.versaoAtual);
                System.out.println("");

            } else if (resultadoComparacao == 0) {

                System.out.println("Atualizacao ignorada. O dispositivo já está nessa versao.");

                dos.writeUTF("Atualizacao ignorada. O dispositivo já está na versao: " + App.versaoAtual);

            } else {

                System.out.println("Atualizacao ignorada. A versao recebida é menor que a versao atual.");

                dos.writeUTF("Atualizacao ignorada. Versao recebida: " + novaVersao
                        + " | Versao atual: " + App.versaoAtual);
            }

            dos.flush();

        } catch (Exception e) {
            System.err.println("Erro ao atender atualizacao: " + e.getMessage());
        }
    }
}