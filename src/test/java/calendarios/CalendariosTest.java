package calendarios;

import calendarios.eventos.Evento;
import calendarios.eventos.EventoRepetitivo;
import calendarios.eventos.EventoUnico;
import calendarios.eventos.tiposderepeticion.*;
import calendarios.exception.ElEventoYaPaso;
import calendarios.recordatorios.Main;
import calendarios.recordatorios.RepositorioRecordatorios;
import calendarios.servicios.GugleMapas;
import calendarios.servicios.PositionService;
import calendarios.servicios.ShemailLib;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.time.Month.DECEMBER;
import static org.mockito.Mockito.*;
import static java.time.temporal.ChronoUnit.*;
import static org.junit.jupiter.api.Assertions.*;

class CalendariosTest {

  private PositionService positionService;
  private GugleMapas gugleMapas;
  private ShemailLib shemailLib;

  Ubicacion utnMedrano = new Ubicacion(-34.5984145, -58.4222096);
  Ubicacion utnCampus = new Ubicacion(-34.6591644,-58.4694862);
  Usuario ian = new Usuario("crespi.ian",positionService,gugleMapas);
  Usuario jorge = new Usuario("ramirez.jorge",positionService,gugleMapas);

  @BeforeEach
  void initFileSystem() {
    positionService = mock(PositionService.class);
    gugleMapas = mock(GugleMapas.class);
    shemailLib = mock(ShemailLib.class);
  }

  // 1. Permitir que une usuarie tenga muchos calendarios

  @Test
  void uneUsuarieTieneMuchosCalendarios() {
    Usuario rene = crearUsuario("rene@gugle.com.ar");
    Calendario calendario = crearCalendarioVacio();

    rene.agregarCalendario(calendario);

    assertTrue(rene.tieneCalendario(calendario));
  }

  // 2. Permitir que en cada calendario se agenden múltiples eventos


  // 3. Permitir que los eventos registren nombre, fecha y hora de inicio y fin, ubicación, invitades (otros usuaries)

  @Test
  void siSePideProximaIteracionDesdeUnDiaPosteriorAlFinDelEventoRompe() {
    Evento eventoCada10Dias = new EventoRepetitivo(
        "CenaFamiliar",
        utnMedrano,
        Collections.emptyList(),
        LocalTime.of(12,0),
        Duration.of(3, HOURS),
        LocalDate.of(2021,7,7),
        LocalDate.of(2024,7,7),
        new CadaN(LocalDate.of(2021,7,7),Period.of(0,0,4))
        );

    assertThrows(ElEventoYaPaso.class, () -> eventoCada10Dias.proximaIteracion(LocalDateTime.of(2025,1,1,1,0)));
  }

  @Test
  void unEventoPuedeTenerMultiplesInvitades() {
    Evento tpRedes = crearEventoSimpleEnMedrano("TP de Redes", LocalDateTime.of(2020, 4, 3, 16, 0), Duration.of(2,  HOURS));
    tpRedes.agregarInvitado(ian);
    tpRedes.agregarInvitado(jorge);

    assertTrue(tpRedes.getInvitados().contains(ian) && tpRedes.getInvitados().contains(jorge) && tpRedes.getInvitados().size()==2);
  }

  @Test
  void unCalendarioPermiteAgendarUnEvento() {
    Calendario calendario = new Calendario();

    Evento seguimientoDeTP = crearEventoSimpleEnMedrano("Seguimiento de TP", LocalDateTime.of(2021, 10, 1, 15, 30), Duration.of(30, MINUTES));
    calendario.agendar(seguimientoDeTP);

    assertTrue(calendario.estaAgendado(seguimientoDeTP));
  }

