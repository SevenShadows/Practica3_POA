package org.example;

import org.example.ipn.upiiz.vista.VentanaPuntoVenta;
import org.example.ipn.upiiz.controlador.ControladorPuntoVenta;
import org.example.ipn.upiiz.modelo.VentaModelo;

import javax.swing.UIManager;
import java.awt.EventQueue;

public class Main {
    // Atributos de composición
    private VentaModelo modelo;
    private VentanaPuntoVenta vista;
    private ControladorPuntoVenta controlador;

    // Constructor
    public Main() {
        this.modelo = new VentaModelo();
        this.vista = new VentanaPuntoVenta("Punto de Venta - Mi Tienda");
        this.controlador = new ControladorPuntoVenta(this.modelo, this.vista);
    }

    // Getters y Setters
    public VentaModelo getModelo() {
        return modelo;
    }

    public void setModelo(VentaModelo modelo) {
        this.modelo = modelo;
    }

    public VentanaPuntoVenta getVista() {
        return vista;
    }

    public void setVista(VentanaPuntoVenta vista) {
        this.vista = vista;
    }

    public ControladorPuntoVenta getControlador() {
        return controlador;
    }

    public void setControlador(ControladorPuntoVenta controlador) {
        this.controlador = controlador;
    }

    // Método de comportamiento
    public void iniciar() {
        if (this.vista != null) {
            this.vista.setVisible(true);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("No se pudo aplicar el Look and Feel del sistema: " + e);
        }

        EventQueue.invokeLater(() -> {
            Main app = new Main();
            app.iniciar();
        });
    }
}