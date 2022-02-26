package com.tining.demonmarket.gui;

import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.PluginUtil;
import com.tining.demonmarket.common.util.WorthUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AcquireListGui {
    /**
     * 当前开启的菜单
     */
    private static final Map<UUID, AcquireListGui> MENU_OPENING = new HashMap();

    /**
     * 单页大小
     */
    private static final Integer PAGE_SIZE = 54;

    /**
     * 可视区域大小
     */
    private static final Integer VIEW_SIZE = 45;

    /**
     * 向右翻页占位坐标
     */
    private static final Integer LEFT_ARROW_INDEX = 45;

    /**
     * 向左翻页占位坐标
     */
    private static final Integer RIGHT_ARROW_INDEX = 53;

    /**
     * 页码占位坐标
     */
    private static final Integer PAGE_SIGN_INDEX = 49;

    /**
     * 占位符图标
     */
    private static final Material PAGE_ARROW = Material.PAPER;

    /**
     * 页码图标
     */
    private static final Material PAGE_SIGN = Material.BOOK;

    /**
     * 所在的箱子的实体
     */
    public Inventory inventory;

    /**
     * 收购列表名称
     */
    private static final String GUI_NAME = "收购列表";

    /**
     * 持有者
     */
    Player player;

    /**
     * 获取一个界面
     *
     * @param player 玩家
     * @return 箱子对象
     */
    public static AcquireListGui getAcquireListGui(Player player) {
        AcquireListGui acquireListGui = new AcquireListGui();
        acquireListGui.inventory = Bukkit.createInventory(player, PAGE_SIZE, LangUtil.get(GUI_NAME));
        acquireListGui.player = player;

        //TODO:
        drawPage(acquireListGui.inventory, 0);


        acquireListGui.registerAcquireListGui();
        acquireListGui.openAcquireListGui();

        return acquireListGui;
    }

    /**
     * 绘制第N页的列表
     *
     * @param pageNum
     */
    private static void drawPage(Inventory inventory, int pageNum) {
        Map<String, Double> worth = WorthUtil.getWorth();
        Map<String, Double> nbtWorth = WorthUtil.getNBTWorth();
        int move = 0;
        boolean set = false;

        List<ItemStack> list = new ArrayList<>();
        worth.forEach((s, aDouble) -> {
            if (!Objects.isNull(s) && !Objects.isNull(PluginUtil.getItem(s))) {
                list.add(PluginUtil.getItem(s));
            }
        });
        nbtWorth.forEach((s, aDouble) -> {
            //list.add(PluginUtil.getItemStackFromNBTString(s));
        });
        for (int i = pageNum * VIEW_SIZE; i < list.size() && i < (pageNum + 1) * VIEW_SIZE; i++) {
            if (!Objects.isNull(list.get(i))) {
                if (!set) {
                    inventory.clear();
                    set = true;
                }
                inventory.setItem(i % VIEW_SIZE, list.get(i));
                move++;
            }
        }
        //设置翻页图标
        ItemStack left = new ItemStack(PAGE_ARROW, 1);
        ItemStack right = new ItemStack(PAGE_ARROW, 1);
        ItemStack mid = new ItemStack(PAGE_SIGN, 1);
        ItemMeta leftItemMeta = left.getItemMeta();
        ItemMeta rightItemMeta = right.getItemMeta();
        ItemMeta midItemMeta = right.getItemMeta();

        if (!Objects.isNull(leftItemMeta) && pageNum != 0) {
            leftItemMeta.setDisplayName(LangUtil.get("上一页"));
            left.setItemMeta(leftItemMeta);
            inventory.setItem(LEFT_ARROW_INDEX, left);
        }
        if (!Objects.isNull(rightItemMeta) && move != 0 && (pageNum + 1) * VIEW_SIZE < list.size()) {
            rightItemMeta.setDisplayName(LangUtil.get("下一页"));
            right.setItemMeta(rightItemMeta);
            inventory.setItem(RIGHT_ARROW_INDEX, right);
        }
        if (!Objects.isNull(midItemMeta) && move != 0) {
            midItemMeta.setDisplayName("< " + (pageNum + 1) + " >");
            mid.setItemMeta(midItemMeta);
            inventory.setItem(PAGE_SIGN_INDEX, mid);
        }
    }

    /**
     * 绘制第N页的列表
     */
    public static void turnPage(Inventory inventory, int slot) {
        try {
            ItemStack itemStack = inventory.getItem(PAGE_SIGN_INDEX);
            String name = itemStack.getItemMeta().getDisplayName();
            int page = Integer.parseInt(name.replace("<", "").replace(">", "").trim());

            if (Objects.equals(slot, LEFT_ARROW_INDEX)) {
                if (page < 2) {
                    return;
                }
                drawPage(inventory, page - 2);
                return;
            }

            if (Objects.equals(slot, RIGHT_ARROW_INDEX)) {
                drawPage(inventory, page);
                return;
            }
        } catch (Exception e) {
        }
    }


    /**
     * 判断是否在册
     *
     * @param player
     * @return
     */
    public static boolean isAcquireListGui(Player player) {
        if (MENU_OPENING.containsKey(player.getUniqueId())) {
            return true;
        }
        return false;
    }

    /**
     * 根据名字是不是收购列表
     *
     * @param name
     * @return
     */
    public static boolean isAcquireListGui(String name) {
        if (StringUtils.equals(name, LangUtil.get(GUI_NAME))) {
            return true;
        }
        return false;
    }

    /**
     * 打开这个收购箱子
     */
    private void openAcquireListGui() {
        player.openInventory(inventory);
    }

    /**
     * 卸载当前对象
     */
    public static void unRegisterAcquireListGui(Player player) {
        MENU_OPENING.remove(player.getUniqueId());
    }


    /**
     * 把当前对象加入全局表
     */
    private void registerAcquireListGui() {
        MENU_OPENING.put(player.getUniqueId(), this);
    }
}