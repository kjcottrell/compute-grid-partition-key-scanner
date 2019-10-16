/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package z.tests.colocatedComputeDataExample;



import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.*;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.*;
import org.apache.ignite.examples.ExampleNodeStartup;
import org.apache.ignite.examples.ExamplesUtils;
import org.apache.ignite.lifecycle.*;

import java.util.Calendar;
import java.util.Date;
import java.time.ZonedDateTime;



/**
 * Starts up an empty node with example compute configuration.
 */
public class b3ServerNode {
    /**
     * Start up an empty node with example compute configuration.
     *
     * @param args Command line arguments, none required.
     * @throws IgniteException If failed.
     */
    public static void main(String[] args) throws IgniteException {
       	
    	String PERSON_CACHE_NAME = "PersonCache" ;
    	final long timestamp = new Date().getTime();

    	IgniteConfiguration cfg = new IgniteConfiguration();
    	cfg.setIgniteInstanceName("B3-"+ Long.toString(timestamp));
    	
    	
 //   	Ignite ignite = Ignition.start("examples/config/example-ignite.xml");
    	Ignite ignite = Ignition.start(cfg);
        IgniteCluster cluster = ignite.cluster();
        
        
        System.out.println("instance name: " + cfg.getIgniteInstanceName() + ", node id: " + cluster.localNode().id());
      /*
        int[] partitions; 
        partitions = ignite.affinity(PERSON_CACHE_NAME).allPartitions(cluster.localNode());
        System.out.print( " # partitions = " + partitions.length + " [");
     	for (int part : partitions)       		
    		System.out.print(part + ",");
    	System.out.print("]\n");
      */
    }
    
   
}
    
    

