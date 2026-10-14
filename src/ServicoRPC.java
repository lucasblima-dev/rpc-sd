import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServicoRPC extends Remote {
    double calcularSimples(double a, double b, String operacao) throws RemoteException;

    String processamentoDemorado(int segundos, String nomeCliente) throws RemoteException;

    String processarColecao(List<Double> valores, String nomeCliente) throws RemoteException;
}
