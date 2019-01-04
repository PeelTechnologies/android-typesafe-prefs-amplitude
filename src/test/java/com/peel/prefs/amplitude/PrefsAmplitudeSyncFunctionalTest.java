/*
 * Copyright (C) 2018 Peel Technologies Inc.
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
package com.peel.prefs.amplitude;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amplitude.api.Amplitude;
import com.amplitude.api.AmplitudeClient;
import com.google.gson.Gson;
import com.peel.prefs.Prefs;
import com.peel.prefs.TypedKey;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Functional tests to validate that {@code PrefsKeyAmplitudeSynced} properties get synced
 * correctly with amplitude user properties.
 *
 * @author Inderjeet Singh
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, SharedPreferences.class, PreferenceManager.class, Amplitude.class})
public class PrefsAmplitudeSyncFunctionalTest {

    private static final String AMPLITUDE_SYNCED = "amplitudeSynced";
    private Prefs prefs;
    private AmplitudeClient amplitudeClient;
    private JSONObject userProperties;

    @Before
    public void setUp() throws Exception {
        Context context = AndroidFixtures.createMockContext();
        Gson gson = new Gson();
        prefs = new Prefs(context, gson);
        amplitudeClient = Mockito.mock(AmplitudeClient.class);
        Mockito.doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                userProperties = (JSONObject) args[0]; // Just set userProperties
                return null;
            }
        }).when(amplitudeClient).setUserProperties(Mockito.any(JSONObject.class));
        prefs.addListener(new PrefsAmplitudeSyncListener(amplitudeClient, gson, AMPLITUDE_SYNCED));
    }

    @Test
    public void testAmplitudeSync() throws Exception {
        TypedKey<String> sync = new TypedKey<>("sync", String.class, AMPLITUDE_SYNCED);
        prefs.put(sync, "test");
        assertEquals("test", userProperties.get("sync"));

        // assert that amplitude sync doesn't happen for non-synced properties
        TypedKey<String> nosync = new TypedKey<>("nosync", String.class);
        prefs.put(nosync, "test");
        assertFalse(userProperties.has("nosync"));
    }

    @Test
    public void testAmplitudeUnsetOnBooleanPropertyRemoval() throws Exception {
        TypedKey<Boolean> bool = new TypedKey<>("bool", Boolean.class, AMPLITUDE_SYNCED);
        prefs.put(bool, true);
        assertTrue(userProperties.getBoolean("bool"));

        prefs.remove(bool);
        assertFalse(userProperties.getBoolean("bool"));
    }

    @SuppressWarnings("unused")
    private static class Bag {
        final int apples;
        final int oranges;
        Bag(int apples, int oranges) {
            this.apples = apples;
            this.oranges = oranges;
        }
    }
    @Test
    public void testAmplitudeSetObjectProperty() throws Exception {
        TypedKey<Bag> bag = new TypedKey<>("bag", Bag.class, AMPLITUDE_SYNCED);
        prefs.put(bag, new Bag(2, 3));
        assertEquals("{\"apples\":2,\"oranges\":3}", userProperties.get("bag"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testAmplitudeSetIntProperty() throws Exception {
        TypedKey<Integer> key = new TypedKey<>("key", Integer.class, AMPLITUDE_SYNCED);
        prefs.put(key, 3);
        assertEquals(3, userProperties.get("key"));

        TypedKey key2 = new TypedKey<>("key2", int.class, AMPLITUDE_SYNCED);
        prefs.put(key2, 4);
        assertEquals(4, userProperties.get("key2"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testAmplitudeSetShortProperty() throws Exception {
        TypedKey<Short> key = new TypedKey<>("key", Short.class, AMPLITUDE_SYNCED);
        prefs.put(key, (short)3);
        assertEquals((short)3, userProperties.get("key"));

        TypedKey key2 = new TypedKey<>("key2", short.class, AMPLITUDE_SYNCED);
        prefs.put(key2, (short)4);
        assertEquals((short)4, userProperties.get("key2"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testAmplitudeSetLongProperty() throws Exception {
        TypedKey<Long> key = new TypedKey<>("key", Long.class, AMPLITUDE_SYNCED);
        prefs.put(key, (long)3);
        assertEquals((long)3, userProperties.get("key"));

        TypedKey key2 = new TypedKey<>("key2", long.class, AMPLITUDE_SYNCED);
        prefs.put(key2, (long)4);
        assertEquals((long)4, userProperties.get("key2"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testAmplitudeSetByteProperty() throws Exception {
        TypedKey<Byte> key = new TypedKey<>("key", Byte.class, AMPLITUDE_SYNCED);
        prefs.put(key, (byte)3);
        assertEquals((byte)3, userProperties.get("key"));

        TypedKey key2 = new TypedKey<>("key2", byte.class, AMPLITUDE_SYNCED);
        prefs.put(key2, (byte)4);
        assertEquals((byte)4, userProperties.get("key2"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testAmplitudeSetCharacterProperty() throws Exception {
        TypedKey<Character> key = new TypedKey<>("key", Character.class, AMPLITUDE_SYNCED);
        prefs.put(key, 'a');
        assertEquals('a', userProperties.get("key"));

        TypedKey key2 = new TypedKey<>("key2", char.class, AMPLITUDE_SYNCED);
        prefs.put(key2, 'b');
        assertEquals('b', userProperties.get("key2"));
    }

    @Test
    public void testAmplitudeSetStringProperty() throws Exception {
        TypedKey<String> key = new TypedKey<>("key", String.class, AMPLITUDE_SYNCED);
        prefs.put(key, "abracadabra");
        assertEquals("abracadabra", userProperties.get("key"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testAmplitudeSetBooleanProperty() throws Exception {
        TypedKey<Boolean> key = new TypedKey<>("key", Boolean.class, AMPLITUDE_SYNCED);
        prefs.put(key, true);
        assertEquals(true, userProperties.get("key"));

        TypedKey key2 = new TypedKey<>("key2", boolean.class, AMPLITUDE_SYNCED);
        prefs.put(key2, true);
        assertEquals(true, userProperties.get("key2"));
    }

    @Test
    public void testAmplitudeSetFloatProperty() throws Exception {
        TypedKey<Float> key = new TypedKey<>("key", Float.class, AMPLITUDE_SYNCED);
        prefs.put(key, 1.23F);
        assertEquals(1.23F, userProperties.get("key"));
    }

    @Test
    public void testAmplitudeSetDoubleProperty() throws Exception {
        TypedKey<Double> key = new TypedKey<>("key", Double.class, AMPLITUDE_SYNCED);
        prefs.put(key, 1.233D);
        assertEquals(1.233D, userProperties.get("key"));
    }
}
