package com.james.common.tools;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Mth {

    public static Vector3f multiply(Vector3f vector, float a) {
        return new Vector3f(vector.x * a, vector.y * a, vector.z * a);
    }

    public static Vector2f multiply(Vector2f vector, float a) {
        return new Vector2f(vector.x * a, vector.y * a);
    }

    private static float pythagoreanTheorem(float a, float b) {
        return (float) Math.sqrt(a * a + b * b);
    }

    public static float distance(Vector3f posA, Vector3f posB) {
        float xAndZDistance = pythagoreanTheorem(posA.x - posB.x, posA.z - posB.z);
        return pythagoreanTheorem(xAndZDistance, posA.y - posB.y);
    }

    public static float squaredDistance(Vector3f posA, Vector3f posB) {
        float xAndZDistance = pythagoreanTheorem(posA.x - posB.x, posA.z - posB.z);
        return xAndZDistance * xAndZDistance + (posA.y - posB.y) * (posA.y - posB.y);
    }

    public static boolean pointInTriangle(Vector2f a, Vector2f b, Vector2f c, Vector2f p) {
        float w1Numerator = a.x * (c.y - a.y) + (p.y - a.y) * (c.x - a.x) - p.x * (c.y - a.y);
        float w1Denominator = (b.y - a.y) * (c.x - a.x) - (b.x - a.x) * (c.y - a.y);
        float w1 = w1Numerator / w1Denominator;

        float w2Numerator = p.y - a.y - w1 * (b.y - a.y);
        float w2Denominator = c.y - a.y;
        float w2 = w2Numerator / w2Denominator;

        return w1 >= 0 && w2 >= 0 && w1+w2 <= 1;
    }

}
