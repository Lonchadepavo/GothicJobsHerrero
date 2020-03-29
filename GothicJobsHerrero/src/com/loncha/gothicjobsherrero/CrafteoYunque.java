
package com.loncha.gothicjobsherrero;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitScheduler;

import net.md_5.bungee.api.ChatColor;

public class CrafteoYunque implements Listener{
	Main m;
	
	HashMap<Player, Boolean> quemarse = new HashMap<Player, Boolean>();
	
	public CrafteoYunque(Main m) {
		this.m = m;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		quemarse.put(p, true);
	}
		
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack itemInHand = p.getInventory().getItemInMainHand();
		String nombreItemInHand = "";
		String loreItemInHand = "";
		
		if (itemInHand.hasItemMeta()) {
			nombreItemInHand = itemInHand.getItemMeta().getDisplayName();
			if (itemInHand.getItemMeta().hasLore()) {
				loreItemInHand = itemInHand.getItemMeta().getLore().get(0);
			}
		} else {
			nombreItemInHand = itemInHand.getType().toString();
		}
		
		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			Block b = e.getClickedBlock();
			
			if (b.hasMetadata("left")) {
				if (b.hasMetadata("yunque")) {
					//Comprueba si tienes la herramienta adecuada en la mano
					if (b.getMetadata("herramientaherrero").get(0).asString().equalsIgnoreCase(nombreItemInHand)) {
						e.setCancelled(true);
						for(ItemForja item : m.listaItemsForja) {
							String nombreItem = item.getNombreItem();
							
							List<String> resultadosNombre = item.getResultadosNombre();
							List<Material> resultadosItem = item.getResultadosItem();
							List<Integer> resultadosUso = item.getResultadosUsos();
							List<Integer> resultadosNivel = item.getResultadosNivel();
							
							for(String s : resultadosNombre) {
								System.out.println(s);
							}
							
							if (b.hasMetadata(nombreItem)) {
								if (b.getMetadata("usos").get(0).asInt() < resultadosUso.get(resultadosUso.size()-1)) {
									int usos = b.getMetadata("usos").get(0).asInt();
									b.setMetadata("usos", new FixedMetadataValue(m,usos+1));
									
									for(int i = 0; i < resultadosUso.size(); i++) {
										if (b.getMetadata("usos").get(0).asInt() >= resultadosUso.get(i)) {
											if (i > resultadosNombre.size()-1) {
												b.setMetadata("resultado", new FixedMetadataValue(m, resultadosItem.get(i-1)));
												b.setMetadata("resultadonombre", new FixedMetadataValue(m, resultadosNombre.get(i-1)));
												b.setMetadata("nivel", new FixedMetadataValue(m, resultadosNivel.get(i)));
												
												for (String s : resultadosNombre) {
													b.removeMetadata(s, m);
												}
												
												b.setMetadata(resultadosNombre.get(i-1), new FixedMetadataValue(m,"true"));
												
											} else {
												b.setMetadata("resultado", new FixedMetadataValue(m, resultadosItem.get(i)));
												b.setMetadata("resultadonombre", new FixedMetadataValue(m, resultadosNombre.get(i)));
												b.setMetadata("nivel", new FixedMetadataValue(m, resultadosNivel.get(i)));
												
												for (String s : resultadosNombre) {
													b.removeMetadata(s, m);
												}
												
												b.setMetadata(resultadosNombre.get(i), new FixedMetadataValue(m,"true"));
												
											}
										}
									}
									
									reproducirSonido(p, Sound.BLOCK_ANVIL_LAND, 10);
									
									//Reducir durabilidad y romper herramienta
									if (itemInHand.getDurability() < itemInHand.getType().getMaxDurability()) {
									 itemInHand.setDurability((short) (itemInHand.getDurability()+1));
									} else {
										p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
									}
								} else {
									b.setType(Material.AIR);
									b.removeMetadata("left", m);
									
									p.sendMessage("¡Has dejado el trozo de metal inservible!");
									
									for (String s : resultadosNombre) {
										b.removeMetadata(s, m);
									}
									
									int maxDamage = itemInHand.getType().getMaxDurability();
									int damage = itemInHand.getDurability() - (maxDamage -1);
									
									if (itemInHand.getDurability() < itemInHand.getType().getMaxDurability()) {
										itemInHand.setDurability((short) (itemInHand.getDurability()+1));
									} else {
										p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
									}
								}
							}
							
						}
					}
				}
			}
			
		} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
			Block b = e.getClickedBlock();
			if (m.ingredientesForja.contains(nombreItemInHand)) {
				int indexItem = m.ingredientesForja.indexOf(nombreItemInHand);

				if (m.ingredientesTipoForja.get(indexItem) == itemInHand.getType()) {
					if (m.checkMesa(b).equalsIgnoreCase("yunque")) {
						Location l = new Location(b.getWorld(),b.getX(),b.getY()+1,b.getZ());
						Block b2 = l.getBlock();
						
						b2.setMetadata("left", new FixedMetadataValue(m, "true"));
						b2.setMetadata(nombreItemInHand, new FixedMetadataValue(m, "true"));
						b2.setMetadata("yunque", new FixedMetadataValue(m, "true"));
						b2.setMetadata("herrero", new FixedMetadataValue(m, "true"));
						b2.setMetadata("usos", new FixedMetadataValue(m, 0));
						b2.setMetadata("herramientaherrero", new FixedMetadataValue(m, "§fmartillo"));
						
						b2.setType(Material.IRON_BLOCK);
						
						//Restar un item de la mano
						restarObjeto(itemInHand, p);
					}
				}
			} else if (nombreItemInHand.equalsIgnoreCase("AIR") || nombreItemInHand == null) {
				if (b.hasMetadata("left")) {
					if (b.hasMetadata("yunque")) {
						if (p.hasPermission("gjobs.herrero"+b.getMetadata("nivel").get(0).asInt())) {
							
							ItemStack[] items = p.getInventory().getContents();
							if (items[38] != null && items[38].hasItemMeta()) {
								if (items[38].getItemMeta().getDisplayName().equalsIgnoreCase("§fGuantes de herrero")) {
									if (b.getMetadata("usos").get(0).asInt() >= 5) {
										String nombre = b.getMetadata("resultadonombre").get(0).asString();
				
										for (ItemStack item : m.itemsCustomHerrero) {
											String nombreItem = "";
											
											if (item.hasItemMeta()) {
												nombreItem = item.getItemMeta().getDisplayName();
											} else {
												nombreItem = item.getType().toString();
											}
											
											if (nombreItem.equalsIgnoreCase(nombre)) {
												p.getWorld().dropItem(b.getLocation(), item);
												
												b.setType(Material.AIR);
												b.removeMetadata("left", m);
											}
										}
									}
									
								} else {
									p.damage(5);
									p.sendMessage(ChatColor.RED+"No llevabas la protección necesaria y te has quemado");
								}
							} else {
								p.damage(5);
								p.sendMessage(ChatColor.RED+"No llevabas la protección necesaria y te has quemado");
							}
						} else {
							b.setType(Material.AIR);
							b.removeMetadata("left", m);
							
							p.sendMessage("¡Has dejado el trozo de metal inservible!");
							
							int maxDamage = itemInHand.getType().getMaxDurability();
							int damage = itemInHand.getDurability() - (maxDamage -1);
							
							if (itemInHand.getDurability() < itemInHand.getType().getMaxDurability()) {
								itemInHand.setDurability((short) (itemInHand.getDurability()+1));
							} else {
								p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
							}
						}
						
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPickupEvent(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			ItemStack item = e.getItem().getItemStack();
			
			if (item.hasItemMeta()) {
				if (item.getItemMeta().getDisplayName().contains("al rojo")) {
					ItemStack[] items = p.getInventory().getContents();
					if (items[38] != null && items[38].hasItemMeta()) {
						if (!items[38].getItemMeta().getDisplayName().equalsIgnoreCase("§fGuantes de herrero")) {
							e.setCancelled(true);
							
							if (quemarse.get(p)) {
								p.damage(5);
								p.sendMessage(ChatColor.RED+"No llevabas la protección necesaria y te has quemado");
								
								quemarse.put(p, false);
								
					            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
					            scheduler.scheduleSyncDelayedTask(m, new Runnable() {
					                @Override
					                public void run() {
					                	quemarse.put(p, true);
					                }
					            }, 100);
		
							}
						}
					} else {
						e.setCancelled(true);
						
						if (quemarse.get(p)) {
							p.damage(5);
							p.sendMessage(ChatColor.RED+"No llevabas la protección necesaria y te has quemado");
							
							quemarse.put(p, false);
							
				            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
				            scheduler.scheduleSyncDelayedTask(m, new Runnable() {
				                @Override
				                public void run() {
				                	quemarse.put(p, true);
				                }
				            }, 100);
	
						}
					}
					
				}
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
	
	public void restarObjeto(ItemStack item, Player p, int cantidad) {
		if (item.getAmount()-cantidad == 0) {
			p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
		} else {
			item.setAmount(item.getAmount()-cantidad);
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
}
