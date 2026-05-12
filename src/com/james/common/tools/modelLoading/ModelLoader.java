package com.james.common.tools.modelLoading;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import templates.common.GlobalConstants;

import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.assimp.Assimp.*;

// TODO: 2026-05-10 Consider adding a way to not load textureCoords and normals from the file
// TODO: 2026-05-10 This is helpful when doing server-side things
/**
 * General class that handles extracting the relevant information from model files. What would like to be done
 * with the extracted info is up to the programmer; the functionality of this class is not closely intertwined
 * with any other class in the engine (aside from SingleMesh and ModelLoaderHelper). One instance of this class
 * is instantiated for each model file that we would like to read from. Wraps around the functionality provided
 * by the Assimp model loader.
 */
// https://www.youtube.com/watch?v=eqlwamit0vU&t=883s
public class ModelLoader {

    private static final String CUSTOM_NAME_START_CHAR = "@";
    private static final String END_STRING = "-mesh";

    private Map<String, List<SingleMesh>> namedSingleMeshes = null;
    private final SingleMesh[] otherSingleMeshes;
    private List<SingleMesh> allSingleMeshes;

    /**
     * Private constructor that extracts the info from the given model file path, creates all the necessary
     * SingleMeshes (sub meshes), and populates the namedSingleMeshes map and the otherSingleMeshes array.
     * namedSingleMeshes will include all the SingleMeshes that were given a name beginning with
     * CUSTOM_NAME_START_CHAR in Blender, and otherSingleMeshes will include all the other SingleMeshes.
     */
    private ModelLoader(String path) {
        String fullPath = GlobalConstants.MODELS_BASE_DIRECTORY + path;
        AIScene scene = aiImportFile(fullPath,aiProcess_Triangulate | aiProcess_FlipUVs |
                aiProcess_JoinIdenticalVertices);

        if (scene == null)
            throw new RuntimeException("Assimp import failed! " + aiGetErrorString());

        PointerBuffer meshes = scene.mMeshes();
        Objects.requireNonNull(meshes);
        PointerBuffer materials = scene.mMaterials();

        List<SingleMesh> otherSingleMeshesList = new ArrayList<>();

        for (int i = 0; i < meshes.limit(); i++) {
            AIMesh mesh = AIMesh.create(meshes.get(i));
            SingleMesh singleMesh = processMesh(materials, mesh);
            String rawName = mesh.mName().dataString();
            String name = singleMesh.name;

            if (rawName.startsWith(CUSTOM_NAME_START_CHAR)) {
                if (namedSingleMeshes == null) {
                    namedSingleMeshes = new HashMap<>();
                }

                if (!namedSingleMeshes.containsKey(name)) {
                    namedSingleMeshes.put(name, new ArrayList<>());
                }

                namedSingleMeshes.get(name).add(singleMesh);
            } else {
                otherSingleMeshesList.add(singleMesh);
            }
        }

        otherSingleMeshes = otherSingleMeshesList.toArray(new SingleMesh[0]);
    }

    /**
     * Extracts the info pertaining to the given mesh. Returns a SingleMesh object, which contains all the
     * info.
     */
    private SingleMesh processMesh(PointerBuffer materials, AIMesh mesh) {
        AIVector3D.Buffer vectors = mesh.mVertices();
        AIVector3D.Buffer coords = mesh.mTextureCoords(0);
        AIVector3D.Buffer norms = mesh.mNormals();
        AIFace.Buffer faces = mesh.mFaces();

        Objects.requireNonNull(vectors);
        Objects.requireNonNull(coords);
        Objects.requireNonNull(norms);
        Objects.requireNonNull(faces);

        List<Float> vertexPositionsList = new ArrayList<>();
        List<Float> textureCoordsList = new ArrayList<>();
        List<Float> normalsList = new ArrayList<>();
        List<Integer> indicesList = new ArrayList<>();

        for (int i = 0; i < vectors.limit(); i++) {
            AIVector3D vector = vectors.get(i);

            vertexPositionsList.add(vector.x());
            vertexPositionsList.add(vector.y());
            vertexPositionsList.add(vector.z());
        }

        for (int i = 0; i < coords.limit(); i++) {
            AIVector3D coord = coords.get(i);

            textureCoordsList.add(coord.x());
            textureCoordsList.add(coord.y());
        }

        for (int i = 0; i < norms.limit(); i++) {
            AIVector3D norm = norms.get(i);

            normalsList.add(norm.x());
            normalsList.add(norm.y());
            normalsList.add(norm.z());
        }

        for (int i = 0; i < faces.limit(); i++) {
            AIFace face = faces.get(i);
            IntBuffer indicesForFace = face.mIndices();

            indicesList.add(indicesForFace.get(0));
            indicesList.add(indicesForFace.get(1));
            indicesList.add(indicesForFace.get(2));
        }

        float[] vertexPositions = new float[vertexPositionsList.size()];
        for (int i = 0; i < vertexPositionsList.size(); i++) {
            vertexPositions[i] = vertexPositionsList.get(i);
        }

        float[] textureCoords = new float[textureCoordsList.size()];
        for (int i = 0; i < textureCoordsList.size(); i++) {
            textureCoords[i] = textureCoordsList.get(i);
        }

        float[] normals = new float[normalsList.size()];
        for (int i = 0; i < normalsList.size(); i++) {
            normals[i] = normalsList.get(i);
        }

        int[] indices = new int[indicesList.size()];
        for (int i = 0; i < indicesList.size(); i++) {
            indices[i] = indicesList.get(i);
        }

        String rawName = mesh.mName().dataString();
        String name = rawName
                .replace(CUSTOM_NAME_START_CHAR, "")
                .replace(END_STRING, "");
        String textureFilePath = processTextureFilePath(materials, mesh);

        return new SingleMesh(vertexPositions, textureCoords, normals, indices, name, textureFilePath);
    }

