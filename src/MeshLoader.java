import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

public class MeshLoader {

    enum State{
        NONE,
        VERTICES,
        INDICES
    }

    public static void loadMesh(String url, Mesh instance){
        Utils.time("meshLoader", () -> {
            if(url.endsWith(".obj")) loadObj(url, instance);
        });
    }

    private static void loadObj(String url, Mesh instance){
        ArrayList<Float> verticesArrList = new ArrayList<>(200);
        ArrayList<Integer> indicesArrList = new ArrayList<>(200);

        try {
            File file = new File(url);
            FileReader fileReader = new FileReader(file); // A stream that connects to the text file
            BufferedReader objFile = new BufferedReader(fileReader, 64*1024); 
            int character;
            char lastChar = '\n';
            State readerState = State.NONE;
            StringBuilder infoBuffer = new StringBuilder();
            while((character = objFile.read()) != -1){
                char c = (char) character;

                if(c == ' '){
                    lastChar = c;
                    continue;
                }

                // if(c == '\n') readerState = State.NONE;

                if(lastChar == '\n' && c == 'v'){readerState = State.VERTICES;}
                else if(lastChar == '\n' && c == 'f'){readerState = State.INDICES;}

                switch (readerState) {
                    case VERTICES:
                        if(lastChar == ' '){
                            verticesArrList.add(Float.parseFloat(infoBuffer.toString()));
                            infoBuffer.delete(0, infoBuffer.length());
                            readerState = State.NONE;
                        }
                        break;
                    case INDICES:
                        if(lastChar == '\n'){
                            ArrayList<Integer> tempIndices = new ArrayList<>(4);
                            String content 
                            for(int i = 1; i < content.length; i++){
                                if(content[i].isEmpty()) continue;
                                String[] objIndices = content[i].split("/");
                                //on récupère que la premiere valeur des indices a savoir les indices des vertex
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
                            readerState = State.NONE;
                        }
                        break;
                    case NONE:
                        break;
                    default:
                        break;
                }
                lastChar = c;

                for(int i = 1; i < content.length; i++){
                    if(content[i].isEmpty()) continue;
                    verticesArrList.add(Float.parseFloat(content[i]));
                }
                } else if(line.startsWith("f ")){
                    ArrayList<Integer> tempIndices = new ArrayList<>(4);
                    for(int i = 1; i < content.length; i++){
                        if(content[i].isEmpty()) continue;
                        String[] objIndices = content[i].split("/");
                        //on récupère que la premiere valeur des indices a savoir les indices des vertex
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
            //les faces sont clockwise de base donc il faut reverse ???
            Collections.reverse(indicesArrList);
            instance.vertices = new float[verticesArrList.size()];
            for (int i = 0; i < verticesArrList.size(); i++) {
                instance.vertices[i] = verticesArrList.get(i).floatValue();
            }

            instance.indices = new int[indicesArrList.size()];
            for (int i = 0; i < indicesArrList.size(); i++) {
                instance.indices[i] = (indicesArrList.get(i).intValue()-1);
            }
            instance.stride = 3;
            objFile.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
