package com.james.common.tools.modelLoading;

/**
 * Container class that represents a single mesh in a model file. Instances of this class are created in the
 * ModelLoader. Usually, only one mesh exists in a model file, so most of the time, one SingleMesh is created
 * for a ModelLoader. This is not always the case though (think about maps), so sometimes, numerous
 * SingleMeshes are created for a ModelLoader.
 */
public class SingleMesh {

    public final float[] vertexPositions;
    public final float[] textureCoords;
    public final float[] normals;
    public final int[] indices;

    public final String name;
    public final String textureFilePath;

    /**
     * Populates the fields that contain information for a mesh.
     *
     * @implNote Note that textureFilePath might be incorrect. Refer to processTextureFilePath() in ModelLoader
     * to see why.
     */
    public SingleMesh(float[] vertexPositions, float[] textureCoords, float[] normals, int[] indices,
                      String name, String textureFilePath) {
        this.vertexPositions = vertexPositions;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;

        this.name = name;
        this.textureFilePath = textureFilePath;
    }

}