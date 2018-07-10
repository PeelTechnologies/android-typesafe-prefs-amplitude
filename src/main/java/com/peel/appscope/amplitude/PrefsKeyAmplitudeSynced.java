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
package com.peel.appscope.amplitude;

import com.google.gson.reflect.TypeToken;
import com.peel.prefs.PrefsKey;

/**
 * A key of this type is kept automatically in sync with an Amplitude user property of the same name
 *
 * @author Inderjeet Singh
 */
public class PrefsKeyAmplitudeSynced<T> extends PrefsKey<T> {

    public PrefsKeyAmplitudeSynced(String name, Class<T> clazz) {
        super(name, clazz);
    }

    public PrefsKeyAmplitudeSynced(String name, Class<T> type, boolean cacheableInMemory) {
        super(name, type, cacheableInMemory);
    }

    public PrefsKeyAmplitudeSynced(String name, TypeToken<T> type) {
        super(name, type);
    }

    public PrefsKeyAmplitudeSynced(String name, TypeToken<T> type, boolean cacheableInMemory) {
        super(name, type, cacheableInMemory);
    }
}
