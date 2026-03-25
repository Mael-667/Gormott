package Elysium;

public class Element {

    //position en pixel
    public int posX;
    public int posY;
    public int width; 
    public int height;

    //normalized device coordonate
    public float startX;
    public float startY; 
    public float endX;
    public float endY;

    //couleur
    //de 0 - 1.0 x 3
    public float[] color = {1.0f, 1.0f, 1.0f};

    public Element(int posX, int posY, int width, int height) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    public void setColor(String hex){
        float r, g, b;
        r = (Integer.parseInt(hex.substring(1, 3), 16)) / 255.0f;
        g = (Integer.parseInt(hex.substring(3, 5), 16)) / 255.0f;
        b = (Integer.parseInt(hex.substring(5, 7), 16)) / 255.0f;

        this.color[0] = r;
        this.color[1] = g;
        this.color[2] = b;
    }

    public void updateNDC(int wWidth, int wHeight){
        this.startX = Utils.pixelToNDC(this.posX, wWidth);
        this.startY = -Utils.pixelToNDC(this.posY, wHeight);

        this.endX = Utils.pixelToNDC(this.posX+this.width, wWidth);
        this.endY = -Utils.pixelToNDC(this.posY+this.height, wHeight);
    }
}
