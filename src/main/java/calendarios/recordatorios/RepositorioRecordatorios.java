package calendarios.recordatorios;

import java.util.ArrayList;
import java.util.List;

public class RepositorioRecordatorios {
  private final List<Recordatorio> repositorio;

  public RepositorioRecordatorios() {
    this.repositorio = new ArrayList<>();
  }

  public List<Recordatorio> getRepositorio() {
    return repositorio;
  }

  private static final RepositorioRecordatorios INSTANCE = new RepositorioRecordatorios();

  public static RepositorioRecordatorios getInstance() {
    return INSTANCE;
  }

  public final void registrar(Recordatorio recordatorio) {
    repositorio.add(recordatorio);
  }

  public void borrar(Recordatorio recordatorio) {
    repositorio.remove(recordatorio);
  }
}
