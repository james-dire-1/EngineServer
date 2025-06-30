package com.james.common.simulation.collisionEngine.prep;

import com.james.common.tools.ModelLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Bank of ModelMeshes in R3 local space.
 */
public class ModelMeshBankInR3 {

    private static final Map<String, ModelMesh> modelMeshMap = new HashMap<>();

    /**
     * Creates all the ModelMeshes from the given file paths. Should be called at the beginning of the
     * game.
     */
    public static void init(String... modelMeshFilePaths) {
        for (String path : modelMeshFilePaths) {
            ModelLoader modelLoader = ModelLoader.get(path);
            ModelMesh modelMesh = new ModelMesh(modelLoader.vertexPositions, modelLoader.indices);
            modelMeshMap.put(path, modelMesh);
        }
    }

    public static Map<String, ModelMesh> getModelMeshMap() {
        return modelMeshMap;
    }

}
