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

import java.util.Collection;
import java.util.Date;

import javax.cache.Cache.Entry;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.examples.ExampleNodeStartup;
// import org.apache.ignite.examples.model.Person; // moved into this package

/**
 * Demonstrates broadcasting computations within cluster.
 * <p>
 * Remote nodes should always be started with special configuration file which
 * enables P2P class loading: {@code 'ignite.{sh|bat} examples/config/example-ignite.xml'}.
 * <p>
 * Alternatively you can run {@link ExampleNodeStartup} in another JVM which will start node
 * with {@code examples/config/example-ignite.xml} configuration.
 */
public class a2ClientCacheStatsBcst {
    /**
     * Executes example.
     *
     * @param args Command line arguments, none required.
     * @throws IgniteException If example execution failed.
     */
    public static void main(String[] args) throws IgniteException {
         	
          	long timestamp = new Date().getTime();
        	

        	IgniteConfiguration cfg = new IgniteConfiguration();
        	
        	String INSTANCE_NAME = "ClientCacheStatsBroadcast-"+ Long.toString(timestamp);
        	String PERSON_CACHE_NAME = "PersonCache" ;
        	
        	Ignition.setClientMode(true);
        	Ignite ignite = Ignition.start(cfg);
        
        	
        	cfg.setIgniteInstanceName(INSTANCE_NAME);

            
            cachePartitionInfo(ignite, PERSON_CACHE_NAME);
            System.out.println(">>> Check Each Node console for info on local node Partitions list ...");

            showKeysFromLocalPartition(ignite, PERSON_CACHE_NAME);
            System.out.println(">>> Check Each Node console for info on local node Keys  list ...");

            
         
  //          hello(ignite);
    //        gatherSystemInfo(ignite);
     
            
            
    }

    private static void cachePartitionInfo(Ignite ignite, String cachename) throws IgniteException {
    
    	ignite.compute().broadcast(() -> {
           
    		IgniteCache<Long, Person> cache = ignite.getOrCreateCache(cachename);
    
    		
            System.out.println("Cache Name =" + cachename 
            				+ "partition Count and List from this node id if present: " 
            		+ ignite.cluster().localNode().id());
            
            int[] partitions; 
            partitions = ignite.affinity(cachename).allPartitions(ignite.cluster().localNode());
            System.out.print( "Total # partitions for this node = " + partitions.length + " [");
         	for (int part : partitions)      		
        		System.out.print(part + ",");
         	System.out.print("]\n");
        	
        	
        	
        	System.out.println("Data Region metrics size : " + ignite.dataRegionMetrics().size());
        
            
        });
    
    
    }
  
    private static void showKeysFromLocalPartition(Ignite ignite, String cachename) throws IgniteException {
        
    	ignite.compute().broadcast(() -> {
           
            System.out.println("BROADCAST list keys from each partition  {" + cachename 
            		+ "} in this Node id: " 	+ ignite.cluster().localNode().id());
            
           
            IgniteCache<Long, Person> personCache = ignite.getOrCreateCache(cachename);
            
            int[] partitions = ignite.affinity(cachename).allPartitions(ignite.cluster().localNode());
            
            System.out.println( "Node ID=" + ignite.cluster().localNode().id() + " has # Partitions=" + partitions.length);
            ScanQuery<Long, Person> filter1 = new ScanQuery<>();
            
       	 
            for (int i=0; i<partitions.length; i++)
            {
            	System.out.print("{" + partitions[i] + "}");
            	filter1.setPartition(partitions[i]);
            	QueryCursor<Entry<Long, Person>> qryCursor = personCache.query(filter1); 
          		qryCursor.forEach(
          		            entry -> System.out.println(      
          		            		"Primary?="   // true otherwise False if backup partition 
          		            					+  ignite.affinity(cachename).isPrimaryOrBackup(ignite.cluster().localNode(), entry.getKey())
          		            	+ "key=" + entry.getKey() 
          		            	+ ",val=" + personCache.get(entry.getKey())  )
          				);
            	//personCache.query((filter1)).getAll();
            	//QueryCursor<Entry<Long,Person>> qrycursor = personCache.query(filter1);
            	//System.out.println("keys=" + qrycursor.toString());
            }
        });
    
    
    }
  
  
  
    private static void hello(Ignite ignite) throws IgniteException {
        // Print out hello message on all nodes.
        ignite.compute().broadcast(() -> {
            System.out.println();
            System.out.println(">>> Hello Node! :)");
        });

        System.out.println();
        System.out.println(">>> Check all nodes for hello message output.");
    }

 
    private static void gatherSystemInfo(Ignite ignite) throws IgniteException {
        // Gather system info from all nodes.
        Collection<String> res = ignite.compute().broadcast(() -> {
            System.out.println();
            System.out.println("Executing task on node: " + ignite.cluster().localNode().id());

            return "Node ID: " + ignite.cluster().localNode().id() + "\n" +
                "OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " " +
                System.getProperty("os.arch") + "\n" +
                "User: " + System.getProperty("user.name") + "\n" +
                "JRE: " + System.getProperty("java.runtime.name") + " " +
                System.getProperty("java.runtime.version");
        });

        // Print result.
        System.out.println();
        System.out.println("Nodes system information:");
        System.out.println();

        res.forEach(r -> {
            System.out.println(r);
            System.out.println();
        });
    }
}