  @Test
  void unCalendarioPermiteAgendarDosEvento() {
    Calendario calendario = new Calendario();
    LocalDateTime inicio = LocalDateTime.of(2021, 10, 1, 15, 30);

    Evento seguimientoDeTPA = crearEventoSimpleEnMedrano("Seguimiento de TPA", inicio, Duration.of(30, MINUTES));
    Evento practicaParcial = crearEventoSimpleEnMedrano("Practica para el primer parcial", inicio.plusMinutes(60), Duration.of(90, MINUTES));

    calendario.agendar(seguimientoDeTPA);
    calendario.agendar(practicaParcial);

    assertTrue(calendario.estaAgendado(seguimientoDeTPA));
    assertTrue(calendario.estaAgendado(practicaParcial));
  }


  // 4. Permitir listar los próximos eventos entre dos fechas

  @Test
  void sePuedeListarUnEventoEntreDosFechasParaUnCalendario() {
    // Nota: Esto es opcional pero puede ayudar a resolver el siguiente item.
    // Borrar este test si no se utiliza

    Calendario calendario = new Calendario();
    Evento tpRedes = crearEventoSimpleEnMedrano("TP de Redes", LocalDateTime.of(2020, 4, 3, 16, 0), Duration.of(2,  HOURS));

    calendario.agendar(tpRedes);

    List<EventoUnico> eventos = calendario.eventosEntreFechas(
        LocalDate.of(2020, 4, 1),
        LocalDate.of(2020, 4, 4));

    assertEquals(eventos, Arrays.asList(tpRedes));
  }

  @Test
  void sePuedeListarUnEventoEntreDosFechasParaUneUsuarie() {
    Usuario rene = crearUsuario("rene@gugle.com.ar");
    Calendario calendario = new Calendario();
    rene.agregarCalendario(calendario);

    Evento tpRedes = crearEventoSimpleEnMedrano("TP de Redes", LocalDateTime.of(2020, 4, 3, 16, 0), Duration.of(2,  HOURS));

    calendario.agendar(tpRedes);

    List<Evento> eventos = rene.eventosEntreFechas(
        LocalDate.of(2020, 4, 1),
        LocalDate.of(2020, 4, 4));

    assertEquals(eventos, Arrays.asList(tpRedes));
  }

  @Test
  void noSeListaUnEventoSiNoEstaEntreLasFechasIndicadasParaUneUsuarie() {
    Usuario dani = crearUsuario("dani@gugle.com.ar");
    Calendario calendario = new Calendario();
    dani.agregarCalendario(calendario);

    Evento tpRedes = crearEventoSimpleEnMedrano("TP de Redes", LocalDateTime.of(2020, 4, 3, 16, 0), Duration.of(1, HOURS));

    calendario.agendar(tpRedes);

    List<Evento> eventos = dani.eventosEntreFechas(
        LocalDate.of(2020, 5, 8),
        LocalDate.of(2020, 5, 16));

    assertTrue(eventos.isEmpty());
  }

  @Test
  void sePuedenListarMultiplesEventoEntreDosFechasParaUneUsuarieConCoincidenciaParcial() {
    Usuario usuario = crearUsuario("rene@gugle.com.ar");
    Calendario calendario = new Calendario();
    usuario.agregarCalendario(calendario);

    Evento tpRedes = crearEventoSimpleEnMedrano("TP de Redes", LocalDateTime.of(2020, 4, 3, 16, 0), Duration.of(2,  HOURS));
    Evento tpDeGestion = crearEventoSimpleEnMedrano("TP de Gestión", LocalDateTime.of(2020, 4, 5, 18, 30), Duration.of(2,  HOURS));
    Evento tpDeDds = crearEventoSimpleEnMedrano("TP de DDS", LocalDateTime.of(2020, 4, 12, 16, 0), Duration.of(2,  HOURS));

    calendario.agendar(tpRedes);
    calendario.agendar(tpDeGestion);
    calendario.agendar(tpDeDds);

    List<Evento> eventos = usuario.eventosEntreFechas(
        LocalDate.of(2020, 4, 1),
        LocalDate.of(2020, 4, 6));

    assertEquals(eventos, Arrays.asList(tpRedes, tpDeGestion));
  }


