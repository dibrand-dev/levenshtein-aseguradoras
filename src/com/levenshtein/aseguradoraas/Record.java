package com.levenshtein.aseguradoraas;

import java.util.Objects;

public class Record {
	
	private int idEntidad;
	private String name;
	private String address;
	private String postal;
	private int distance;
	private double similitud;
	private boolean padre;
	private String found;
	
	public int getIdEntidad() {
		return idEntidad;
	}
	public void setIdEntidad(int posicion) {
		this.idEntidad = posicion;
	}
	public String getName() {
		return name;
	}
	public void setName(String cliente) {
		this.name = cliente;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String direccion) {
		this.address = direccion;
	}
    public String getPostal() {
		return postal;
	}
	public void setPostal(String codigoPostal) {
		this.postal = codigoPostal;
	}	
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public double getSimilitud() {
		return similitud;
	}
	public void setSimilitud(double similitud) {
		this.similitud = similitud;
	}
	public boolean isPadre() {
		return padre;
	}
	public void setPadre(boolean padre) {
		this.padre = padre;
	}
	public String getFound() {
		return found;
	}
	public void setFound(String found) {
		this.found = found;
	}
	@Override
    public String toString() {
		if(padre)
	        return "type:PADRE" + 
    		", idEntidad:" + idEntidad +
            ", name:" + name+
            ", address:" + address+
            ", postal:" + postal;
		
        return	"type:HIJO " + 
        		", idEntidad:" + idEntidad +
                ", name:" + name+
                ", address:" + address+
                ", postal:" + postal+
                ", distance: " + distance  +
                ", similitud: " + String.format("%.2f", similitud) +
                ", found reason: " + found;
    }
	@Override
	public int hashCode() {
		return Objects.hash(postal, address, distance, idEntidad, name);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Record other = (Record) obj;
		return Objects.equals(postal, other.postal) && Objects.equals(address, other.address)
				&& distance == other.distance && idEntidad == other.idEntidad && Objects.equals(name, other.name);
	}
	
	
	
}