    /**
     * Gets the texture file path that is attached to the given mesh. If no file path is attached, then returns
     * null. If more than one file path is attached, only one of them is returned. (The engine at the moment
     * does not support meshes with more than one attached texture.) Since the path returned came from the
     * model file, it could be incorrect (for instance, the path could be an absolute path). As such, if you
     * want to use this path, additional processing may be necessary.
     */
    private String processTextureFilePath(PointerBuffer materials, AIMesh mesh) {
        String texturePath = null;

        int materialIndex = mesh.mMaterialIndex();
        AIMaterial material = AIMaterial.create(materials.get(materialIndex));
        AIString texturePathBuffer = AIString.calloc();

        int status = aiGetMaterialTexture(material, aiTextureType_DIFFUSE, 0, texturePathBuffer,
                (IntBuffer) null, null, null, null, null, null);

        if (status == aiReturn_SUCCESS) {
            texturePath = texturePathBuffer.dataString();
        }

        texturePathBuffer.free();
        return texturePath;
    }

    /**
     * vertexPositions(), textureCoords(), normals(), and indices() are methods that return information for the
     * zeroth unnamed mesh. If the current ModelLoader instance was used to extract info from a model file that
     * only included a single mesh (which is usually the case when the model file does not contain a whole
     * map), then these methods should be used.
     */
    public float[] vertexPositions() { return otherSingleMeshes[0].vertexPositions; }
    public float[] textureCoords() { return otherSingleMeshes[0].textureCoords; }
    public float[] normals() { return otherSingleMeshes[0].normals; }
    public int[] indices() { return otherSingleMeshes[0].indices; }

    private float[] vertexPositionsAllMeshes;
    private float[] textureCoordsAllMeshes;
    private float[] normalsAllMeshes;
    private int[] indicesAllMeshes;

    public float[] vertexPositionsForAllSingleMeshes() {
        if (vertexPositionsAllMeshes == null)
            vertexPositionsAllMeshes = ModelLoaderHelper.vertexPositionsAllMeshes(this);
        return vertexPositionsAllMeshes;
    }

    public float[] textureCoordsForAllSingleMeshes() {
        if (textureCoordsAllMeshes == null)
            textureCoordsAllMeshes = ModelLoaderHelper.textureCoordsAllMeshes(this);
        return textureCoordsAllMeshes;
    }

    public float[] normalsForAllSingleMeshes() {
        if (normalsAllMeshes == null)
            normalsAllMeshes = ModelLoaderHelper.normalsAllMeshes(this);
        return normalsAllMeshes;
    }

    public int[] indicesForAllSingleMeshes() {
        if (indicesAllMeshes == null)
            indicesAllMeshes = ModelLoaderHelper.indicesAllMeshes(this);
        return indicesAllMeshes;
    }

    /**
     * Retrieves the SingleMeshes of the given custom name (but don't include CUSTOM_NAME_START_CHAR at the
     * beginning).
     */
    public List<SingleMesh> getSingleMeshes(String name) {
        return namedSingleMeshes.get(name);
    }

    /**
     * Retrieves all the unnamed SingleMeshes.
     */
    public SingleMesh[] getOtherSingleMeshes() {
        return otherSingleMeshes;
    }

    /**
     * Retrieves all the SingleMeshes, whether they be named or unnamed. Useful for loops that do something for
     * each mesh of the model file.
     */
    public List<SingleMesh> getAllSingleMeshes() {
        if (allSingleMeshes == null) {
            allSingleMeshes = new ArrayList<>();

            if (namedSingleMeshes != null) {
                for (List<SingleMesh> singleMeshes : namedSingleMeshes.values()) {
                    allSingleMeshes.addAll(singleMeshes);
                }
            }
            allSingleMeshes.addAll(Arrays.asList(otherSingleMeshes));
        }

        return allSingleMeshes;
    }

    private static final Map<String, ModelLoader> modelLoaderMap = new HashMap<>();

    /**
     * Creates a ModelLoader for each model file path provided, and puts them in the map for retrieval later.
     * This should be called at the beginning of the game.
     */
    public static void init(String... modelFilePaths) {
        for (String path : modelFilePaths) {
            ModelLoader loader = new ModelLoader(path);
            modelLoaderMap.put(path, loader);
        }
    }

    /**
     * Retrieves the ModelLoader for the given model file path.
     */
    public static ModelLoader get(String path) {
        return modelLoaderMap.get(path);
    }

}
