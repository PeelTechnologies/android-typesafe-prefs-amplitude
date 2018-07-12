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
import com.peel.prefs.PrefsKey;
import com.peel.prefs.amplitude.PrefsAmplitudeSyncListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Functional tests to validate that {@code PrefsKeyAmplitudeSynced} properties get synced correctly with amplitude user properties.
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
        prefs = new Prefs(context, new Gson());
        amplitudeClient = Mockito.mock(AmplitudeClient.class);
        Mockito.doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                userProperties = (JSONObject) args[0]; // Just set userProperties
                return null;
            }
        }).when(amplitudeClient).setUserProperties(Mockito.any(JSONObject.class));
        prefs.addListener(new PrefsAmplitudeSyncListener(amplitudeClient, AMPLITUDE_SYNCED));
    }

    @Test
    public void testAmplitudeSync() throws Exception {
        PrefsKey<String> sync = new PrefsKey<>("sync", String.class, AMPLITUDE_SYNCED);
        prefs.put(sync, "test");
        assertEquals("test", userProperties.get("sync"));

        // assert that amplitude sync doesn't happen for non-synced properties
        PrefsKey<String> nosync = new PrefsKey<>("nosync", String.class);
        prefs.put(nosync, "test");
        assertFalse(userProperties.has("nosync"));
    }

    @Test
    public void testAmplitudeUnsetOnBooleanPropertyRemoval() throws Exception {
        PrefsKey<Boolean> bool = new PrefsKey<>("bool", Boolean.class, AMPLITUDE_SYNCED);
        prefs.put(bool, true);
        assertTrue(userProperties.getBoolean("bool"));

        prefs.remove(bool);
        assertFalse(userProperties.getBoolean("bool"));
    }
}
