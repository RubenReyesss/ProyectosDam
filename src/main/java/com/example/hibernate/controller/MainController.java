package com.example.hibernate.controller;

import com.example.hibernate.dao.DispositivoDAO;
import com.example.hibernate.dao.IncidenciaDAO;
import com.example.hibernate.model.Dispositivo;
import com.example.hibernate.model.Incidencia;
import com.example.hibernate.model.TipoIncidencia;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {

    @FXML
    private TableColumn<Incidencia, String> Descripción;

    @FXML
    private TableColumn<Incidencia, LocalDate> Fecha;

    @FXML
    private TableColumn<Incidencia, Integer> IdDispositivo;

    @FXML
    private TableColumn<Incidencia, TipoIncidencia> Tipo;

    @FXML
    private Button borrarFiltros;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private PieChart graficoTarta;

    @FXML
    private TableColumn<Dispositivo, Integer> idColumn;

    @FXML
    private TableColumn<Dispositivo, String> nombreColumn;

    @FXML
    private CheckBox leve;

    @FXML
    private CheckBox media;

    @FXML
    private CheckBox urgente;

    @FXML
    private TableView<Dispositivo> dispositivosTable;

    @FXML
    private TableView<Incidencia> incidenciasTable;

    private final DispositivoDAO dispositivoDAO = new DispositivoDAO();
    private final IncidenciaDAO incidenciaDAO = new IncidenciaDAO();

    @FXML
    public void initialize() {
        // Configuración de columnas para dispositivos
        idColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nombreColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));

        // Configuración de columnas para incidencias
        IdDispositivo.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleIntegerProperty(
                        cellData.getValue().getDispositivo().getId()).asObject());
        Descripción.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescripcion()));
        Fecha.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getFecha().toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate()));
        Tipo.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getTipo()));

        actualizarTablas(); // Cargar los datos al inicializar

        // Añadir listeners para los filtros
        comboBox.setOnAction(event -> filtrarIncidencias());
        datePicker.setOnAction(event -> filtrarIncidencias());
        leve.setOnAction(event -> filtrarIncidencias());
        media.setOnAction(event -> filtrarIncidencias());
        urgente.setOnAction(event -> filtrarIncidencias());
        borrarFiltros.setOnAction(event -> limpiarFiltros());

        // Cargar opciones en el ComboBox
        cargarOpcionesComboBox();
    }

    private void actualizarTablas() {
        // Cargar dispositivos en la tabla
        List<Dispositivo> dispositivos = dispositivoDAO.obtenerTodos();
        ObservableList<Dispositivo> dispositivosList = FXCollections.observableArrayList(dispositivos);
        dispositivosTable.setItems(dispositivosList);

        // Cargar incidencias en la tabla
        List<Incidencia> incidencias = incidenciaDAO.obtenerTodos();
        ObservableList<Incidencia> incidenciasList = FXCollections.observableArrayList(incidencias);
        incidenciasTable.setItems(incidenciasList);

        // Actualizar gráfico de tarta
        actualizarGraficoTarta(incidencias);
    }

    private void filtrarIncidencias() {
        List<Incidencia> incidencias = incidenciaDAO.obtenerTodos();

        // Filtrar por nombre de dispositivo
        String nombreDispositivo = comboBox.getValue();
        if (nombreDispositivo != null && !nombreDispositivo.isEmpty()) {
            incidencias = incidencias.stream()
                    .filter(incidencia -> incidencia.getDispositivo().getNombre().equals(nombreDispositivo))
                    .collect(Collectors.toList());
        }

        // Filtrar por fecha
        LocalDate fechaSeleccionada = datePicker.getValue();
        if (fechaSeleccionada != null) {
            incidencias = incidencias.stream()
                    .filter(incidencia -> incidencia.getFecha().toInstant().atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate().equals(fechaSeleccionada))
                    .collect(Collectors.toList());
        }

        // Filtrar por tipo de incidencia
        if (leve.isSelected() || media.isSelected() || urgente.isSelected()) {
            incidencias = incidencias.stream()
                    .filter(incidencia -> (leve.isSelected() && incidencia.getTipo() == TipoIncidencia.Leve) ||
                            (media.isSelected() && incidencia.getTipo() == TipoIncidencia.Media) ||
                            (urgente.isSelected() && incidencia.getTipo() == TipoIncidencia.Urgente))
                    .collect(Collectors.toList());
        }

        ObservableList<Incidencia> incidenciasList = FXCollections.observableArrayList(incidencias);
        incidenciasTable.setItems(incidenciasList);

        // Actualizar gráfico de tarta
        actualizarGraficoTarta(incidencias);
    }

    private void limpiarFiltros() {
        comboBox.setValue(null);
        datePicker.setValue(null);
        leve.setSelected(false);
        media.setSelected(false);
        urgente.setSelected(false);
        actualizarTablas();
    }

    private void cargarOpcionesComboBox() {
        List<Dispositivo> dispositivos = dispositivoDAO.obtenerTodos();
        List<String> nombresDispositivos = dispositivos.stream()
                .map(Dispositivo::getNombre)
                .collect(Collectors.toList());
        ObservableList<String> opciones = FXCollections.observableArrayList(nombresDispositivos);
        comboBox.setItems(opciones);
    }

    private void actualizarGraficoTarta(List<Incidencia> incidencias) {
        long countLeve = incidencias.stream().filter(incidencia -> incidencia.getTipo() == TipoIncidencia.Leve).count();
        long countMedia = incidencias.stream().filter(incidencia -> incidencia.getTipo() == TipoIncidencia.Media)
                .count();
        long countUrgente = incidencias.stream().filter(incidencia -> incidencia.getTipo() == TipoIncidencia.Urgente)
                .count();

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Leve", countLeve),
                new PieChart.Data("Media", countMedia),
                new PieChart.Data("Urgente", countUrgente));

        graficoTarta.setData(pieChartData);
    }

    // Método para mostrar una alerta de error o advertencia
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}