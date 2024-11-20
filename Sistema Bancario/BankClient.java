import java.io.*;
import java.net.*;

public class BankClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080);
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Conectado ao servidor bancário.");

            System.out.print("Digite o número da conta: ");
            String numeroConta = console.readLine();

            System.out.print("Digite o tipo de operação (SAQUE ou DEPOSITO): ");
            String tipoOperacao = console.readLine();

            System.out.print("Digite o valor: ");
            double valor = Double.parseDouble(console.readLine());

            String requisicao = numeroConta + ";" + tipoOperacao + ";" + valor;
            out.println(requisicao);

            String resposta = in.readLine();
            System.out.println("Mensagem do servidor: " + resposta);

        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro no cliente: " + e.getMessage());
        }
    }
}
