import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class BankServer {
    private static final String DATA_FILE = "dados_bancarios.txt";
    private static Map<String, Double> contas = new HashMap<>();

    public static void main(String[] args) {
        carregarContas();

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Servidor bancário iniciado. Aguardando conexões...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
                tratarCliente(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }

    private static void tratarCliente(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            String requisicao = in.readLine();
            System.out.println("Requisição recebida: " + requisicao);

            String[] partes = requisicao.split(";");
            if (partes.length == 3) {
                String numeroConta = partes[0];
                String tipoOperacao = partes[1];
                double valor = Double.parseDouble(partes[2]);

                String resposta = processarOperacao(numeroConta, tipoOperacao, valor);
                out.println(resposta);
            } else {
                out.println("Erro: Formato de requisição inválido.");
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao tratar cliente: " + e.getMessage());
        }
    }

    private static String processarOperacao(String numeroConta, String tipoOperacao, double valor) {
        if (!contas.containsKey(numeroConta)) {
            return "Erro: Conta não cadastrada.";
        }

        double saldoAtual = contas.get(numeroConta);
        if ("SAQUE".equalsIgnoreCase(tipoOperacao)) {
            if (valor > saldoAtual) {
                return "Erro: Saldo insuficiente.";
            }
            saldoAtual -= valor;
        } else if ("DEPOSITO".equalsIgnoreCase(tipoOperacao)) {
            saldoAtual += valor;
        } else {
            return "Erro: Tipo de operação inválido.";
        }

        contas.put(numeroConta, saldoAtual);
        salvarContas();
        return "Operação realizada com sucesso. Saldo atual: R$ " + saldoAtual;
    }

    private static void carregarContas() {
        try (BufferedReader br = new BufferedReader(new FileReader(DATA_FILE))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length == 2) {
                    contas.put(partes[0], Double.parseDouble(partes[1]));
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar contas: " + e.getMessage());
        }
    }

    private static void salvarContas() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Map.Entry<String, Double> conta : contas.entrySet()) {
                bw.write(conta.getKey() + ";" + conta.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar contas: " + e.getMessage());
        }
    }
}
