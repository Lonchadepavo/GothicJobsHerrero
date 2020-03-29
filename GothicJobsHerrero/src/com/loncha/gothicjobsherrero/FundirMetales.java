package com.loncha.gothicjobsherrero;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitScheduler;

import net.md_5.bungee.api.ChatColor;

public class FundirMetales implements Listener{
	Main m;
	
	ArrayList<String> tipoConstrucciones = new ArrayList<String>(Arrays.asList("mesa de trabajo","estante para cuero","estante para comida","horno para minerales"));
	
	int maximaCapacidad = 20;
	
	public FundirMetales(Main m) {
		this.m = m;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		ItemStack itemInHand = p.getInventory().getItemInMainHand();
		String nombreItemInHand = "";
		
		if (itemInHand.hasItemMeta()) {
			nombreItemInHand = itemInHand.getItemMeta().getDisplayName();
		} else {
			nombreItemInHand = itemInHand.getType().toString();
		}
		
		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			Block b = e.getClickedBlock();
			
			if (b.getType() == Material.FLOWER_POT) {
				//Comprobar si el pot es parte de un horno para fundir minerales
				if (m.checkMesa(b).equalsIgnoreCase("horno para minerales")) {
					for (Ingrediente ing : m.listaIngredientes) {
						String nombreIngrediente = ing.getNombreIngrediente();
						int dataIngrediente = ing.getDataIngrediente();

						if (nombreIngrediente.equals(nombreItemInHand)) {
							System.out.println("entra");
							if (!b.hasMetadata("estado")) {
								b.setMetadata("estado", new FixedMetadataValue(m,"ingredientes"));
								b.setMetadata("tiempofundicion", new FixedMetadataValue(m, 0));
								b.setMetadata("left", new FixedMetadataValue(m, "true"));
								b.setMetadata("capacidad", new FixedMetadataValue(m, 0));
							}
						}
					}

						if (b.hasMetadata("estado")) {
							e.setCancelled(true);
				
							switch(b.getMetadata("estado").get(0).asString()) {
								case "ingredientes":
									if (b.getMetadata("capacidad").get(0).asInt() < maximaCapacidad) {
										for (Ingrediente ing : m.listaIngredientes) {
											String nombreIngrediente = ing.getNombreIngrediente();
											if (nombreIngrediente.equals(nombreItemInHand)) {
												if (b.hasMetadata(nombreItemInHand)) {
													int data = b.getMetadata(nombreItemInHand).get(0).asInt();	
													b.setMetadata(nombreItemInHand, new FixedMetadataValue(m, data+1));
													b.setMetadata(nombreItemInHand+"data", new FixedMetadataValue(m,itemInHand.getData().getData()));
													b.setMetadata(nombreItemInHand+"resultado", new FixedMetadataValue(m,ing.getResultado()));
													
													Double cantidadResultado = b.getMetadata(nombreItemInHand+"cantidadresultado").get(0).asDouble();
													b.setMetadata(nombreItemInHand+"cantidadresultado", new FixedMetadataValue(m,cantidadResultado+ing.getCantidadResultado()));
													
													int tiempo = b.getMetadata("tiempofundicion").get(0).asInt();	
													b.setMetadata("tiempofundicion", new FixedMetadataValue(m,tiempo+ing.getTiempoFundicion()));
													
													int capacidad = b.getMetadata("capacidad").get(0).asInt();
													b.setMetadata("capacidad", new FixedMetadataValue(m,capacidad++));
													
													//Restarte un objeto de la mano.
													restarObjeto(itemInHand, p);
													
													p.sendMessage(ChatColor.GREEN+"Introduces un ingrediente en el tarro");
													
												} else {
													b.setMetadata(nombreItemInHand, new FixedMetadataValue(m, 1));
													b.setMetadata(nombreItemInHand+"data", new FixedMetadataValue(m,itemInHand.getData().getData()));
													b.setMetadata(nombreItemInHand+"resultado", new FixedMetadataValue(m,ing.getResultado()));
													b.setMetadata(nombreItemInHand+"cantidadresultado", new FixedMetadataValue(m,ing.getCantidadResultado()));
													b.setMetadata("tiempofundicion", new FixedMetadataValue(m,ing.getTiempoFundicion()));
													
													int capacidad = b.getMetadata("capacidad").get(0).asInt();
													b.setMetadata("capacidad", new FixedMetadataValue(m,capacidad++));
													
													//Restarte un objeto de la mano
													restarObjeto(itemInHand, p);
													
													p.sendMessage(ChatColor.GREEN+"Introduces un ingrediente en el tarro");
												}
											}
										}
									} else {
										p.sendMessage("No cabe nada más en el tarro");
									}
									
									break;
									
								case "espera":
									if (!b.hasMetadata("fundiendo")) {
										p.sendMessage("Le colocas la tapa al tarro");
										int tiempoFundicion = b.getMetadata("tiempofundicion").get(0).asInt();
										b.setMetadata("fundiendo", new FixedMetadataValue(m, "true"));
										
							            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
							            scheduler.scheduleSyncDelayedTask(m, new Runnable() {
							                @Override
							                public void run() {
							                	//Recorrer la lista de ingredientes, comprobar que ingredientes hay en el tarro y realizar los cambios
							                	
							                	Location lFire = new Location(b.getWorld(), b.getX(), b.getY()-2, b.getZ());
							                	
							                	if (lFire.getBlock().getType() == Material.FIRE) {
								                	for (Ingrediente ing : m.listaIngredientes) {
								                		String nombreIng = ing.getNombreIngrediente();
								                		
								                		if (b.hasMetadata(nombreIng)) {							                			
								                			//Añadir los datos del resultado
								                			if (b.hasMetadata(b.getMetadata(nombreIng+"resultado").get(0).asString())) {
								                				Double c = b.getMetadata(b.getMetadata(nombreIng+"resultado").get(0).asString()).get(0).asDouble() + b.getMetadata(nombreIng+"cantidadresultado").get(0).asDouble();
								                				b.setMetadata(b.getMetadata(nombreIng+"resultado").get(0).asString(), new FixedMetadataValue(m, c));
								                			
								                			} else {
								                				b.setMetadata(b.getMetadata(nombreIng+"resultado").get(0).asString(), new FixedMetadataValue(m, b.getMetadata(nombreIng+"cantidadresultado").get(0).asDouble()));
								                				System.out.println(b.getMetadata(nombreIng+"resultado").get(0).asString());
								                				if (b.hasMetadata("Hierro fundido")) {
								                					System.out.println("tienehierrofundido");
								                				}
								                			}
								                			
								                			//Eliminar los datos del ingrediente
								                			b.removeMetadata(nombreIng, m);
								                			b.removeMetadata(nombreIng+"data", m);
								                			b.removeMetadata(nombreIng+"resultado", m);
								                			b.removeMetadata(nombreIng+"cantidadresultado", m);
								                			b.removeMetadata("fundiendo", m);
								                			
								                			b.setMetadata("estado", new FixedMetadataValue(m, "recolectar"));
								                			
								                		}
								                	}
								                	
								                	enviarMensajeSimple(p, ChatColor.GREEN,"Escuchas como burbujea el metal fundido.", 10);
										            reproducirSonido(p,Sound.ENTITY_HORSE_EAT,5);
							                	} else {
							                		enviarMensajeSimple(p, ChatColor.RED,"El fuego debe estar encendido, destapas el tarro",10);
							                		b.setMetadata("estado", new FixedMetadataValue(m, "ingredientes"));
							                		
							                	}
							                }
							            }, tiempoFundicion);
									} else {
										p.sendMessage(ChatColor.RED+"El tarro está cerrado y escuchas el metal burbujear dentro.");
									}
									break;
									
								case "recolectar":
									List<String> ingredientes = new ArrayList<String>();
									List<Integer> data = new ArrayList<Integer>();
									List<Double> cantidades = new ArrayList<Double>();
									for (Ingrediente ing : m.listaIngredientes) {
										String nombreIngrediente = ing.getNombreIngrediente();
										
										if (nombreIngrediente.equals(nombreItemInHand)) {
											b.setMetadata("estado", new FixedMetadataValue(m, "ingredientes"));
											b.setMetadata(nombreItemInHand, new FixedMetadataValue(m, 1));
											b.setMetadata(nombreItemInHand+"data", new FixedMetadataValue(m,itemInHand.getData().getData()));
											b.setMetadata(nombreItemInHand+"resultado", new FixedMetadataValue(m,ing.getResultado()));
											b.setMetadata(nombreItemInHand+"cantidadresultado", new FixedMetadataValue(m,ing.getCantidadResultado()));
											b.setMetadata("tiempofundicion", new FixedMetadataValue(m,ing.getTiempoFundicion()));
											
											int capacidad = b.getMetadata("capacidad").get(0).asInt();
											b.setMetadata("capacidad", new FixedMetadataValue(m,capacidad++));
											
											//Restarte un objeto de la mano
											restarObjeto(itemInHand, p);
											
											p.sendMessage(ChatColor.GREEN+"Introduces un ingrediente en el tarro");
										} else {
											for (int i = 0; i < m.listaRecetas.size(); i++) {
												Receta rec = m.listaRecetas.get(i);
												
												List<String> tempIng = rec.getIngredientes();
												List<Double> tempCant = rec.getCantidadIngredientes();
												
												for (int k = 0; k < tempIng.size(); k++) {
													if (b.hasMetadata(tempIng.get(k))) {
														ingredientes.add(tempIng.get(k));
														cantidades.add(tempCant.get(k));
													}
												}
												
											}
											
											//Se busca un match en la lista de recetas
											int numeroReceta = buscarMatch(ingredientes, cantidades, nombreItemInHand);
											if (numeroReceta != -1) {
												Receta recetaSeleccionada = m.listaRecetas.get(numeroReceta);
												
												if (p.hasPermission("gjobs.herrero"+recetaSeleccionada.getNivel())) {
													List<String> ingredientesFinal = recetaSeleccionada.getIngredientes();
													List<Double> cantidadIngredientesFinal = recetaSeleccionada.getCantidadIngredientes();
													
													ItemStack[] items = p.getInventory().getContents();
													
													if (items[38] != null && items[38].hasItemMeta()) {
														if (items[38].getItemMeta().getDisplayName().equalsIgnoreCase("§fGuantes de herrero")) {
															for(int k = 0; k < ingredientesFinal.size(); k++) {
																if (b.hasMetadata(ingredientesFinal.get(k))) {
																	Double cantidadIngrediente = b.getMetadata(ingredientesFinal.get(k)).get(0).asDouble();
																	if (cantidadIngrediente >= cantidadIngredientesFinal.get(k)) {
																		if (nombreItemInHand.equalsIgnoreCase(recetaSeleccionada.getHerramienta())) {
																			//Dropear el resultado
																			for (ItemStack item : m.itemsCustomHerrero) {
																				String nombreItem = "";
																				
																				if (item.hasItemMeta()) {
																					nombreItem = item.getItemMeta().getDisplayName();
																				} else {
																					nombreItem = item.getType().toString();
																				}
																				
																				if (recetaSeleccionada.getNombreResultado() == null || recetaSeleccionada.getNombreResultado().equals("")) {
																					if (nombreItem.equalsIgnoreCase(recetaSeleccionada.getResultado())) {
																						p.getWorld().dropItem(b.getLocation(), item);
																					}
																				} else {
																					if (nombreItem.equalsIgnoreCase(recetaSeleccionada.getNombreResultado())) {
																						p.getWorld().dropItem(b.getLocation(), item);
																					}
																				}
																			}
																			
																			//Restar la cantidad de ingrediente al total (si es 0 se elimina el ingrediente)
																			cantidadIngrediente -= cantidadIngredientesFinal.get(k);
																			
																			if (cantidadIngrediente > 0) {
																				b.setMetadata(ingredientesFinal.get(k), new FixedMetadataValue(m, cantidadIngrediente));
																			} else {
																				b.removeMetadata(ingredientesFinal.get(k), m);
																			}
																		}
																	}
																}
															}
														}  else {
															//Hace daño al player por no llevar guantes de herrero
															p.damage(5);
															p.sendMessage(ChatColor.RED+"No llevabas la protección necesaria y te has quemado");
														}
													}  else {
														//Hace daño al player por no llevar guantes de herrero
														p.damage(5);
														p.sendMessage(ChatColor.RED+"No llevabas la protección necesaria y te has quemado");
													}
												}
											}
										}
									
									break;
							}
						}
					}
				}
			}	
		} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			//Resetear el tarro
			Block b = e.getClickedBlock();
			
