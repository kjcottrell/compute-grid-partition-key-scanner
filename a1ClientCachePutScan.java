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


import javax.cache.Cache.Entry;
import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.*;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cluster.*;
import org.apache.ignite.examples.ExampleNodeStartup;
import org.apache.ignite.examples.ExamplesUtils;
import org.apache.ignite.lifecycle.*;
import org.apache.ignite.cache.query.*;
import org.apache.ignite.cache.CacheEntryProcessor;
import org.apache.ignite.lang.IgniteBiPredicate;

import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.time.ZonedDateTime;

import org.apache.ignite.ml.dataset.impl.cache.util.DatasetAffinityFunctionWrapper;


public class a1ClientCachePutScan {
   
    public static void main(String[] args) throws IgniteException {
     	
    	long timestamp = new Date().getTime();
    	

    	IgniteConfiguration cfg = new IgniteConfiguration();
    	
    	String INSTANCE_NAME = "ClientCachePuts-"+ Long.toString(timestamp);
    	String PERSON_CACHE_NAME = "PersonCache" ;
    	
    	CacheConfiguration<Long, Person> personCacheCfg = new CacheConfiguration<>();
    	personCacheCfg.setName(PERSON_CACHE_NAME);
    	
    	// register with SQL layer engine
    	personCacheCfg.setIndexedTypes(Long.class, Person.class);
    	
    	personCacheCfg.setBackups(1);
    	
    	System.out.println("Cache/Backups = " + PERSON_CACHE_NAME + "/" + personCacheCfg.getBackups());
     	
    	cfg.setIgniteInstanceName(INSTANCE_NAME);
   
    	

    	
 //   	Ignite ignite = Ignition.start("examples/config/example-ignite.xml");
    	
    	Ignition.setClientMode(true);
    	Ignite ignite = Ignition.start(cfg);
    	
    	IgniteCluster cluster = ignite.cluster();
      // ClusterGroup grp = ignite.cluster();
     
    	int j = 1;
    	for (ClusterNode node : cluster.forServers().nodes()) {
            
    		System.out.println("Server node {" + j + "} up and Running,  id  =  " + node.id() /* + " Attributes = " + node.attributes() */);
    		j++;
    	}
    	
    	IgniteCache<Long, Person> personCache = ignite.getOrCreateCache(personCacheCfg);
    	
  
    	
    	 for (long i = 1; i< 50; i++)
    	 {
    		 String ctr = Long.toString(i);
  
    		// these orgIds are set to 100, 101, 102, 103, 104 about 10 each....
            Person val = new Person(i, (Long) (100 + i/10) , "FN" + ctr, "LN" + ctr, 60000+i, "some random text " + ctr);
 
            personCache.put(i,  val);
            if(i%10 == 0)
            	System.out.println("Got [key=" + i + ", val=" + personCache.get(i) + ']');
            
    	 }
    	 
    	 // Long orgID = 20L;
    	 ScanQuery<Long, Person> filter1 = new ScanQuery<>();
    	 System.out.println("scan results" + personCache.query(filter1).getAll()) ;
    	 
    	 // filter1.setPartition(0);
    	 // filter1.setLocal(true);
    	 // filter1.setFilter(xx);
    	 
    	 long myPredicate = 104L;
    	 
     	 IgniteBiPredicate<Long, Person> orgfilter = (key, p) -> p.orgId == myPredicate;
     	

    	 try (QueryCursor<Entry<Long, Person>> qryCursor = personCache.query(new ScanQuery<>(orgfilter))) {
    		    qryCursor.forEach(
    		            entry -> System.out.println("query cursor predicate ={" + myPredicate + "} , Key = " + entry.getKey() + ", Value = " + entry.getValue()));
    		}
    	 

        filter1.setFilter(orgfilter);
        System.out.println("Orgid == 104 scan results" + personCache.query(filter1).getAll()) ;
         
        QueryCursor<List<?>> cursor = personCache.query(new SqlFieldsQuery(
        		  "select salary from Person"));
        System.out.print("Person.salary=");
        for (List<?> row : cursor)
            System.out.print(row.get(0) +",");
        
        
        
        System.out.println("closing this Client Node now ....  Person cache objects have been created in Server nodes");
    	 ignite.close(); 
    	 
    	 
    	
    	 
        
    }
    
   
}
    
    

