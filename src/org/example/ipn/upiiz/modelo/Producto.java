package org.example.ipn.upiiz.modelo;

public class Producto {
    private String codigo;
    private String nombre;
    private String categoria;
    private double precio;
    private int stock;
    private String rutaImagen;

    public Producto() {
        this("000", "Sin nombre", "Abarrotes", 0.0, 0, "");
    }

    public Producto(String codigo, String nombre, String categoria, double precio) {
        this(codigo, nombre, categoria, precio, 50, "");
    }

    public Producto(String codigo, String nombre, String categoria, double precio, int stock) {
        this(codigo, nombre, categoria, precio, stock, "");
    }

    public Producto(String codigo, String nombre, String categoria, double precio, int stock, String rutaImagen) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.rutaImagen = rutaImagen;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getRutaImagen() { return rutaImagen; }
    public void setRutaImagen(String rutaImagen) { this.rutaImagen = rutaImagen; }

    public boolean coincideBusqueda(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return true;
        }
        String filtro = criterio.trim().toLowerCase();
        return nombre.toLowerCase().contains(filtro) || codigo.toLowerCase().contains(filtro);
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + String.format(" ($%.2f)", precio);
    }
}