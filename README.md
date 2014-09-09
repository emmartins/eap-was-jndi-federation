eap-was-jndi-federation
=======================

A showcase for JNDI Federation in JBoss EAP 6.3, by integrating the JNDI tree a remote IBM WebSphere AS (WAS) 8.5 instance.

The setup in both app servers is as simple as possible, an Echo EJB is deployed in a WebSphere AS instance, and a web app containing a single servlet is deployed in JBoss EAP. Beyond that, the JBoss EAP instance is configured to federate the WebSphere AS remotely available namespace, in a java:global local name.

When the web app opened in a web browser, the servlet is triggered and it invokes the remote EJB. The remote EJB is looked up through the local java:global context containing the federated WebSphere remote namespace.   

The project uses Apache Maven to build, and includes 3 modules:

* **shared** - Provides the Echo bean interfaces used by the EAP and WAS deployments

* **was-ejb** - An EJB jAR containing the Echo bean implementation and the interfaces provided by the 'share' module, to be deployed in WAS

* **eap-war** - The WAR to be deployed in EAP, containing the servlet that invokes the Echo bean in WAS
    
## Build, Install, Run!

To build simple do 'mvn install'.
    
To deploy the EJB JAR in a started WAS instance, open the management console in a web browser, available at http://localhost:9060/ibm/console if using the default install configuration, then select 'Applications -> New Application', point to the eap-was-jndi-federation-was-ejb*.jar in was-ejb/target, and select 'Next' till install completes. Finally start the installed app to complete deployment at WAS.
  
To federate the WAS instance JNDI namespace, edit the standalone-full.xml in standalone/configuration dir of EAP, and setup the naming subsystem as:

```
<subsystem xmlns="urn:jboss:domain:naming:1.4">
  <bindings>
    <external-context name="java:global/was" module="org.jboss.as.jacorb" class="javax.naming.InitialContext">
      <environment>
        <property name="java.naming.factory.initial" value="org.jboss.as.jacorb.naming.jndi.CNCtxFactory"/>
        <property name="java.naming.provider.url" value="corbaname:iiop:localhost:2809"/>
      </environment>
    </external-context>
  </bindings>
  <remote-naming/>
</subsystem>
```

Before deploying the WAR in the EAP instance, don't forget to start EAP with the standalone-full configuration, by adding param '-c standalone-full.xml', then all need is a 'mvn -f eap-war/pom.xml jboss-as:deploy' at this project root directory.

Finally, to fire the missiles, point a web browser to http://localhost:8080/eap-was-jndi-federation-eap-war, and if everything goes as expected you should see a page indicating 'OK' as result of invoking the remote EJB. 
   
Enjoy!
   
**--E** 
 
