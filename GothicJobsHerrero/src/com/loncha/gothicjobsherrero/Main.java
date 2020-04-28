package com.loncha.gothicjobsherrero;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Cauldron;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	FundirMetales fMetales = new FundirMetales(this);
	CrafteoYunque cYunque = new CrafteoYunque(this);
	EnfriarMetales eMetales = new EnfriarMetales(this);
	
	ArrayList<Ingrediente> listaIngredientes = new ArrayList<Ingrediente>();
	ArrayList<Receta> listaRecetas = new ArrayList<Receta>();
	ArrayList<ItemForja> listaItemsForja = new ArrayList<ItemForja>();
	
	ArrayList<ItemStack> itemsCustomHerrero = new ArrayList<ItemStack>();
	
	ArrayList<String> tipoConstrucciones = new ArrayList<String>(Arrays.asList("mesa de trabajo","estante para cuero","estante para comida","horno para minerales","yunque"));
	
	ArrayList<String> ingredientesForja = new ArrayList<String>();
	ArrayList<Material> ingredientesTipoForja = new ArrayList<Material>();
	
	FileConfiguration configFile;
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(this.fMetales, this);
		getServer().getPluginManager().registerEvents(this.cYunque, this);
		getServer().getPluginManager().registerEvents(this.eMetales, this);
		
		getCommand("reloadherrero").setExecutor(new Reload(this));
		
		rellenarListaIngredientes();
		rellenarListaRecetas();
		rellenarListaItemsForja();
		cargarItemsCustom();
	}
	
	//Método para rellenar la lista de ingredientes utilizando objetos
	public void rellenarListaIngredientes() {
		File config = new File("plugins/GothicJobsHerrero/recetas.yml");
		configFile = new YamlConfiguration();
		
		listaIngredientes = new ArrayList<Ingrediente>();
		
		try {
			configFile.load(config);
			
			for (String s : getCustomConfig().getConfigurationSection("ingredientes").getKeys(false)) {
				Ingrediente tempIng = new Ingrediente();
				
				tempIng.setNombreIngrediente(getCustomConfig().getString("ingredientes."+s+".nombre"));
				
				tempIng.setTiempoFundicion(getCustomConfig().getInt("ingredientes."+s+".tiempo"));
				
				tempIng.setResultado(getCustomConfig().getString("ingredientes."+s+".resultado"));
				tempIng.setCantidadResultado(getCustomConfig().getDouble("ingredientes."+s+".cantidadresultado"));
				
				listaIngredientes.add(tempIng);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void rellenarListaRecetas() {
		File config = new File("plugins/GothicJobsHerrero/recetas.yml");
		configFile = new YamlConfiguration();
		
		listaRecetas = new ArrayList<Receta>();
		
		try {
			configFile.load(config);
			
			for (String s : getCustomConfig().getConfigurationSection("recetas").getKeys(false)) {
				Receta tempRec = new Receta();
				
				tempRec.setResultado(getCustomConfig().getString("recetas."+s+".resultado"));
				tempRec.setNombreResultado(getCustomConfig().getString("recetas."+s+".nombreresultado"));
				tempRec.setResultadoData(getCustomConfig().getInt("recetas."+s+".resultadodata"));
				tempRec.setResultadoCantidad(getCustomConfig().getInt("recetas."+s+".resultadocantidad"));
				
				tempRec.setIngredientes(getCustomConfig().getStringList("recetas."+s+".ingredientes"));
				tempRec.setCantidadIngredientes(getCustomConfig().getDoubleList("recetas."+s+".cantidadingredientes"));
				
				tempRec.setHerramienta(getCustomConfig().getString("recetas."+s+".herramienta"));
				
				tempRec.setNivel(getCustomConfig().getInt("recetas."+s+".nivel"));
				
				listaRecetas.add(tempRec);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void rellenarListaItemsForja() {
		File config = new File("plugins/GothicJobsHerrero/recetas.yml");
		configFile = new YamlConfiguration();
		
		listaItemsForja = new ArrayList<ItemForja>();
		ingredientesForja = new ArrayList<String>();
		ingredientesTipoForja = new ArrayList<Material>();
		
		try {
			configFile.load(config);
			
			for (String s : getCustomConfig().getConfigurationSection("forja").getKeys(false)) {
				ItemForja item = new ItemForja();
				
				List<String> listaNombres = getCustomConfig().getStringList("forja."+s+".resultados_nombre");
				List<String> listaTipos = getCustomConfig().getStringList("forja."+s+".resultados_item");
				List<Integer> listaUsos = getCustomConfig().getIntegerList("forja."+s+".resultados_usos");
				List<Integer> listaNivel = getCustomConfig().getIntegerList("forja."+s+".resultados_niveles");
				
				List<Material> listaTiposFinal = new ArrayList<Material>();
				
				for (String s2 : listaTipos) {
					listaTiposFinal.add(Material.getMaterial(s2));
				}
				
				item.setNombreItem(s);
				item.setResultadosNombre(listaNombres);
				item.setResultadosItem(listaTiposFinal);
				item.setResultadosUsos(listaUsos);
				item.setResultadosNivel(listaNivel);
				
				listaItemsForja.add(item);

			}
			
			for(String s: getCustomConfig().getConfigurationSection("ingredientesforja").getKeys(false)) {
				ingredientesForja.add(getCustomConfig().getString("ingredientesforja."+s+".nombre"));
				String tipo = getCustomConfig().getString("ingredientesforja."+s+".tipo");			
				ingredientesTipoForja.add(Material.getMaterial(tipo));

			}
		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cargarItemsCustom(){
		File config = new File("plugins/GothicJobsHerrero/itemscustom.yml");
		
		configFile = new YamlConfiguration();
		
		itemsCustomHerrero = new ArrayList<ItemStack>();
		
		try {
			//Resetear arrays
			itemsCustomHerrero = new ArrayList<ItemStack>();
			
			configFile.load(config);
		
			if (getCustomConfig().getConfigurationSection("items").getKeys(true) != null) {
				for (String s : getCustomConfig().getConfigurationSection("items").getKeys(false)) {
					String nombre, material;
					int cantidad, data;
					List<String> lore = new ArrayList<String>();
					
					nombre = getCustomConfig().getString("items."+s+".nombre");
					material = getCustomConfig().getString("items."+s+".material");
					data = getCustomConfig().getInt("items."+s+".data");
					cantidad = getCustomConfig().getInt("items."+s+".cantidad");
					lore = getCustomConfig().getStringList("items."+s+".lore");
					
					ItemStack is = new ItemStack(Material.getMaterial(material), cantidad, (short)data);
					ItemMeta im = is.getItemMeta();
					im.setDisplayName(nombre);
					im.setLore(lore);
					is.setItemMeta(im);
					
					itemsCustomHerrero.add(is);
					
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String checkMesa(Block b) {	
		Location lMesa = new Location(b.getWorld(), b.getLocation().getX(), b.getLocation().getY()-1, b.getLocation().getZ());
		Location lEstante = new Location(b.getWorld(), b.getLocation().getX(), b.getLocation().getY()+1, b.getLocation().getZ());
		
		//COMPRUEBA SI ES UNA MESA DE TRABAJO DE PIEDRA
		if (lMesa.getBlock().getType() == Material.COBBLESTONE_STAIRS) {
			Block mesaCentro = lMesa.getBlock();
			Location lMesa1 = new Location(mesaCentro.getWorld(), mesaCentro.getX()-1, mesaCentro.getY(), mesaCentro.getZ());
			Location lMesa2 = new Location(mesaCentro.getWorld(), mesaCentro.getX()+1, mesaCentro.getY(), mesaCentro.getZ());
			Location lMesa3 = new Location(mesaCentro.getWorld(), mesaCentro.getX(), mesaCentro.getY(), mesaCentro.getZ()-1);
			Location lMesa4 = new Location(mesaCentro.getWorld(), mesaCentro.getX(), mesaCentro.getY(), mesaCentro.getZ()+1);
			
			Location[] locations = {lMesa1, lMesa2, lMesa3, lMesa4};
			
			for (Location l : locations) {
				if (l.getBlock().getType() == Material.COBBLESTONE_STAIRS) {
					return tipoConstrucciones.get(0);
				}
			}
		}
		
		//COMPRUEBA SI ES UN ESTANTE DE CUERO
		if (b.getType().toString().contains("FENCE")) {
			Location posicion1 = new Location (b.getWorld(), b.getLocation().getX()-1, b.getLocation().getY()-1, b.getLocation().getZ());
			Location posicion2 = new Location (b.getWorld(), b.getLocation().getX()+1, b.getLocation().getY()-1, b.getLocation().getZ());
			Location posicion3 = new Location (b.getWorld(), b.getLocation().getX(), b.getLocation().getY()-1, b.getLocation().getZ()-1);
			Location posicion4 = new Location (b.getWorld(), b.getLocation().getX(), b.getLocation().getY()-1, b.getLocation().getZ()+1);
			
			Block estanteCentro = lEstante.getBlock();
			Location lEstante1 = new Location(estanteCentro.getWorld(), estanteCentro.getX()-1, estanteCentro.getY(), estanteCentro.getZ());
			Location lEstante2 = new Location(estanteCentro.getWorld(), estanteCentro.getX()+1, estanteCentro.getY(), estanteCentro.getZ());
			Location lEstante3 = new Location(estanteCentro.getWorld(), estanteCentro.getX(), estanteCentro.getY(), estanteCentro.getZ()-1);
			Location lEstante4 = new Location(estanteCentro.getWorld(), estanteCentro.getX(), estanteCentro.getY(), estanteCentro.getZ()+1);
			
			Location[] locations = {lEstante1, lEstante2, lEstante3, lEstante4};
			
			for (Location l : locations) {
				if (l.getBlock().getType().toString().contains("FENCE")) {
					return tipoConstrucciones.get(2);
				}
			}
			
			if (posicion1.getBlock().getType().toString().contains("FENCE")) {
				if (posicion2.getBlock().getType().toString().contains("FENCE")) {
					return tipoConstrucciones.get(1);
				}
			} else if (posicion3.getBlock().getType().toString().contains("FENCE")) {
				if (posicion4.getBlock().getType().toString().contains("FENCE")) {
					return tipoConstrucciones.get(1);
				}
			}
		}
		
		if (b.getType() == Material.FLOWER_POT) {
			Location posicion1 = new Location (b.getWorld(), b.getLocation().getX(), b.getLocation().getY()-1, b.getLocation().getZ());
			
			if (posicion1.getBlock().getType() == Material.STEP) {
				return tipoConstrucciones.get(3);
			}
		}
		
		if (b.getType() == Material.ANVIL) {
			return tipoConstrucciones.get(4);
		}
		
		return "nada";
	}
	
	public FileConfiguration getCustomConfig() {
		return this.configFile;
	}
}
