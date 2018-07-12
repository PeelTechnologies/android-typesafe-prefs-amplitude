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

import org.json.JSONObject;

import com.amplitude.api.Amplitude;
import com.amplitude.api.AmplitudeClient;
import com.peel.prefs.Prefs;
import com.peel.prefs.PrefsKey;

/**
 * Bind this listener to {@code Prefs#addListener(Prefs.EventListener)} to automatically sync all
 * properties of type {@code PrefsKeyAmplitudeSynced} as Amplitude user properties
 *
 * @author Inderjeet Singh
 */
public class PrefsAmplitudeSyncListener implements Prefs.EventListener {
    private final AmplitudeClient amplitudeClient;
    private final String amplitudeTag;

    public PrefsAmplitudeSyncListener() {
        this(Amplitude.getInstance(), "amplitude");
    }

    public PrefsAmplitudeSyncListener(AmplitudeClient client, String amplitudeTag) {
        this.amplitudeClient = client;
        this.amplitudeTag = amplitudeTag;
    }

    @Override
    public <T> void onPut(PrefsKey<T> key, T value) {
        if (key.containsTag(amplitudeTag)) {
            try {
                JSONObject props = new JSONObject();
                props.put(key.getName(), value);
                amplitudeClient.setUserProperties(props);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public <T> void onRemove(PrefsKey<T> key) {
        try {
            if (key.containsTag(amplitudeTag) && key.getTypeOfValue() == Boolean.class) {
                // Only for boolean keys, set them to false in Amplitude
                JSONObject props = new JSONObject();
                props.put(key.getName(), false);
                amplitudeClient.setUserProperties(props);
            }
        } catch (Exception ignored) {
        }
    }
}