  @Test
  void sePuedenListarMultiplesEventoEntreDosFechasParaUneUsuarieConCoincidenciaTotal() {
    Usuario juli = crearUsuario("juli@gugle.com.ar");
    Calendario calendario = new Calendario();
    juli.agregarCalendario(calendario);

    Evento tpRedes = crearEventoSimpleEnMedrano("TP de Redes", LocalDateTime.of(2020, 4, 3, 16, 0), Duration.of(2, HOURS));
    Evento tpDeGestion = crearEventoSimpleEnMedrano("TP de Gestión", LocalDateTime.of(2020, 4, 5, 18, 30), Duration.of(30, MINUTES));
    Evento tpDeDds = crearEventoSimpleEnMedrano("TP de DDS", LocalDateTime.of(2020, 4, 12, 16, 0), Duration.of(1, HOURS));

    calendario.agendar(tpRedes);
    calendario.agendar(tpDeGestion);
    calendario.agendar(tpDeDds);

    List<Evento> eventos = juli.eventosEntreFechas(
        LocalDate.of(2020, 4, 1),
        LocalDate.of(2020, 4, 13));

    assertEquals(eventos, Arrays.asList(tpRedes, tpDeGestion, tpDeDds));
  }

  @Test
  void sePuedenListarEventosDeMultiplesCalendarios() {
    Usuario juli = crearUsuario("juli@gugle.com.ar");

    Calendario calendarioFacultad = new Calendario();

    Calendario calendarioLaboral = new Calendario();

    Evento entregarPedido = crearEventoSimpleEnUbicacionRandom("Entregar Pedido",LocalDateTime.of(2021,1,3,15,20),Duration.of(2, HOURS));
    Evento entregarCertificadoExamen = crearEventoSimpleEnUbicacionRandom("Entregar certificado examen",LocalDateTime.of(2021,1,6,10,20),Duration.of(2, HOURS));
    Evento entregarTP = crearEventoSimpleEnCampus("Entregar tp fisica 1", LocalDateTime.of(2021,1,5,8,30), Duration.of(2, HOURS));

    calendarioFacultad.agendar(entregarTP);
    calendarioLaboral.agendar(entregarPedido);
    calendarioLaboral.agendar(entregarCertificadoExamen);

    juli.agregarCalendario(calendarioLaboral);
    juli.agregarCalendario(calendarioFacultad);


    List<Evento> eventos = juli.eventosEntreFechas(
        LocalDate.of(2021, 1, 1),
        LocalDate.of(2021, 1, 10));

    assertEquals(eventos, Arrays.asList(entregarPedido, entregarCertificadoExamen, entregarTP));
  }

  // 5. Permitir saber cuánto falta para un cierto calendarios.evento (por ejemplo, 15 horas)

  @Test
  void unEventoSabeCuantoFalta() {
    LocalDateTime inicio = LocalDateTime.now().plusDays(60);
    Evento parcialDds = crearEventoSimpleEnMedrano("Parcial DDS", inicio, Duration.of(2,  HOURS));

    assertTrue(parcialDds.cuantoFalta().compareTo(Duration.of(60, ChronoUnit.DAYS)) <= 0);
    assertTrue(parcialDds.cuantoFalta().compareTo(Duration.of(59, ChronoUnit.DAYS)) >= 0);
  }

  // 7. Permitir agendar eventos con repeticiones, con una frecuencia diaria, semanal, mensual o anual

  @Test
  void sePuedenAgendarYListarEventosRecurrrentes() {
    Usuario usuario = crearUsuario("rene@gugle.com.ar");

    Evento evento = new EventoRepetitivo(
        "eventox",
        utnMedrano,
        Collections.emptyList(),
        LocalTime.of(19, 0),
        Duration.of(45, MINUTES),
        LocalDate.of(2020, 9, 1),
        LocalDate.of(2020, 9, 28),
        new Semanal(DayOfWeek.TUESDAY));

    Calendario calendario = new Calendario();
    calendario.agendar(evento);
    usuario.agregarCalendario(calendario);

    System.out.println(evento.sucedeDia(LocalDate.of(2020, 9, 15)));
    System.out.println(evento.sucedeDia(LocalDate.of(2020, 9, 22)));


    List<Evento> eventos = usuario.eventosEntreFechas(
        LocalDate.of(2020, 9, 14),
        LocalDate.of(2020, 9, 28));

    assertEquals(eventos.size(), 2);
  }

