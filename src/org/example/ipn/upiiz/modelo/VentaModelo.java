package org.example.ipn.upiiz.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VentaModelo {
    // Atributos
    private List<Producto> catalogoProductos;
    private List<ItemVenta> itemsVenta;
    private double tasaIva;
    private String usuarioActual;
    private List<String> historialVentas;
    private int folioVenta;

    // Constructor por defecto
    public VentaModelo() {
        this("admin", 0.16);
    }

    // Constructor sobrecargado
    public VentaModelo(String usuarioActual, double tasaIva) {
        this.usuarioActual = usuarioActual;
        this.tasaIva = tasaIva;
        this.catalogoProductos = new ArrayList<>();
        this.itemsVenta = new ArrayList<>();
        this.historialVentas = new ArrayList<>();
        this.folioVenta = 1001;
        cargarCatalogoInicial();
    }

    // Carga inicial de productos idénticos a la interfaz
    private void cargarCatalogoInicial() {
        catalogoProductos.add(new Producto("001", "Coca-Cola", "Bebidas", 15.00, 40, "imagenes/coca.png"));
        catalogoProductos.add(new Producto("002", "Agua 1 L", "Bebidas", 12.00, 50, "imagenes/agua.png"));
        catalogoProductos.add(new Producto("003", "Sabritas", "Botanas", 20.00, 35, "imagenes/sabritas.png"));
        catalogoProductos.add(new Producto("004", "Doritos", "Botanas", 20.00, 30, "imagenes/doritos.png"));
        catalogoProductos.add(new Producto("005", "Pepsi", "Bebidas", 15.00, 40, "imagenes/pepsi.png"));
        catalogoProductos.add(new Producto("006", "Cheetos", "Botanas", 18.00, 32, "imagenes/cheetos.png"));
        catalogoProductos.add(new Producto("007", "Leche Lala 1L", "Abarrotes", 24.00, 25, "imagenes/leche.png"));
        catalogoProductos.add(new Producto("008", "Pan Bimbo", "Abarrotes", 18.00, 20, "imagenes/pan.png"));
        catalogoProductos.add(new Producto("009", "Cloralex 1L", "Limpieza", 16.00, 28, "imagenes/cloralex.png"));
        catalogoProductos.add(new Producto("010", "Fabuloso 1L", "Limpieza", 22.00, 24, "imagenes/fabuloso.png"));
    }

    // Métodos sobrecargados para agregar productos al carrito
    public void agregarProducto(Producto producto) {
        agregarProducto(producto, 1);
    }

    public void agregarProducto(Producto producto, int cantidad) {
        if (producto == null || cantidad <= 0) {
            return;
        }
        for (ItemVenta item : itemsVenta) {
            if (item.getProducto().getCodigo().equals(producto.getCodigo())) {
                item.incrementarCantidad(cantidad);
                return;
            }
        }
        itemsVenta.add(new ItemVenta(producto, cantidad));
    }

    public void eliminarItem(int indice) {
        if (indice >= 0 && indice < itemsVenta.size()) {
            itemsVenta.remove(indice);
        }
    }

    public void limpiarVenta() {
        itemsVenta.clear();
    }

    public void registrarNuevoProducto(Producto nuevo) {
        if (nuevo != null) {
            catalogoProductos.add(nuevo);
        }
    }

    // Métodos sobrecargados de búsqueda y filtrado
    public List<Producto> buscarProductos(String texto) {
        return buscarProductos(texto, "Todos");
    }

    public List<Producto> buscarProductos(String texto, String categoria) {
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : catalogoProductos) {
            boolean coincideCategoria = categoria == null
                    || categoria.equalsIgnoreCase("Todos")
                    || p.getCategoria().equalsIgnoreCase(categoria);
            if (coincideCategoria && p.coincideBusqueda(texto)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    // Cálculos financieros
    public double calcularSubtotal() {
        double subtotal = 0.0;
        for (ItemVenta item : itemsVenta) {
            subtotal += item.calcularImporte();
        }
        return Math.round(subtotal * 100.0) / 100.0;
    }

    public double calcularIVA() {
        double iva = calcularSubtotal() * tasaIva;
        return Math.round(iva * 100.0) / 100.0;
    }

    public double calcularTotal() {
        double total = calcularSubtotal() + calcularIVA();
        return Math.round(total * 100.0) / 100.0;
    }

    // Generación de ticket y registro en corte de caja
    public String generarTicketTexto(double pagoRecibido, double cambio) {
        StringBuilder sb = new StringBuilder();
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        sb.append("========================================\n");
        sb.append("         PUNTO DE VENTA - MI TIENDA     \n");
        sb.append("========================================\n");
        sb.append("Folio:  #").append(folioVenta).append("\n");
        sb.append("Fecha:  ").append(fecha).append("\n");
        sb.append("Cajero: ").append(usuarioActual).append("\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-5s %-18s %7s %8s\n", "Cant", "Producto", "P.Unit", "Importe"));
        sb.append("----------------------------------------\n");

        for (ItemVenta item : itemsVenta) {
            sb.append(String.format("%-5d %-18s $%6.2f $%7.2f\n",
                    item.getCantidad(),
                    acortar(item.getProducto().getNombre(), 18),
                    item.getProducto().getPrecio(),
                    item.calcularImporte()));
        }

        sb.append("----------------------------------------\n");
        sb.append(String.format("%29s $%7.2f\n", "Subtotal:", calcularSubtotal()));
        sb.append(String.format("%29s $%7.2f\n", "IVA (16%):", calcularIVA()));
        sb.append(String.format("%29s $%7.2f\n", "TOTAL:", calcularTotal()));

        if (pagoRecibido > 0) {
            sb.append(String.format("%29s $%7.2f\n", "Efectivo:", pagoRecibido));
            sb.append(String.format("%29s $%7.2f\n", "Cambio:", cambio));
        }

        sb.append("========================================\n");
        sb.append("       ¡GRACIAS POR SU COMPRA!          \n");
        sb.append("========================================\n");
        return sb.toString();
    }

    public String registrarVenta(double pagoRecibido) {
        double total = calcularTotal();
        double cambio = Math.round((pagoRecibido - total) * 100.0) / 100.0;
        String ticket = generarTicketTexto(pagoRecibido, cambio);

        String resumen = String.format("Folio #%d | Cajero: %s | Artículos: %d | Total: $%.2f",
                folioVenta, usuarioActual, itemsVenta.size(), total);
        historialVentas.add(resumen);
        folioVenta++;
        limpiarVenta();
        return ticket;
    }

    private String acortar(String texto, int max) {
        if (texto.length() <= max) return texto;
        return texto.substring(0, max - 1) + ".";
    }

    // Getters y Setters
    public List<Producto> getCatalogoProductos() {
        return catalogoProductos;
    }

    public List<ItemVenta> getItemsVenta() {
        return itemsVenta;
    }

    public double getTasaIva() {
        return tasaIva;
    }

    public void setTasaIva(double tasaIva) {
        this.tasaIva = tasaIva;
    }

    public String getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(String usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    public List<String> getHistorialVentas() {
        return historialVentas;
    }
}