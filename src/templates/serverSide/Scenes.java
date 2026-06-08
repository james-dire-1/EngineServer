package templates.serverSide;

import com.james.common.simulation.collisionEngine.prep.ModelMeshBankInR3;
import com.james.serverSide.Scene;
import templates.common.simulation.objects.PhysicalObjectType;
import com.james.serverSide.simulation.Level;
import com.james.serverSide.simulation.collisionEngine.hitboxes.AABBHitbox;
import com.james.serverSide.simulation.objects.MovableObject;
import com.james.serverSide.simulation.objects.PhysicalObject;
import org.lwjgl.util.vector.Vector3f;

public class Scenes {

    public static final Scene nothingScene = new Scene() {
        @Override
        public void onStartup(Level level) {
        }

        @Override
        public String name() {
            return "nothing";
        }
    };

    public static final Scene testScene = new Scene() {
        @Override
        public void onStartup(Level level) {
            for (int i = 0; i < 10; i++) {
                PhysicalObject wall = new PhysicalObject(PhysicalObjectType.Wall, new Vector3f(-i*2, 0, -10), new Vector3f(0, 0, 0), 1);
                level.add(wall);
            }

            PhysicalObject testEnvironment = new PhysicalObject(PhysicalObjectType.TestEnvironment, new Vector3f(), new Vector3f(), 1);
            level.add(testEnvironment);

            AABBHitbox aabbHitbox = new AABBHitbox(testEnvironment, "/scenes/test-scene.dae");
            level.addAABBHitbox(aabbHitbox);

            MovableObject abstractArt = new MovableObject(level, PhysicalObjectType.Other, new Vector3f(0, 5, 0), new Vector3f(), 1) {
                private boolean firstTime = true;

                @Override
                public boolean update() {
                    rotate(0, 10, 0);

                    return super.update();
                }

                @Override
                public void moveUpdate() {
                    if (firstTime) {
                        firstTime = false;
                        isAffectedByGravity = false;
                        setVelocity(6, 0, 0);
                    }

                    if (getPosition().x >= 10) {
                        setVelocity(-6, 0, 0);
                    } else if (getPosition().x <= -10) {
                        setVelocity(6, 0, 0);
                    }

                    super.moveUpdate();
                }
            };

            level.add(abstractArt);
        }

        @Override
        public String name() {
            return "test";
        }

        @Override
        public Vector3f primarySpawnPoint() {
            return new Vector3f(-6, 7, -6);
        }
    };

    public static final Scene desertScene = new Scene() {
        @Override
        public void onStartup(Level level) {
            PhysicalObject desertEnvironment = new PhysicalObject(PhysicalObjectType.DesertEnvironment, new Vector3f(0, 0, 0), new Vector3f(), 1);
            level.add(desertEnvironment);

            for (int i = 0; i < ModelMeshBankInR3.getNumberOfSubMeshes("/scenes/desert-scene.dae"); i++) {
                AABBHitbox aabbHitbox = new AABBHitbox(desertEnvironment, "/scenes/desert-scene.dae", i);
                level.addAABBHitbox(aabbHitbox);
            }
        }

        @Override
        public String name() {
            return "desert";
        }

        @Override
        public Vector3f primarySpawnPoint() {
            return new Vector3f(2, 5, 7);
        }
    };

    public static final Scene beachScene = new Scene() {
        @Override
        public void onStartup(Level level) {
            PhysicalObject beachEnvironment = new PhysicalObject(PhysicalObjectType.BeachEnvironment, new Vector3f(0, 0, 0), new Vector3f(), 1);
            level.add(beachEnvironment);

            for (int i = 0; i < ModelMeshBankInR3.getNumberOfSubMeshes("/scenes/beach-scene.dae"); i++) {
                AABBHitbox aabbHitbox = new AABBHitbox(beachEnvironment, "/scenes/beach-scene.dae", i);
                level.addAABBHitbox(aabbHitbox);
            }
        }

        @Override
        public String name() {
            return "beach";
        }

        @Override
        public Vector3f primarySpawnPoint() {
            return new Vector3f(0, 38, 0);
        }
    };

    public static final Scene plainsScene = new Scene() {
        @Override
        public void onStartup(Level level) {
            PhysicalObject plainsEnvironment = new PhysicalObject(PhysicalObjectType.PlainsEnvironment, new Vector3f(0, 0, 0), new Vector3f(), 1);
            level.add(plainsEnvironment);

            AABBHitbox aabbHitbox = new AABBHitbox(plainsEnvironment, "/scenes/plains-scene.dae");
            level.addAABBHitbox(aabbHitbox);
        }

        @Override
        public String name() {
            return "plains";
        }

        @Override
        public Vector3f primarySpawnPoint() {
            return new Vector3f(0, 5, 0);
        }
    };

}
