package com.loncha.gothicjobsherrero;

import org.bukkit.Material;

public class Ingrediente {
	//OBJETO QUE GUARDA LOS ATRIBUTOS DE UN INGREDIENTE
	String nombreIngrediente; //Nombre del ingrediente (Puede tener o no)
	int dataIngrediente;
	
	int tiempoFundicion; //Tiempo que tarda este ingrediente en fundirse
	
	String resultado; //Resultado que da este ingrediente
	double cantidadResultado; //Cantidad de resultado que da este ingrediente
	
	public String getNombreIngrediente() {
		return nombreIngrediente;
	}
	
	public void setNombreIngrediente(String nombreIngrediente) {
		this.nombreIngrediente = nombreIngrediente;
	}
	
	public int getDataIngrediente() {
		return dataIngrediente;
	}
	
	public void setDataIngrediente(int dataIngrediente) {
		this.dataIngrediente = dataIngrediente;
	}

	public int getTiempoFundicion() {
		return tiempoFundicion;
	}
	
	public void setTiempoFundicion(int tiempoFundicion) {
		this.tiempoFundicion = tiempoFundicion;
	}
	
	public String getResultado() {
		return resultado;
	}
	
	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	
	public double getCantidadResultado() {
		return cantidadResultado;
	}
	
	public void setCantidadResultado(double cantidadResultado) {
		this.cantidadResultado = cantidadResultado;
	}
	
	
	
}
