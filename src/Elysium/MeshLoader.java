package Elysium;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

public class MeshLoader {
    public static void loadMesh(String url, Mesh instance){
        Utils.time("meshLoader", () -> {
            if(url.endsWith(".obj")) loadObj(url, instance);
        });
    }

    public static void loadObj(String url, Mesh instance){
        ArrayList<Float> verticesArrList = new ArrayList<>(2000);
        ArrayList<Integer> indicesArrList = new ArrayList<>(2000);

        try {
            File file = new File(url);
            FileReader fileReader = new FileReader(file); // A stream that connects to the text file
            BufferedReader objFile = new BufferedReader(fileReader, 128*1024); 
            String line;
            while((line = objFile.readLine()) != null){
                //ignore les commentaires
                if(line.isEmpty() || line.charAt(0) == '#') continue;

                String[] content = line.split(" ");

                if(line.startsWith("v ")){
                    for(int i = 1; i < content.length; i++){
                        if(content[i].isEmpty()) continue;
                        verticesArrList.add(Float.parseFloat(content[i]));
                    }
                } else if(line.startsWith("f ")){
                    ArrayList<Integer> tempIndices = new ArrayList<>(4);
                    for(int i = 1; i < content.length; i++){
                        if(content[i].isEmpty()) continue;
                        String[] objIndices = content[i].split("/");
                        //dans la descriptions des indices on recup seulement celle des vertices
                        tempIndices.add(Integer.parseInt(objIndices[0]));
                    }
                    //on vérifie si on obtient bien une description de triangle, si on obtient une description de quad on le reformate
                    if(tempIndices.size() == 3){
                        indicesArrList.addAll(tempIndices);
                    } else if(tempIndices.size() == 4){
                        // 0 1 2 3 ->  0 1 2
                        //             0 2 3
                        indicesArrList.addAll(tempIndices.subList(0, 3));
                        indicesArrList.add(tempIndices.get(0));
                        indicesArrList.addAll(tempIndices.subList(2, 4));
                    }
                }
            }
            
            instance.vertices = new float[verticesArrList.size()];
            for (int i = 0; i < verticesArrList.size(); i++) {
                instance.vertices[i] = verticesArrList.get(i);
            }
            
            //les faces sont clockwise de base donc il faut reverse ???
            instance.indices = new int[indicesArrList.size()];
            int j = 0;
            for (int i = indicesArrList.size()-1; i >= 0; --i) {
                instance.indices[j] = (indicesArrList.get(i)-1);
                j++;
            }
            instance.stride = 3;
            objFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
