package org.example.ipn.upiiz.vista;

import org.example.ipn.upiiz.modelo.ItemVenta;
import org.example.ipn.upiiz.modelo.Producto;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class VentanaPuntoVenta extends JFrame {
    private static final Color AZUL_MEDIO = new Color(36, 82, 138);
    private static final Color VERDE_COBRAR = new Color(46, 158, 68);

    // Componentes enlazados con VentanaPuntoVenta.form
    private JPanel panelPrincipal;
    private JPanel panelBanner;
    private JLabel lblIconoCarrito;
    private JLabel lblTitulo;
    private JLabel lblSubtitulo;
    private JLabel lblUsuario;
    private JLabel lblFechaHora;

    private JPanel panelIzquierdo;
    private JLabel lblBuscar;
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JTabbedPane tabbedCategorias;
    private JScrollPane scrollProductos;
    private JPanel panelGridProductos;
    private JLabel lblCantidad;
    private JSpinner spnCantidad;
    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnImprimirTicket;
    private JButton btnCancelarVenta;

    private JPanel panelDerecho;
    private JPanel panelHeaderTabla;
    private JLabel lblHeaderTabla;
    private JScrollPane scrollTabla;
    private JTable tablaVenta;
    private JPanel panelTotales;
    private JLabel lblSubTexto;
    private JLabel lblSubtotalValor;
    private JLabel lblIvaTexto;
    private JLabel lblIvaValor;
    private JLabel lblTotalTexto;
    private JLabel lblTotalValor;
    private JButton btnCobrar;

    // Elementos complementarios no soportados directamente por la paleta .form
    private DefaultTableModel modeloTabla;
    private JMenuBar menuBar;
    private JMenuItem itemNuevaVenta;
    private JMenuItem itemSalir;
    private JMenuItem itemCobrarMenu;
    private JMenuItem itemCancelarMenu;
    private JMenuItem itemNuevoProducto;
    private JMenuItem itemReporteVentas;
    private JMenuItem itemCambiarUsuario;
    private JMenuItem itemAcercaDe;

    private Producto productoSeleccionado;
    private JButton botonTarjetaSeleccionada;

    // Constructor por defecto
    public VentanaPuntoVenta() {
        this("Punto de Venta - Mi Tienda");
    }

    // Constructor sobrecargado
    public VentanaPuntoVenta(String titulo) {
        super(titulo);
        setSize(980, 640);
        setMinimumSize(new Dimension(920, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        if (panelPrincipal != null) {
            setContentPane(panelPrincipal);
        }

        configurarComponentesVisuales();
        configurarMenu();
    }

    private void configurarComponentesVisuales() {
        // Iconos vectoriales en Banner y Botones
        lblIconoCarrito.setIcon(IconoProductoFactory.crearIconoCarrito(36, 36, Color.WHITE));
        lblUsuario.setIcon(IconoProductoFactory.crearIconoUsuario(18, 18));
        btnBuscar.setIcon(IconoProductoFactory.crearIconoLupa(16, 16));
        btnAgregar.setIcon(IconoProductoFactory.crearIconoSimbolo("+", new Color(40, 155, 65)));
        btnEliminar.setIcon(IconoProductoFactory.crearIconoSimbolo("-", new Color(205, 50, 50)));
        btnLimpiar.setIcon(IconoProductoFactory.crearIconoEscoba());
        btnImprimirTicket.setIcon(IconoProductoFactory.crearIconoImpresora());
        btnCancelarVenta.setIcon(IconoProductoFactory.crearIconoCancelar());
        lblHeaderTabla.setIcon(IconoProductoFactory.crearIconoCarrito(18, 18, Color.WHITE));

        // Pestañas de categorías (altura compacta sin cuadro gris vacío)
        String[] categorias = {"Todos", "Bebidas", "Botanas", "Abarrotes", "Limpieza"};
        for (String cat : categorias) {
            tabbedCategorias.addTab(cat, new JLabel());
        }

        // Cuadrícula de productos 4 columnas
        panelGridProductos.setLayout(new GridLayout(0, 4, 8, 8));
        panelGridProductos.setBorder(new EmptyBorder(8, 8, 8, 8));
        scrollProductos.getVerticalScrollBar().setUnitIncrement(16);

        // Modelo numérico de Cantidad
        spnCantidad.setModel(new SpinnerNumberModel(1, 1, 99, 1));

        // Configuración de la tabla de artículos
        String[] columnas = {"Cant.", "Producto", "P. Unitario", "Importe"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaVenta.setModel(modeloTabla);
        tablaVenta.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaVenta.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaVenta.setShowGrid(true);
        tablaVenta.setGridColor(new Color(225, 230, 238));
        scrollTabla.getViewport().setBackground(Color.WHITE);

        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer derecha = new DefaultTableCellRenderer();
        derecha.setHorizontalAlignment(SwingConstants.RIGHT);

        tablaVenta.getColumnModel().getColumn(0).setPreferredWidth(45);
        tablaVenta.getColumnModel().getColumn(0).setCellRenderer(centrado);
        tablaVenta.getColumnModel().getColumn(1).setPreferredWidth(165);
        tablaVenta.getColumnModel().getColumn(2).setPreferredWidth(85);
        tablaVenta.getColumnModel().getColumn(2).setCellRenderer(derecha);
        tablaVenta.getColumnModel().getColumn(3).setPreferredWidth(85);
        tablaVenta.getColumnModel().getColumn(3).setCellRenderer(derecha);

        // Estilo del botón verde Cobrar (F5) compatible con LookAndFeel nativo
        btnCobrar.setIcon(IconoProductoFactory.crearIconoBillete());
        btnCobrar.setUI(new BasicButtonUI());
        btnCobrar.setBackground(VERDE_COBRAR);
        btnCobrar.setForeground(Color.WHITE);
        btnCobrar.setFocusPainted(false);
        btnCobrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCobrar.setBorder(new LineBorder(new Color(32, 120, 50), 2));
    }

    private void configurarMenu() {
        menuBar = new JMenuBar();

        JMenu menuArchivo = new JMenu("Archivo");
        itemNuevaVenta = new JMenuItem("Nueva Venta");
        itemSalir = new JMenuItem("Salir");
        menuArchivo.add(itemNuevaVenta);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);

        JMenu menuVentas = new JMenu("Ventas");
        itemCobrarMenu = new JMenuItem("Cobrar (F5)");
        itemCancelarMenu = new JMenuItem("Cancelar Venta");
        menuVentas.add(itemCobrarMenu);
        menuVentas.add(itemCancelarMenu);

        JMenu menuProductos = new JMenu("Productos");
        itemNuevoProducto = new JMenuItem("Agregar Nuevo Producto...");
        menuProductos.add(itemNuevoProducto);

        JMenu menuReportes = new JMenu("Reportes");
        itemReporteVentas = new JMenuItem("Corte de Caja / Ventas del Día");
        menuReportes.add(itemReporteVentas);

        JMenu menuUsuarios = new JMenu("Usuarios");
        itemCambiarUsuario = new JMenuItem("Cambiar Cajero...");
        menuUsuarios.add(itemCambiarUsuario);

        JMenu menuAyuda = new JMenu("Ayuda");
        itemAcercaDe = new JMenuItem("Acerca de Punto de Venta");
        menuAyuda.add(itemAcercaDe);

        menuBar.add(menuArchivo);
        menuBar.add(menuVentas);
        menuBar.add(menuProductos);
        menuBar.add(menuReportes);
        menuBar.add(menuUsuarios);
        menuBar.add(menuAyuda);

        setJMenuBar(menuBar);
    }

    public void mostrarProductos(List<Producto> productos, ActionListener dobleClicListener) {
        panelGridProductos.removeAll();
        botonTarjetaSeleccionada = null;

        for (Producto prod : productos) {
            JButton card = new JButton();
            card.setUI(new BasicButtonUI());
            card.setLayout(new BorderLayout(2, 2));
            card.setBackground(Color.WHITE);
            card.setBorder(new LineBorder(new Color(205, 214, 226), 1));
            card.setPreferredSize(new Dimension(112, 118));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel lblImg = new JLabel(IconoProductoFactory.obtenerIconoProducto(prod), SwingConstants.CENTER);
            lblImg.setBorder(new EmptyBorder(4, 0, 0, 0));

            String htmlInfo = "<html><center><b>" + prod.getNombre() + "</b><br>"
                    + "<span style='color:#1A3E6C;font-size:11px;'><b>$"
                    + String.format("%.2f", prod.getPrecio()) + "</b></span></center></html>";
            JLabel lblTexto = new JLabel(htmlInfo, SwingConstants.CENTER);
            lblTexto.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblTexto.setBorder(new EmptyBorder(0, 2, 4, 2));

            card.add(lblImg, BorderLayout.CENTER);
            card.add(lblTexto, BorderLayout.SOUTH);

            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    seleccionarTarjeta(card, prod);
                    if (e.getClickCount() == 2 && dobleClicListener != null) {
                        dobleClicListener.actionPerformed(null);
                    }
                }
            });

            panelGridProductos.add(card);
        }

        if (!productos.isEmpty() && panelGridProductos.getComponentCount() > 0) {
            seleccionarTarjeta((JButton) panelGridProductos.getComponent(0), productos.get(0));
        } else {
            productoSeleccionado = null;
        }

        panelGridProductos.revalidate();
        panelGridProductos.repaint();
    }

    private void seleccionarTarjeta(JButton card, Producto prod) {
        if (botonTarjetaSeleccionada != null) {
            botonTarjetaSeleccionada.setBorder(new LineBorder(new Color(205, 214, 226), 1));
            botonTarjetaSeleccionada.setBackground(Color.WHITE);
        }
        botonTarjetaSeleccionada = card;
        productoSeleccionado = prod;
        card.setBorder(new LineBorder(AZUL_MEDIO, 2));
        card.setBackground(new Color(238, 245, 255));
    }

    public void actualizarTablaVenta(List<ItemVenta> items, double subtotal, double iva, double total) {
        modeloTabla.setRowCount(0);
        for (ItemVenta item : items) {
            Object[] fila = {
                    item.getCantidad(),
                    item.getProducto().getNombre(),
                    String.format("$%.2f", item.getProducto().getPrecio()),
                    String.format("$%.2f", item.calcularImporte())
            };
            modeloTabla.addRow(fila);
        }

        lblSubtotalValor.setText(String.format("$%.2f", subtotal));
        lblIvaValor.setText(String.format("$%.2f", iva));
        lblTotalValor.setText(String.format("$%.2f", total));
    }

    public void actualizarFechaHora(String fechaHora) {
        lblFechaHora.setText(fechaHora);
    }

    public void actualizarUsuarioEtiqueta(String usuario) {
        lblUsuario.setText("Usuario: " + usuario + " ▼");
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Punto de Venta - Mi Tienda", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarMensaje(String titulo, String mensaje, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }

    // Getters de componentes para el Controlador
    public Producto getProductoSeleccionado() { return productoSeleccionado; }
    public int getCantidadSeleccionada() { return (Integer) spnCantidad.getValue(); }
    public void reiniciarCantidad() { spnCantidad.setValue(1); }
    public JTextField getTxtBuscar() { return txtBuscar; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JTabbedPane getTabbedCategorias() { return tabbedCategorias; }
    public JButton getBtnAgregar() { return btnAgregar; }
    public JButton getBtnEliminar() { return btnEliminar; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JButton getBtnImprimirTicket() { return btnImprimirTicket; }
    public JButton getBtnCancelarVenta() { return btnCancelarVenta; }
    public JButton getBtnCobrar() { return btnCobrar; }
    public JTable getTablaVenta() { return tablaVenta; }
    public JMenuItem getItemNuevaVenta() { return itemNuevaVenta; }
    public JMenuItem getItemSalir() { return itemSalir; }
    public JMenuItem getItemCobrarMenu() { return itemCobrarMenu; }
    public JMenuItem getItemCancelarMenu() { return itemCancelarMenu; }
    public JMenuItem getItemNuevoProducto() { return itemNuevoProducto; }
    public JMenuItem getItemReporteVentas() { return itemReporteVentas; }
    public JMenuItem getItemCambiarUsuario() { return itemCambiarUsuario; }
    public JMenuItem getItemAcercaDe() { return itemAcercaDe; }

    // Fábrica interna de iconos vectoriales y carga de fotos
    private static class IconoProductoFactory {
        public static Icon obtenerIconoProducto(Producto p) {
            if (p.getRutaImagen() != null && !p.getRutaImagen().isEmpty()) {
                File archivoImg = new File(p.getRutaImagen());
                if (archivoImg.exists()) {
                    try {
                        Image imgOriginal = ImageIO.read(archivoImg);
                        if (imgOriginal != null) {
                            Image imgEscalada = imgOriginal.getScaledInstance(58, 58, Image.SCALE_SMOOTH);
                            return new ImageIcon(imgEscalada);
                        }
                    } catch (Exception e) {
                        System.out.println("No se pudo cargar imagen: " + p.getRutaImagen());
                    }
                }
            }

            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.translate(x, y);

                    switch (p.getCodigo()) {
                        case "001": // Coca-Cola
                            g2.setColor(new Color(215, 25, 32));
                            g2.fillRoundRect(14, 8, 28, 46, 8, 8);
                            g2.setColor(Color.LIGHT_GRAY);
                            g2.fillRect(16, 6, 24, 3);
                            g2.setColor(Color.WHITE);
                            g2.drawArc(15, 24, 26, 12, 0, 180);
                            break;
                        case "002": // Agua 1 L
                            g2.setColor(new Color(30, 110, 210));
                            g2.fillRect(23, 4, 10, 5);
                            g2.setColor(new Color(175, 220, 250));
                            g2.fillRoundRect(17, 11, 22, 43, 10, 10);
                            g2.setColor(new Color(45, 135, 230));
                            g2.fillRect(17, 26, 22, 11);
                            break;
                        case "003": // Sabritas
                            g2.setColor(new Color(250, 205, 25));
                            g2.fillRoundRect(12, 7, 32, 47, 6, 6);
                            g2.setColor(new Color(220, 35, 35));
                            g2.fillOval(17, 18, 22, 16);
                            g2.setColor(Color.YELLOW);
                            g2.drawArc(21, 23, 14, 8, 190, 160);
                            break;
                        case "004": // Doritos
                            g2.setColor(new Color(205, 30, 30));
                            g2.fillRoundRect(12, 7, 32, 47, 6, 6);
                            g2.setColor(new Color(255, 150, 20));
                            int[] tx = {28, 18, 38};
                            int[] ty = {18, 38, 38};
                            g2.fillPolygon(tx, ty, 3);
                            break;
                        case "005": // Pepsi
                            g2.setColor(new Color(18, 78, 165));
                            g2.fillRoundRect(14, 8, 28, 46, 8, 8);
                            g2.setColor(Color.LIGHT_GRAY);
                            g2.fillRect(16, 6, 24, 3);
                            g2.setColor(new Color(225, 35, 45));
                            g2.fillArc(19, 21, 18, 18, 0, 180);
                            g2.setColor(Color.WHITE);
                            g2.fillRect(19, 29, 18, 3);
                            break;
                        case "006": // Cheetos
                            g2.setColor(new Color(245, 115, 20));
                            g2.fillRoundRect(12, 7, 32, 47, 6, 6);
                            g2.setColor(new Color(255, 230, 60));
                            g2.fillOval(18, 20, 20, 14);
                            break;
                        case "007": // Leche Lala 1L
                            g2.setColor(new Color(245, 248, 252));
                            g2.fillRect(15, 13, 26, 41);
                            g2.setColor(new Color(40, 125, 210));
                            g2.drawRect(15, 13, 26, 41);
                            g2.fillRect(15, 22, 26, 16);
                            int[] rx = {15, 28, 41};
                            int[] ry = {13, 6, 13};
                            g2.fillPolygon(rx, ry, 3);
                            break;
                        case "008": // Pan Bimbo
                            g2.setColor(new Color(235, 195, 135));
                            g2.fillRoundRect(15, 12, 26, 42, 10, 10);
                            g2.setColor(new Color(25, 85, 165));
                            g2.fillRect(15, 24, 26, 15);
                            g2.setColor(new Color(215, 35, 40));
                            g2.fillRect(23, 6, 10, 6);
                            break;
                        default:
                            g2.setColor(new Color(45, 165, 155));
                            g2.fillRoundRect(15, 10, 26, 44, 8, 8);
                            g2.setColor(Color.WHITE);
                            g2.fillRect(19, 22, 18, 14);
                            break;
                    }
                    g2.dispose();
                }

                @Override public int getIconWidth() { return 56; }
                @Override public int getIconHeight() { return 58; }
            };
        }

        public static Icon crearIconoCarrito(int w, int h, Color color) {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawLine(x + 2, y + 4, x + 6, y + 4);
                    g2.drawLine(x + 6, y + 4, x + 10, y + h - 8);
                    g2.drawLine(x + 10, y + h - 8, x + w - 4, y + h - 8);
                    g2.drawLine(x + w - 4, y + h - 8, x + w - 2, y + 8);
                    g2.drawLine(x + 7, y + 8, x + w - 2, y + 8);
                    g2.fillOval(x + 10, y + h - 6, 4, 4);
                    g2.fillOval(x + w - 8, y + h - 6, 4, 4);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return w; }
                @Override public int getIconHeight() { return h; }
            };
        }

        public static Icon crearIconoUsuario(int w, int h) {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    g2.fillOval(x + 5, y + 2, 8, 8);
                    g2.fillArc(x + 2, y + 10, 14, 10, 0, 180);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return w; }
                @Override public int getIconHeight() { return h; }
            };
        }

        public static Icon crearIconoLupa(int w, int h) {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(45, 65, 95));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawOval(x + 2, y + 2, 9, 9);
                    g2.drawLine(x + 10, y + 10, x + 14, y + 14);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return w; }
                @Override public int getIconHeight() { return h; }
            };
        }

        public static Icon crearIconoSimbolo(String simbolo, Color bg) {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bg);
                    g2.fillOval(x + 1, y + 1, 16, 16);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 13));
                    g2.drawString(simbolo, x + 5, y + 13);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return 18; }
                @Override public int getIconHeight() { return 18; }
            };
        }

        public static Icon crearIconoEscoba() {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(145, 95, 45));
                    g2.fillRect(x + 8, y + 1, 3, 9);
                    g2.setColor(new Color(235, 180, 45));
                    g2.fillRect(x + 4, y + 10, 11, 7);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return 18; }
                @Override public int getIconHeight() { return 18; }
            };
        }

        public static Icon crearIconoImpresora() {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(55, 115, 185));
                    g2.fillRect(x + 2, y + 6, 16, 8);
                    g2.setColor(Color.WHITE);
                    g2.fillRect(x + 5, y + 2, 10, 4);
                    g2.fillRect(x + 5, y + 11, 10, 6);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return 20; }
                @Override public int getIconHeight() { return 20; }
            };
        }

        public static Icon crearIconoCancelar() {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(210, 45, 45));
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawLine(x + 3, y + 3, x + 15, y + 15);
                    g2.drawLine(x + 15, y + 3, x + 3, y + 15);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return 18; }
                @Override public int getIconHeight() { return 18; }
            };
        }

        public static Icon crearIconoBillete() {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRect(x + 1, y + 4, 24, 15);
                    g2.drawOval(x + 9, y + 7, 8, 8);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return 26; }
                @Override public int getIconHeight() { return 22; }
            };
        }
    }
}