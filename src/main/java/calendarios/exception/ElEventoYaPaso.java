package calendarios.exception;

public class ElEventoYaPaso extends RuntimeException {
  public ElEventoYaPaso() {
    super("ERROR: El evento ya ocurrio.");
  }
}
