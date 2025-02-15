/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.mode.manager.cluster.coordinator.lock.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Lock node util.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LockNodeUtil {
    
    private static final String LOCK_DELIMITER = "-";
    
    /**
     * Generate database ack lock name.
     *
     * @param database database name
     * @param instanceId instance id
     * @return ack lock name
     */
    public static String generateDatabaseLockName(final String database, final String instanceId) {
        return database + LOCK_DELIMITER + instanceId;
    }
    
    /**
     * Parse database lock name.
     *
     * @param lockedName locked name
     * @return string array of database name and instance id
     */
    public static String[] parseDatabaseLockName(final String lockedName) {
        return lockedName.trim().split(LOCK_DELIMITER);
    }
}
