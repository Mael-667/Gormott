package GormottEngine;
import java.util.ArrayList;

public class Scene {
    private ArrayList<Mesh> objects = new ArrayList<>();

    public void addObj(Mesh obj){
        objects.add(obj);
    }

    public void render(){
        objects.forEach((e) -> {
            e.draw();
        });
    }
}
