package me.phantomclone.levelpvp.gui;

import me.phantomclone.anvilgui.api.Anvilgui;
import me.phantomclone.api.inventorygui.ClickItem;
import me.phantomclone.api.inventorygui.InventoryGui;
import me.phantomclone.levelpvp.LevelPvP;
import me.phantomclone.levelpvp.kit.Effect;
import me.phantomclone.levelpvp.kit.EffectEnum;
import me.phantomclone.levelpvp.kit.EventEnum;
import me.phantomclone.levelpvp.kit.Kit;
import me.phantomclone.levelpvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class EffectChangeGui extends InventoryGui {

    private LevelPvP plugin;
    private Kit kit;
    private int page;

    public EffectChangeGui(LevelPvP plugin, Kit kit, int page) {
        super(plugin, plugin.getServer().createInventory(null, 9 * 6, "§8" + kit.getName() + " §7- §8Effekt Manager"));
        this.plugin = plugin;
        this.kit = kit;
        this.page = page;
        setFillItem(new ItemBuilder(Material.BARRIER).setDisplayname("§7").build());
        initListener();
        setDestroyOnClose(true);

        ItemStack black = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15).setDisplayname("§7").build();

        for (int i = 0; i < 9; i++) {
            setItem(i, black);
            setItem(i + 5 * 9, black);
        }
        for (int i = 9; i < 45; i += 9) {
            setItem(i, black);
            setItem(i + 8, black);
        }

        if (page != 0) {
            setItem(0, new ClickItem(new ItemStack(Material.DOUBLE_PLANT)).setDisplayname("§7<- §8Page §7(§d" + page + "§7)").setClicked(p -> new EffectChangeGui(plugin, kit,page - 1).openInventory(p)));
        }
        setItem(8, new ClickItem(new ItemStack(Material.DOUBLE_PLANT)).setDisplayname("§8Page §7(§d" + (page + 2) + "§7) ->").setClicked(p -> new EffectChangeGui(plugin, kit,page + 1).openInventory(p)));

        setItem(4, new ClickItem(new ItemStack(Material.ANVIL)).setDisplayname("§7Füge ein Effekt hinzu...").setClicked(p -> new AddEffectGui(plugin, kit).openInventory(p)));

        setItem(18, new ClickItem(new ItemStack(Material.STONE_BUTTON)).setDisplayname("§cZurück zum Kit Editor §7- §d" + kit.getName()).setClicked(player -> {
            plugin.getGuiHandler().getKitEdit(0).openEditorGui(player, kit);
        }));

        setEffects();
    }

    private void setEffects() {
        int count = 4 * 9 * page;
        int equip = kit.getEventEffects().get(EventEnum.EQUIP).size();
        int hit = kit.getEventEffects().get(EventEnum.HIT).size();
        int kill = kit.getEventEffects().get(EventEnum.KILL).size();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 7; j++) {
                if (count < equip) {
                    setItem(convertSlot(j, i), createClickitem(EventEnum.EQUIP, kit.getEventEffects().get(EventEnum.EQUIP).get(count)));
                } else if (count < equip + hit) {
                    setItem(convertSlot(j, i), createClickitem(EventEnum.HIT, kit.getEventEffects().get(EventEnum.HIT).get(count - equip)));
                } else if (count < equip + hit + kill) {
                    setItem(convertSlot(j, i), createClickitem(EventEnum.KILL, kit.getEventEffects().get(EventEnum.KILL).get(count - equip - hit)));
                } else {
                    setItem(convertSlot(j, i), getFillItem());
                }
                count++;
            }
        }
        update();
    }

    private ClickItem createClickitem(EventEnum event, Effect effect) {
        return new ClickItem(new ItemBuilder(event.getMaterial()).setDisplayname(event.name()).setLore("Effekt Type: " + effect.getType().name(), "Power: " + effect.getPower(), "Duration: " + effect.getDuration(), "§4Klicke zum Löschen").build()).setClicked(player -> {
            kit.getEventEffects().get(event).remove(effect);
            player.sendMessage("§7Effekt wurde entfernt!");
            kit.save();
            player.sendMessage("§7Effekt änderung wurde gespeichert!");
            setEffects();
        });
    }

    private int convertSlot(int x, int y) {
        return y * 9 + 9 + x + 1;
    }

    public int getPage() {
        return page;
    }

    private class AddEffectGui extends InventoryGui {

        private EventEnum event;
        private int power;
        private int duration;

        public AddEffectGui(LevelPvP plugin, Kit kit) {
            super(plugin, plugin.getServer().createInventory(null, 9 * 5, "§8" + kit.getName() + " §7- §8Add Effekt"));
            this.power = -1;
            this.duration = -1;
            initListener();
            setDestroyOnClose(true);

            ItemStack black = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15).setDisplayname("§7").build();

            for (int i = 0; i < 9; i++) {
                setItem(i, black);
                setItem(i + 4 * 9, black);
            }
            for (int i = 9; i < 36; i += 9) {
                setItem(i, black);
                setItem(i + 8, black);
            }

            setItem(18, new ClickItem(new ItemStack(Material.STONE_BUTTON)).setDisplayname("§cZrück zum Kit §d" + kit.getName()).setClicked(player -> new EffectChangeGui(plugin, kit, EffectChangeGui.this.getPage()).openInventory(player)));

            int x = 0;
            int y = 0;
            for (EffectEnum effect : EffectEnum.values()) {
                setItem(convertSlot(x, y), new ClickItem(effect.getItem()).setClicked(player -> {
                    player.closeInventory();
                    new Anvilgui.Builder().plugin(plugin).text("Effektevent...").onComplete((p, s) -> {
                        if (event == null) {
                            try {
                                event = EventEnum.valueOf(s.toUpperCase());
                                return Anvilgui.Response.text("Power...");
                            } catch (IllegalArgumentException e) {
                                player.sendMessage("Falsches Event...");
                                player.sendMessage(Arrays.toString(EventEnum.values()));
                                return Anvilgui.Response.text("Effektevent...");
                            }
                        }
                        try {
                            int i = Integer.parseInt(s);
                            if (i <= 0) {
                                p.sendMessage("§7Die Zahl muss großer als 0 sein!");
                                return Anvilgui.Response.text(power == -1 ? "Power..." : "Duration...");
                            }
                            if (power == -1) {
                                power = i;
                                return Anvilgui.Response.text("Duration...");
                            }
                            duration = i;
                            Effect e = new Effect();
                            e.setType(effect);
                            e.setPower(power);
                            e.setDuration(duration);
                            kit.getEventEffects().get(event).add(e);
                            p.sendMessage("§7Effekt wurde dem Kit hinzufügt.");
                            kit.save();
                            p.sendMessage("§7Effekt wurde gespeichert");
                            EffectChangeGui.this.setEffects();
                            return Anvilgui.Response.close();
                        } catch (Exception e) {
                            p.sendMessage("§7Nur Zahlen...");
                            return Anvilgui.Response.text(power == -1 ? "Power..." : "Duration...");
                        }
                    }).open(player);
                }));
                x++;
                if (x == 7) {
                    x = 0;
                    y++;
                }
            }
            update();
        }

        private int convertSlot(int x, int y) {
            return y * 9 + 9 + x + 1;
        }
    }
}