			if (b.getType() == Material.FLOWER_POT) {
				
				if (b.hasMetadata("estado")) {
					//Comprobar si el pot es parte de un horno para fundir minerales
					if (m.checkMesa(b).equalsIgnoreCase("horno para minerales")) {
						//Recorrer los ingredientes sin fundir y borrarlos
						for (Ingrediente ing : m.listaIngredientes) {
							String nombreIngrediente = ing.getNombreIngrediente();
							b.removeMetadata(nombreIngrediente, m);
							b.removeMetadata(nombreIngrediente+"data", m);
							b.removeMetadata(nombreIngrediente+"resultado", m);
							b.removeMetadata(nombreIngrediente+"cantidadresultado", m);
						}
						
						//Recorrer los ingredientes de las recetas y borrarlos
						for (Receta rec : m.listaRecetas) {
							List<String> ingredientes = rec.getIngredientes();
							
							for (String ingrediente : ingredientes) {
								b.removeMetadata(ingrediente, m);
							}
						}
						
						b.removeMetadata("estado", m);
						b.removeMetadata("tiempofundicion", m);
						b.removeMetadata("left", m);
						b.removeMetadata("capacidad", m);
						
						p.sendMessage("Vacías el tarro y lo dejas listo para el próximo uso.");
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		
		Location pos1 = new Location(b.getWorld(), b.getX(), b.getY()+1, b.getZ());
		Location pos2 = new Location(b.getWorld(), b.getX(), b.getY()+2, b.getZ());
		
		if (pos1.getBlock().getType() == Material.STEP) {
			if (pos2.getBlock().getType() == Material.FLOWER_POT) {
				Block tarro = pos2.getBlock();
				
				if (tarro.hasMetadata("left")) {
					if (tarro.hasMetadata("estado")) {
						if (tarro.getMetadata("estado").get(0).asString().equals("ingredientes")) {
							tarro.setMetadata("estado", new FixedMetadataValue(m,"espera"));
						}
					}
					
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ItemStack clickedItem = e.getCurrentItem();
		
		String nombreItem = "";
		if (clickedItem.hasItemMeta()) {
			nombreItem = clickedItem.getItemMeta().getDisplayName();
			
		} else {
			nombreItem = clickedItem.getType().toString();
		}
		
		if (nombreItem.contains("al rojo")) {
			ItemStack[] items = p.getInventory().getContents();
			
			if (items[38] != null && items[38].hasItemMeta()) {
				if (!items[38].getItemMeta().getDisplayName().equalsIgnoreCase("§fGuantes de herrero")) {
					//Te quemas
					e.setCancelled(true);
					p.damage(5);
					p.sendMessage(ChatColor.RED+"No llevabas la protección necesaria y te has quemado");
				}
			} else {
				//Te quemas
				e.setCancelled(true);
				p.damage(5);
				p.sendMessage(ChatColor.RED+"No llevabas la protección necesaria y te has quemado");
			}
		}
	}
		
	public void restarObjeto(ItemStack item, Player p) {
		if (item.getAmount()-1 == 0) {
			p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
		} else {
			item.setAmount(item.getAmount()-1);
			p.getInventory().setItemInMainHand(item);
		}
	}
	
	public void reproducirSonido(Player p, Sound sonido, int rango) {
        for (Player players : Bukkit.getOnlinePlayers()) {
			if (p.getLocation().distanceSquared(players.getLocation()) <= 10) {
				players.getWorld().playSound(p.getLocation(), sonido, 1.0F, 0.01F);
			}
        }
	}
	
	public void enviarMensajeSimple(Player p, ChatColor color, String mensaje, int rango) {
        for (Player players : Bukkit.getOnlinePlayers()) {
			if (p.getLocation().distanceSquared(players.getLocation()) <= 10) {
				players.sendMessage(color+mensaje);
			}
        }
	}

	public int buscarMatch(List<String> ingredientes, List<Double> cantidades, String herramienta) {
		int nIngredientes = ingredientes.size();
		
		while(nIngredientes >= 0) {
			for (int i = m.listaRecetas.size()-1; i >= 0; i--) {
				Receta tempRec = m.listaRecetas.get(i);
				
				List<String> tempIng = tempRec.getIngredientes();
				List<Double> tempCant = tempRec.getCantidadIngredientes();
				String tempHerramienta = tempRec.getHerramienta();
				
				if (tempIng.size() == nIngredientes) {
					int contadorIngredientes = 0;
					
					for (int k = 0; k < ingredientes.size(); k++) {

						if (tempIng.contains(ingredientes.get(k))) {
							int posicionIng = k;
							int posicionCantidad = tempIng.indexOf(ingredientes.get(k));
	
							if (tempCant.get(posicionCantidad) == cantidades.get(posicionIng)) {
								if (tempHerramienta.equals(herramienta)) {
									contadorIngredientes++;
								}
							}
							
						}
						
					}
					
					if (contadorIngredientes == tempIng.size()) {
						//RECETA ENCONTRADA
						return i;
					}
				}
			}
			nIngredientes--;
		}
		return -1;
	}

}
