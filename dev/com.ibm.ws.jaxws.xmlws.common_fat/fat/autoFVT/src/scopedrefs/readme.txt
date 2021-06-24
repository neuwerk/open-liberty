
These tests check that the new scoped references, java:module, app and global, work for
webservice references in ejb, war and application client containers.

A service-ref defined at the module level should be visible to all beans within the module.

A service-ref defined at the app level should be visible to all beans within the app.

A service-ref defined at the global level should be visible to all apps within the cell.
By GM, this will include apps installed on different servers.

Test Design:
There are two server-side applications, each containing multiple modules.

Web services within each module are called and verify service-refs of the appropriate
scope are usable.

Note that for war and application client containers, java:module scope is already the
default, it's only new for ejb's, where service-refs are normally scoped per-bean.

Based on the table below, we'll add refs into the apps and modules so all the invokes
in the table can be tested.

app1:
module1 is two ejb's in a jar.
module2 is two ejb's in a war 
module3 is two servlets in a war.

app2:
module1 is two ejb's in a jar
module2 does not exist
module3 is a servlet (b1) and an ejb (b2) in a war. 

=== module ref tests (refs within a module) ====
test#                                         type        where   defined_in  defined   used_in
                                              _of_ref     defined             how

10       a1m1b2_lookup_and_invoke_a1m1b1      mod          a1m1b1      ejb                 ejb
20       a1m1b1_lookup_and_invoke_a1m1b1      mod          a1m1b1      ejb                 ejb  (using self ref)
30       a1m3b1                   a1m3b2      mod                                                 (using module ref between servlets, should work)


=== app ref tests (refs within an app)    ====
100       a1m3b1_lookup_and_invoke_a1m1b2      app          a1m1        ejb       dd        war
110       a1m2b1_lookup_and_invoke_a1m1b1      app          a1m3b2      ejb       anno      ejb_in_war
120       a1m3b1_lookup_and_invoke_a1m2b2      app          a1m2b1      ejb-inwar anno      war
130       a1m1b2                   a1m2b1      app          a1m3b1      war       anno      ejb
140       a1m1b1                   a1m2b1  (m1 invoke m2 using module ref, should be nul

=== global ref tests ===
200       a1m2b1                   a2m3b1      glo          a2m3b2      ejb_inwar anno     ejb
210       a1m3b1                   a2m3b1      glo          a1m1b1                dd     (war-to-war)
220       a1m1b1                   a2m2b1      glo          a2_anywhere           anno   (ejb-in-war to ejb)
230       a1m3b2                   a1m1b1      glo          a1_anywhere           dd     (ejb-to-ejb
240       a2m1b1                   a2m3b1      glo          a2_anywhere


Application client:
    Does multiple modules in an app client even make sense?
    There can be >1 module, but only one main.
    Should the client be able to see the server side global refs? - yes. 


I was torn between writing separate impls for each module, that are mostly
duplicates, and writing generic ones that could be used anywhere.  My experience
with tests like jms and addressing has left me thinking that while the latter
results in more code reuse, it also results in more confusion when debugging, so
we'll go with some duplicate, but explicit code.

a note on the _real and _fake directories:
  - the "fakes" are needed to be compiled so client code can be  produced,
    upon which the "real" classes depend.

  - the "real"s could not be left in a correctly named directory because
    at build time wsgen runs in the websvcs.fvt/src dir and sees them, reads
    the java files, and complains that it can't find the "generatedclient*" files.
    Way too helpful.



Some fancier tests we thought of but didn't have time to implement:
  - Try an @webserviceref annotation to inject a client ref into a java class, using 
    the new "lookup" attribute.  See if we can lookup across apps/servers.
  
  - References of same name, different scope, determine which one should
    win and make sure we work correctly.
    
  - Multiple globals of the same name, we should get some sort of conflict
    or error, last one to deploy should not just "win". 
    
  - Try a handler chain defined in a global or app dd.
  
  - Try a single file update, change the dd or injecting annotation, see
    if  namespaces are correctly updated as a result of the application update.
    
  - Try some of the new attributes like mtom and addressing and make sure they
    can still be overridden by dd's even if annotations are in one app and 
    global dd's are declared in another.   
    