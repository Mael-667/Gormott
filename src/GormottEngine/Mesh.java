package GormottEngine;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
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
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

public class Mesh{
    protected float[] vertices;
    protected int[] indices;
    protected int stride;

    private int shaderProgram; 
    private int VAO;

    private FloatBuffer transformMatrixBuffer = BufferUtils.createFloatBuffer(16);
    private float scale = 1.0f;
    private float[] translate = {.0f, .0f, .0f};

    public Mesh(float[] vertices, int[] indices, int stride){
        this.vertices = vertices;
        this.indices = indices;
        this.stride = stride;
        setupObj();
    }

    public Mesh(float[] vertices, int[] indices){
        this(vertices, indices, 3);
    }

    public Mesh(String url){
        MeshLoader.loadMesh(url, this);
        setupObj();
    }

    private void setupObj(){
        //transforme l'array java en array c
        //Le VAO sert a stocker le binding d'un vertexbuffer, sa définition et ses index
        this.VAO = glGenVertexArrays();
        glBindVertexArray(this.VAO);
        
        int vboId = glGenBuffers();
        FloatBuffer vertexBuffer = MemoryUtil.memCallocFloat(this.vertices.length);
        vertexBuffer.put(0, this.vertices);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(vertexBuffer);
        
		int ebo = glGenBuffers();
		IntBuffer indicesBuffer = MemoryUtil.memCallocInt(this.indices.length);
        indicesBuffer.put(0, this.indices);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		MemoryUtil.memFree(indicesBuffer);


        this.shaderProgram = glCreateProgram();
		Utils.bindNewShader(this.shaderProgram, GL_VERTEX_SHADER, "src\\shaders\\scene.vert");		
		Utils.bindNewShader(this.shaderProgram, GL_FRAGMENT_SHADER, "src\\shaders\\scene.frag");		
        glLinkProgram(this.shaderProgram);
		//pour le stride on lui dit de combien d'index bouger a partir de l'index 0 des données pour obtenir le prochain batch multiplié par la longueur d'une donnée en byte
		//vu qu'il y a 3 float pour la position et 3 pour la couleur on doit bouger de 6 par 4 (la taille d'un float en byte) a partir de l'index 0 pour obtenir les prochaines positions
		//positions
        //VertexAttribPointer définit le layout des vertex
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride * Utils.floatSize, 0);
        glEnableVertexAttribArray(0);  
        
        //Bind tous les parametres avant d'unbind le vao
        glBindVertexArray(0);

    }

    public Mesh scale(float scale){
        this.scale = scale;
        return this;
    }

    public Mesh translate(float x, float y, float z){
        this.translate[0] = x;
        this.translate[1] = y;
        this.translate[2] = z;
        return this;
    }


    public void draw(){
        //il faut bind le bon shaderprogram avant de définir les uniform
        glUseProgram(this.shaderProgram);
		//on déclare un uniform dans un shader et cette ligne va récupérer le pointeur vers cet uniform dans le shader pour pouvoir y passer de la data
        int timeLocation = glGetUniformLocation(this.shaderProgram, "time");
		int transLocation = glGetUniformLocation(this.shaderProgram, "translation");

        glUniform1f(timeLocation, (float) glfwGetTime());
        new Matrix4f().scale(this.scale)
                      .translate(this.translate[0], this.translate[1], this.translate[2])
                      .rotateY((float) glfwGetTime()/2)
                      .rotateX(-90)
                      .get(this.transformMatrixBuffer);
        glUniformMatrix4fv(transLocation, false, this.transformMatrixBuffer);

        glBindVertexArray(this.VAO);
        glDrawElements(GL_TRIANGLES, this.indices.length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }
}