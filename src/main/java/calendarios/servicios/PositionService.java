package calendarios.servicios;

import calendarios.Ubicacion;

public interface PositionService {
  Ubicacion ubicacionActual(String email);
}
