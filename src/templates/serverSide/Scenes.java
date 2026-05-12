package templates.serverSide;

import com.james.common.simulation.collisionEngine.prep.ModelMeshBankInR3;
import templates.common.simulation.objects.PhysicalObjectType;
import com.james.serverSide.simulation.Level;
import com.james.serverSide.simulation.collisionEngine.hitboxes.AABBHitbox;
import com.james.serverSide.simulation.objects.MovableObject;
import com.james.serverSide.simulation.objects.PhysicalObject;
import org.lwjgl.util.vector.Vector3f;

public class Scenes {

    public static void nothingScene(Level level) {
    }

    public static void testScene(Level level) {
        for (int i = 0; i < 10; i++) {
            PhysicalObject wall = new PhysicalObject(PhysicalObjectType.Wall, new Vector3f(-i*2, 0, -10), new Vector3f(0, 0, 0), 1);
            level.add(wall);
        }

        PhysicalObject testEnvironment = new PhysicalObject(PhysicalObjectType.TestEnvironment, new Vector3f(), new Vector3f(), 1);
        level.add(testEnvironment);

        AABBHitbox aabbHitbox = new AABBHitbox(testEnvironment, "/test-environment.dae");
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

    public static void desertScene(Level level) {
        PhysicalObject desertEnvironment = new PhysicalObject(PhysicalObjectType.DesertEnvironment, new Vector3f(0, -100, 0), new Vector3f(), 1);
        level.add(desertEnvironment);

        for (int i = 0; i < ModelMeshBankInR3.getNumberOfSubMeshes("/desert-2.dae"); i++) {
            AABBHitbox aabbHitbox = new AABBHitbox(desertEnvironment, "/desert-2.dae", i);
            level.addAABBHitbox(aabbHitbox);
        }
    }

    public static void beachScene(Level level) {
        PhysicalObject beachEnvironment = new PhysicalObject(PhysicalObjectType.BeachEnvironment, new Vector3f(0, -35, 0), new Vector3f(), 1);
        level.add(beachEnvironment);

        for (int i = 0; i < ModelMeshBankInR3.getNumberOfSubMeshes("/beach-scene.dae"); i++) {
            AABBHitbox aabbHitbox = new AABBHitbox(beachEnvironment, "/beach-scene.dae", i);
            level.addAABBHitbox(aabbHitbox);
        }
    }

}
