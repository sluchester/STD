package engtelecom.std;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {

    public static final String ENDERECO_MULTICAST = "231.0.0.1";
    public static final int PORTA_MULTICAST = 1290;

    public static void main(String[] args) {

        HashMap<String, String> dispositivos = new HashMap<>();

        System.out.println("Gerenciador de atualizacoes iniciado");
        System.out.printf("Escutando multicast em %s:%d%n", ENDERECO_MULTICAST, PORTA_MULTICAST);
        System.out.println("");

        var escutador = new EscutadorMulticast(
                ENDERECO_MULTICAST,
                PORTA_MULTICAST,
                dispositivos
        );

        Thread.ofVirtual()
                .name("escutador-multicast")
                .start(escutador);

        iniciarCli(dispositivos);
    }

    private static void iniciarCli(HashMap<String, String> dispositivos) {
        Scanner scanner = new Scanner(System.in);

        boolean executando = true;

        while (executando) {
            exibirMenu();

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1" -> listarDispositivos(dispositivos);
                case "2" -> atualizarDispositivoEspecifico(dispositivos, scanner);
                case "3" -> atualizarTodos(dispositivos);
                case "4" -> {
                    System.out.println("Encerrando gerenciador...");
                    executando = false;
                }
                default -> System.out.println("Opcao invalida.");
            }
        }
    }

    private static void exibirMenu() {
        System.out.println();
        System.out.println("========== MENU GERENCIADOR ==========");
        System.out.println("1 - Listar dispositivos IoT ativos");
        System.out.println("2 - Atualizar um dispositivo especifico");
        System.out.println("3 - Atualizar todos os dispositivos");
        System.out.println("4 - Sair");
        System.out.println("");
        System.out.print("Escolha uma opção: ");
    }

    private static void listarDispositivos(HashMap<String, String> dispositivos) {
        try {
            if (dispositivos.isEmpty()) {
                System.out.println("Nenhum dispositivo IoT ativo encontrado.");
                return;
            }

            System.out.println();
            System.out.println("Dispositivos IoT ativos:");

            int indice = 1;

            for (Map.Entry<String, String> dispositivo : dispositivos.entrySet()) {

                System.out.printf(
                        "%d - %s -> %s%n",
                        indice,
                        dispositivo.getKey(),
                        dispositivo.getValue()
                );
                indice++;
            }
            System.out.println("");

        } catch (Exception e) {
            System.out.println("Erro ao listar dispositivos.");
        }
    }

    private static void atualizarDispositivoEspecifico(
            HashMap<String, String> dispositivos,
            Scanner scanner
    ) {
        ArrayList<Map.Entry<String, String>> listaDispositivos;

        try {
            listaDispositivos = new ArrayList<>(dispositivos.entrySet());
        } catch (Exception e) {
            System.out.println("Erro ao acessar dispositivos.");
            return;
        }

        if (listaDispositivos.isEmpty()) {
            System.out.println("Nenhum dispositivo disponível.");
            return;
        }

        System.out.println();
        System.out.println("Escolha o dispositivo:");

        for (int i = 0; i < listaDispositivos.size(); i++) {
            Map.Entry<String, String> dispositivo = listaDispositivos.get(i);

            System.out.printf(
                    "%d - %s -> %s%n",
                    i + 1,
                    dispositivo.getKey(),
                    dispositivo.getValue()
            );
        }

        System.out.print("Número do dispositivo: ");

        String entrada = scanner.nextLine();

        int indiceEscolhido;

        try {
            indiceEscolhido = Integer.parseInt(entrada) - 1;
        } catch (Exception e) {
            System.out.println("Número inválido.");
            return;
        }

        if (indiceEscolhido < 0 || indiceEscolhido >= listaDispositivos.size()) {
            System.out.println("Dispositivo inválido.");
            return;
        }

        Map.Entry<String, String> dispositivoEscolhido =
                listaDispositivos.get(indiceEscolhido);

        String chave = dispositivoEscolhido.getKey();
        String versaoAtual = dispositivoEscolhido.getValue();

        String novaVersao = incrementarVersao(versaoAtual);

        String[] enderecoEPorta = separarEndereco(chave);

        String endereco = enderecoEPorta[0];
        int porta = Integer.parseInt(enderecoEPorta[1]);

        System.out.printf(
                "Atualizando dispositivo %s de %s para %s%n",
                chave,
                versaoAtual,
                novaVersao
        );

        boolean atualizou = enviarAtualizacaoTcp(
                endereco,
                porta,
                novaVersao
        );

        if (atualizou) {
            try {
                dispositivos.put(chave, novaVersao);
                System.out.println("Dispositivo atualizado com sucesso.");
            } catch (Exception e) {
                System.out.println("Erro ao atualizar tabela.");
            }
        } else {
            try {
                dispositivos.remove(chave);
            } catch (Exception e) {
                System.out.println("Erro ao remover dispositivo.");
            }
            
            System.out.println("Falha ao remover dispositivos inoperantes.");
        }
    }

    private static void atualizarTodos(HashMap<String, String> dispositivos) {

        ArrayList<Map.Entry<String, String>> listaDispositivos;

        try {
            listaDispositivos = new ArrayList<>(dispositivos.entrySet());
        } catch (Exception e) {
            System.out.println("Erro ao acessar dispositivos.");
            return;
        }

        if (listaDispositivos.isEmpty()) {
            System.out.println("Nenhum dispositivo disponível.");
            return;
        }

        System.out.println();
        System.out.println("Atualizando todos os dispositivos...");

        for (Map.Entry<String, String> dispositivo : listaDispositivos) {

            String chave = dispositivo.getKey();
            String versaoAtual = dispositivo.getValue();

            String novaVersao = incrementarVersao(versaoAtual);

            String[] enderecoEPorta = separarEndereco(chave);

            String endereco = enderecoEPorta[0];
            int porta = Integer.parseInt(enderecoEPorta[1]);

            System.out.printf(
                    "Atualizando %s de %s para %s%n",
                    chave,
                    versaoAtual,
                    novaVersao
            );

            boolean atualizou = enviarAtualizacaoTcp(
                    endereco,
                    porta,
                    novaVersao
            );

            if (atualizou) {
                try {
                    dispositivos.put(chave, novaVersao);
                    System.out.println("Dispositivo atualizado com sucesso.");
                } catch (Exception e) {
                    System.out.println("Erro ao atualizar tabela.");
                }
            } else {
                try {
                    dispositivos.remove(chave);
                } catch (Exception e) {
                    System.out.println("Erro ao remover dispositivo.");
                }
                System.out.println("Falha ao remover dispositivos inoperantes.");
            }
        }
        System.out.println("Atualizacao em massa finalizada.");
    }

    private static boolean enviarAtualizacaoTcp(
            String endereco,
            int porta,
            String novaVersao
    ) {

        try (
                var socket = new Socket(endereco, porta);
                var dos = new DataOutputStream(socket.getOutputStream());
                var dis = new DataInputStream(socket.getInputStream())
        ) {

            dos.writeUTF(novaVersao);
            dos.flush();

            String resposta = dis.readUTF();

            System.out.println("Resposta:");
            System.out.println(resposta);

            return true;

        } catch (Exception e) {
            System.err.println("Erro ao atualizar dispositivo: " + e.getMessage());

            return false;
        }
    }

    private static String incrementarVersao(String versaoAtual) {

        int[] partes = separarVersao(versaoAtual);

        int maior = partes[0];
        int menor = partes[1];

        menor++;

        return "v" + maior + "." + menor;
    }

    private static int[] separarVersao(String versao) {

        String versaoSemV = versao.replace("v", "");

        String[] partes = versaoSemV.split("\\.");

        int maior = Integer.parseInt(partes[0]);
        int menor = Integer.parseInt(partes[1]);

        return new int[]{maior, menor};
    }

    private static String[] separarEndereco(String chave) {

        int posicao = chave.lastIndexOf(":");

        String endereco = chave.substring(0, posicao);

        String porta = chave.substring(posicao + 1);

        return new String[]{endereco, porta};
    }
}