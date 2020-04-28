package com.loncha.gothicjobsherrero;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Cauldron;

import net.md_5.bungee.api.ChatColor;

public class EnfriarMetales implements Listener{
	Main m;
	
	public EnfriarMetales(Main m) {
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
		
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = e.getClickedBlock();
			
			if (b.getType() == Material.CAULDRON) {
				Cauldron c = (Cauldron) b.getState().getData();
				
				if (nombreItemInHand.contains("al rojo")) {
					if (!c.isEmpty()) {
						
						if (nombreItemInHand.contains("Cubo")) {
							restarObjeto(itemInHand, p);
							p.getInventory().addItem(new ItemStack(Material.BUCKET));
							
						} else {						
							//Quitarte el item al rojo de la mano
							restarObjeto(itemInHand, p);
							String nuevoNombre = nombreItemInHand.substring(0, nombreItemInHand.length()-8);
							
							//Darte el nuevo item
							for (ItemStack item : m.itemsCustomHerrero) {
								if (item.hasItemMeta()) {
									if (item.getItemMeta().getDisplayName().equalsIgnoreCase(nuevoNombre)) {
										p.getInventory().addItem(item);
										
										reproducirSonido(p, Sound.ITEM_ARMOR_EQUIP_LEATHER, 10);
									}
								}
							}
							
							//Vaciar caldero
							BlockState d = b.getState();
			                d.getData().setData((byte) (c.getData()-1));
			                d.update();
			                
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
	
	public void reproducirSonido(Player p, Sound sonido, int rango) {
        for (Player players : Bukkit.getOnlinePlayers()) {
        	if (p.getWorld() == players.getWorld()) {
				if (p.getLocation().distanceSquared(players.getLocation()) <= 10) {
					
					players.getWorld().playSound(p.getLocation(), sonido, 1.0F, 0.01F);
				}
        	}
        }
	}
	
	public void enviarMensajeSimple(Player p, ChatColor color, String mensaje, int rango) {
        for (Player players : Bukkit.getOnlinePlayers()) {
        	if (p.getWorld() == players.getWorld()) {
				if (p.getLocation().distanceSquared(players.getLocation()) <= 10) {
					players.sendMessage(color+mensaje);
				}
        	}
        }
	}
}
