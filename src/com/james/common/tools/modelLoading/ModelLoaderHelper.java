package com.james.common.tools.modelLoading;

import java.util.List;

/**
 * A helper class for the ModelLoader. Provides methods that combine information from all SingleMeshes into a
 * single collection (as if there were one SingleMesh instead of numerous). The functionality provided in this
 * class is desirable in some cases. All that is defined here could have been placed in the ModelLoader, but
 * is placed here instead so as not to clutter the ModelLoader too much.
 */
public class ModelLoaderHelper {

    /**
     * Returns in a single array all vertex positions from all meshes. Useful if we would like to make a single
     * Model or ModelMesh from all the meshes.
     */
    public static float[] vertexPositionsAllMeshes(ModelLoader loader) {
        List<SingleMesh> singleMeshes = loader.getAllSingleMeshes();

        int arrayLength = 0;
        for (SingleMesh singleMesh : singleMeshes) {
            arrayLength += singleMesh.vertexPositions.length;
        }

        float[] vertexPositionsAllMeshes = new float[arrayLength];
        int currentIndex = 0;

        for (SingleMesh singleMesh : singleMeshes) {
            for (float vertexCoord : singleMesh.vertexPositions) {
                vertexPositionsAllMeshes[currentIndex] = vertexCoord;
                currentIndex++;
            }
        }

        return vertexPositionsAllMeshes;
    }

    /**
     * Returns in a single array all texture coords from all meshes.
     */
    public static float[] textureCoordsAllMeshes(ModelLoader loader) {
        List<SingleMesh> singleMeshes = loader.getAllSingleMeshes();

        int arrayLength = 0;
        for (SingleMesh singleMesh : singleMeshes) {
            arrayLength += singleMesh.textureCoords.length;
        }

        float[] textureCoordsAllMeshes = new float[arrayLength];
        int currentIndex = 0;

        for (SingleMesh singleMesh : singleMeshes) {
            for (float textureCoord : singleMesh.textureCoords) {
                textureCoordsAllMeshes[currentIndex] = textureCoord;
                currentIndex++;
            }
        }

        return textureCoordsAllMeshes;
    }

    /**
     * Returns in a single array all normal vectors from all meshes.
     */
    public static float[] normalsAllMeshes(ModelLoader loader) {
        List<SingleMesh> singleMeshes = loader.getAllSingleMeshes();

        int arrayLength = 0;
        for (SingleMesh singleMesh : singleMeshes) {
            arrayLength += singleMesh.normals.length;
        }

        float[] normalsAllMeshes = new float[arrayLength];
        int currentIndex = 0;

        for (SingleMesh singleMesh : singleMeshes) {
            for (float normalCoord : singleMesh.normals) {
                normalsAllMeshes[currentIndex] = normalCoord;
                currentIndex++;
            }
        }

        return normalsAllMeshes;
    }

    /**
     * Returns in a single array all indices from all meshes. Index offsetting is taken into account.
     */
    public static int[] indicesAllMeshes(ModelLoader loader) {
        List<SingleMesh> singleMeshes = loader.getAllSingleMeshes();

        int arrayLength = 0;
        for (SingleMesh singleMesh : singleMeshes) {
            arrayLength += singleMesh.indices.length;
        }

        int[] indicesAllMeshes = new int[arrayLength];
        int glIndexOffset = 0;
        int currentIndex = 0;

        for (SingleMesh singleMesh : singleMeshes) {
            for (int glIndex : singleMesh.indices) {
                indicesAllMeshes[currentIndex] = glIndexOffset + glIndex;
                currentIndex++;
            }

            if (singleMesh.vertexPositions.length % 3 != 0)
                throw new RuntimeException();

            glIndexOffset += singleMesh.vertexPositions.length / 3;
        }

        return indicesAllMeshes;
    }

}