  @Test
  void unEventoRecurrenteSabeCuantoFaltaParaSuProximaRepeticion() {
    // Este test fallara si se prueba a las 0000h, ya que ahi el evento sucedera en 23 horas y 59 minutos. No es un error.
    Evento unRecurrente = new EventoRepetitivo(
        "eventoRecurrente",
        utnMedrano,
        Collections.emptyList(),
        LocalTime.now().minus(1, MINUTES),
        Duration.of(1, HOURS),
        LocalDate.now(),
        LocalDate.now().plus(1, YEARS),
        new CadaN(LocalDate.now(), Period.of(0, 0,15)));

    assertTrue(unRecurrente.cuantoFalta().compareTo(Duration.of(15, ChronoUnit.DAYS)) <= 0);
    assertTrue(unRecurrente.cuantoFalta().compareTo(Duration.of(14, ChronoUnit.DAYS)) >= 0);
  }


  // 6. Permitir saber si dos eventos están solapado, y en tal caso, con qué otros eventos del calendario

  @Test
  void sePuedeSaberSiUnEventoEstaSolapadoCuandoEstaParcialmenteIncluido() {
    // TODO: esto es opcional pero probablemente ayuda a implementar el requerimiento principal
    Evento recuperatorioSistemasDeGestion = crearEventoSimpleEnMedrano("Recuperatorio Sistemas de Gestion", LocalDateTime.of(2021, 6, 19, 9, 0), Duration.of(2, HOURS));
    Evento tpOperativos = crearEventoSimpleEnMedrano("Entrega de Operativos", LocalDateTime.of(2021, 6, 19, 10, 0), Duration.of(2, HOURS));

    assertTrue(recuperatorioSistemasDeGestion.estaSolapadoCon(tpOperativos));
    assertTrue(tpOperativos.estaSolapadoCon(recuperatorioSistemasDeGestion));
  }

  @Test
  void sePuedeSaberSiUnEventoEstaSolapadoCuandoEstaTotalmenteIncluido() {
    // TODO: esto es opcional pero probablemente ayuda a implementar el requerimiento principal
    Evento recuperatorioSistemasDeGestion = crearEventoSimpleEnMedrano("Recuperatorio Sistemas de Gestion", LocalDateTime.of(2021, 6, 19, 9, 0), Duration.of(4, HOURS));
    Evento tpOperativos = crearEventoSimpleEnMedrano("Entrega de Operativos", LocalDateTime.of(2021, 6, 19, 10, 0), Duration.of(2, HOURS));

    assertTrue(recuperatorioSistemasDeGestion.estaSolapadoCon(tpOperativos));
    assertTrue(tpOperativos.estaSolapadoCon(recuperatorioSistemasDeGestion));
  }

  @Test
  void sePuedeSaberSiUnEventoEstaSolapadoCuandoNoEstaSolapado() {
    // TODO: esto es opcional pero probablemente ayuda a implementar el requerimiento principal
    Evento recuperatorioSistemasDeGestion = crearEventoSimpleEnMedrano("Recuperatorio Sistemas de Gestion", LocalDateTime.of(2021, 6, 19, 9, 0), Duration.of(3, HOURS));
    Evento tpOperativos = crearEventoSimpleEnMedrano("Entrega de Operativos", LocalDateTime.of(2021, 6, 19, 18, 0), Duration.of(2, HOURS));

    assertFalse(recuperatorioSistemasDeGestion.estaSolapadoCon(tpOperativos));
    assertFalse(tpOperativos.estaSolapadoCon(recuperatorioSistemasDeGestion));
  }

