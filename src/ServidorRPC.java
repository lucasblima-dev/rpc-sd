import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class ServidorRPC extends UnicastRemoteObject implements ServicoRPC {

  private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

  protected ServidorRPC() throws RemoteException {
    super();
  }

  private void log(String msg) {
    String hora = LocalTime.now().format(dtf);
    String threadName = Thread.currentThread().getName();
    System.out.println("[" + hora + "] [" + threadName + "] " + msg);
  }

  @Override
  public double calcularSimples(double a, double b, String operacao) throws RemoteException {
    log("Recebida chamada: calcularSimples(" + a + ", " + b + ", " + operacao + ")");
    switch (operacao) {
      case "+": return a + b;
      case "-": return a - b;
      case "*": return a * b;
      case "/": return b != 0 ? a / b : 0;
      default: return 0;
    }
  }

  @Override
  public String processamentoDemorado(int segundos, String nomeCliente) throws RemoteException {
    log("INICIANDO processamentoDemorado de " + segundos + "s solicitado por " + nomeCliente + "...");
    try {
        // Simula tarefa pesada bloqueando apenas a Thread do servidor
        Thread.sleep(segundos * 1000L);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    log("FINALIZADO processamentoDemorado de " + segundos + "s para " + nomeCliente + "!");
    return "Tarefa pesada de " + segundos + " segundos concluida com sucesso para " + nomeCliente;
  }

  @Override
  public String processarColecao(List<Double> valores, String nomeCliente) throws RemoteException {
    log("INICIANDO processarColecao (" + valores.size() + " itens) para " + nomeCliente + "...");
    try {
      // Simula um tempo de 3s para processar a lista
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    double soma = 0;
    for (double v : valores) soma += v;
    double media = valores.isEmpty() ? 0 : soma / valores.size();
    double maior = valores.isEmpty() ? 0 : Collections.max(valores);
    double menor = valores.isEmpty() ? 0 : Collections.min(valores);

    log("FINALIZADO processarColecao para " + nomeCliente + "!");
    return String.format("Relatorio Colecao -> Media: %.2f | Maior: %.2f | Menor: %.2f", media, maior, menor);
  }

  public static void main(String[] args) {
    try {
      System.setProperty("java.rmi.server.hostname", "192.168.10.10");

      Registry registry = LocateRegistry.createRegistry(1099);
      ServidorRPC servidor = new ServidorRPC();
      registry.rebind("ServicoRPC", servidor);

      System.out.println("====================================================");
      System.out.println("Servidor RPC Multithread Iniciado na VM1 (192.168.10.10:1099)");
      System.out.println("Aguardando chamadas remotas dos clientes...");
      System.out.println("====================================================");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}