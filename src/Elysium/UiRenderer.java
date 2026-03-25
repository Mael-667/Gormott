package Elysium;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUniform2iv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

public class UiRenderer {

    protected ArrayList<Float> vertices = new ArrayList<>();
    protected ArrayList<Integer> indices = new ArrayList<>();

    protected ArrayList<Element> elements = new ArrayList<>();

    private int shaderProgram; 
    private int VAO;
    private IntBuffer dimensionBuffer;

    protected int wHeight, wWidth = 0;

    public UiRenderer(int wWidth, int wHeight){
        this.wWidth = wWidth;
        this.wHeight = wHeight;

        //Le VAO sert a stocker le binding d'un vertexbuffer, sa définition et ses index
        //Création des buffers
        this.VAO = glGenVertexArrays();
        glBindVertexArray(this.VAO);
        
        int vboId = glGenBuffers();
		int ebo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

        //pour le stride on lui dit de combien d'index bouger a partir de l'index 0 des données pour obtenir le prochain batch multiplié par la longueur d'une donnée en byte
		//vu qu'il y a 3 float pour la position et 3 pour la couleur on doit bouger de 6 par 4 (la taille d'un float en byte) a partir de l'index 0 pour obtenir les prochaines positions
		//positions
        //VertexAttribPointer définit le layout des vertex
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Utils.floatSize, 0);
        glEnableVertexAttribArray(0);  
        //color ---          id de l'attribut - le nb d'element qui le compose - son type - jsp - combien de bit a sauter depuis son premier elemtn pour obtenir le prochain 1er - position du 1er element
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Utils.floatSize, 3 * Utils.floatSize);
        glEnableVertexAttribArray(1);  


        this.shaderProgram = glCreateProgram();
		Utils.bindNewShader(this.shaderProgram, GL_VERTEX_SHADER, "src\\shaders\\scene.vert");		
		Utils.bindNewShader(this.shaderProgram, GL_FRAGMENT_SHADER, "src\\shaders\\scene.frag");		
        glLinkProgram(this.shaderProgram);

        this.dimensionBuffer = BufferUtils.createIntBuffer(2);
        new Vector2i().add(this.wWidth, this.wHeight).get(this.dimensionBuffer);
    }

    public void draw(){
        glBindVertexArray(this.VAO);
        //juste link ne suffit pas il faut bien préciser quel shader utiliser
        glUseProgram(this.shaderProgram);

        int wDimension = glGetUniformLocation(this.shaderProgram, "wDimension");
        glUniform2iv(wDimension, this.dimensionBuffer);
        glDrawElements(GL_TRIANGLES, this.indices.size(), GL_UNSIGNED_INT, 0);

        int error;
        while((error = glGetError()) != GL_NO_ERROR){
            System.out.println("GL ERROR: " + error);
        }
    }

    public void addElement(Element element){
        this.generateVertices(element, this.elements.size());
        this.elements.add(element);

        updateGlBuffers();
    }

    public void updateAllNDC(int wWidth, int wHeight){
        this.wWidth = wWidth;
        this.wHeight = wHeight;
        new Vector2i().add(this.wWidth, this.wHeight).get(this.dimensionBuffer);
        this.vertices = new ArrayList<>();
        this.indices = new ArrayList<>();

        for(int i = 0; i < this.elements.size(); ++i){
            Element element = this.elements.get(i);
            this.generateVertices(element, i);
        }

        updateGlBuffers();
    }

    private void generateVertices(Element element, int elementIndex){
        element.updateNDC(this.wWidth, this.wHeight);

        Float[] vertices = {
			//Un vertex représente un point qui contient plusieurs parametres tels que sa position ou sa couleur
			//positions                                  //colors
			element.endX,    element.startY,  0.0f,       element.color[0], element.color[1], element.color[2],  // top right
			element.endX,    element.endY,    0.0f,       element.color[0], element.color[1], element.color[2],  // bottom right
			element.startX,  element.endY,    0.0f,       element.color[0], element.color[1], element.color[2],  // bottom left
			element.startX,  element.startY,  0.0f,       element.color[0], element.color[1], element.color[2]  // top left 
		};

        // multiplier la position des indices par le nombre de vertex et non d'indices logique psk c les vertex qu'on traque
        int indicePos = elementIndex*4;

		Integer[] indices = {  // note that we start from 0!
			3 + indicePos, 2 + indicePos, 1 + indicePos ,  // second triangle
			3 + indicePos, 1 + indicePos, 0 + indicePos,   // first triangle
		}; 

        this.vertices.addAll(Arrays.asList(vertices));
        this.indices.addAll(Arrays.asList(indices));
    }

    private void updateGlBuffers(){
        glBindVertexArray(this.VAO);

        //transforme l'array java en array c
        // Définition des buffers
        FloatBuffer vertexBuffer = MemoryUtil.memCallocFloat(this.vertices.size());
        vertexBuffer.put(0, this.FloatListToArray(vertices));
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(vertexBuffer);
        
		IntBuffer indicesBuffer = MemoryUtil.memCallocInt(this.indices.size());
        indicesBuffer.put(0, this.indices.stream().mapToInt(i->i).toArray());
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		MemoryUtil.memFree(indicesBuffer);

        //Bind tous les parametres avant d'unbind le vao
        // glBindVertexArray(0);
    }

    protected float[] FloatListToArray(ArrayList<Float> collection){
        float[] result = new float[collection.size()];
        int i = 0;
        for (Float f : collection) {
            result[i++] = f;
        }
        return result;
    }
}
