package calendarios.recordatorios;

import java.time.LocalDateTime;

public class Main {
  public static void main(String[] args) {
    RepositorioRecordatorios
        .getInstance()
        .getRepositorio()
        .forEach(recordatorio -> recordatorio.run(LocalDateTime.now()));
  }
}

// * * * * * java /path.java Main.main()

