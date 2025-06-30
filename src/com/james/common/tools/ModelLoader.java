package com.james.common.tools;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.assimp.Assimp.*;

// TODO: 2023-01-04 Specify where you got this info from
public class ModelLoader {

    public float[] vertexPositions;
    public float[] textureCoords;
    public float[] normals;
    public int[] indices;

    private ModelLoader(String path) {
        AIScene scene = aiImportFile(path,aiProcess_Triangulate | aiProcess_FlipUVs | aiProcess_JoinIdenticalVertices);
//        AIScene scene = aiImportFileEx(path, aiProcess_Triangulate | aiProcess_FlipUVs | aiProcess_JoinIdenticalVertices, AIFileIO.create());

        if (scene == null) {
            System.err.println("Assimp import failed! " + aiGetErrorString());
        }

        PointerBuffer buffer = scene.mMeshes();

        for (int i = 0; i < buffer.limit(); i++) {
            AIMesh mesh = AIMesh.create(buffer.get(i));
            processMesh(mesh);
        }
    }

    private void processMesh(AIMesh mesh) {
        // Vertices
        AIVector3D.Buffer vectors = mesh.mVertices();
        List<Float> vertexPositionsList = new ArrayList<>();

        for (int i = 0; i < vectors.limit(); i++) {
            AIVector3D vector = vectors.get(i);

            vertexPositionsList.add(vector.x());
            vertexPositionsList.add(vector.y());
            vertexPositionsList.add(vector.z());
        }
        vertexPositions = new float[vertexPositionsList.size()];
        for (int i = 0; i < vertexPositionsList.size(); i++) {
            vertexPositions[i] = vertexPositionsList.get(i);
        }

        // Texture Coords
        AIVector3D.Buffer coords = mesh.mTextureCoords(0);
        List<Float> textureCoordsList = new ArrayList<>();

        for (int i = 0; i < coords.limit(); i++) {
            AIVector3D coord = coords.get(i);

            textureCoordsList.add(coord.x());
            textureCoordsList.add(coord.y());
        }
        textureCoords = new float[textureCoordsList.size()];
        for (int i = 0; i < textureCoordsList.size(); i++) {
            textureCoords[i] = textureCoordsList.get(i);
        }

        // Normals
        AIVector3D.Buffer norms = mesh.mNormals();
        List<Float> normalsList = new ArrayList<>();

        for (int i = 0; i < norms.limit(); i++) {
            AIVector3D norm = norms.get(i);

            normalsList.add(norm.x());
            normalsList.add(norm.y());
            normalsList.add(norm.z());
        }
        normals = new float[normalsList.size()];
        for (int i = 0; i < normalsList.size(); i++) {
            normals[i] = normalsList.get(i);
        }

        // Indices
        AIFace.Buffer faces = mesh.mFaces();
        List<Integer> indicesList = new ArrayList<>();

        for (int i = 0; i < faces.limit(); i++) {
            AIFace face = faces.get(i);
            IntBuffer indicesForFace = face.mIndices();
            indicesList.add(indicesForFace.get(0));
            indicesList.add(indicesForFace.get(1));
            indicesList.add(indicesForFace.get(2));
        }
        indices = new int[indicesList.size()];
        for (int i = 0; i < indicesList.size(); i++) {
            indices[i] = indicesList.get(i);
        }
    }

    private static final Map<String, ModelLoader> modelLoaderMap = new HashMap<>();

    public static void init(String... modelFilePaths) {
        for (String path : modelFilePaths) {
            ModelLoader loader = new ModelLoader(path);
            modelLoaderMap.put(path, loader);
        }
    }

    public static ModelLoader get(String path) {
        return modelLoaderMap.get(path);
    }

}
