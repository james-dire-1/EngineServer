package newStuff;

import java.util.*;

public class PlayerColors {

    private static final int MAX_COLOR_VALUE = (int) Math.pow(2, 24);

    private static final Random r = new Random();
    private static final Map<Integer, Integer> unavailablePlayerSlotsToColorsMap = new HashMap<>();
    private static final int[] predefinedColors = {
            0xf7bf23, // orange
            0x4397f7, // blue
            0x16c970, // green
            0xff6b6b, // red
            0xf8ff2b, // yellow
            0xe485ff, // purple
            0x9c6135, // brown
            0xff9ce0 // pink
    };

    public static int getNextAvailableColor() {
        int currentSlot = -1;
        boolean foundAvailableSlot = false;

        while (!foundAvailableSlot) {
            currentSlot++;

            if (!unavailablePlayerSlotsToColorsMap.containsKey(currentSlot)) {
                foundAvailableSlot = true;
            }
        }

        int color;
        if (currentSlot < predefinedColors.length) {
            color = predefinedColors[currentSlot];
        } else {
            r.setSeed(currentSlot * 1234L);
            color = r.nextInt(MAX_COLOR_VALUE);
        }

        unavailablePlayerSlotsToColorsMap.put(currentSlot, color);
        return color;
    }

    public static void freeColor(int colorToFree) {
        Integer slotToRemove = null;

        for (Map.Entry<Integer, Integer> entry : unavailablePlayerSlotsToColorsMap.entrySet()) {
            int entryColor = entry.getValue();

            if (entryColor == colorToFree) {
                slotToRemove = entry.getKey();
                break;
            }
        }

        if (slotToRemove != null) {
            unavailablePlayerSlotsToColorsMap.remove(slotToRemove);
        }
    }

}
