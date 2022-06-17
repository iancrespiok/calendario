package calendarios.servicios;

import calendarios.Ubicacion;
import java.time.Duration;

public interface GugleMapas {
  Duration tiempoEstimadoHasta(Ubicacion partida, Ubicacion llegada);
}
