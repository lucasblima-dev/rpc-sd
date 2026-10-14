import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ClienteAssincrono {
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
      System.out.println("INICIO EXECUÇÃO ASSÍNCRONA - " + nomeCliente);
      System.out.println("==========================================");

      log("Requisicao assincrona enviada: processamentoDemorado(" + tempoEspera + "s)");
      CompletableFuture<String> futureDemorado = CompletableFuture.supplyAsync(() -> {
        try {
          return rpc.processamentoDemorado(tempoEspera, nomeCliente);
        } catch (Exception e) {
          return "Erro: " + e.getMessage();
        }
      });

      List<Double> valores = Arrays.asList(15.0, 25.0, 35.0, 45.0);
      CompletableFuture<String> futureColecao = CompletableFuture.supplyAsync(() -> {
        try {
          return rpc.processarColecao(valores, nomeCliente);
        } catch (Exception e) {
          return "Erro: " + e.getMessage();
        }
      });

      log("Cliente NAO bloqueou e continuou sua execução normal!");

      for (int i = 1; i <= 3; i++) {
        Thread.sleep(2000);
        log("Executando atividade local " + i + " no cliente (interface livre)...");
        if (i == 2) {
          double calc = rpc.calcularSimples(100, 50, "+");
          log(" -> Fez calculo rápido paralelo (100 + 50) = " + calc);
        }
      }

      String resColecao = futureColecao.get();
      log("Resultado Assíncrono (Coleção) ja disponível: " + resColecao);

      log("Finalizou tarefas locais. Aguardando conclusão do futureDemorado.get()...");

      String resultadoFinal = futureDemorado.get();
      log("Resultado RPC disponível!");
      log("Retorno do Servidor: " + resultadoFinal);

      System.out.println("==========================================");
      System.out.println("FIM EXECUÇÃO ASSÍNCRONA");
      System.out.println("==========================================");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}