package ru.itis.gui;

import ru.itis.general.entities.Car;
import ru.itis.general.entities.Player;
import ru.itis.gui.components.MainJPanel;

import java.util.List;

public class GuiManager {
    private final Window window;
    private MainJPanel mainJPanel;

    public GuiManager(Window window) {
        this.window = window;
        this.mainJPanel = (MainJPanel) window.getMainFrame().getContentPane().getComponent(0);
    }

    public void showInvalidNameTip(){
        mainJPanel.getInputNameJPanel().showInvalidNameTip();
    }

    public void showEnterRoomButton(){
        mainJPanel.showEnterRoomButton();
    }

    public void changePlayersInRoom(List<Player> players){
        mainJPanel.getRoomJPanel().changePlayers(players);
    }

    public void addReadyButton(){
        mainJPanel.getRoomJPanel().addReadyButton();
    }

    public void showCarShop(Player player, List<Car> cars){
        if(cars == null){
            mainJPanel.showCarShop(player, mainJPanel.getCarShopJPanel().getCars());
        } else {
            mainJPanel.showCarShop(player, cars);
        }
    }

    public void showRoundResults(List<Player> players) {
        mainJPanel.showResults(players, false);
    }

    public void showFinalResults(List<Player> players) {
        mainJPanel.showResults(players, true);
    }

    public void showNotEnoughMoney(int price){
        mainJPanel.getCarShopJPanel().showFrame(false, price);
    }
}