  @Test
  void sePuedeSaberConQueEventosEstaSolapado() {
    Evento recuperatorioSistemasDeGestion = crearEventoSimpleEnMedrano("Recuperatorio Sistemas de Gestion", LocalDateTime.of(2021, 6, 19, 9, 0), Duration.of(2, HOURS));
    Evento tpOperativos = crearEventoSimpleEnMedrano("Entrega de Operativos", LocalDateTime.of(2021, 6, 19, 10, 0), Duration.of(2, HOURS));
    Evento tramiteEnElBanco = crearEventoSimpleEnMedrano("Tramite en el banco", LocalDateTime.of(2021, 6, 19, 9, 0), Duration.of(4, HOURS));

    Calendario calendario = crearCalendarioVacio();

    calendario.agendar(recuperatorioSistemasDeGestion);
    calendario.agendar(tpOperativos);

    assertEquals(Arrays.asList(recuperatorioSistemasDeGestion, tpOperativos), calendario.eventosSolapadosCon(tramiteEnElBanco));
  }



  // 9. Permitir asignarle a un evento varios recordatorios, que se enviarán cuando falte un cierto tiempo

  @Test
  void agregoRecordatorioAlEventoDiarioYVerificoSi15MinAntesDeUnaRepeticionDebeRecordar() {
    Usuario usuario = crearUsuario("rene@gugle.com.ar");

    Evento evento = new EventoRepetitivo(
        "eventox",
        utnMedrano,
        Collections.emptyList(),
        LocalTime.of(19, 0),
        Duration.of(45, MINUTES),
        LocalDate.of(2020, 9, 1),
        LocalDate.of(2020, 9, 28),
        new Diaria());

    Calendario calendario = new Calendario();
    calendario.agendar(evento);
    usuario.agregarCalendario(calendario);
    usuario.recordarConMail(evento,Duration.of(15, MINUTES),shemailLib);

    assertTrue(usuario.getRecordatorios().get(0).esMomentoDeRecordar(LocalDateTime.of(2020,9,18,18,45)));
  }

  void agregoRecordatorioAlEvento() {
    Usuario usuario = crearUsuario("rene@gugle.com.ar");

    Evento evento = new EventoRepetitivo(
        "eventox",
        utnMedrano,
        Collections.emptyList(),
        LocalTime.of(19, 0),
        Duration.of(45, MINUTES),
        LocalDate.of(2020, 9, 1),
        LocalDate.of(2020, 9, 28),
        new Semanal(DayOfWeek.TUESDAY));

    Calendario calendario = new Calendario();
    calendario.agendar(evento);
    usuario.agregarCalendario(calendario);
    usuario.recordarConMail(evento,Duration.of(15, MINUTES),shemailLib);

    assertTrue(RepositorioRecordatorios.getInstance().getRepositorio().get(0).getEvento().equals(evento));
  }


  // 8. Permitir saber si le usuarie llega al evento más próximo a tiempo, tomando en cuenta la ubicación actual de le usuarie y destino.


  @Test
  void llegaATiempoAlProximoEventoCuandoNoHayEventos() {
    Usuario feli = crearUsuario("feli@gugle.com.ar");
    assertTrue(feli.llegaATiempoAlProximoEvento());
  }

  @Test
  void llegaATiempoAlProximoEventoCuandoHayUnEventoCercano() {
    Usuario feli = crearUsuario("feli@gugle.com.ar");
    Calendario calendario = crearCalendarioVacio();
    feli.agregarCalendario(calendario);

    //"mockear al Position Service para que diga que ya está en medrano y a GugleMaps para que diga que tarda 0 minutos en llegar");
    when(positionService.ubicacionActual(feli.getEmail())).thenReturn(utnMedrano);
    when(gugleMapas.tiempoEstimadoHasta(utnMedrano,utnMedrano)).thenReturn(Duration.of(0, MINUTES));

    calendario.agendar(crearEventoSimpleEnMedrano("Parcial", LocalDateTime.now().plusMinutes(30), Duration.of(2, HOURS)));

    assertTrue(feli.llegaATiempoAlProximoEvento());
  }

