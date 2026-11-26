package org.example.ipn.upiiz.controlador;

import org.example.ipn.upiiz.vista.VentanaPuntoVenta;
import org.example.ipn.upiiz.modelo.Producto;
import org.example.ipn.upiiz.modelo.VentaModelo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ControladorPuntoVenta implements ActionListener {
    // Atributos de agregación
    private VentaModelo modelo;
    private VentanaPuntoVenta vista;
    private Timer relojTimer;

    // Constructor principal
    public ControladorPuntoVenta(VentaModelo modelo, VentanaPuntoVenta vista) {
        this.modelo = modelo;
        this.vista = vista;
        registrarEventos();
        iniciarReloj();
        filtrarProductos();
    }

    // Constructor sobrecargado recibiendo solo la vista
    public ControladorPuntoVenta(VentanaPuntoVenta vista) {
        this(new VentaModelo(), vista);
    }

    private void registrarEventos() {
        // Botones principales
        vista.getBtnBuscar().addActionListener(this);
        vista.getTxtBuscar().addActionListener(this);
        vista.getBtnAgregar().addActionListener(this);
        vista.getBtnEliminar().addActionListener(this);
        vista.getBtnLimpiar().addActionListener(this);
        vista.getBtnImprimirTicket().addActionListener(this);
        vista.getBtnCancelarVenta().addActionListener(this);
        vista.getBtnCobrar().addActionListener(this);

        // Pestañas de categorías
        vista.getTabbedCategorias().addChangeListener(e -> filtrarProductos());

        // Elementos de la barra de menú
        vista.getItemNuevaVenta().addActionListener(this);
        vista.getItemSalir().addActionListener(this);
        vista.getItemCobrarMenu().addActionListener(this);
        vista.getItemCancelarMenu().addActionListener(this);
        vista.getItemNuevoProducto().addActionListener(this);
        vista.getItemReporteVentas().addActionListener(this);
        vista.getItemCambiarUsuario().addActionListener(this);
        vista.getItemAcercaDe().addActionListener(this);

        // Atajo global de teclado F5 para Cobrar
        JRootPane rootPane = vista.getRootPane();
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "accionCobrarF5");
        rootPane.getActionMap().put("accionCobrarF5", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cobrarVenta();
            }
        });
    }

    private void iniciarReloj() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        vista.actualizarFechaHora(LocalDateTime.now().format(formato));

        relojTimer = new Timer(1000, e ->
                vista.actualizarFechaHora(LocalDateTime.now().format(formato))
        );
        relojTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == vista.getBtnBuscar() || src == vista.getTxtBuscar()) {
            filtrarProductos();
        } else if (src == vista.getBtnAgregar()) {
            agregarProductoSeleccionado();
        } else if (src == vista.getBtnEliminar()) {
            eliminarProductoSeleccionado();
        } else if (src == vista.getBtnLimpiar() || src == vista.getBtnCancelarVenta()
                || src == vista.getItemNuevaVenta() || src == vista.getItemCancelarMenu()) {
            limpiarVentaActual();
        } else if (src == vista.getBtnCobrar() || src == vista.getItemCobrarMenu()) {
            cobrarVenta();
        } else if (src == vista.getBtnImprimirTicket()) {
            imprimirTicket();
        } else if (src == vista.getItemNuevoProducto()) {
            agregarNuevoProductoDialogo();
        } else if (src == vista.getItemReporteVentas()) {
            mostrarReporteVentas();
        } else if (src == vista.getItemCambiarUsuario()) {
            cambiarUsuarioDialogo();
        } else if (src == vista.getItemAcercaDe()) {
            vista.mostrarMensaje(
                    "Acerca de",
                    "Punto de Venta - Mi Tienda\nArquitectura MVC en Java Swing - IPN UPIIZ",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else if (src == vista.getItemSalir()) {
            System.exit(0);
        }
    }

    public void filtrarProductos() {
        String texto = vista.getTxtBuscar().getText();
        int idxTab = vista.getTabbedCategorias().getSelectedIndex();
        String categoria = (idxTab >= 0) ? vista.getTabbedCategorias().getTitleAt(idxTab) : "Todos";

        List<Producto> filtrados = modelo.buscarProductos(texto, categoria);
        vista.mostrarProductos(filtrados, evt -> {
            // Al dar doble clic en una tarjeta agrega 1 unidad inmediatamente
            Producto prod = vista.getProductoSeleccionado();
            if (prod != null) {
                modelo.agregarProducto(prod, 1);
                refrescarVistaVenta();
            }
        });
    }

    public void agregarProductoSeleccionado() {
        Producto seleccionado = vista.getProductoSeleccionado();
        if (seleccionado == null) {
            vista.mostrarMensaje("Aviso", "Selecciona primero un producto del catálogo.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int cantidad = vista.getCantidadSeleccionada();
        modelo.agregarProducto(seleccionado, cantidad);
        vista.reiniciarCantidad();
        refrescarVistaVenta();
    }

    public void eliminarProductoSeleccionado() {
        int fila = vista.getTablaVenta().getSelectedRow();
        if (fila == -1) {
            vista.mostrarMensaje("Aviso", "Selecciona un artículo en la tabla derecha para eliminarlo.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modelo.eliminarItem(fila);
        refrescarVistaVenta();
    }

    public void limpiarVentaActual() {
        if (modelo.getItemsVenta().isEmpty()) {
            return;
        }
        int resp = JOptionPane.showConfirmDialog(
                vista,
                "¿Deseas limpiar todos los artículos de la venta actual?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );
        if (resp == JOptionPane.YES_OPTION) {
            modelo.limpiarVenta();
            vista.reiniciarCantidad();
            refrescarVistaVenta();
        }
    }

    public void cobrarVenta() {
        if (modelo.getItemsVenta().isEmpty()) {
            vista.mostrarMensaje("Carrito vacío", "Agrega al menos un producto antes de cobrar.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = modelo.calcularTotal();
        String entrada = JOptionPane.showInputDialog(
                vista,
                String.format("Total a pagar: $%.2f\nIngresa el efectivo recibido:", total),
                "Cobrar Venta (F5)",
                JOptionPane.QUESTION_MESSAGE
        );

        if (entrada == null) {
            return; // Usuario canceló el diálogo
        }

        try {
            double efectivo = Double.parseDouble(entrada.trim());
            if (efectivo < total) {
                vista.mostrarMensaje("Monto insuficiente",
                        String.format("El efectivo ($%.2f) es menor al total ($%.2f).", efectivo, total),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            double cambio = Math.round((efectivo - total) * 100.0) / 100.0;
            String ticketFinal = modelo.registrarVenta(efectivo);
            refrescarVistaVenta();
            mostrarVentanaTicket(ticketFinal, cambio);

        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("Error de formato", "Ingresa una cantidad numérica válida.", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void imprimirTicket() {
        if (modelo.getItemsVenta().isEmpty()) {
            vista.mostrarMensaje("Sin artículos", "No hay artículos en la venta actual para generar ticket.", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String vistaPrevia = modelo.generarTicketTexto(0.0, 0.0);
        mostrarVentanaTicket(vistaPrevia, -1);
    }

    private void mostrarVentanaTicket(String contenidoTicket, double cambio) {
        JTextArea area = new JTextArea(contenidoTicket);
        area.setFont(new Font("Consolas", Font.PLAIN, 13));
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(370, 340));

        String titulo = (cambio >= 0)
                ? String.format("Venta Completada - Cambio: $%.2f", cambio)
                : "Vista Previa del Ticket";

        JOptionPane.showMessageDialog(vista, scroll, titulo, JOptionPane.PLAIN_MESSAGE);
    }

    public void agregarNuevoProductoDialogo() {
        JTextField txtCod = new JTextField("011");
        JTextField txtNom = new JTextField();
        JComboBox<String> cmbCat = new JComboBox<>(new String[]{"Bebidas", "Botanas", "Abarrotes", "Limpieza"});
        JTextField txtPre = new JTextField();
        JTextField txtRutaImg = new JTextField();
        txtRutaImg.setEditable(false);

        JButton btnElegirFoto = new JButton("Seleccionar foto...");
        btnElegirFoto.addActionListener(e -> {
            FileDialog fd = new FileDialog(vista, "Elegir imagen del producto", FileDialog.LOAD);
            fd.setFile("*.png;*.jpg;*.jpeg");
            fd.setVisible(true);
            if (fd.getFile() != null) {
                txtRutaImg.setText(fd.getDirectory() + fd.getFile());
            }
        });

        Object[] campos = {
                "Código:", txtCod,
                "Nombre del Producto:", txtNom,
                "Categoría:", cmbCat,
                "Precio ($):", txtPre,
                "Fotografía:", btnElegirFoto,
                "Ruta seleccionada:", txtRutaImg
        };

        int opt = JOptionPane.showConfirmDialog(vista, campos, "Registrar Nuevo Producto", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                String cod = txtCod.getText().trim();
                String nom = txtNom.getText().trim();
                String cat = (String) cmbCat.getSelectedItem();
                double pre = Double.parseDouble(txtPre.getText().trim());
                String ruta = txtRutaImg.getText().trim();

                if (cod.isEmpty() || nom.isEmpty() || pre <= 0) {
                    throw new IllegalArgumentException();
                }

                modelo.registrarNuevoProducto(new Producto(cod, nom, cat, pre, 50, ruta));
                filtrarProductos();
                vista.mostrarMensaje("Producto agregado correctamente al catálogo.");
            } catch (Exception ex) {
                vista.mostrarMensaje("Datos inválidos", "Verifica que el nombre no esté vacío y el precio sea válido.", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void mostrarReporteVentas() {
        List<String> historial = modelo.getHistorialVentas();
        if (historial.isEmpty()) {
            vista.mostrarMensaje("Reporte de Ventas", "Aún no se han cobrado ventas en esta sesión.", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder("=== VENTAS COBRADAS EN LA SESIÓN ===\n\n");
        for (String v : historial) {
            sb.append("• ").append(v).append("\n");
        }
        vista.mostrarMensaje("Corte de Caja", sb.toString(), JOptionPane.INFORMATION_MESSAGE);
    }

    public void cambiarUsuarioDialogo() {
        String nuevoUsuario = JOptionPane.showInputDialog(vista, "Nombre del cajero:", modelo.getUsuarioActual());
        if (nuevoUsuario != null && !nuevoUsuario.trim().isEmpty()) {
            modelo.setUsuarioActual(nuevoUsuario.trim());
            vista.actualizarUsuarioEtiqueta(modelo.getUsuarioActual());
        }
    }

    private void refrescarVistaVenta() {
        vista.actualizarTablaVenta(
                modelo.getItemsVenta(),
                modelo.calcularSubtotal(),
                modelo.calcularIVA(),
                modelo.calcularTotal()
        );
    }

    // Getters y Setters
    public VentaModelo getModelo() { return modelo; }
    public void setModelo(VentaModelo modelo) { this.modelo = modelo; }
    public VentanaPuntoVenta getVista() { return vista; }
    public void setVista(VentanaPuntoVenta vista) { this.vista = vista; }
}