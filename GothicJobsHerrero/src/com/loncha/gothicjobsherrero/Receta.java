package com.loncha.gothicjobsherrero;

import java.util.ArrayList;
import java.util.List;

public class Receta {
	List<String> ingredientes = new ArrayList<String>();

	List<Double> cantidadIngredientes = new ArrayList<Double>();

	String resultado;
	String nombreResultado;	

	int resultadoData;
	
	int resultadoCantidad;
	
	String herramienta;
	
	int nivel;

	public List<String> getIngredientes() {
		return ingredientes;
	}

	public void setIngredientes(List<String> ingredientes) {
		this.ingredientes = ingredientes;
	}
	
	public List<Double> getCantidadIngredientes() {
		return cantidadIngredientes;
	}

	public void setCantidadIngredientes(List<Double> cantidadIngredientes) {
		this.cantidadIngredientes = cantidadIngredientes;
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	
	public String getNombreResultado() {
		return nombreResultado;
	}

	public void setNombreResultado(String nombreResultado) {
		this.nombreResultado = nombreResultado;
	}

	public int getResultadoData() {
		return resultadoData;
	}

	public void setResultadoData(int resultadoData) {
		this.resultadoData = resultadoData;
	}

	public int getResultadoCantidad() {
		return resultadoCantidad;
	}

	public void setResultadoCantidad(int resultadoCantidad) {
		this.resultadoCantidad = resultadoCantidad;
	}

	public String getHerramienta() {
		return herramienta;
	}

	public void setHerramienta(String herramienta) {
		this.herramienta = herramienta;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}
}