  @Test
  void noLlegaATiempoAlProximoEventoCuandoHayUnEventoFísicamenteLejano() {
    Usuario feli = crearUsuario("feli@gugle.com.ar");
    Calendario calendario = crearCalendarioVacio();
    feli.agregarCalendario(calendario);

    when(positionService.ubicacionActual(feli.getEmail())).thenReturn(utnCampus);
    when(gugleMapas.tiempoEstimadoHasta(utnCampus,utnMedrano)).thenReturn(Duration.of(90, MINUTES));

    calendario.agendar(crearEventoSimpleEnMedrano("Parcial", LocalDateTime.now().plusMinutes(30), Duration.of(2, HOURS)));

    assertFalse(feli.llegaATiempoAlProximoEvento());
  }


  @Test
  void llegaATiempoAlProximoEventoCuandoHayUnEventoCercanoAunqueAlSiguienteNoLlegue() {
    Usuario feli = crearUsuario("feli@gugle.com.ar");
    Calendario calendario = crearCalendarioVacio();
    feli.agregarCalendario(calendario);

    //fail("mockear al Position Service para que diga que está en Medrano y a GugleMaps para que diga que tarda 0 minutos en llegar a Medrano y 1:30 horas en llegar a Campus");
    when(positionService.ubicacionActual(feli.getEmail())).thenReturn(utnMedrano);
    when(gugleMapas.tiempoEstimadoHasta(utnCampus,utnMedrano)).thenReturn(Duration.of(90, MINUTES));
    when(gugleMapas.tiempoEstimadoHasta(utnMedrano,utnMedrano)).thenReturn(Duration.of(0, MINUTES));

    calendario.agendar(crearEventoSimpleEnMedrano("Parcial", LocalDateTime.now().plusMinutes(30), Duration.of(3, HOURS)));
    calendario.agendar(crearEventoSimpleEnCampus("Final", LocalDateTime.now().plusMinutes(45), Duration.of(1, HOURS)));

    assertTrue(feli.llegaATiempoAlProximoEvento());
  }

  @Test
  void eventoTodosLos28AlMedioDiaSucedeUn28AlMediodia(){
    Evento unRecurrente = new EventoRepetitivo(
        "eventoRecurrente",
        utnMedrano,
        Collections.emptyList(),
        LocalTime.of(12,0),
        Duration.of(1, HOURS),
        LocalDate.now(),
        LocalDate.now().plus(3, YEARS),
        new Mensual(28));
    assertTrue(unRecurrente.sucedeMomento(LocalDateTime.of(2022,3,28,12,0)));
  }

  @Test
  void seSolapanDosEventosConRepeticionDeCada4y6Dias() {
    Evento eventoCada4Dias = new EventoRepetitivo(
        "evento cada 4 dias",
        utnMedrano,
        Collections.emptyList(),
        LocalTime.of(12, 0),
        Duration.of(1, HOURS),
        LocalDate.of(2022, 1, 4),
        LocalDate.of(2022, 1, 4).plus(3, YEARS),
        new CadaN(LocalDate.of(2022, 1, 4), Period.of(0, 0, 4))
    );
  }

  @Test
  void recordatorioNotificadorNotifica(){
    Evento eventoSimple = crearEventoSimple("evento",
        LocalDateTime.of(2021,12,25,18,20),
        LocalDateTime.of(2021,12,25,19,20),
        utnMedrano,
        new ArrayList<>());

    Calendario calendario = new Calendario();
    calendario.agendar(eventoSimple);

    ian.agregarCalendario(calendario);
    ian.recordarConNotificacion(eventoSimple, Duration.of(1, HOURS));

    assertTrue(RepositorioRecordatorios.getInstance().getRepositorio().stream().anyMatch(recordatorio -> recordatorio.getEvento().equals(eventoSimple)));
  }

