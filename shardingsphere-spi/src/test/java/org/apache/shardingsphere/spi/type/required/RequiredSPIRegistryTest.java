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

package org.apache.shardingsphere.spi.type.required;

import org.apache.shardingsphere.spi.ShardingSphereServiceLoader;
import org.apache.shardingsphere.spi.exception.ServiceProviderNotFoundException;
import org.apache.shardingsphere.spi.type.required.fixture.empty.EmptyRequiredSPIFixture;
import org.apache.shardingsphere.spi.type.required.fixture.multiple.DefaultMultipleRequiredSPIFixtureImpl;
import org.apache.shardingsphere.spi.type.required.fixture.multiple.MultipleRequiredSPIFixture;
import org.apache.shardingsphere.spi.type.required.fixture.single.SingleRequiredSPIFixture;
import org.apache.shardingsphere.spi.type.required.fixture.single.SingleRequiredSPIFixtureImpl;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public final class RequiredSPIRegistryTest {
    
    static {
        ShardingSphereServiceLoader.register(EmptyRequiredSPIFixture.class);
        ShardingSphereServiceLoader.register(SingleRequiredSPIFixture.class);
        ShardingSphereServiceLoader.register(MultipleRequiredSPIFixture.class);
    }
    
    @Test(expected = ServiceProviderNotFoundException.class)
    public void assertRegisteredServiceWithEmptyImplementation() {
        RequiredSPIRegistry.getRegisteredService(EmptyRequiredSPIFixture.class);
    }
    
    @Test
    public void assertRegisteredServiceWithOneImplementation() {
        assertTrue(RequiredSPIRegistry.getRegisteredService(SingleRequiredSPIFixture.class) instanceof SingleRequiredSPIFixtureImpl);
    }
    
    @Test
    public void assertRegisteredServiceWithMoreImplementations() {
        assertTrue(RequiredSPIRegistry.getRegisteredService(MultipleRequiredSPIFixture.class) instanceof DefaultMultipleRequiredSPIFixtureImpl);
    }
}
