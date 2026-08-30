package templates.serverSide;

import com.james.common.simulation.LevelProperties;
import com.james.common.simulation.collisionEngine.prep.EllipsoidDimensions;
import com.james.common.simulation.collisionEngine.prep.ModelMeshBankInR3;
import com.james.serverSide.Scene;
import com.james.serverSide.simulation.collisionEngine.hitboxes.EllipsoidHitbox;
import com.james.serverSide.simulation.objects.VirtualDirectionalLight;
import com.james.serverSide.simulation.objects.Updatable;
import com.james.serverSide.simulation.collisionEngine.hitboxes.SphereHitbox;
import templates.common.audio.Sound;
import templates.common.simulation.objects.PhysicalObjectType;
import com.james.serverSide.simulation.Level;
import com.james.serverSide.simulation.collisionEngine.hitboxes.AABBHitbox;
import com.james.serverSide.simulation.objects.MovableObject;
import com.james.serverSide.simulation.objects.PhysicalObject;
import org.lwjgl.util.vector.Vector3f;

import java.util.Random;

public class Scenes {

    public static final Scene nothingScene = new Scene() {
        @Override
        public void onStartup(Level level) {
            level.setSkyboxDetails("sky gradient", false);
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

            class CoolObject extends MovableObject implements Updatable {
                private CoolObject(LevelProperties levelProperties, PhysicalObjectType type, Vector3f position, Vector3f rotation, float scale) {
                    super(levelProperties, type, position, rotation, scale);
                    isAffectedByGravity = true;
                    setVelocity(6, 0, 0);
                }

                @Override
                public void moveUpdate() {
                    rotate(0, 10, 0);

                    if (getPosition().x >= 8) {
                        setVelocity(-6, 0, 0);
                    } else if (getPosition().x <= -8) {
                        setVelocity(6, 0, 0);
                    }

                    super.moveUpdate();
                }

                @Override
                public boolean update() {
                    level.events.sendPlaySoundAtPhysicalObject(Sound.CLICK, id);
                    return false;
                }
            }

            CoolObject coolObject = new CoolObject(level, PhysicalObjectType.Other, new Vector3f(0, 4, 0), new Vector3f(), 1);
            level.add(coolObject);
            level.addUpdatable(coolObject, null);

            EllipsoidHitbox coolObjectHitbox = new EllipsoidHitbox(coolObject, EllipsoidDimensions.get(1, 1, 1));
            level.addEllipsoidHitbox(coolObjectHitbox);

            float xDirection = (float) Math.cos(Math.toRadians(80));
            float yDirection = (float) Math.sin(Math.toRadians(80));
            VirtualDirectionalLight sun = new VirtualDirectionalLight(new Vector3f(xDirection, yDirection, 0), new Vector3f(1, 1, 1));
            level.addVirtualDirectionalLight(sun, false);

            level.setSkyboxDetails("sky with clouds", false);
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

            float xDirection = (float) Math.cos(Math.toRadians(80));
            float yDirection = (float) Math.sin(Math.toRadians(80));
            VirtualDirectionalLight sun = new VirtualDirectionalLight(new Vector3f(xDirection, yDirection, 0), new Vector3f(1, 1, 1));
            level.addVirtualDirectionalLight(sun, false);

            level.setSkyboxDetails("sky gradient", false);
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

    private static int counter;
    private static int itemsAdded;
    public static final Scene beachScene = new Scene() {
        @Override
        public void onStartup(Level level) {
            PhysicalObject beachEnvironment = new PhysicalObject(PhysicalObjectType.BeachEnvironment, new Vector3f(0, 0, 0), new Vector3f(), 1);
            level.add(beachEnvironment);

            for (int i = 0; i < ModelMeshBankInR3.getNumberOfSubMeshes("/scenes/beach-scene.dae"); i++) {
                AABBHitbox aabbHitbox = new AABBHitbox(beachEnvironment, "/scenes/beach-scene.dae", i);
                level.addAABBHitbox(aabbHitbox);
            }

            float xDirection = (float) Math.cos(Math.toRadians(80));
            float yDirection = (float) Math.sin(Math.toRadians(80));
            VirtualDirectionalLight sun = new VirtualDirectionalLight(new Vector3f(xDirection, yDirection, 0), new Vector3f(1, 1, 1));
            level.addVirtualDirectionalLight(sun, false);

            level.setSkyboxDetails("sky gradient", false);

            Random r = new Random();

            for (int i = 0; i < 30; i++) {
                Vector3f position = new Vector3f(r.nextFloat() * 20 - 10, 50, r.nextFloat() * 20 - 10);
                MovableObject item = new MovableObject(level, PhysicalObjectType.Other, position, new Vector3f(), 0.5f);
//                item.addFallingAndGravityStateWithDefaultProperties();
                level.add(item);
                EllipsoidHitbox ellipsoid = new EllipsoidHitbox(item, EllipsoidDimensions.get(1, 1, 1));
                level.addEllipsoidHitbox(ellipsoid);
                SphereHitbox sphere = new SphereHitbox(item, 0.85f);
                level.addSphereHitbox(sphere);
            }

            for (int i = 0; i < 30; i++) {
                Vector3f position = new Vector3f(r.nextFloat() * 20 - 10, 100, r.nextFloat() * 20 - 10 + 110);
                MovableObject item = new MovableObject(level, PhysicalObjectType.Other, position, new Vector3f(), 0.5f);
//                item.addFallingAndGravityStateWithDefaultProperties();
                level.add(item);
                EllipsoidHitbox ellipsoid = new EllipsoidHitbox(item, EllipsoidDimensions.get(1, 1, 1));
                level.addEllipsoidHitbox(ellipsoid);
                SphereHitbox sphere = new SphereHitbox(item, 1.0f);
                level.addSphereHitbox(sphere);
            }

            itemsAdded = 0;
            level.addUpdatable(() -> {
                counter++;

                if (counter == 10) {
                    counter = 0;
                    itemsAdded++;
//                    Vector3f position = new Vector3f(0, 20, 110);
                    Vector3f position = new Vector3f(0 + r.nextFloat() * 0.01f, 20 + r.nextFloat() * 0.01f, 110 + r.nextFloat() * 0.01f);
                    MovableObject item = new MovableObject(level, PhysicalObjectType.Other, position, new Vector3f(), 0.5f);
                    level.add(item);
                    EllipsoidHitbox ellipsoid = new EllipsoidHitbox(item, EllipsoidDimensions.get(1, 1, 1));
                    level.addEllipsoidHitbox(ellipsoid);
                    SphereHitbox sphere = new SphereHitbox(item, 0.85f);
                    level.addSphereHitbox(sphere);
                }

                return itemsAdded >= 15;
            }, null);
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

            float xDirection = (float) Math.cos(Math.toRadians(80));
            float yDirection = (float) Math.sin(Math.toRadians(80));
            VirtualDirectionalLight sun = new VirtualDirectionalLight(new Vector3f(xDirection, yDirection, 0), new Vector3f(1, 1, 1));
            level.addVirtualDirectionalLight(sun, false);

            level.setSkyboxDetails("sky gradient", false);
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

    public static final Scene[] allScenes = { nothingScene, testScene, desertScene, beachScene, plainsScene };

}
