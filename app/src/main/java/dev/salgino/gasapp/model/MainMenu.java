package dev.salgino.gasapp.model;

public class MainMenu {
    private String menu;
    private int image;

    public MainMenu(String menu, int image) {
        this.menu = menu;
        this.image = image;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
