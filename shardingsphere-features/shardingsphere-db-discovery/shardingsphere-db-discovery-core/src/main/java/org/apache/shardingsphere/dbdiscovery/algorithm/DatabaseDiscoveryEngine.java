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

package org.apache.shardingsphere.dbdiscovery.algorithm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.dbdiscovery.spi.DatabaseDiscoveryProviderAlgorithm;
import org.apache.shardingsphere.infra.eventbus.ShardingSphereEventBus;
import org.apache.shardingsphere.infra.metadata.schema.QualifiedDatabase;
import org.apache.shardingsphere.infra.rule.event.impl.DataSourceDisabledEvent;
import org.apache.shardingsphere.infra.rule.event.impl.PrimaryDataSourceChangedEvent;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Database discovery engine.
 */
@RequiredArgsConstructor
@Slf4j
public final class DatabaseDiscoveryEngine {
    
    private final DatabaseDiscoveryProviderAlgorithm databaseDiscoveryProviderAlgorithm;
    
    /**
     * Check environment of database cluster.
     *
     * @param databaseName database name
     * @param dataSourceMap data source map
     * @throws SQLException SQL exception
     */
    public void checkEnvironment(final String databaseName, final Map<String, DataSource> dataSourceMap) throws SQLException {
        for (Entry<String, DataSource> entry : dataSourceMap.entrySet()) {
            // TODO query with multiple threads
            databaseDiscoveryProviderAlgorithm.checkEnvironment(databaseName, entry.getValue());
        }
    }
    
    /**
     * Change primary data source.
     *
     * @param databaseName database name
     * @param groupName group name
     * @param originalPrimaryDataSourceName original primary data source name
     * @param dataSourceMap data source map
     * @param disabledDataSourceNames disabled data source names
     * @return changed primary data source name
     */
    public String changePrimaryDataSource(final String databaseName, final String groupName, final String originalPrimaryDataSourceName,
                                          final Map<String, DataSource> dataSourceMap, final Collection<String> disabledDataSourceNames) {
        Optional<String> newPrimaryDataSourceName = findPrimaryDataSourceName(dataSourceMap, disabledDataSourceNames);
        if (newPrimaryDataSourceName.isPresent() && !newPrimaryDataSourceName.get().equals(originalPrimaryDataSourceName)) {
            ShardingSphereEventBus.getInstance().post(new PrimaryDataSourceChangedEvent(new QualifiedDatabase(databaseName, groupName, newPrimaryDataSourceName.get())));
        }
        String result = newPrimaryDataSourceName.orElse(originalPrimaryDataSourceName);
        postReplicaDataSourceDisabledEvent(databaseName, groupName, result, dataSourceMap);
        return result;
    }
    
    private Optional<String> findPrimaryDataSourceName(final Map<String, DataSource> dataSourceMap, final Collection<String> disabledDataSourceNames) {
        for (Entry<String, DataSource> entry : getActiveDataSourceMap(dataSourceMap, disabledDataSourceNames).entrySet()) {
            try {
                if (databaseDiscoveryProviderAlgorithm.isPrimaryInstance(entry.getValue())) {
                    return Optional.of(entry.getKey());
                }
            } catch (final SQLException ex) {
                log.error("An exception occurred while judge primary data source: ", ex);
            }
        }
        return Optional.empty();
    }
    
    private Map<String, DataSource> getActiveDataSourceMap(final Map<String, DataSource> dataSourceMap, final Collection<String> disabledDataSourceNames) {
        Map<String, DataSource> result = new HashMap<>(dataSourceMap);
        if (!disabledDataSourceNames.isEmpty()) {
            result.entrySet().removeIf(each -> disabledDataSourceNames.contains(each.getKey()));
        }
        return result;
    }
    
    private void postReplicaDataSourceDisabledEvent(final String databaseName, final String groupName, final String primaryDataSourceName, final Map<String, DataSource> dataSourceMap) {
        for (Entry<String, DataSource> entry : dataSourceMap.entrySet()) {
            if (!entry.getKey().equals(primaryDataSourceName)) {
                ShardingSphereEventBus.getInstance().post(
                        new DataSourceDisabledEvent(databaseName, groupName, entry.getKey(), databaseDiscoveryProviderAlgorithm.getStorageNodeDataSource(entry.getValue())));
            }
        }
    }
}
