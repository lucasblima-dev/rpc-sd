import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class ClienteSincrono {
  private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

  private static void log(String msg) {
    System.out.println(LocalTime.now().format(dtf) + " - " + msg);
  }

  public static void main(String[] args) {
    String nomeCliente = args.length > 0 ? args[0] : "CLIENTE 1";
    int tempoEspera = args.length > 1 ? Integer.parseInt(args[1]) : 10;

    try {
      Registry registry = LocateRegistry.getRegistry("192.168.10.10", 1099);
      ServicoRPC rpc = (ServicoRPC) registry.lookup("ServicoRPC");

      System.out.println("==========================================");
      System.out.println("INICIO EXECUÇÃO SÍNCRONA - " + nomeCliente);
      System.out.println("==========================================");

      log("Enviando requisição RPC Síncrona...");
      log("Operação solicitada: processamentoDemorado(" + tempoEspera + "s)");
      System.out.println(">>> [Cliente bloqueado aguardando resposta do servidor...] <<<");
          
      long inicio = System.currentTimeMillis();
      String resDemorado = rpc.processamentoDemorado(tempoEspera, nomeCliente);
      long fim = System.currentTimeMillis();

      log("Resposta recebida: " + resDemorado);
      log("Tempo total aguardado: " + ((fim - inicio) / 1000) + " segundos\n");

      List<Double> notas = Arrays.asList(8.5, 9.0, 7.5, 10.0, 6.0);
      log("Solicitando processarColecao" + notas + "...");
      String resColecao = rpc.processarColecao(notas, nomeCliente);
      log("Resposta da Coleção: " + resColecao + "\n");

      log("Solicitando calcularSimples(25, 4, '*')...");
      double resCalc = rpc.calcularSimples(25, 4, "*");
      log("Resposta do Calculo: " + resCalc);

      System.out.println("==========================================");
      System.out.println("FIM EXECUÇÃO SÍNCRONA");
      System.out.println("==========================================");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}