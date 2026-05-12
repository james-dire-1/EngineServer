package com.james.common.simulation.collisionEngine.prep;

import com.james.common.tools.modelLoading.ModelLoader;
import com.james.common.tools.modelLoading.SingleMesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bank of ModelMeshes in R3 local space.
 */
public class ModelMeshBankInR3 {

    private static final Map<String, List<ModelMesh>> modelMeshMap = new HashMap<>();

    /**
     * Creates all the ModelMeshes from the given file paths. Should be called at the beginning of the game.
     *
     * @implNote Each key in modelMeshMap refers to a model file path, such as a Collada file path. Each value
     * refers to all the ModelMeshes that were extracted from the file in question. (Hence, each value is a
     * list of ModelMeshes.) That is to say that a single model file can include more than one mesh. This is
     * most commonly seen in map files, where there is a lot of geometry that makes up the scene. Occasionally
     * throughout the code, a single mesh is referred to as a sub mesh. (This can be seen in subMeshIdentifier
     * in AbstractAABBHitbox.)
     */
    public static void init(String... modelMeshFilePaths) {
        for (String path : modelMeshFilePaths) {
            ModelLoader modelLoader = ModelLoader.get(path);
            List<SingleMesh> singleMeshes = modelLoader.getAllSingleMeshes();

            for (SingleMesh singleMesh : singleMeshes) {
                ModelMesh modelMesh = new ModelMesh(singleMesh.vertexPositions, singleMesh.indices);

                if (!modelMeshMap.containsKey(path)) {
                    modelMeshMap.put(path, new ArrayList<>());
                }

                modelMeshMap.get(path).add(modelMesh);
            }
        }
    }

    public static Map<String, List<ModelMesh>> getModelMeshMap() {
        return modelMeshMap;
    }

    /**
     * Returns the number of sub meshes (SingleMeshes) that exists for a given model file path. This is useful
     * for knowing how many AABB hitboxes need to be instantiated for the given model file's contents. Used
     * mostly for maps.
     */
    public static int getNumberOfSubMeshes(String meshPath) {
        return modelMeshMap.get(meshPath).size();
    }

}
