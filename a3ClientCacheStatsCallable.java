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

import java.util.*;
import javax.cache.Cache.Entry;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.examples.ExampleNodeStartup;
import org.apache.ignite.examples.model.Person;


public class a3ClientCacheStatsCallable {
 
    public static void main(String[] args) throws IgniteException {
         	
          	long timestamp = new Date().getTime();
        	

        	IgniteConfiguration cfg = new IgniteConfiguration();
        	
        	String INSTANCE_NAME = "ClientCacheStatsCallable-"+ Long.toString(timestamp);
        	String PERSON_CACHE_NAME = "PersonCache" ;
        	
        	Ignition.setClientMode(true);
        	Ignite ignite = Ignition.start(cfg);
        
        	
        	cfg.setIgniteInstanceName(INSTANCE_NAME);

            
            CollectPartitionInfoPerNode(ignite, PERSON_CACHE_NAME);
            System.out.println(">>> Check Each Node console for info on local node Partitions list ...");

            CollectKeysListEachPartition(ignite, PERSON_CACHE_NAME);
            System.out.println(">>> Check Each Node console for info on local node Keys  list ...");

            
     
     
            
            
    }

    private static void CollectPartitionInfoPerNode(Ignite ignite, String cachename) throws IgniteException {
    
    
    	// ignite.compute().run( () -> System.out.println("sample message -  this on all nodes"));
    	
    	Collection<IgniteCallable<int[]>> partitionsList = new ArrayList<>();
    	Collection<IgniteCallable<int[]>> keysList = new ArrayList<>();
    	Collection<IgniteCallable<Integer>> calls = new ArrayList<>();
    	
    	partitionsList.add(() -> {    		
            IgniteCache<Long, Person> cache = ignite.getOrCreateCache(cachename);    		
            System.out.println("Cache Name =" + cachename 
            				+ "partition Count and List from this node id if present: " 
            		+ ignite.cluster().localNode().id());
            
            int [] partitions; 
            partitions = ignite.affinity(cachename).allPartitions(ignite.cluster().localNode());
            System.out.print( "Total # partitions for this node = " + partitions.length + " [");
         	for (int part : partitions)      		
        		System.out.print(part + ",");
         	System.out.print("]\n");
        	
        	
        	        
            return partitions;
        });
    	
    	
    	keysList.add(() -> {

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
            
            int [] temp = {0,1,2};

            return temp;
        });
    	
        // Iterate through all words in the sentence and create callable jobs.
        for (String word : "Count characters using callable".split(" ")) {
            calls.add(() -> {
                System.out.println();
                System.out.println(">>> Printing '" + word + "' on this node from ignite job.");

                return word.length();
            });
        }

        // Execute collection of callables on the ignite.
        Collection<int[]> partitionsAll = ignite.compute().call(partitionsList);

        Collection<int[]> keysAll = ignite.compute().call(keysList);
        
        System.out.println("PArtitions / counts info:" + partitionsAll.size() + "keys = " + keysAll.size());
        
        
   //      int sum = res.stream().mapToInt(i -> i).sum();



   

        
    
    }
  
    private static void CollectKeysListEachPartition(Ignite ignite, String cachename) throws IgniteException {
        
    	
        System.out.println("Collecting Getting Cache {" + cachename 
        		+ "} partition Count and Keys List from this node id= " 
        		+ ignite.cluster().localNode().id());
        
        int[] partitions; 
        partitions = ignite.affinity(cachename).allPartitions(ignite.cluster().localNode());
        System.out.print( " # partitions = " + partitions.length + " [");
     	for (int part : partitions)       		
    		System.out.print(part + ",");
    	System.out.print("]\n");
    	
    	
    	System.out.println("Data Region metrics size : " + ignite.dataRegionMetrics().size());
    	
    	Collection<IgniteCallable<Integer>> calls = new ArrayList<>();

        // Iterate through all words in the sentence and create callable jobs.
        for (String word : "Count characters using callable".split(" ")) {
            calls.add(() -> {
                System.out.println();
                System.out.println(">>> Printing '" + word + "' on this node from ignite job.");

                return word.length();
            });
        }

        // Execute collection of callables on the ignite.
        Collection<Integer> res = ignite.compute().call(calls);

        int sum = res.stream().mapToInt(i -> i).sum();

        
    
    }
  
  
 
 
    
}