  @Test
  void eventoConRepeticionAnualSucedeElDia(){
    Evento navidad = new EventoRepetitivo(
        "Navidad",
        utnMedrano,
        new ArrayList<>(),
        LocalTime.of(0,0),
        Duration.of(23, HOURS),
        LocalDate.of(2021,1,1),
        LocalDate.of(2100,12,31),
        new Anual(25,DECEMBER)
    );

    Evento eventoSimpleEnNavidad = crearEventoSimple("evento",
        LocalDateTime.of(2021,12,25,18,20),
        LocalDateTime.of(2021,12,25,19,20),
        utnMedrano,
        new ArrayList<>());

    assertTrue(navidad.estaSolapadoCon(eventoSimpleEnNavidad));
  }

  @Test
  void eventoEnUnaHoraSucedeEntreAhoraYDosHoras(){
    Evento eventoEnUnaHora = new EventoUnico("evento",
        utnMedrano,
        new ArrayList<>(),
        LocalTime.now().plus(1, HOURS),
        Duration.of(15, MINUTES),
        LocalDate.now());

    assertTrue(eventoEnUnaHora.sucedeEntreMomentos(LocalDateTime.now(), LocalDateTime.now().plus(2, HOURS)));
  }

  @Test
  void latitudUTNMedranoEsCorrecta(){
    assertEquals(-34.5984145,utnMedrano.getLatitud());
  }

  @Test
  void seEnviaUnMailALaPersonaCorrectaCuandoDebeSerEnviado(){
    Usuario usuario = crearUsuario("rene@gugle.com.ar");

    Evento evento = new EventoRepetitivo(
        "eventox",
        utnMedrano,
        Collections.emptyList(),
        LocalTime.of(19, 0),
        Duration.of(45, MINUTES),
        LocalDate.of(2021, 1, 1),
        LocalDate.of(2021, 12, 31),
        new Semanal(DayOfWeek.WEDNESDAY));

    Calendario calendario = new Calendario();
    calendario.agendar(evento);
    usuario.agregarCalendario(calendario);
    usuario.recordarConMail(evento,Duration.of(15, MINUTES),shemailLib);
    RepositorioRecordatorios.getInstance().getRepositorio().forEach(recordatorio -> recordatorio.run(LocalDateTime.of(2021,7,7,18,45)));

    verify(shemailLib).enviarMailA("rene@gugle.com.ar", "Recordatorio de eventox", "Hola! Te recordamos que el evento: eventox sucede en " + Duration.of(15, MINUTES));
  }

  Usuario crearUsuario(String email) {
    return new Usuario(email,positionService,gugleMapas);
  }

  Calendario crearCalendarioVacio() {
    return new Calendario();
  }

  Evento crearEventoSimpleEnMedrano(String nombre, LocalDateTime inicio, Duration duracion) {
    return crearEventoSimple(nombre, inicio, inicio.plus(duracion), utnMedrano, new ArrayList<>());
  }

  Evento crearEventoSimpleEnUbicacionRandom(String nombre, LocalDateTime inicio, Duration duration) {
    return crearEventoSimple(nombre,inicio,inicio.plus(duration),new Ubicacion(100,50),new ArrayList<>());
  }

  Evento crearEventoSimpleEnCampus(String nombre, LocalDateTime inicio, Duration duracion) {
    return crearEventoSimple("Seguimiento de TPA", inicio, inicio.plus(duracion), utnCampus, new ArrayList<>());
  }

  Evento crearEventoSimple(String nombre, LocalDateTime inicio, LocalDateTime fin, Ubicacion ubicacion, List<Usuario> invitados) {
    return new EventoUnico(nombre,ubicacion,invitados,inicio.toLocalTime(),Duration.between(inicio,fin),inicio.toLocalDate());
  }

}