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

package org.apache.shardingsphere.integration.data.pipline.container.database;

import lombok.Getter;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.shardingsphere.infra.database.type.DatabaseType;
import org.apache.shardingsphere.test.integration.framework.container.atomic.DockerITContainer;
import org.testcontainers.containers.BindMode;

import java.util.LinkedList;
import java.util.List;

/**
 * Docker storage container.
 */
@Getter
public abstract class DockerDatabaseContainer extends DockerITContainer {
    
    private final DatabaseType databaseType;
    
    private final List<String> sourceDatabaseNames;
    
    private final List<String> targetDatabaseNames;
    
    public DockerDatabaseContainer(final DatabaseType databaseType, final String dockerImageName) {
        super(databaseType.getName().toLowerCase(), dockerImageName);
        this.databaseType = databaseType;
        sourceDatabaseNames = new LinkedList<>();
        targetDatabaseNames = new LinkedList<>();
    }
    
    @Override
    protected void configure() {
        withClasspathResourceMapping(String.format("/env/%s", databaseType.getName()), "/docker-entrypoint-initdb.d/", BindMode.READ_ONLY);
    }
    
    @Override
    protected void postStart() {
        sourceDatabaseNames.addAll(Lists.newArrayList("ds_0", "ds_1"));
        targetDatabaseNames.addAll(Lists.newArrayList("ds_2", "ds_3", "ds_4"));
    }
    
    /**
     * Get database port.
     *
     * @return database port
     */
    public abstract int getPort();
}
