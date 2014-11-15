/*
 * Copyright 2014 Higher Frequency Trading
 *
 * http://www.higherfrequencytrading.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.chronicle.map;

import net.openhft.lang.values.LongValue;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static net.openhft.chronicle.map.Alignment.OF_4_BYTES;
import static net.openhft.lang.model.DataValueClasses.newDirectReference;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class EntryCountMapTest {
    double score = 0;
    int scoreCount = 0;

    static File getPersistenceFile() {
        String TMP = System.getProperty("java.io.tmpdir");
        File file = new File(TMP + "/small-chm-test" + System.nanoTime());
        file.deleteOnExit();
        return file;
    }

    private static ChronicleMap<CharSequence, LongValue> getSharedMap(
            long entries, int segments, int entrySize) throws IOException {
        return getSharedMap(entries, segments, entrySize, OF_4_BYTES);
    }

    private static ChronicleMap<CharSequence, LongValue> getSharedMap(
            long entries, int segments, int entrySize, Alignment alignment)
            throws IOException {
        ChronicleMapBuilder<CharSequence, LongValue> mapBuilder = ChronicleMapBuilder.of(CharSequence
                .class, LongValue.class)
                .entries(entries)
                .actualSegments(segments)
                .entrySize(entrySize)
                .entryAndValueAlignment(alignment);
        return mapBuilder.createPersistedTo(getPersistenceFile());
    }

    @Test
    public void testVerySmall() throws Exception {
        int s = 1, i = 1;
        // regression test.
        testEntriesMaxSize(s, 1, 1, i);
        testEntriesMaxSize(s, 2, 4, i);
        testEntriesMaxSize(s, 4, 6, i);
        testEntriesMaxSize(s, 8, 13, i);
        testEntriesMaxSize(s, 16, 55, i); // todo fix the max.
        testEntriesMaxSize(s, 32, 82, i); // todo fix the max.
        testEntriesMaxSize(s, 64, 128, i);
        testEntriesMaxSize(s, 128, 256, i);
    }

    @Test
    public void testSmall() throws Exception {
        for (int i = 0; i < 5; i++) {
            // regression test.
            for (int s : new int[]{1, 2, 4, 8}) {
                testEntriesMaxSize(s, 256, 510, i);
                testEntriesMaxSize(s, 512, 1010, i);
                testEntriesMaxSize(s, 1000, 1730, i);
                testEntriesMaxSize(s, 2000, 2800, i);
                testEntriesMaxSize(s, 4000, 5300, i);
                testEntriesMaxSize(s, 5000, 6800, i);
                testEntriesMaxSize(s, 8000, 10600, i);
                testEntriesMaxSize(s, 12000, 16000, i);
                testEntriesMaxSize(s, 16000, 21300, i);
            }
        }
        // hyperbolic average gives more weight to small numbers.
        System.out.printf("Score: %.2f%n", scoreCount / score);
    }

    @Test
    public void testMedium() throws Exception {
        for (int i = 0; i < 2; i++) {
            // regression test.
            int s = 16;
            testEntriesMaxSize(s, 512, 1000, i);
            testEntriesMaxSize(s, 1000, 2100, i);
            testEntriesMaxSize(s, 2000, 3100, i);
            testEntriesMaxSize(s, 4000, 5100, i);
            testEntriesMaxSize(s, 5000, 7100, i);
            testEntriesMaxSize(s, 8000, 11000, i);
            testEntriesMaxSize(s, 12000, 17000, i);
            testEntriesMaxSize(s, 16000, 22000, i);
            s = 32;
            testEntriesMaxSize(s, 2000, 3900, i);
            testEntriesMaxSize(s, 4000, 5900, i);
            testEntriesMaxSize(s, 5000, 7900, i);
            testEntriesMaxSize(s, 8000, 12000, i);
            testEntriesMaxSize(s, 12000, 17000, i);
            testEntriesMaxSize(s, 16000, 23000, i);
            testEntriesMaxSize(s, 32000, 46000, i);
            s = 64;
            testEntriesMaxSize(s, 5000, 7550, i);
            testEntriesMaxSize(s, 8000, 11400, i);
            testEntriesMaxSize(s, 12000, 18000, i);
            testEntriesMaxSize(s, 16000, 24000, i);
            testEntriesMaxSize(s, 32000, 47000, i);
            testEntriesMaxSize(s, 40000, 60000, i);
            s = 128;
            testEntriesMaxSize(s, 16000, 24000, i);
            testEntriesMaxSize(s, 32000, 50000, i);
            testEntriesMaxSize(s, 40000, 62000, i);
            testEntriesMaxSize(s, 64000, 98000, i);
            testEntriesMaxSize(s, 129000, 204000, i);
            s = 256;
            testEntriesMaxSize(s, 64000, 100000, i);
            testEntriesMaxSize(s, 129000, 213000, i);
        }
        // hyperbolic average gives more weight to small numbers.
        System.out.printf("Score: %.2f%n", scoreCount / score);
    }

    @Test
    @Ignore
    public void testLarge() throws Exception {
        int s = 8 * 1024;
        for (int i = 0; i < 10; i++) {
            System.out.println("i:" + i);
            int entries = s * s;
            testEntriesMaxSize(s, entries * 4 / 5, entries * 4 / 3, i);
//            testEntriesMaxSize(s, entries + 100, entries * 3 / 2, i);
            entries *= 2;
            testEntriesMaxSize(s, entries * 4 / 5, entries * 4 / 3, i);
//            testEntriesMaxSize(s, entries + 100, entries * 3 / 2, i);
        }
        // hyperbolic average gives more weight to small numbers.
        System.out.printf("Score: %.2f%n", scoreCount / score);
    }

    private void testEntriesMaxSize(int segments, int minSize, int maxSize, int seed) throws IOException {
        int counter = minSize + new Random(seed + segments * 1000).nextInt(9999);
        ChronicleMap<CharSequence, LongValue> map = getSharedMap(minSize, segments, 32);
        LongValue longValue = newDirectReference(LongValue.class);
        try {
            for (int j = 0; j < maxSize * 12 / 10 + 300; j++) {
                String key = "key:" + counter++;
                // give a biased hashcode distribution.
                if ((Integer.bitCount(key.hashCode()) & 7) != 0) {
                    j--;
                    continue;
                }
                map.acquireUsing(key, longValue);
                longValue.setValue(1);
            }
            long[] a = ((VanillaChronicleMap) map).segmentSizes();
            System.out.println("segs: " + segments + " min: " + minSize + " was " + map.size() + " "
                    + Arrays.toString(a)
                    + " sum: " + sum(a));
            fail("Expected the map to be full.");
        } catch (IllegalStateException e) {
            // calculate the hyperbolic average.
            score += (double) minSize / map.size();
            scoreCount++;
            boolean condition = minSize <= map.size() && map.size() <= maxSize;
            if (!condition) {
                long[] a = ((VanillaChronicleMap) map).segmentSizes();
                System.out.println("segs: " + segments + " min: " + minSize + " was " + map.size() + " "
                        + Arrays.toString(a)
                        + " sum: " + sum(a));
                assertTrue("seg: " + segments + ", min: " + minSize + ", size: " + map.size(), condition);
            }
        } finally {
            map.close();
        }
    }

    private long sum(long[] longs) {
        long sum = 0;
        for (long i : longs) {
            sum += i;
        }
        return sum;
    }


}
