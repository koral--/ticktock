/*
 * Copyright (C) 2020 Zac Sweers & Gabriel Ittner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.zacsweers.ticktock.runtime;

import java.time.ZoneId;
import java.time.zone.ZoneRulesProvider;
import java.util.Set;

/** Utilities for eager zone rule caching. */
public final class EagerZoneRulesLoading {

  /**
   * Call on background thread to eagerly load all zones. Starts with loading {@link
   * ZoneId#systemDefault()} which is the one most likely to be used.
   */
  public static void cacheZones() {
    try {
      ZoneId.systemDefault().getRules();
      Set<String> zoneIds = ZoneRulesProvider.getAvailableZoneIds();
      if (zoneIds.isEmpty()) {
        throw new IllegalStateException("No zone ids available!");
      }
      for (String zoneId : zoneIds) {
        ZoneRulesProvider.getRules(zoneId, true);
      }
    } catch (NoSuchMethodError e) {
      // If targeting a newer Android device or minSdk 26, this will fail because ZoneRulesProvider
      // is a strangely hidden API: https://issuetracker.google.com/issues/159421054
      System.err.println("Could not eagerly initialize zone rules: " + e.getMessage());
    }
  }
}