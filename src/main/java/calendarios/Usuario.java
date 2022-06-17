package calendarios;

import calendarios.eventos.Evento;
import calendarios.recordatorios.Recordatorio;
import calendarios.recordatorios.RecordatorioMailer;
import calendarios.recordatorios.RecordatorioNotificador;
import calendarios.recordatorios.RepositorioRecordatorios;
import calendarios.servicios.GugleMapas;
import calendarios.servicios.PositionService;
import calendarios.servicios.ShemailLib;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Usuario {
  private List<Calendario> calendarios = new ArrayList<>();
  private List<Recordatorio> recordatorios = new ArrayList<>();
  private String email;
  private PositionService positionService;
  private GugleMapas gugleMapas;

  public Usuario(String email, PositionService positionService, GugleMapas gugleMapas) {
    this.email = email;
    this.positionService = positionService;
    this.gugleMapas = gugleMapas;
  }

  public String getEmail() {
    return email;
  }

  public List<Recordatorio> getRecordatorios() {
    return recordatorios;
  }

  public void recordarConNotificacion(Evento evento, Duration tiempoAnticipacion) {
    Recordatorio recordatorio = new RecordatorioNotificador(evento,tiempoAnticipacion,this);
    RepositorioRecordatorios.getInstance().registrar(recordatorio);
  }

  public void recordarConMail(Evento evento, Duration tiempoAnticipacion, ShemailLib shemailLib) {
    Recordatorio recordatorio = new RecordatorioMailer(evento,tiempoAnticipacion,this, shemailLib);
    RepositorioRecordatorios.getInstance().registrar(recordatorio);
    recordatorios.add(recordatorio);
  }

  public void agregarCalendario(Calendario calendario) {
    calendarios.add(calendario);
  }

  public List<Evento> eventosEntreFechas(LocalDate inicio, LocalDate fin) {
    List<Evento> eventos = new ArrayList<Evento>();
    calendarios.forEach(calendario -> eventos.addAll(calendario.eventosEntreFechas(inicio,fin)));
    return eventos;
  }

  public boolean llegaATiempoAlProximoEvento() {
    Evento proximoEvento = getProximoEvento();
    return llegaATiempoA(proximoEvento);
  }

  private Evento getProximoEvento() {
    List<Evento> eventosTotales = new ArrayList<Evento>();
    if (calendarios.isEmpty()) {
      return null;
    }
    calendarios.forEach(calendario -> eventosTotales.addAll(calendario.getEventos()));
    return eventosTotales.stream().min(Comparator.comparing(
        evento -> evento.tiempoRestante())).get();
  }

  public boolean tieneCalendario(Calendario calendario) {
    return calendarios.contains(calendario);
  }

  private Boolean llegaATiempoA(Evento evento) {
    if (evento == null) {
      return true;
    }
    Ubicacion ubicacionActual = positionService.ubicacionActual(email);
    Duration tiempoParaLlegarAlEvento =  gugleMapas.tiempoEstimadoHasta(ubicacionActual,evento.getUbicacion());
    Duration tiempoQueFaltaParaQueComienceElEvento = Duration.between(LocalDateTime.now(), evento.proximaIteracion(LocalDateTime.now()));
    return tiempoQueFaltaParaQueComienceElEvento.compareTo(tiempoParaLlegarAlEvento) > 0;
  }

  public Ubicacion getUbicacion() {
    return positionService.ubicacionActual(email);
  }

}
