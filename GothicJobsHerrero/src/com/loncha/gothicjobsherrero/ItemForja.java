package com.loncha.gothicjobsherrero;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

public class ItemForja {
	String nombreItem;
	
	List<String> resultadosNombre = new ArrayList<String>();
	List<Material> resultadosItem = new ArrayList<Material>();
	List<Integer> resultadosUsos = new ArrayList<Integer>();
	List<Integer> resultadosNivel = new ArrayList<Integer>();
	
	public List<Integer> getResultadosNivel() {
		return resultadosNivel;
	}

	public void setResultadosNivel(List<Integer> resultadosNivel) {
		this.resultadosNivel = resultadosNivel;
	}

	public String getNombreItem() {
		return nombreItem;
	}

	public void setNombreItem(String nombreItem) {
		this.nombreItem = nombreItem;
	}

	public List<String> getResultadosNombre() {
		return resultadosNombre;
	}
	
	public void setResultadosNombre(List<String> resultadosNombre) {
		this.resultadosNombre = resultadosNombre;
	}
	
	public List<Material> getResultadosItem() {
		return resultadosItem;
	}
	
	public void setResultadosItem(List<Material> resultadosItem) {
		this.resultadosItem = resultadosItem;
	}
	
	public List<Integer> getResultadosUsos() {
		return resultadosUsos;
	}
	
	public void setResultadosUsos(List<Integer> resultadosUsos) {
		this.resultadosUsos = resultadosUsos;
	}

}
